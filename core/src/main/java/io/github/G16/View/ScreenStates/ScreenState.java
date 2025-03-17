package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract public class ScreenState {

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
