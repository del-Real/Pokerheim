package io.github.G16.View;

import com.badlogic.gdx.Game;

import javax.swing.text.View;

import io.github.G16.View.ScreenStates.ScreenState;

public class ViewManager {

    private ScreenState state;
    private Game game;

    public ViewManager(Game game){
        this.game = game;
    }
    public ScreenState getState() {
        return state;
    }

    public void setState(ScreenState state) {
        if (this.state != null){
            this.state.dispose();
        }
        this.state = state;
        game.setScreen(state);
    }
}
