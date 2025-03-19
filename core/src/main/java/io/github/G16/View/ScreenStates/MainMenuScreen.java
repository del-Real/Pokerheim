package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.Color;

import io.github.G16.Controller.InputManager;
import io.github.G16.View.Button;

public class MainMenuScreen extends ScreenState {
    public MainMenuScreen(InputManager inputManager) {
        super(inputManager);
    }

    // Show is called when a screen appears for the first time
    // Try to put most stuff here and not in the constructor
    @Override
    public void show() {
        super.show();

        // Creazione del pulsante "Join Lobby"
        Button joinLobbyButton = new Button(100, 100, 200, 150, Color.WHITE, "join lobby", () -> {
            inputManager.changeScreen(new JoinLobbyScreen(inputManager)); // Azione al click
        });

        // Creazione del pulsante "Create Lobby"
        Button createLobbyButton = new Button(400, 400, 200, 150, Color.BLUE, "create lobby", () -> {
            inputManager.changeScreen(new CreateLobbyScreen(inputManager)); // Azione al click
        });

        // Aggiungi i pulsanti alla scena
        stage.addActor(joinLobbyButton);
        stage.addActor(createLobbyButton);
    }
}