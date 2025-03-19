package io.github.G16.Model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {


    private ArrayList<Card> cards;

    // Deck constructor
    public Deck() {
        this.cards = new ArrayList<>();
        initializeDeck();
    }

    // Initializes the deck with all 52 cards for Texas Hold'em poker
    private void initializeDeck() {
        Suit[] suits = {Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS, Suit.SPADES};
        Rank[] ranks = {Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE};

        for (Suit suit : suits) {
            for (Rank rank : ranks) {
                Card card = new Card(rank, suit);
                cards.add(card);
            }
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    // Shuffle all the cards
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Draws the top card from the deck and removes it.
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    /**
     * Draws multiple cards from the top of the deck.
     *
     * @param count the number of cards to draw
     * @return ArrayList of drawn cards
     */
    public ArrayList<Card> drawCards(int count) {
        ArrayList<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count && !cards.isEmpty(); i++) {
            drawnCards.add(drawCard());
        }
        return drawnCards;
    }

    /**
     * Returns the number of cards remaining in the deck.
     *
     * @return the count of remaining cards
     */
    public int getRemainingCards() {
        return cards.size();
    }

    // Resets the deck to a complete set of 52 cards and shuffles them
    public void reset() {
        cards.clear();
        initializeDeck();
        shuffle();
    }
}
