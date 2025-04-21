package io.github.G16.View.ScreenStates;

import io.github.G16.Controller.InputManager;

public class LaunchLoadingScreen extends ScreenState{
    // This screen appears only while the assets are loading
    public LaunchLoadingScreen(InputManager inputManager){
        super(inputManager);
    }
}
