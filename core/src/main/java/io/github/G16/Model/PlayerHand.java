package io.github.G16.Model;

import java.util.ArrayList;

public class PlayerHand {
    private ArrayList<Card> cards;

    public PlayerHand(){
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card){
        this.cards.add(card);
    }
}
