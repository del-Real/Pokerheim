import random
from treys import Card as treysCard
from treys import Evaluator

evaluator = Evaluator()

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

def to_treys_format(card_array: list) -> list:
    """Convert player cards to treys format"""
    return [treysCard.new(card.rank + card.suit) for card in card_array]

def eval_hand(board: list, player_cards: list) -> int:
    """Evaluate the hand using treys"""
    board_treys = to_treys_format(board)
    player_cards_treys = to_treys_format(player_cards)
    score = evaluator.evaluate(board_treys, player_cards_treys)
    return score

class CardDeck:
    def __init__(self):
        self.cards = []
        suits = ["h", "d", "c", "s"]
        ranks = ["2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"]
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
    def __init__(self, name: str, last_action, playerId: str = None):
        if playerId is None:
            playerId = random.randint(0, 1000000)
            playerId = str(playerId)
            self.playerId = playerId.zfill(6)
        else:
            self.playerId = playerId

        self.name = name
        self.last_action = last_action
        self.cards = []
        self.stack = 1000  # Default starting stack
        self.folded = False
        self.all_in = False
        # STATUS: "playing", "spectating", "ready"
        self.status = "spectating"
        self.had_action = False

    def reset(self):
        self.cards = []
        self.folded = False
        self.all_in = self.stack == 0
        self.last_action = None
        self.had_action = False

    @classmethod
    def from_dict(cls, data: dict):
        player = cls(data["name"], data["last_action"], data["playerId"])
        player.cards = [Card.from_dict(card) for card in data["cards"]]
        player.stack = data["stack"]
        player.folded = data.get("folded", False)
        player.all_in = data.get("all_in", False)
        player.status = data.get("status", "spectating")
        player.had_action = data.get("had_action", False)
        return player

    def to_dict(self):
        return {
            "playerId": self.playerId,
            "last_action": self.last_action,
            "name": self.name,
            "cards": [card.to_dict() for card in self.cards],
            "stack": self.stack,
            "folded": self.folded,
            "all_in": self.all_in,
            "status": self.status,
            "had_action": self.had_action,
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
        self.last_action = None  # This will store the last action description

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

        table.last_action = data.get("last_action", "")
        
        return table
    
    def add_player(self, player: Player):
        if player.playerId not in self.players:
            self.players[player.playerId] = player
        else:
            raise ValueError(f"Player {player.playerId} already exists in the table.")

    def remove_player(self, playerId: str):
        if playerId in self.players:
            if self.currentTurn == playerId:
                # If the player being removed is the current turn, move to the next player
                self.next_player()

            del self.players[playerId]
            if playerId in self.player_order:
                self.player_order.remove(playerId)
            if playerId in self.bets:
                del self.bets[playerId]
                
            if len(self.players) < 2:
                self.status = "waiting"
                self.currentTurn = None
                self.reset_game()

    def change_player_status(self, playerId: str, status: str):
        if playerId in self.players:
            self.players[playerId].status = status

    def start_game_possible(self):
        # check if atleast 2 players are 'ready' and no game is in progress
        ready_players = [p for p in self.players.values() if p.status == "ready"]
        if len(ready_players) >= 2 and (self.status == "waiting" or self.status == "complete"):
            return True
        return False

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
            player.reset()
            
        for playerId in self.player_order:
            self.bets[playerId] = 0

    def start_game(self):
        self.reset_game()
        self.player_order = [p_id for p_id in self.players if self.players[p_id].status == "ready" or self.players[p_id].status == "playing"]
        self.bets = {p_id: 0 for p_id in self.player_order}
        # update player status to playing
        for playerId in self.player_order:
            if self.players[playerId].status == "ready":
                self.players[playerId].status = "playing"
        
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
        self.status = "playing"

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
            return False
            
        # Find next player who hasn't folded or gone all-in
        for _ in range(len(self.player_order)):
            self.current_player_idx = (self.current_player_idx + 1) % len(self.player_order)
            next_player_id = self.player_order[self.current_player_idx]
            player = self.players[next_player_id]
            
            if not player.folded and not player.all_in:
                self.currentTurn = next_player_id
                return True
            
        # If we get here, all players are either folded or all-in
        return True
        

    def get_active_players(self, include_all_in=True):
        """Get a list of active players (not folded or all-in)"""
        if include_all_in:
            return [p_id for p_id in self.player_order if not self.players[p_id].folded]
        return [p_id for p_id in self.player_order if not self.players[p_id].folded and not self.players[p_id].all_in]

    def check_round_complete(self, action=None):
        """Check if the current betting round is complete"""
        active_players = self.get_active_players(include_all_in=True)
        if len(active_players) <= 1:
            return True
        
        # If all players have either folded or gone all-in, the round is complete
        for player_id in active_players:
            player = self.players[player_id]
            if player.folded or player.all_in:
                continue
            
            # If the player has not acted yet, the round is not complete
            if not player.had_action:
                return False
        
            if self.bets[player_id] < self.current_bet:
                return False
            
        return True

    def conclude_round(self):
        # Move all bets to the pot
        for player_id, bet in self.bets.items():
            self.pot += bet
            self.bets[player_id] = 0
            
        # Reset current bet
        self.current_bet = 0
        self.minimum_raise = self.big_blind
        
        active_players = self.get_active_players(include_all_in=True)
        for player_id in active_players:
            player = self.players[player_id]
            player.had_action = False
        
        # If there's only one player left, they win
        if len(active_players) == 1:
            winner_id = active_players[0]
            winner = self.players[winner_id]
            winner.stack += self.pot
            self.game_ended()
            self.last_action = f"{winner.name} wins {self.pot}."
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

        if len(self.get_active_players(include_all_in=False)) <= 1 and self.check_round_complete():
            self.conclude_round()

    def evaluate_hands(self):
        active_players = self.get_active_players()
        if len(active_players) == 0:
            self.last_action = "No active players left."
            self.game_ended()
            return
        scores = {}
        for player_id in active_players:
            player = self.players[player_id]
            player_score = eval_hand(self.community_cards, player.cards)
            scores[player_id] = player_score

        # Sort players by score (lower is better)
        sorted_players = sorted(scores.items(), key=lambda x: x[1])
        
        # Find all winners (players with the same best score)
        best_score = sorted_players[0][1]
        winners = []
        
        for player_id, score in sorted_players:
            if score == best_score:
                winners.append(player_id)
            else:
                # Since we've sorted by score, once we find a different score,
                # we can break out of the loop
                break
        
        # Distribute pot evenly among winners
        if winners:
            split_amount = self.pot // len(winners)
            remainder = self.pot % len(winners)
            
            for winner_id in winners:
                self.players[winner_id].stack += split_amount
                
            # If there's a remainder, give it to the first winner
            if remainder > 0 and winners:
                self.players[winners[0]].stack += remainder
        self.last_action = f"Winner(s): {', '.join([self.players[winner_id].name for winner_id in winners])}"
        self.game_ended()

        # TODO : Handle ties and side pots if necessary

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
            "minimum_raise": self.minimum_raise,
            "last_action": self.last_action
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
            if amount <= 0:
                raise ValueError("Bet/raise amount must be greater than 0.")

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
                self.last_raiser = playerId
                self.current_bet = bet_amount

                # Update last action
                self.last_action = f"{player.name} {action} {bet_amount}"  # Action message format like "John raised 50"

            else:  # raise
                # Raise must be at least the current bet plus the minimum raise
                minimum_total = self.current_bet + self.minimum_raise
                if amount < minimum_total:
                    raise ValueError(f"Raise total must be at least {minimum_total}.")

                # Calculate how much more the player needs to add
                to_call = self.current_bet - self.bets[playerId]
                to_raise = amount

                # Cannot raise more than you have
                total_amount = min(to_call + to_raise, player.stack)
                new_bet = self.bets[playerId] + total_amount
                if new_bet >= self.current_bet:
                    self.current_bet = new_bet
                    self.last_raise = playerId
                player.stack -= total_amount
                self.bets[playerId] = new_bet

                # If player has no stack left, they go all-in
                if player.stack == 0:
                    player.all_in = True

                # Update last action
                self.last_action = f"{player.name} {action} {total_amount}"  # Action message format like "John raised 50"

        elif action == "fold":
            player.folded = True
            self.last_action = f"{player.name} folded"

        elif action == "call":
            to_call = self.current_bet - self.bets[playerId]

            # Cannot call more than you have
            call_amount = min(to_call, player.stack)
            player.stack -= call_amount
            self.bets[playerId] += call_amount

            # If player can't match the full bet, they go all-in
            if call_amount < to_call or player.stack == 0:
                player.all_in = True

            self.last_action = f"{player.name} called {to_call}"

        elif action == "check":
            # Can only check if no bet has been made or player has matched current bet
            if self.current_bet > self.bets[playerId]:
                raise ValueError("Cannot check when there's an active bet.")

            self.last_action = f"{player.name} checked"

        else:
            raise ValueError(f"Unknown action: {action}")
        
        # Mark the player as having acted
        player.had_action = True

        # Check if the round is complete after this action
        rc = self.check_round_complete(action)
        next_player_exists = self.next_player()
        if rc and next_player_exists: # trust the spaghetti monster
            self.conclude_round()
        return True
    
    def game_ended(self):
        '''Call when game is ended'''
        self.status = "complete"
        for player in self.players.values():
            if player.status == "playing":
                player.status = "ready"
            if player.stack == 0:
                player.status = "spectating"

