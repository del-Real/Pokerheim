package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import io.github.G16.Controller.InputManager;
import io.github.G16.Main;
import io.github.G16.Model.Card;
import io.github.G16.Model.Rank;
import io.github.G16.Model.Suit;

public class GameScreen extends ScreenState{

    public GameScreen(InputManager inputManager){
        super(inputManager);
    }

    @Override
    public void show(){
        super.show();

        skin.getFont("font").getData().setScale(2.5f);

        TextButton checkButton = new TextButton("CHECK", skin);
        checkButton.setPosition((float) (Main.SCREEN_WIDTH*0.05),(float)(Main.SCREEN_HEIGHT*0.05));
        checkButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));
        checkButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Check");
            }
        });

        stage.addActor(checkButton);

        TextButton callButton = new TextButton("CALL", skin);
        callButton.setPosition((float) (Main.SCREEN_WIDTH*0.55),(float)(Main.SCREEN_HEIGHT*0.05));
        callButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));
        callButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Call");
            }
        });

        stage.addActor(callButton);

        skin.getFont("font").getData().setScale(3f);

        TextButton raiseButton = new TextButton("RAISE", skin);
        raiseButton.setPosition((float) (Main.SCREEN_WIDTH*0.05),(float)(Main.SCREEN_HEIGHT*0.15));
        raiseButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));
        raiseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Raise");
            }
        });

        stage.addActor(raiseButton);

        TextButton foldButton = new TextButton("FOLD", skin);
        foldButton.setPosition((float) (Main.SCREEN_WIDTH*0.55),(float)(Main.SCREEN_HEIGHT*0.15));
        foldButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));
        foldButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Fold");
            }
        });

        stage.addActor(foldButton);

        Card card = new Card(Rank.ACE, Suit.CLUBS);
        TextureRegionDrawable cardDrawable = new TextureRegionDrawable(new TextureRegion(card.getTextureRegion()));

        Image cardImage = new Image(cardDrawable);
        cardImage.setPosition((float) (Main.SCREEN_WIDTH * 0.75), (float) (Main.SCREEN_HEIGHT * 0.25));
        cardImage.setSize(cardImage.getWidth() * 3, cardImage.getHeight() * 3);

        stage.addActor(cardImage);

    }

}
