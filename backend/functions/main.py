from firebase_functions import firestore_fn, https_fn
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
    except KeyError:
        return https_fn.Response("No tableId or playerId provided", status=400)
    # Check if the tableId exists
    try:
        table = read_table_from_firestore(tableId)
    except ValueError:
        return https_fn.Response("Table does not exist", status=400)
    player = Player(name=name)
    table.add_player(player)

    write_table_to_firestore(table)

    # Add the player to the players collection: key = playerId, value = tableId
    doc_ref = players_ref.document(player.playerId)
    doc_ref.set({"tableId": tableId, "name": name})

    return https_fn.Response("playerId: " + player.playerId, status=200)    

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
    """Delete all empty tables"""
    for table in tables_ref.stream():
        table_id = table.id
        local_players_ref = tables_ref.document(table_id).collection("players")
        players = local_players_ref.stream()
        player_count = sum(1 for _ in players)
        if player_count == 0:
            # Delete the table document
            delete_table(tables_ref.document(table_id))
            print(f"Deleted table {table.id}")
        else:
            print(f"Table {table.id} has players, not deleting")
    return https_fn.Response("OK", status=200)

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
        table.start_game()
        # Update the table document
        write_table_to_firestore(table)

    return https_fn.Response("Game started", status=200)

def delete_collection(coll_ref, batch_size=10):
    docs = coll_ref.limit(batch_size).stream()
    deleted = 0
    for doc in docs:
        print(f"Deleting document: {doc.id}")
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

@https_fn.on_request()
def queue_test(request: https_fn.Request):
    """Test the queue"""
    create_task(10, "cleanup")
    return https_fn.Response("Task created", status=200)
    
