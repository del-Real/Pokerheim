import random

class Card:
    def __init__(self, suit: str, rank: str):
        self.suit = suit
        self.rank = rank

    @classmethod
    def from_dict(cls, data: dict):
        return cls(data["suit"], data["rank"])

    def to_dict(self):
        return {
            "suit": self.suit,
            "rank": self.rank,
        }

class CardDeck:
    def __init__(self):
        self.cards = []
        suits = ["Hearts", "Diamonds", "Clubs", "Spades"]
        ranks = ["2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"]
        for suit in suits:
            for rank in ranks:
                self.cards.append(Card(suit, rank))

    def shuffle(self):
        random.shuffle(self.cards)

    def draw(self):
        return self.cards.pop() if self.cards else None
    
    def reset(self):
        self.cards = []
        self.__init__()
    

class Player:
    def __init__(self, name: str, playerId: str = None):
        if playerId is None:
            playerId = random.randint(0, 1000000)
            playerId = str(playerId)
            self.playerId = playerId.zfill(6)
        else:
            self.playerId = playerId
        self.name = name
        self.cards = []
        self.stack = 0

    @classmethod
    def from_dict(cls, data: dict):
        player = cls(data["name"], data["playerId"])
        player.cards = [Card.from_dict(card) for card in data["cards"]]
        player.stack = data["stack"]
        return player

    def to_dict(self):
        return {
            "playerId": self.playerId,
            "name": self.name,
            "cards": [card.to_dict() for card in self.cards],
            "stack": self.stack,
        }


class Table:
    def __init__(self, tableId: str):
        self.tableId = tableId
        self.players = {}
        self.currentTurn = None
        self.status = "waiting"
        self.deck = CardDeck()
        self.deck.shuffle()

    @classmethod
    def from_dict(cls, data: dict):
        table = cls(data["tableId"])
        table.currentTurn = data.get("currentTurn")
        table.status = data.get("status")
        for playerId, player_data in data["players"].items():
            table.players[playerId] = Player.from_dict(player_data)
        table.deck = CardDeck()
        table.deck.cards = [Card.from_dict(card) for card in data["deck"]]
        return table
    
    def add_player(self, player: Player):
        if player.playerId not in self.players:
            self.players[player.playerId] = player
        else:
            raise ValueError(f"Player {player.playerId} already exists in the table.")
        if len(self.players) == 2 and self.status == "waiting":
            self.status = "playing"
            self.currentTurn = list(self.players.keys())[0]
            self.deal_cards(2)  # Deal 2 cards to each player when the game starts
            self.start_game()

    def remove_player(self, playerId: str):
        if playerId in self.players:
            del self.players[playerId]
            if len(self.players) < 2:
                self.status = "waiting"
                self.currentTurn = None

    def deal_cards(self, num_cards: int):
        for player in self.players.values():
            for _ in range(num_cards):
                card = self.deck.draw()
                if card:
                    player.cards.append(card)
                else:
                    break

    def start_game(self):
        # Placeholder for game start logic
        pass

    def to_dict(self):
        return {
            "tableId": self.tableId,
            "players": {playerId: player.to_dict() for playerId, player in self.players.items()},
            "currentTurn": self.currentTurn,
            "status": self.status,
            "deck": [card.to_dict() for card in self.deck.cards],
        }