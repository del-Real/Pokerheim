package io.github.G16.View.ScreenStates;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import io.github.G16.View.Observer;

public class GameScreen extends ScreenState implements Observer {

    private Label potLabel;
    private Label stackLabel;
    private Label turnLabel;
    private Window currentOpenWindow=null;
    private Label errorLabel;
    public GameScreen(InputManager inputManager){
        super(inputManager);
    }

    @Override
    public void show(){
        super.show();
        turnLabel = new Label("Not Your Turn", skin);
        turnLabel.setPosition((float) (Main.SCREEN_WIDTH * 0.35), (float) (Main.SCREEN_HEIGHT * 0.9));
        turnLabel.setAlignment(Align.center);

        stage.addActor(turnLabel);

        skin.getFont("font").getData().setScale(3f);

        errorLabel = new Label("",skin);
        errorLabel.setPosition((float) (Main.SCREEN_WIDTH*0.05), (float) (Main.SCREEN_HEIGHT * 0.75));
        errorLabel.setAlignment(Align.center);


        errorLabel.setWrap(true);
        errorLabel.setWidth((float) (Main.SCREEN_WIDTH * 0.9));

        stage.addActor(errorLabel);

        checkLogic();
        callLogic();
        raiseLogic();
        foldLogic();


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

    private void checkLogic() {
        TextButton checkButton = new TextButton("CHECK", skin);
        checkButton.setPosition((float) (Main.SCREEN_WIDTH * 0.05), (float) (Main.SCREEN_HEIGHT * 0.05));
        checkButton.setSize((float) (Main.SCREEN_WIDTH * 0.4), (float) (Main.SCREEN_HEIGHT * 0.075));

        stage.addActor(checkButton);

        final Window checkWindow = new Window("", skin);
        checkWindow.setSize((float) (Main.SCREEN_WIDTH * 0.7), (float) (Main.SCREEN_HEIGHT * 0.2));
        checkWindow.setPosition((float) (Main.SCREEN_WIDTH * 0.15), (float) (Main.SCREEN_HEIGHT * 0.5));
        checkWindow.setVisible(false);

        Label checkLabel = new Label("Check?", skin);
        checkLabel.setAlignment(Align.center);
        checkWindow.add(checkLabel).expandX().fillX().pad(10);

        Table buttonTable = new Table();
        buttonTable.top().padTop(10);
        buttonTable.defaults().width(300).height(100).pad(5);

        TextButton confirmCheckButton = new TextButton("CONFIRM", skin);
        confirmCheckButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerController.getInstance().performAction(
                        PlayerController.getInstance().getCurrentTable().getPlayerId(),
                        "check",
                        0,
                        checkWindow,
                        errorLabel
                );
            }
        });

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkWindow.setVisible(false);
                currentOpenWindow=null;
            }
        });

        buttonTable.add(confirmCheckButton).expandX().fillX();
        buttonTable.add(backButton).expand().fillX();

        checkWindow.row();
        checkWindow.add(buttonTable).expandX().fillX();

        stage.addActor(checkWindow);

        checkButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentOpenWindow!=null){
                    currentOpenWindow.setVisible(false);
                }
                checkWindow.setVisible(true);
                currentOpenWindow=checkWindow;
            }
        });
    }


    private void callLogic() {
        TextButton callButton = new TextButton("CALL", skin);
        callButton.setPosition((float) (Main.SCREEN_WIDTH*0.55),(float)(Main.SCREEN_HEIGHT*0.05));
        callButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));
        stage.addActor(callButton);

        final Window callWindow = new Window("", skin);
        callWindow.setSize((float) (Main.SCREEN_WIDTH * 0.7), (float) (Main.SCREEN_HEIGHT * 0.2));
        callWindow.setPosition((float) (Main.SCREEN_WIDTH * 0.15), (float) (Main.SCREEN_HEIGHT * 0.5));
        callWindow.setVisible(false);

        Label callLabel = new Label("Call?", skin);
        callLabel.setAlignment(Align.center);
        callWindow.add(callLabel).expandX().fillX().pad(10);

        Table buttonTable = new Table();
        buttonTable.top().padTop(10);
        buttonTable.defaults().width(300).height(100).pad(5);

        TextButton confirmCallButton = new TextButton("CONFIRM", skin);
        confirmCallButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerController.getInstance().performAction(
                        PlayerController.getInstance().getCurrentTable().getPlayerId(),
                        "call",
                        0,
                        callWindow,
                        errorLabel
                );
            }
        });

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callWindow.setVisible(false);
                currentOpenWindow=null;
            }
        });

        buttonTable.add(confirmCallButton).expandX().fillX();
        buttonTable.add(backButton).expand().fillX();

        callWindow.row();
        callWindow.add(buttonTable).expandX().fillX();

        stage.addActor(callWindow);

        callButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentOpenWindow!=null){
                    currentOpenWindow.setVisible(false);
                }
                callWindow.setVisible(true);
                currentOpenWindow=callWindow;
            }
        });
    }


    private void raiseLogic(){

        TextButton raiseButton = new TextButton("RAISE", skin);
        raiseButton.setPosition((float) (Main.SCREEN_WIDTH*0.05),(float)(Main.SCREEN_HEIGHT*0.15));
        raiseButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));

        stage.addActor(raiseButton);

        final Window raiseWindow = new Window("", skin);
        raiseWindow.setSize((float) (Main.SCREEN_WIDTH * 0.7), (float) (Main.SCREEN_HEIGHT * 0.2));
        raiseWindow.setPosition((float) (Main.SCREEN_WIDTH * 0.15), (float) (Main.SCREEN_HEIGHT * 0.5));
        raiseWindow.setVisible(false);

        Label raiseLabel = new Label("Raise Amount", skin);
        raiseLabel.setAlignment(Align.center);
        raiseWindow.add(raiseLabel).expandX().fillX().pad(10);

        final TextField raiseAmountField = new TextField("", skin);
        raiseAmountField.setMessageText("Enter amount");
        raiseAmountField.setAlignment(Align.center);
        raiseWindow.row();
        raiseWindow.add(raiseAmountField).expandX().fillX().pad(10);

        Table buttonTable = new Table();
        buttonTable.top().padTop(10);
        buttonTable.defaults().width(300).height(100).pad(5);

        TextButton confirmRaiseButton = new TextButton("CONFIRM", skin);
        confirmRaiseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String amountText = raiseAmountField.getText();
                try {
                    int raiseAmount = Integer.parseInt(amountText);
                    if (raiseAmount <= 0){
                        throw new NumberFormatException();
                    }
                    PlayerController.getInstance().performAction(
                            PlayerController.getInstance().getCurrentTable().getPlayerId(),
                            "raise",
                            raiseAmount,
                            raiseWindow,
                            errorLabel
                    );
                } catch (NumberFormatException e) {
                    raiseLabel.setText("Invalid input");
                    System.out.println("Invalid input");
                }
            }
        });

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                raiseWindow.setVisible(false);
                currentOpenWindow=null;
            }
        });

        buttonTable.add(confirmRaiseButton).expandX().fillX();
        buttonTable.add(backButton).expandX().fillX();

        raiseWindow.row();
        raiseWindow.add(buttonTable).expandX().fillX();

        stage.addActor(raiseWindow);

        raiseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentOpenWindow!=null){
                    currentOpenWindow.setVisible(false);
                }
                raiseWindow.setVisible(true);
                currentOpenWindow=raiseWindow;
            }
        });

    }

    private void foldLogic(){
        TextButton foldButton = new TextButton("FOLD", skin);
        foldButton.setPosition((float) (Main.SCREEN_WIDTH*0.55),(float)(Main.SCREEN_HEIGHT*0.15));
        foldButton.setSize((float) (Main.SCREEN_WIDTH*0.4), (float)(Main.SCREEN_HEIGHT*0.075));

        stage.addActor(foldButton);

        final Window foldWindow = new Window("", skin);
        foldWindow.setSize((float) (Main.SCREEN_WIDTH * 0.7), (float) (Main.SCREEN_HEIGHT * 0.2));
        foldWindow.setPosition((float) (Main.SCREEN_WIDTH * 0.15), (float) (Main.SCREEN_HEIGHT * 0.5));
        foldWindow.setVisible(false);

        Label foldLabel = new Label("Fold?", skin);
        foldLabel.setAlignment(Align.center);
        foldWindow.add(foldLabel).expandX().fillX().pad(10);

        Table buttonTable = new Table();
        buttonTable.top().padTop(10);
        buttonTable.defaults().width(300).height(100).pad(5);

        TextButton confirmFoldButton = new TextButton("CONFIRM", skin);
        confirmFoldButton.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y){
               PlayerController.getInstance().performAction(
                       PlayerController.getInstance().getCurrentTable().getPlayerId(),
                       "fold",
                       0,
                       foldWindow,
                       errorLabel);
           }
        });

        TextButton backButton = new TextButton("BACK",skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                foldWindow.setVisible(false);
                currentOpenWindow=null;
            }
        });

        buttonTable.add(confirmFoldButton).expandX().fillX();
        buttonTable.add(backButton).expand().fillX();

        foldWindow.row();
        foldWindow.add(buttonTable).expandX().fillX();

        stage.addActor(foldWindow);

        foldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                if (currentOpenWindow!=null){
                    currentOpenWindow.setVisible(false);
                }
                foldWindow.setVisible(true);
                currentOpenWindow=foldWindow;
            }
        });

    }

    @Override
    public void update(PlayerTable playerTable) {
        System.out.println("Got an update");
        potLabel.setText("Pot: "+playerTable.getPot());
        stackLabel.setText("Stack: "+playerTable.getStack());
        errorLabel.setText("");
        if (playerTable.getCurrentTurn() == null){
            turnLabel.setText("Game is starting, please wait");
        } else if (playerTable.getCurrentTurn().equals(playerTable.getPlayerId())){
            turnLabel.setText("Your turn");
        } else {
            turnLabel.setText(playerTable.getCurrentPlayer()+"'s turn");
        }
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
