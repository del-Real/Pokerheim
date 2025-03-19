package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.G16.Controller.InputManager;

public class MainMenuScreen extends ScreenState {
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    public MainMenuScreen(InputManager inputManager) {
        super(inputManager);
    }

    // Show is called when a screen appears for the first time
    // Try to put most stuff here and not in the constructor
    @Override
    public void show(){
        stage = new Stage();
        inputManager.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();

        Actor rectangleActor = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(100, 100, 200, 150);
                shapeRenderer.end();
            }
        };

        // This is important don't forget to put it
        rectangleActor.setBounds(100, 100, 200, 150);

        rectangleActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("cliccato");
                inputManager.changeScreen(new JoinLobbyScreen(inputManager));
            }
        });

        stage.addActor(rectangleActor);
        System.out.println("Show Show");
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("Disposing");
        shapeRenderer.dispose();
        stage.dispose();
    }
}