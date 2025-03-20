package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;

public class GameSettingsScreen extends ScreenState{

    public GameSettingsScreen(InputManager inputManager) {
        super(inputManager);
    }

    @Override
    public void show(){
        super.show();


        skin.getFont("font").getData().setScale(4f);

        TextButton backButton = new TextButton("<", skin);
        backButton.setPosition((float) (0), (float) (Main.SCREEN_HEIGHT*0.9));
        backButton.setSize((float) (Main.SCREEN_WIDTH*0.1), (float) (Main.SCREEN_HEIGHT*0.1));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                inputManager.changeScreen(new CreateLobbyScreen(inputManager));
            }
        });

        stage.addActor(backButton);
    }
}
