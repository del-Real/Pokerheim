package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

abstract public class ScreenState{

    private SpriteBatch batch;

    public void create() {
        batch = new SpriteBatch();
    }

    public void render() {
        batch.begin();
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }
}
