package io.github.G16;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    // Class used to load the cards sprites
    public static Texture cardAtlas;

    public static void load() {
        cardAtlas = new Texture(Gdx.files.internal("poker_cards.png"));
    }

    public static void dispose() {
        cardAtlas.dispose();
    }
}