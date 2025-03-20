package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;

public class CreateLobbyScreen extends ScreenState{

    public CreateLobbyScreen(InputManager inputManager) {
        super(inputManager);

    }

    @Override
    public void show(){
        super.show();

        skin.getFont("font").getData().setScale(2f);

        TextButton testButton = new TextButton("Now on create lobby screen", skin);
        testButton.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.25));
        testButton.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));
        testButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                testButton.setText("Clicked");
            }
        });

        stage.addActor(testButton);
    }

}
