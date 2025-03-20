package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.G16.Controller.InputManager;
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
        infoLabel.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.55));

        infoLabel.setAlignment(Align.center);

        TextField codeField = new TextField("Enter code here...", skin);
        codeField.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.4));
        codeField.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));

        codeField.setAlignment(Align.center);

        TextButton confirmButton = new TextButton("CONFIRM", skin);
        confirmButton.setPosition((float) (Main.SCREEN_WIDTH * 0.40), (float) (Main.SCREEN_HEIGHT * 0.35));
        confirmButton.setSize((float) (Main.SCREEN_WIDTH * 0.2), (float) (Main.SCREEN_HEIGHT * 0.05));

        confirmButton.getLabel().setFontScale(2f);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String enteredCode = codeField.getText();
                infoLabel.setText("Entered code: " + enteredCode);

                /*
                Make so that when a valid code is entered, the button clicked function
                Makes the player ready and the text becomes ready
                 */
            }
        });

        stage.addActor(infoLabel);
        stage.addActor(codeField);
        stage.addActor(confirmButton);
    }
}