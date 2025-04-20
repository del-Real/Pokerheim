package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.G16.Controller.InputManager;
import io.github.G16.Main;

public class MainMenuScreen extends ScreenState {
    private Texture logo;

    public MainMenuScreen(InputManager inputManager) {
        super(inputManager);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("Main menu Show called");
        Gdx.gl.glClearColor(0.5f, 0.9f, 0.7f, 1f);


        Label creditLabel = new Label("UI Skin by Raymond 'Raeleus' Buckley (Terra Mother UI)\nCards sprites from Balatro",skin);
        creditLabel.setPosition((float) (Main.SCREEN_WIDTH*0.05), (float) (Main.SCREEN_HEIGHT*0.025));
        creditLabel.setAlignment(Align.center);
        creditLabel.setWrap(true);
        creditLabel.setWidth((float) (Main.SCREEN_WIDTH * 0.9));
        stage.addActor(creditLabel);
        creditLabel.setFontScale(2f);

        skin.getFont("font").getData().setScale(4f);

        // Logo
        logo = new Texture(Gdx.files.internal("pokerheim_logo.png"));
        Image logoImage = new Image(logo);

        // Set logo size
        float logoWidth = Main.SCREEN_WIDTH * 0.7f;
        float logoHeight = logo.getHeight() * (logoWidth / logo.getWidth());
        logoImage.setSize(logoWidth, logoHeight);

        // Set logo position
        logoImage.setPosition(
                (Main.SCREEN_WIDTH - logoImage.getWidth()) / 2,
                (float) (Main.SCREEN_HEIGHT * 0.7)
        );

        stage.addActor(logoImage);

        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition(0, (float) (Main.SCREEN_HEIGHT * 0.9));
        exitButton.setSize((float) (Main.SCREEN_WIDTH * 0.1), (float) (Main.SCREEN_HEIGHT * 0.1));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        stage.addActor(createLobbyButton);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (logo != null) {
            logo.dispose();
        }
    }
}
