package io.github.G16.View;

import io.github.G16.View.ScreenStates.ScreenState;

public class ViewManager {

    private ScreenState state;

    public ScreenState getState() {
        return state;
    }

    public void setState(ScreenState state) {
        this.state = state;
    }
}
