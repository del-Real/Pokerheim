package io.github.G16.View;

import com.badlogic.gdx.Game;

import io.github.G16.View.ScreenStates.ScreenState;

public class ViewManager {

    private ScreenState screen;
    private Game game;

    public ViewManager(Game game){
        this.game = game;
    }
    public ScreenState getScreen() {
        return this.screen;
    }

    public void setScreen(ScreenState screen) {
        if (this.screen != null){
            this.screen.dispose();
        }
        this.screen = screen;
        game.setScreen(screen);
    }
}
