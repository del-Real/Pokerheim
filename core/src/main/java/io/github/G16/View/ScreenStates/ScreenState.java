package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.G16.Controller.InputManager;

public abstract class ScreenState implements Screen {

    protected InputManager inputManager;

    public ScreenState(InputManager inputManager){
        this.inputManager=inputManager;
    }

    public void create(){

    }
    public void show() {
    }

    public void render(float delta) {
    }

    public void resize(int width, int height){

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    public void dispose() {
    }
}
