package io.github.G16;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.G16.Controller.FirestoreTableListener;
import io.github.G16.Controller.InputManager;
import io.github.G16.View.ScreenStates.LaunchLoadingScreen;
import io.github.G16.View.ViewManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    static public float SCREEN_WIDTH;
    static public float SCREEN_HEIGHT;

    // Cambia il tipo di viewManager e inputManager per usare il Singleton
    private ViewManager viewManager;
    private InputManager inputManager;
    private final FirestoreTableListener firestoreListener;

    public Main(FirestoreTableListener firestoreListener){
        this.firestoreListener = firestoreListener;
    }
    @Override
    public void create() {
        this.viewManager = ViewManager.getInstance(this);
        this.inputManager = InputManager.getInstance(viewManager, firestoreListener);

        this.viewManager.setScreen(new LaunchLoadingScreen(inputManager));

        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        Assets.load();
    }

    @Override
    public void render() {
        super.render();
        float delta = Gdx.graphics.getDeltaTime();
        viewManager.getScreen().render(delta);
    }

    @Override
    public void resize(int width, int height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
    }

    @Override
    public void dispose() {
        viewManager.getScreen().dispose();
        Assets.dispose();
    }
}