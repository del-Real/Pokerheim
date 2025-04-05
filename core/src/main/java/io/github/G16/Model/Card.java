package io.github.G16.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;
    private Sprite sprite;
    private Sprite backSprite; // For card back
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

        Texture frontCardTexture = new Texture(Gdx.files.internal("poker_cards.png"));
        // Extract the appropriate region from the texture
        this.textureRegion = new TextureRegion(
                frontCardTexture,
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

    public Card(Rank rank, Suit suit, Texture frontCardTexture, Texture backCardTexture) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = false;

        // Create texture regions for front and back of card
        initializeSprites(frontCardTexture, backCardTexture);
        System.out.println(rank + " " + suit);
    }

    private void initializeSprites(Texture frontCardTexture, Texture backCardTexture) {
        // Calculate the position in the texture atlas
        int row = suit.ordinal(); // Hearts=0, Diamonds=1, Clubs=2, Spades=3
        int col = rank.ordinal(); // Two=0, Three=1, ..., King=11, Ace=12

        // Calculate position with gaps between cards
        int xPos = col * (CARD_WIDTH + CARD_GAP);
        int yPos = row * (CARD_HEIGHT + CARD_GAP);

        // Extract the appropriate region from the texture
        TextureRegion region = new TextureRegion(
                frontCardTexture,
                xPos,
                yPos,
                CARD_WIDTH,
                CARD_HEIGHT
        );

        // Create sprite from the region
        this.sprite = new Sprite(region);

        TextureRegion backRegion = new TextureRegion(
                backCardTexture,
                0,
                0,
                CARD_WIDTH,
                CARD_HEIGHT
        );
        this.backSprite = new Sprite(backRegion);
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    // sprite
    public Sprite getSprite() {
        // Return the appropriate sprite based on face up/down state
        return faceUp ? this.sprite : this.backSprite;
    }
}