package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.Color;

import io.github.G16.Controller.InputManager;
import io.github.G16.View.Button;

public class CreateLobbyScreen extends ScreenState{

    public CreateLobbyScreen(InputManager inputManager) {
        super(inputManager);
    }

    @Override
    public void show(){
        super.show();

        Button backButton = new Button(500,500,200,150, Color.WHITE, "back", () -> {
            inputManager.changeScreen(new MainMenuScreen(inputManager));
        });

        stage.addActor(backButton);
    }

}
