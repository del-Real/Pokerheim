package io.github.G16;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.G16.Controller.InputManager;
import io.github.G16.View.ScreenStates.MainMenuScreen;
import io.github.G16.View.ViewManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    static public float SCREEN_WIDTH;
    static public float SCREEN_HEIGHT;


    // it might be a good idea to use the singleton pattern for the view manager and inputmanager
    private ViewManager viewManager;
    private InputManager inputManager;
    @Override
    public void create() {
        this.viewManager = new ViewManager(this);
        this.inputManager = new InputManager(this.viewManager);
        this.viewManager.setState(new MainMenuScreen(inputManager));

        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
    }

    @Override
    public void render() {
        super.render();
        float delta = Gdx.graphics.getDeltaTime();
        viewManager.getState().render(delta);
    }

    @Override
    public void dispose() {
        viewManager.getState().dispose();
    }
}
