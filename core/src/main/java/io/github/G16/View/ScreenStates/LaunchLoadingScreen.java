package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.utils.Timer;

import io.github.G16.Controller.InputManager;

public class LaunchLoadingScreen extends ScreenState{
    public LaunchLoadingScreen(InputManager inputManager){
        super(inputManager);

        // This is temporary
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                inputManager.changeScreen(new MainMenuScreen(inputManager));
            }
        }, 0.5f);
    }
}
