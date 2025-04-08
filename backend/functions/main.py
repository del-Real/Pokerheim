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

@https_fn.on_request()
def createTable(req: https_fn.Request) -> https_fn.Response:
    """Create a table document in the tables collection"""
    try:    
        tableId = req.args.get("tableId")
    except KeyError:
        tableId = random.randint(0, 1000000)
        tableId = str(tableId)
        tableId = tableId.zfill(6)
    
    # Check if the tableId already exists
    tables_ref = db.collection("tables")
    doc_ref = tables_ref.document(tableId)
    if doc_ref.get().exists:
        # If it exists, return an error
        return https_fn.Response("Table already exists", status=400)
    new_table = Table(tableId)

    # Add the table to the tables collection
    tables_ref = db.collection("tables")
    doc_ref = tables_ref.document(new_table.tableId)
    doc_ref.set(new_table.to_dict())
    return https_fn.Response("tableId: " + new_table.tableId, status=200)

@https_fn.on_request()
def joinTable(req: https_fn.Request) -> https_fn.Response:
    """Join a table document in the tables collection"""
    try:
        tableId = tableId = req.args.get("tableId")
        name = req.args.get("name")
    except KeyError:
        return https_fn.Response("No tableId or playerId provided", status=400)
    # Check if the tableId exists
    tables_ref = db.collection("tables")
    doc_ref = tables_ref.document(tableId)
    if not doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)

    # Get the table document
    tables_ref = db.collection("tables")
    doc_ref = tables_ref.document(tableId)
    table = Table.from_dict(doc_ref.get().to_dict())

    player = Player(name=name)
    # Add the player to the table
    table.add_player(player)
    doc_ref.set(table.to_dict())


    # Add the player to the players collection: key = playerId, value = tableId
    players_ref = db.collection("players")
    doc_ref = players_ref.document(player.playerId)
    doc_ref.set({"tableId": table.tableId})

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
    players_ref = db.collection("players")
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return https_fn.Response("Player does not exist", status=400)
    # Get the tableId from the player document
    player_data = players_doc_ref.get().to_dict()
    tableId = player_data["tableId"]
    # Check if the tableId exists
    tables_ref = db.collection("tables")
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)
    # Get the table document
    table = Table.from_dict(tables_doc_ref.get().to_dict())
    # Check if the playerId exists in the table
    if playerId not in table.players:
        return https_fn.Response("Player does not exist in the table", status=400)
    
    # Perform the action
    try:
        table.perform_action(playerId, action, amount)
        # Update the table document
        tables_doc_ref.set(table.to_dict())
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
    players_ref = db.collection("players")
    players_doc_ref = players_ref.document(playerId)
    if not players_doc_ref.get().exists:
        return https_fn.Response("Player does not exist", status=400)
    # Get the tableId from the player document
    player_data = players_doc_ref.get().to_dict()
    tableId = player_data["tableId"]
    # Check if the tableId exists
    tables_ref = db.collection("tables")
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)
    # Get the table document
    table = Table.from_dict(tables_doc_ref.get().to_dict())
    # Check if the playerId exists in the table
    if playerId not in table.players:
        return https_fn.Response("Player does not exist in the table", status=400)
    # Update the player status
    table.change_player_status(playerId, status)
    # Update the table document
    tables_doc_ref.set(table.to_dict())
    if table.start_game_possible():
        params = {
            "tableId": table.tableId,
        }
        create_task(15, "startGame", params)
    return https_fn.Response("Player status updated", status=200)



def create_task(delay:int, fn_name:str, params:dict) -> str:
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
    tables_ref = db.collection("tables")
    docs = tables_ref.stream()
    for doc in docs:
        table = Table.from_dict(doc.to_dict())
        if len(table.players) == 0 or table.status == "complete":
            # Delete the table document
            tables_ref.document(doc.id).delete()
            print(f"Deleted table {doc.id}")
        else:
            print(f"Table {doc.id} has players, not deleting")
    return https_fn.Response("OK", status=200)

@https_fn.on_request()
def startGame(request: https_fn.Request):
    """Start the game for a table"""
    try:
        tableId = request.args.get("tableId")
    except KeyError:
        return https_fn.Response("Parameters missing", status=400)
    # Check if the tableId exists
    tables_ref = db.collection("tables")
    tables_doc_ref = tables_ref.document(tableId)
    if not tables_doc_ref.get().exists:
        return https_fn.Response("Table does not exist", status=400)
    # Get the table document
    table = Table.from_dict(tables_doc_ref.get().to_dict())
    # Check if the game has already started
    if table.start_game_possible():
        table.start_game()
        # Update the table document
        tables_doc_ref.set(table.to_dict())

    return https_fn.Response("Game started", status=200)

@https_fn.on_request()
def queue_test(request: https_fn.Request):
    """Test the queue"""
    create_task(10, "cleanup")
    return https_fn.Response("Task created", status=200)
    
