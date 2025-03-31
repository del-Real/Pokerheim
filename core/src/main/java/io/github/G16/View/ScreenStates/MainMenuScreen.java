package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;

public class MainMenuScreen extends ScreenState {


    public MainMenuScreen(InputManager inputManager) {
        super(inputManager);
    }

    @Override
    public void show() {
        super.show();
        Gdx.gl.glClearColor(0, 1, 0, 1);

        skin.getFont("font").getData().setScale(4f);

        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition((float) (0), (float) (Main.SCREEN_HEIGHT*0.9));
        exitButton.setSize((float) (Main.SCREEN_WIDTH*0.1), (float) (Main.SCREEN_HEIGHT*0.1));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });

        stage.addActor(exitButton);


        TextButton joinLobbyButton = new TextButton("JOIN GAME", skin);
        joinLobbyButton.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.25));
        joinLobbyButton.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));
        joinLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inputManager.changeScreen(new JoinLobbyScreen(inputManager));
            }
        });

        stage.addActor(joinLobbyButton);


        TextButton createLobbyButton = new TextButton("CREATE GAME", skin);

        createLobbyButton.setPosition((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.4));
        createLobbyButton.setSize((float) (Main.SCREEN_WIDTH * 0.5), (float) (Main.SCREEN_HEIGHT * 0.1));
        createLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inputManager.changeScreen(new CreateLobbyScreen(inputManager));
            }
        });

        // These are temporary
        stage.addActor(createLobbyButton);

        // Debug
        TextButton debugButton = new TextButton("DEBUG", skin);

        debugButton.setPosition((float) (0), (float) (0));
        debugButton.setSize((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.05));
        debugButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inputManager.changeScreen(new DebugScreen(inputManager));
            }
        });

        stage.addActor(debugButton);

        TextButton gameButton = new TextButton("GAME TEST", skin);
        gameButton.setPosition(Main.SCREEN_WIDTH/2, 0);
        gameButton.setSize((float) (Main.SCREEN_WIDTH * 0.25), (float) (Main.SCREEN_HEIGHT * 0.05));
        gameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                inputManager.changeScreen(new GameScreen(inputManager));
            }
        });

        stage.addActor(gameButton);

    }
}