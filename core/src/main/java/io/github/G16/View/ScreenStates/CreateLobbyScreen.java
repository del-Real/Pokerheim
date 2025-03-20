package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;

public class CreateLobbyScreen extends ScreenState{

    public CreateLobbyScreen(InputManager inputManager) {
        super(inputManager);

    }

    @Override
    public void show(){
        super.show();


        TextButton gameSettingsButton = new TextButton("GAME SETTINGS", skin);
        gameSettingsButton.setPosition((float) (Main.SCREEN_WIDTH*0.3), (float) (Main.SCREEN_HEIGHT * 0.6));
        gameSettingsButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float) (Main.SCREEN_HEIGHT*0.05));
        gameSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                inputManager.changeScreen(new GameSettingsScreen(inputManager));
            }
        });


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

        Window codeField = new Window("", skin);
        codeField.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.4));
        codeField.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));

        Label codeLabel = new Label("Lobby Code", skin);
        codeLabel.setAlignment(Align.center);

        codeField.add(codeLabel).expand().fill().center();

        TextButton generateButton = new TextButton("GENERATE LOBBY CODE", skin);
        generateButton.setPosition((float) (Main.SCREEN_WIDTH * 0.3), (float) (Main.SCREEN_HEIGHT * 0.35));
        generateButton.setSize((float) (Main.SCREEN_WIDTH * 0.4), (float) (Main.SCREEN_HEIGHT * 0.05));

        generateButton.getLabel().setFontScale(2f);
        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                codeLabel.setText("Code");
                /*
                Make so that when a code is generated the button text become ready and the
                clicked function makes the player ready
                 */


                // When the code is generated

                skin.getFont("font").getData().setScale(2f);
                stage.addActor(gameSettingsButton);
            }
        });

        stage.addActor(codeField);
        stage.addActor(generateButton);
    }

}
