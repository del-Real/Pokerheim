package io.github.G16.View;

import com.badlogic.gdx.Game;
import io.github.G16.View.ScreenStates.ScreenState;

public class ViewManager {
    private static ViewManager instance;

    private ScreenState screen;
    private final Game game;
    private ViewManager(Game game) {
        this.game = game;
    }
    public static ViewManager getInstance(Game game) {
        if (instance == null) {
            synchronized (ViewManager.class) {
                if (instance == null) {
                    instance = new ViewManager(game);
                }
            }
        }
        return instance;
    }
    public ScreenState getScreen() {
        return screen;
    }
    public void setScreen(ScreenState screen) {
        if (this.screen != null) {
            this.screen.dispose();
        }
        this.screen = screen;
        game.setScreen(screen);
    }
}
