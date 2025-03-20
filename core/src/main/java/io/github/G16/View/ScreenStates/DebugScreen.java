package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;
import io.github.G16.Model.Card;
import io.github.G16.Model.Deck;

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
    }

    @Override
    public void render(float delta) {
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

            // Position and draw the card
            card.getSprite().setPosition(posX, posY);
            card.getSprite().draw(batch);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        deck.dispose(); // Make sure to dispose the deck to free the texture
    }
}
