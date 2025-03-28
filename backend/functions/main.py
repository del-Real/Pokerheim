# The Cloud Functions for Firebase SDK to create Cloud Functions and set up triggers.
from firebase_functions import firestore_fn, https_fn

# The Firebase Admin SDK to access Cloud Firestore.
from firebase_admin import initialize_app, firestore
import google.cloud.firestore

from classes import Table, Player
import random

app = initialize_app()
firestore_client: google.cloud.firestore.Client = firestore.client()

@https_fn.on_request()
def createTable(req: https_fn.Request) -> https_fn.Response:
    """Create a table document in the tables collection"""
    try:    
        tableId = req.args.get("tableId")
    except KeyError:
        # No needed field, so do nothing.
        tableId = random.randint(0, 1000000)
        tableId = str(tableId)
        tableId = tableId.zfill(6)
    
    # Check if the tableId already exists
    tables_ref = firestore_client.collection("tables")
    doc_ref = tables_ref.document(tableId)
    if doc_ref.get().exists:
        # If it exists, return an error
        return https_fn.Response("Table already exists", status=400)
    new_table = Table(tableId)

    # Add the table to the tables collection
    tables_ref = firestore_client.collection("tables")
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
        # No needed field, so do nothing.
        return https_fn.Response("No tableId or playerId provided", status=400)
    # Check if the tableId exists
    tables_ref = firestore_client.collection("tables")
    doc_ref = tables_ref.document(tableId)
    if not doc_ref.get().exists:
        # If it doesn't exist, return an error
        return https_fn.Response("Table does not exist", status=400)

    # Get the table document
    tables_ref = firestore_client.collection("tables")
    doc_ref = tables_ref.document(tableId)
    table = Table.from_dict(doc_ref.get().to_dict())

    player = Player(name=name)
    # Add the player to the table
    table.add_player(player)
    doc_ref.set(table.to_dict())

    return https_fn.Response("playerId: " + player.playerId, status=200)