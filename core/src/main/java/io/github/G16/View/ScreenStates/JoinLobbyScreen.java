package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.G16.Controller.InputManager;

public class JoinLobbyScreen extends ScreenState{
    private ShapeRenderer shapeRenderer;

    public JoinLobbyScreen(InputManager inputManager){
        super(inputManager);
    }

    public void show(){
        shapeRenderer = new ShapeRenderer();
    }

    public void render(float delta){

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(200, 200, 300, 300);
        shapeRenderer.end();
    }

    public void dispose(){
        shapeRenderer.dispose();
    }
}
