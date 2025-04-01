# Our lovely backend
## Setup
1. Set up firebase CLI (https://firebase.google.com/docs/cli)
2. `firebase login`
3. from the backend directory:
    - `pip install virtualenv` (if you dont have it already)
    - `virtualenv .\functions\venv && .\functions\venv\Scripts\activate.bat && pip install -r .\functions\requirements.txt`

its more detailed here: https://firebase.google.com/docs/functions/get-started?gen=2nd#python \
<b>backend is in python because i love python, sorry not sorry </b>

## Running 

Run locally with `firebase emulators:start`.\
You can see our dashboard at `http://127.0.0.1:4000`.

Deploy with `i dont actually know how`, but i dont recommend doing it yet because it costs money.

## Endpoints

### http://127.0.0.1:5001/pokergame-007/us-central1/createTable
Creates a table, returns its ID so others can join.\
Params: 
- tableId (optional, randomized if not given)

### http://127.0.0.1:5001/pokergame-007/us-central1/joinTable
Adds a player to a table.\
Params:
- tableId: ID of the table to join
- playerId (optional, randomized if not given): the joining player's ID

## TODO endpoints
### http://127.0.0.1:5001/pokergame-007/us-central1/performAction
Player performs an action.\
Params:
- actionType (CALL, BET, FOLD, etc.)
- value (if applicable)

and ofcourse the logic behind all this...