package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


import java.util.ArrayList;

import io.github.G16.Controller.InputManager;
import io.github.G16.Controller.PlayerController;
import io.github.G16.Main;
import io.github.G16.Model.Card;
import io.github.G16.Model.PlayerTable;
import io.github.G16.View.ObserverScreen;

public class GameScreen extends ScreenState implements ObserverScreen {

    Label potLabel;
    Label stackLabel;
    public GameScreen(InputManager inputManager){
        super(inputManager);
    }

    @Override
    public void show(){
        super.show();

        skin.getFont("font").getData().setScale(3f);

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

        raiseLogic();

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


        Window stackWindow = new Window("", skin);
        stackWindow.setPosition((float)(Main.SCREEN_WIDTH*0.10),(float)(Main.SCREEN_HEIGHT*0.275));
        stackWindow.setSize((float)(Main.SCREEN_WIDTH*0.3),(float)(Main.SCREEN_HEIGHT*0.075));
        // The label is so that its easier to center
        stackLabel = new Label("Chips: 0",skin);
        stackLabel.setAlignment(Align.center);

        stackWindow.add(stackLabel).expand().fill().center();

        stage.addActor(stackWindow);

        Window potWindow = new Window("", skin);
        potWindow.setPosition((float)(Main.SCREEN_WIDTH*0.35),(float)(Main.SCREEN_HEIGHT*0.8));
        potWindow.setSize((float)(Main.SCREEN_WIDTH*0.30),(float)(Main.SCREEN_HEIGHT*0.075));
        // The label is so that its easier to center
        potLabel = new Label("Pot: 0",skin);
        potLabel.setAlignment(Align.center);

        potWindow.add(potLabel).expand().fill().center();

        stage.addActor(potWindow);

    }

    private void raiseLogic(){
        // Unfinished

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

        final Window raiseWindow = new Window("", skin);
        raiseWindow.setSize((float) (Main.SCREEN_WIDTH * 0.4), (float) (Main.SCREEN_HEIGHT * 0.2));
        raiseWindow.setPosition((float) (Main.SCREEN_WIDTH * 0.3), (float) (Main.SCREEN_HEIGHT * 0.5));
        raiseWindow.setVisible(false);

        Label raiseLabel = new Label("Raise Amount", skin);
        raiseLabel.setAlignment(Align.center);
        raiseWindow.add(raiseLabel).expandX().fillX().pad(10);

        final TextField raiseAmountField = new TextField("", skin);
        raiseAmountField.setMessageText("Enter amount");
        raiseAmountField.setAlignment(Align.center);
        raiseWindow.row();
        raiseWindow.add(raiseAmountField).expandX().fillX().pad(10);

        TextButton confirmRaiseButton = new TextButton("CONFIRM", skin);
        confirmRaiseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String amountText = raiseAmountField.getText();
                try {
                    int raiseAmount = Integer.parseInt(amountText);
                    if (raiseAmount <= 0){ // Add other checks (raise < chips) ...
                        throw new NumberFormatException();
                    }
                    System.out.println("Raise of: " + raiseAmount);
                    raiseWindow.setVisible(false);
                } catch (NumberFormatException e) {
                    raiseLabel.setText("Invalid input");
                    System.out.println("Invalid input");
                }
            }
        });

        raiseWindow.row();
        raiseWindow.add(confirmRaiseButton).pad(10);

        stage.addActor(raiseWindow);

        raiseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseWindow.setVisible(true);
            }
        });
    }

    @Override
    public void update(PlayerTable playerTable) {
        System.out.println("Got an update");
        potLabel.setText("Pot: "+playerTable.getPot());
        stackLabel.setText("Stack: "+playerTable.getStack());
        ArrayList<Card> communityCards = playerTable.getCommunityCards();
        int i=0;
        for (Card card: communityCards){
            TextureRegionDrawable cardDrawable = new TextureRegionDrawable(new TextureRegion(card.getTextureRegion()));
            Image cardImage = new Image(cardDrawable);
            cardImage.setPosition((float) (Main.SCREEN_WIDTH * (0.125+i*0.15)), (float) (Main.SCREEN_HEIGHT * 0.4));

            cardImage.setSize(cardImage.getWidth() * 2, cardImage.getHeight() * 2);
            stage.addActor(cardImage);
            i++;
        }

        ArrayList<Card> playerHand = playerTable.getPlayerHand();
        i=0;
        for (Card card: playerHand){
            TextureRegionDrawable cardDrawable = new TextureRegionDrawable(new TextureRegion(card.getTextureRegion()));
            Image cardImage = new Image(cardDrawable);
            cardImage.setPosition((float) (Main.SCREEN_WIDTH * (0.5+i*.25)), (float) (Main.SCREEN_HEIGHT * 0.25));

            cardImage.setSize(cardImage.getWidth() * 3, cardImage.getHeight() * 3);
            stage.addActor(cardImage);
            i++;
        }


    }
}
