package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.G16.Controller.InputManager;
import io.github.G16.Controller.PlayerController;
import io.github.G16.Main;

public class JoinLobbyScreen extends ScreenState {


    public JoinLobbyScreen(InputManager inputManager) {
        super(inputManager);
    }

    @Override
    public void show() {
        super.show();

        skin.getFont("font").getData().setScale(4f);

        TextButton backButton = new TextButton("<", skin);
        backButton.setPosition((float) (0), (float) (Main.SCREEN_HEIGHT*0.9));
        backButton.setSize((float) (Main.SCREEN_WIDTH*0.1), (float) (Main.SCREEN_HEIGHT*0.1));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                inputManager.changeScreen(new MainMenuScreen(inputManager));
            }
        });

        stage.addActor(backButton);

        Label infoLabel = new Label("Enter Lobby Code:", skin);
        infoLabel.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.60));

        infoLabel.setAlignment(Align.center);
        stage.addActor(infoLabel);

        TextField codeField = new TextField("Enter code here...", skin);
        codeField.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.45));
        codeField.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));

        codeField.setAlignment(Align.center);

        codeField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (codeField.getText().equals("Enter code here...")) {
                    codeField.setText("");
                }
            }
        });

        stage.addActor(codeField);


        TextField nameField = new TextField("Enter name here...", skin);
        nameField.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.30));
        nameField.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));

        nameField.setAlignment(Align.center);


        nameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nameField.getText().equals("Enter name here...")) {
                    nameField.setText("");
                }
            }
        });
        stage.addActor(nameField);

        errorLabel = new Label("",skin);
        errorLabel.setPosition((float) (Main.SCREEN_WIDTH*0.05), (float) (Main.SCREEN_HEIGHT * 0.75));
        errorLabel.setAlignment(Align.center);


        errorLabel.setWrap(true);
        errorLabel.setWidth((float) (Main.SCREEN_WIDTH * 0.9));

        stage.addActor(errorLabel);

        TextButton confirmButton = new TextButton("CONFIRM", skin);
        confirmButton.setPosition((float) (Main.SCREEN_WIDTH * 0.40), (float) (Main.SCREEN_HEIGHT * 0.20));
        confirmButton.setSize((float) (Main.SCREEN_WIDTH * 0.2), (float) (Main.SCREEN_HEIGHT * 0.05));

        confirmButton.getLabel().setFontScale(2f);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmButton.setDisabled(true);
                infoLabel.setText("Trying to join lobby");


                /*
                Make so that when a valid code is entered, the button clicked function
                Makes the player ready and the text becomes ready
                 */
                PlayerController.getInstance().joinLobby(codeField.getText(),nameField.getText(),codeField, confirmButton, errorLabel);
            }
        });

        stage.addActor(confirmButton);
    }
}