package io.github.G16.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import io.github.G16.View.ScreenStates.ScreenState;
import io.github.G16.View.ViewManager;


public class InputManager implements InputProcessor {
    private ViewManager viewManager;
    private InputProcessor currentInputProcessor;

    public InputManager(ViewManager viewManager) {
        this.viewManager = viewManager;

        //I tried to make so that when you go back with your phone it goes to the previous screen but I failed
        //Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        this.currentInputProcessor = inputProcessor;
        Gdx.input.setInputProcessor(inputProcessor);
    }


    public void changeScreen(ScreenState screen) {
        viewManager.setScreen(screen);
    }


    // Idk what these do

    @Override
    public boolean keyDown(int keycode) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.keyUp(keycode);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.keyTyped(character);
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.touchDown(screenX, screenY, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.touchUp(screenX, screenY, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.touchDragged(screenX, screenY, pointer);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.mouseMoved(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.scrolled(amountX, amountY);
        }
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (currentInputProcessor != null) {
            return currentInputProcessor.touchCancelled(screenX, screenY, pointer, button);
        }
        return false;
    }

}