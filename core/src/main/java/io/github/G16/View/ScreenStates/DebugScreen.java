package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;
import io.github.G16.Model.Card;
import io.github.G16.Model.Deck;
import io.github.G16.Model.Rank;

public class DebugScreen extends ScreenState{

    private Deck deck;
    private SpriteBatch batch;

    public DebugScreen(InputManager inputManager) {
        super(inputManager);
        batch = new SpriteBatch();
        deck = new Deck();
        deck.shuffle();
    }

    @Override
    public void show() {
        super.show();

        skin.getFont("font").getData().setScale(4f);
        // Example: Create a button to draw a card
        TextButton drawButton = new TextButton("Draw Card", skin);
        drawButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Card drawnCard = deck.drawCard();
                if (drawnCard != null) {
                    drawnCard.setFaceUp(true);
                    // You could add the drawn card to a hand or display it
                }
            }
        });


        // Add button to stage, position it, etc.
        stage.addActor(drawButton);



        TextButton backButton = new TextButton("<", skin);
        backButton.setPosition((float) (Main.SCREEN_WIDTH*0.9), (float) (0));
        backButton.setSize((float) (Main.SCREEN_WIDTH*0.1), (float) (Main.SCREEN_HEIGHT*0.1));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                inputManager.changeScreen(new MainMenuScreen(inputManager));
            }
        });

        stage.addActor(backButton);
    }

    @Override
    public void render(float delta) {
        // Set the background color to RGB: 82, 146, 112 (normalize to 0-1 range)
        Gdx.gl.glClearColor(82/255f, 146/255f, 112/255f, 1f);
        super.render(delta);

        batch.begin();

        // Display all cards in a grid for debugging
        int cardsPerRow = 13;
        int cardSpacing = 5;
        int startX = 50;
        int startY = Gdx.graphics.getHeight() - 150;

        // Example: Render some sample cards
        ArrayList<Card> cards = deck.getCards();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            card.setFaceUp(true);

            // Calculate position in grid
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;

            float posX = startX + col * (card.getSprite().getWidth() + cardSpacing);
            float posY = startY - row * (card.getSprite().getHeight() + cardSpacing);

            // Use the new renderCard method
            renderCard(card, posX, posY);
        }

        // TEST CODE: Render a single card in the center of the screen with different scales
        if (!cards.isEmpty()) {
            // Get a reference card (Ace of Spades if available, otherwise first card)
            Card testCard = cards.get(0);

            if (testCard != null) {
                testCard.setFaceUp(true);

                // Center of screen
                float centerX = Gdx.graphics.getWidth() / 2f;
                float centerY = Gdx.graphics.getHeight() / 2f;

                // Calculate time-based scaling for animation effect
                float scale = 2.5f + 1.5f * (float) Math.sin(Gdx.graphics.getFrameId() * 0.05f);

                // Render the card at the center of the screen with animated scaling
                renderCard(testCard,
                        centerX - (testCard.getSprite().getWidth() * scale / 2),
                        centerY - (testCard.getSprite().getHeight() * scale / 2),
                        scale);
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        deck.dispose(); // Make sure to dispose the deck to free the texture
    }

    public void renderCard(Card card, float x, float y, float scale) {
        if (card == null) return;

        Sprite sprite = card.getSprite();

        // Save original scale
        float originalScaleX = sprite.getScaleX();
        float originalScaleY = sprite.getScaleY();

        // Set position and scale
        sprite.setPosition(x, y);
        sprite.setScale(scale);

        // Draw the card
        sprite.draw(batch);

        // Restore original scale
        sprite.setScale(originalScaleX, originalScaleY);
    }

    public void renderCard(Card card, float x, float y) {
        renderCard(card, x, y, 1.0f);
    }
}
