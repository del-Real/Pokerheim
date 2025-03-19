package io.github.G16.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;
    private Sprite sprite;

    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    // value
    public Rank getRank() {
        return this.rank;
    }

    // suit
    public Suit getSuit() {
        return this.suit;
    }

    // faceUp
    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    // sprite
    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite=sprite;
    }

}
