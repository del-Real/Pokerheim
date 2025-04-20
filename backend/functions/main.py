from firebase_functions import firestore_fn, https_fn, scheduler_fn
from firebase_admin import initialize_app, firestore, credentials
import google.cloud.firestore
import asyncio
import os
import json
from google.cloud import tasks_v2
from google.protobuf import duration_pb2, timestamp_pb2
import datetime

from classes import Table, Player
import random

cred = credentials.Certificate('creds.json')
app = initialize_app(cred)
db: google.cloud.firestore.Client = firestore.client()
players_ref = db.collection("players")
tables_ref = db.collection("tables")

etc = ['players']
public = ['pot', 'currentTurn', 'community_cards', 'last_action']

def write_table_to_firestore(table: Table, ref=None) -> None:
    """Write a table to Firestore"""
    if ref is None:
        ref = tables_ref.document(table.tableId)
    table_dict = table.to_dict()
    # save private data in subcollection
    private_data = {key: table_dict[key] for key in table_dict if key not in public and key not in etc}
    public_data = {key: table_dict[key] for key in table_dict if key in public}
    # save public data in the main document
    ref.set(public_data)
    # save private data in a subcollection
    private_ref = ref.collection("private").document("info")
    private_ref.set(private_data)
    # save players in a subcollection
    for playerId, player in table.players.items():
        player_ref = ref.collection("players").document(playerId)
        player_dict = player.to_dict()
        player_ref.set(player_dict)

def read_table_from_firestore(tableId: str) -> Table:
    """Read a table from Firestore"""
    table_doc_ref = tables_ref.document(tableId)
    if not table_doc_ref.get().exists:
        raise ValueError("Table does not exist")
    # Get the public data
    public_data = table_doc_ref.get().to_dict()
    # Get the private data
    private_data = table_doc_ref.collection("private").document("info").get().to_dict()
    # Get the players data
    players_data = table_doc_ref.collection("players").stream()
    players = {}
    for player in players_data:
        players[player.id] = player.to_dict()
    # combine the three data into a single dictionary
    public_data.update(private_data)
    public_data['players'] = players
    table = Table.from_dict(public_data)
    return table

def gen_key() -> str:
    """Generate a random key"""
    return str(random.randint(0, 1000000)).zfill(6)

@https_fn.on_request()
def createTable(req: https_fn.Request) -> https_fn.Response:
    """Create a table document in the tables collection"""
    try:    
        tableId = req.args.get("tableId")
    except KeyError:
        tableId = gen_key()
    if tableId is None:
        tableId = gen_key()
    
    table_doc_ref = tables_ref.document(tableId)
    if table_doc_ref.get().exists:
        return https_fn.Response("Table already exists", status=400)
    
    new_table = Table(tableId)
    write_table_to_firestore(new_table)
    return https_fn.Response("tableId: " + tableId, status=200)

@https_fn.on_request()
def joinTable(req: https_fn.Request) -> https_fn.Response:
    """Join a table document in the tables collection"""
    try:
        tableId = req.args.get("tableId")
        name = req.args.get("name")
        playerId = req.args.get("playerId", None)
    except KeyError:
        return https_fn.Response("No tableId or name provided", status=400)
    # Check if the tableId exists
    try:
        table = read_table_from_firestore(tableId)
    except ValueError:
        return https_fn.Response("Table does not exist", status=400)
    # Check if the playerId exists
    if playerId is not None:
        players_doc_ref = players_ref.document(playerId)
        if players_doc_ref.get().exists:
            return https_fn.Response("Player already exists", status=400)
    player = Player(name=name, last_action=datetime.datetime.utcnow(), playerId=playerId)
    table.add_player(player)

    write_table_to_firestore(table)

    # Add the player to the players collection: key = playerId, value = tableId
    doc_ref = players_ref.document(player.playerId)
    doc_ref.set({"tableId": tableId, "name": name, "last_action": player.last_action})

    return https_fn.Response("playerId: " + player.playerId, status=200)

@https_fn.on_request()
def leaveTable(req: https_fn.Request) -> https_fn.Response:
    """Leave a table document in the tables collection"""
    try:
        playerId = req.args.get("playerId")
    except KeyError:
        return https_fn.Response("No playerId provided", status=400)
    
    # Check if the playerId exists
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return https_fn.Response("Player does not exist", status=400)
    
    delete_player(playerId)

    return https_fn.Response("Player removed", status=200)

@https_fn.on_request()
def performAction(req: https_fn.Request) -> https_fn.Response:
    """Perform an action on a table document in the tables collection"""
    try:
        playerId = req.args.get("playerId")
        action = req.args.get("action")
        amount = int(req.args.get("amount", 0))
    except KeyError:
        return https_fn.Response("Parameters missing", status=400)
    
    # Check if the playerId exists
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return https_fn.Response("Player does not exist", status=400)
    
    # Get the tableId from the player document
    player_data = players_doc_ref.get().to_dict()
    players_doc_ref.update({"last_action": datetime.datetime.utcnow()})
    tableId = player_data["tableId"]

    # Check if the tableId exists
    try:
        table = read_table_from_firestore(tableId)
    except ValueError:
        return https_fn.Response("Table does not exist", status=400)
    
    # Check if the playerId exists in the table
    if playerId not in table.players:
        return https_fn.Response("Player does not exist in the table", status=400)
    prev_status = table.status
    # Perform the action
    try:
        table.perform_action(playerId, action, amount)
        # Update the table document
        write_table_to_firestore(table)
        new_status = table.status

        if new_status == 'complete' and prev_status != 'complete':
            create_task(20, "startGame", {"tableId": tableId})
    except Exception as e:
        return https_fn.Response("Error performing action: " + str(e), status=400)

    return https_fn.Response("Action performed", status=200)

@https_fn.on_request()
def playerStatus(req: https_fn.Request) -> https_fn.Response:
    """Send status update of player"""
    try:
        playerId = req.args.get("playerId")
        status = req.args.get("status")
    except KeyError:
        return https_fn.Response("Parameters missing", status=400)
    # Check if the playerId exists
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return https_fn.Response("Player does not exist", status=400)
    # Get the tableId from the player document
    player_data = players_doc_ref.get().to_dict()
    players_doc_ref.update({"last_action": datetime.datetime.utcnow()})
    tableId = player_data["tableId"]
    # Check if the tableId exists
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)
    # Get the table document
    table = read_table_from_firestore(tableId)
    # Check if the playerId exists in the table
    if playerId not in table.players:
        return https_fn.Response("Player does not exist in the table", status=400)
    if status not in ['ready', 'spectating']:
        return https_fn.Response("Invalid status", status=400)
    if status == 'ready' and table.players[playerId].stack <= table.minimum_raise:
        return https_fn.Response("Player stack is too low", status=400)
    # Update the player status
    table.change_player_status(playerId, status)
    # Update the table document
    write_table_to_firestore(table, ref=tables_doc_ref)
    if table.start_game_possible():
        params = {
            "tableId": table.tableId,
        }
        create_task(15, "startGame", params)
    return https_fn.Response("Player status updated", status=200)



def create_task(delay:int, fn_name:str, params:dict=None) -> str:
    client = tasks_v2.CloudTasksClient()

    project = 'pokergame-007'
    location = 'us-central1'
    queue = 'poker-game-queue'
    url = f'https://us-central1-pokergame-007.cloudfunctions.net/{fn_name}'
    # add query parameters to the url
    if params:
        url += '?'
        url += '&'.join([f"{key}={value}" for key, value in params.items()])

    parent = client.queue_path(project, location, queue)

    task = {
        "http_request": {
            "http_method": tasks_v2.HttpMethod.POST,
            "url": url,
        }
    }

    if delay is not None:
        d = datetime.datetime.utcnow() + datetime.timedelta(seconds=delay)
        timestamp = timestamp_pb2.Timestamp()
        timestamp.FromDatetime(d)
        task["schedule_time"] = timestamp

    response = client.create_task(request={"parent": parent, "task": task})
    return response.name

@https_fn.on_request()
def cleanup(request: https_fn.Request):
    try:
        ttl = int(request.args.get("ttl", 3600))
    except ValueError:
        return https_fn.Response("Invalid ttl value", status=400)
    clean_old_data(ttl)
    return https_fn.Response("Cleanup done", status=200)

@scheduler_fn.on_schedule(schedule="every 1 hours")
def scheduled_cleanup(event):
    """Scheduled function to clean up old data"""
    clean_old_data(3600)
    return https_fn.Response("Scheduled cleanup done", status=200)

def clean_old_data(ttl: int = 3600):
    """Delete empty tables and players that have not been active for the specified TTL"""
    now = datetime.datetime.now(datetime.timezone.utc)

    # Batch delete inactive players
    inactive_players = []
    for player in players_ref.stream():
        player_data = player.to_dict()
        last_action = player_data["last_action"].replace(tzinfo=datetime.timezone.utc)
        if (now - last_action).total_seconds() > ttl:
            inactive_players.append(player.id)

    for player_id in inactive_players:
        delete_player(player_id)

    # Batch delete empty tables
    empty_tables = []
    for table in tables_ref.stream():
        table_id = table.id
        local_players_ref = tables_ref.document(table_id).collection("players")
        if not any(local_players_ref.stream()):  # Check if the table has no players
            empty_tables.append(table_id)

    for table_id in empty_tables:
        delete_table(tables_ref.document(table_id))

@https_fn.on_request()
def startGame(request: https_fn.Request):
    """Start the game for a table"""
    try:
        tableId = request.args.get("tableId")
    except KeyError:
        return https_fn.Response("Parameters missing", status=400)
    # Check if the tableId exists
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)
    # Get the table document
    table = read_table_from_firestore(tableId)
    # Check if the game has already started
    if table.start_game_possible():
        table.reset_game()
        table.start_game()
        # Update the table document
        write_table_to_firestore(table)

    return https_fn.Response("Game started", status=200)

def delete_collection(coll_ref, batch_size=10):
    docs = coll_ref.limit(batch_size).stream()
    deleted = 0
    for doc in docs:
        doc.reference.delete()
        deleted += 1
    if deleted >= batch_size:
        # Repeat until all docs are deleted
        return delete_collection(coll_ref, batch_size)

def delete_table(ref):
    delete_collection(ref.collection('private'))
    local_players_ref = ref.collection("players")
    players = local_players_ref.stream()
    for player in players:
        players_ref.document(player.playerId).delete()
    delete_collection(ref.collection('players'))
    ref.delete()

def delete_player(playerId):
    """Delete a player from the players collection"""
    # Check if the playerId exists
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return
    tableId = players_doc_ref.get().to_dict()["tableId"]
    players_doc_ref.delete()

    # Check if the tableId exists
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return
    # Delete the player from the table
    table = read_table_from_firestore(tableId)
    table.remove_player(playerId)
    write_table_to_firestore(table)
    tables_doc_ref.collection("players").document(playerId).delete()

    
