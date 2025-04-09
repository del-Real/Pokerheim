package io.github.G16.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.G16.Assets;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;
    private Sprite sprite;
    private TextureRegion textureRegion;

    private static final int CARD_WIDTH = 69; // Width of each card in the texture
    private static final int CARD_HEIGHT = 93; // Height of each card in the texture
    private static final int CARD_GAP = 2; // Gap between cards in the texture

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;

        int row = suit.ordinal();
        int col = rank.ordinal();


        int xPos = col * (CARD_WIDTH + CARD_GAP);
        int yPos = row * (CARD_HEIGHT + CARD_GAP);

        this.textureRegion = new TextureRegion(
                Assets.cardAtlas,
                xPos,
                yPos,
                CARD_WIDTH,
                CARD_HEIGHT
        );
        this.faceUp=true;
        this.sprite = new Sprite(textureRegion);

    }

    public TextureRegion getTextureRegion(){
        return this.textureRegion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Card otherCard = (Card) obj;
        return rank == otherCard.rank && suit == otherCard.suit;
    }

}