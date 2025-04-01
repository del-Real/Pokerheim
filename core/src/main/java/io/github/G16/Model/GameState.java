package io.github.G16.Model;

import java.util.ArrayList;

public class GameState {

    // All temporary
    private final ArrayList<Card> communityCards;

    private int pot;
    public GameState() {
        this.communityCards =new ArrayList<>();
        communityCards.add(new Card(Rank.NINE,Suit.SPADES));
        communityCards.add(new Card(Rank.ACE,Suit.SPADES));
        communityCards.add(new Card(Rank.KING,Suit.CLUBS));
        communityCards.add(new Card(Rank.NINE,Suit.DIAMONDS));
        communityCards.add(new Card(Rank.THREE,Suit.HEARTS));
        for (Card card: communityCards){
            card.setFaceUp(false);
        }
        this.pot=100;
    }

    public ArrayList<Card> getCommunityCards() {
        return this.communityCards;
    }

    public int getPot() {
        return this.pot;
    }
}
