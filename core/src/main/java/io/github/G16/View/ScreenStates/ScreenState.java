package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.G16.Controller.InputManager;

public abstract class ScreenState implements Screen {

    protected InputManager inputManager;
    protected Stage stage;
    protected Skin skin;

    public ScreenState(InputManager inputManager){
        this.inputManager=inputManager;
        skin = new Skin(Gdx.files.internal("uiskin/skin/terra-mother-ui.json"));
    }

    public void create(){

    }
    public void show() {
        stage = new Stage();
        inputManager.setInputProcessor(stage);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
