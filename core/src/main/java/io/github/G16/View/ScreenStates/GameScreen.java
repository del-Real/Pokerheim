package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


import io.github.G16.Controller.InputManager;
import io.github.G16.Main;
import io.github.G16.Model.Card;
import io.github.G16.Model.Player;
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

        //Temp
        Player player=new Player(100);
        player.getPlayerHand().addCard(new Card(Rank.ACE,Suit.CLUBS));
        player.getPlayerHand().addCard(new Card(Rank.TEN,Suit.HEARTS));
        //

        for (int i=0;i<player.getPlayerHand().getCards().size();i++){
            Card card = player.getPlayerHand().getCards().get(i);
            TextureRegionDrawable cardDrawable = new TextureRegionDrawable(new TextureRegion(card.getTextureRegion()));
            Image cardImage = new Image(cardDrawable);
            cardImage.setPosition((float) (Main.SCREEN_WIDTH * (0.5+i*.25)), (float) (Main.SCREEN_HEIGHT * 0.25));

            cardImage.setSize(cardImage.getWidth() * 3, cardImage.getHeight() * 3);
            stage.addActor(cardImage);
        }


        Window chipsWindow = new Window("", skin);
        chipsWindow.setPosition((float)(Main.SCREEN_WIDTH*0.10),(float)(Main.SCREEN_HEIGHT*0.275));
        chipsWindow.setSize((float)(Main.SCREEN_WIDTH*0.3),(float)(Main.SCREEN_HEIGHT*0.075));
        // The label is so that its easier to center
        Label chipsLabel = new Label("Chips: "+player.getChips(),skin);
        chipsLabel.setAlignment(Align.center);

        chipsWindow.add(chipsLabel).expand().fill().center();

        stage.addActor(chipsWindow);


    }

}
