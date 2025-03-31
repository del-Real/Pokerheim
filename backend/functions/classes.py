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

    def __str__(self):
        return f"{self.rank} of {self.suit}"

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
        self.stack = 1000  # Default starting stack
        self.folded = False
        self.all_in = False

    @classmethod
    def from_dict(cls, data: dict):
        player = cls(data["name"], data["playerId"])
        player.cards = [Card.from_dict(card) for card in data["cards"]]
        player.stack = data["stack"]
        player.folded = data.get("folded", False)
        player.all_in = data.get("all_in", False)
        return player

    def to_dict(self):
        return {
            "playerId": self.playerId,
            "name": self.name,
            "cards": [card.to_dict() for card in self.cards],
            "stack": self.stack,
            "folded": self.folded,
            "all_in": self.all_in,
        }

    def __str__(self):
        return f"Player {self.name} (ID: {self.playerId}) with stack: {self.stack}"


class Table:
    def __init__(self, tableId: str):
        self.tableId = tableId
        self.players = {}
        self.player_order = []  # To maintain player order for turns
        self.current_player_idx = 0
        self.currentTurn = None
        self.status = "waiting"
        self.deck = CardDeck()
        self.deck.shuffle()
        self.pot = 0
        self.community_cards = []
        self.bets = {}  # Current round bets
        self.current_bet = 0  # Highest bet in the current round
        self.round = 0  # 0: pre-flop, 1: flop, 2: turn, 3: river
        self.small_blind = 5
        self.big_blind = 10
        self.dealer_position = 0  # Index of the dealer in player_order
        self.last_raiser = None  # To track when betting round ends
        self.minimum_raise = self.big_blind

    @classmethod
    def from_dict(cls, data: dict):
        table = cls(data["tableId"])
        table.currentTurn = data.get("currentTurn")
        table.status = data.get("status")
        table.pot = data.get("pot", 0)
        table.round = data.get("round", 0)
        table.current_bet = data.get("current_bet", 0)
        table.minimum_raise = data.get("minimum_raise", table.big_blind)
        table.player_order = data.get("player_order", [])
        table.current_player_idx = data.get("current_player_idx", 0)
        table.dealer_position = data.get("dealer_position", 0)
        table.community_cards = [Card.from_dict(card) for card in data.get("community_cards", [])]
        
        for playerId, player_data in data["players"].items():
            table.players[playerId] = Player.from_dict(player_data)
        
        table.deck = CardDeck()
        if "deck" in data:
            table.deck.cards = [Card.from_dict(card) for card in data["deck"]]
            
        table.bets = data.get("bets", {})
        table.last_raiser = data.get("last_raiser")
        
        return table
    
    def add_player(self, player: Player):
        if player.playerId not in self.players:
            self.players[player.playerId] = player
            self.player_order.append(player.playerId)
            self.bets[player.playerId] = 0
        else:
            raise ValueError(f"Player {player.playerId} already exists in the table.")
        
        if len(self.players) == 2 and self.status == "waiting":
            self.status = "playing"
            self.start_game()

    def remove_player(self, playerId: str):
        if playerId in self.players:
            del self.players[playerId]
            self.player_order.remove(playerId)
            if playerId in self.bets:
                del self.bets[playerId]
                
            if len(self.players) < 2:
                self.status = "waiting"
                self.currentTurn = None
                self.reset_game()

    def deal_cards(self, num_cards: int):
        for playerId in self.player_order:
            player = self.players[playerId]
            if not player.folded:
                for _ in range(num_cards):
                    card = self.deck.draw()
                    if card:
                        player.cards.append(card)
                    else:
                        break

    def reset_game(self):
        self.deck.reset()
        self.deck.shuffle()
        self.pot = 0
        self.community_cards = []
        self.round = 0
        self.current_bet = 0
        self.minimum_raise = self.big_blind
        self.last_raiser = None
        
        for player in self.players.values():
            player.cards = []
            player.folded = False
            player.all_in = False
            
        for playerId in self.player_order:
            self.bets[playerId] = 0

    def start_game(self):
        self.reset_game()
        
        # Move dealer button
        if self.player_order:
            self.dealer_position = (self.dealer_position + 1) % len(self.player_order)
        
        # Post blinds
        self.post_blinds()
        
        # Deal initial cards
        self.deal_cards(2)
        
        # Set current player (after big blind)
        sb_pos = (self.dealer_position + 1) % len(self.player_order)
        bb_pos = (self.dealer_position + 2) % len(self.player_order)
        self.current_player_idx = (bb_pos + 1) % len(self.player_order)
        self.currentTurn = self.player_order[self.current_player_idx]
        
        # Set last_raiser to big blind position initially
        self.last_raiser = self.player_order[bb_pos]

    def post_blinds(self):
        if len(self.player_order) < 2:
            return
            
        # Small blind
        sb_pos = (self.dealer_position + 1) % len(self.player_order)
        sb_player_id = self.player_order[sb_pos]
        sb_player = self.players[sb_player_id]
        
        amount = min(self.small_blind, sb_player.stack)
        sb_player.stack -= amount
        self.bets[sb_player_id] = amount
        if amount == sb_player.stack:
            sb_player.all_in = True
            
        # Big blind
        bb_pos = (self.dealer_position + 2) % len(self.player_order)
        bb_player_id = self.player_order[bb_pos]
        bb_player = self.players[bb_player_id]
        
        amount = min(self.big_blind, bb_player.stack)
        bb_player.stack -= amount
        self.bets[bb_player_id] = amount
        self.current_bet = amount
        
        if amount == bb_player.stack:
            bb_player.all_in = True

    def next_player(self):
        active_players = self.get_active_players()
        if len(active_players) <= 1:
            self.conclude_round()
            return
            
        # Find next player who hasn't folded or gone all-in
        for _ in range(len(self.player_order)):
            print(self.current_player_idx, self.player_order)
            self.current_player_idx = (self.current_player_idx + 1) % len(self.player_order)
            next_player_id = self.player_order[self.current_player_idx]
            player = self.players[next_player_id]
            print(f"Next player: {next_player_id}, folded: {player.folded}, all_in: {player.all_in}")
            
            if not player.folded and not player.all_in:
                self.currentTurn = next_player_id
                return
            print('all folded')
                
        # If we get here, all players are either folded or all-in
        self.conclude_round()

    def get_active_players(self):
        return [p_id for p_id in self.player_order if not self.players[p_id].folded]

    def check_round_complete(self):
        """Check if the current betting round is complete"""
        # If there's only one active player, the round is complete
        active_players = self.get_active_players()
        if len(active_players) <= 1:
            return True
            
        # Check if all active players have matched the current bet or gone all-in
        for p_id in active_players:
            player = self.players[p_id]
            # Skip players who are all-in
            if player.all_in:
                continue
                
            # If a player hasn't matched the current bet, the round continues
            if self.bets[p_id] < self.current_bet:
                return False
                
            # If the last player to act is the current player, and they've matched the bet
            if self.last_raiser and self.currentTurn == self.last_raiser:
                return True
                
        # All active players have matched the current bet
        return True

    def conclude_round(self):
        # Move all bets to the pot
        for player_id, bet in self.bets.items():
            self.pot += bet
            self.bets[player_id] = 0
            
        # Reset current bet
        self.current_bet = 0
        self.minimum_raise = self.big_blind
        self.last_raiser = None
        
        active_players = self.get_active_players()
        
        # If there's only one player left, they win
        if len(active_players) == 1:
            winner_id = active_players[0]
            winner = self.players[winner_id]
            winner.stack += self.pot
            self.pot = 0
            self.status = "complete"
            return
            
        # Deal community cards based on the round
        if self.round == 0:  # After pre-flop, deal the flop
            for _ in range(3):
                self.community_cards.append(self.deck.draw())
        elif self.round == 1 or self.round == 2:  # After flop/turn, deal one card
            self.community_cards.append(self.deck.draw())
            
        # Move to the next round
        self.round += 1
        
        # If we've completed the river (round 3), evaluate hands and determine winner
        if self.round > 3:
            self.evaluate_hands()
            return
            
        # Otherwise, start next betting round
        # First active player after dealer acts first
        self.next_player()

    def evaluate_hands(self):
        # In a real implementation, this would evaluate the best hand for each player
        # For simplicity, we'll just distribute the pot evenly among active players
        # TODO TODO TODO 
        active_players = self.get_active_players()
        if active_players:
            split_amount = self.pot // len(active_players)
            for player_id in active_players:
                self.players[player_id].stack += split_amount
            self.pot = 0
            
        self.status = "complete"
        # Normally you would restart the game here

    def to_dict(self):
        return {
            "tableId": self.tableId,
            "players": {playerId: player.to_dict() for playerId, player in self.players.items()},
            "player_order": self.player_order,
            "current_player_idx": self.current_player_idx,
            "currentTurn": self.currentTurn,
            "status": self.status,
            "deck": [card.to_dict() for card in self.deck.cards],
            "pot": self.pot,
            "community_cards": [card.to_dict() for card in self.community_cards],
            "bets": self.bets,
            "current_bet": self.current_bet,
            "round": self.round,
            "dealer_position": self.dealer_position,
            "last_raiser": self.last_raiser,
            "minimum_raise": self.minimum_raise
        }
    
    # ACTIONS
    def perform_action(self, playerId: str, action: str, amount: int = 0):
        if playerId not in self.players:
            raise ValueError(f"Player {playerId} does not exist in the table.")
            
        if playerId != self.currentTurn:
            raise ValueError(f"It's not player {playerId}'s turn.")
            
        player = self.players[playerId]
        
        if player.folded or player.all_in:
            raise ValueError(f"Player {playerId} cannot act (folded or all-in).")
        
        if action == "bet" or action == "raise":
            # Convert bet to raise if there's already a bet
            if self.current_bet > 0:
                action = "raise"
                
            # Validate raise/bet amount
            if action == "bet":
                # First bet must be at least the big blind
                if amount < self.big_blind:
                    raise ValueError(f"Bet must be at least the big blind ({self.big_blind}).")
                    
                # Cannot bet more than you have
                bet_amount = min(amount, player.stack)
                player.stack -= bet_amount
                if player.stack == 0:
                    player.all_in = True
                self.bets[playerId] = bet_amount
                self.current_bet = bet_amount
                self.minimum_raise = bet_amount
                
            else:  # raise
                # Raise must be at least the current bet plus the minimum raise
                minimum_total = self.current_bet + self.minimum_raise
                if amount < minimum_total:
                    raise ValueError(f"Raise total must be at least {minimum_total}.")
                    
                # Calculate how much more the player needs to add
                to_call = self.current_bet - self.bets[playerId]
                raise_amount = amount - self.current_bet
                
                # Cannot raise more than you have
                total_amount = min(to_call + raise_amount, player.stack)
                bet_amount = min(to_call + raise_amount, player.stack + self.bets[playerId])
                player.stack -= total_amount
                self.bets[playerId] += total_amount
                
                # If player doesn't have enough to complete the raise, they go all-in
                if total_amount < to_call + raise_amount:
                    player.all_in = True
                if total_amount >= self.current_bet:
                    # Set the new current bet and minimum raise
                    self.current_bet = bet_amount
                    self.minimum_raise = bet_amount
                    
            # Player who raises becomes the last raiser
            self.last_raiser = playerId
            
        elif action == "fold":
            player.folded = True
            
        elif action == "call":
            to_call = self.current_bet - self.bets[playerId]
            
            # Cannot call more than you have
            call_amount = min(to_call, player.stack)
            player.stack -= call_amount
            self.bets[playerId] += call_amount
            
            # If player can't match the full bet, they go all-in
            if call_amount < to_call or player.stack == 0:
                player.all_in = True
                
        elif action == "check":
            # Can only check if no bet has been made or player has matched current bet
            if self.current_bet > self.bets[playerId]:
                raise ValueError("Cannot check when there's an active bet.")
                
        else:
            raise ValueError(f"Unknown action: {action}")
            
        # Check if the round is complete after this action
        if self.check_round_complete():
            self.conclude_round()
        else:
            # Move to the next player
            self.next_player()
            
        return True