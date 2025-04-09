package io.github.G16.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

import io.github.G16.Model.PlayerTable;
import io.github.G16.View.ScreenStates.GameScreen;

public class PlayerController {
    private static PlayerController instance;
    private PlayerTable currentTable;
    private PlayerController(){}

    public static PlayerController getInstance(){
        if (instance == null){
            instance = new PlayerController();
        }
        return instance;
    }

    public PlayerTable getCurrentTable() {
        return currentTable;
    }



    public void joinLobby(String code, String name, TextField codeField, TextButton button){
        String url = "https://us-central1-pokergame-007.cloudfunctions.net/joinTable?tableId=table"+code+"&name="+name;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK){
                    Gdx.app.log("HTTP","Joined lobby successfully: "+code);

                    String responseText = httpResponse.getResultAsString();
                    System.out.println(extractPlayerId(responseText));
                    if (codeField != null){
                        codeField.setText("Joined lobby successfully");
                    }
                    currentTable = new PlayerTable(code, extractPlayerId(responseText));
                    InputManager.getInstance(null,null).listenForTableUpdates(currentTable);
                    button.clearListeners();
                    button.setText("READY?");
                    button.addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y){
                            button.setDisabled(true);
                            setPlayerStatus(getCurrentTable().getPlayerId(),"ready",button);
                        }
                    });
                    button.setDisabled(false);
                } else {
                    Gdx.app.log("HTTP", "Error joining lobby: "+statusCode);
                    if (codeField!=null){
                        codeField.setText("Failed to join lobby");
                    }

                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", "Request failed: " + t.getMessage());
                codeField.setText("Connection Error");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });


    }

    private String extractPlayerId(String responseText) {
        String[] parts = responseText.split(": ");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }

    public void createLobby(Label codeLabel, String name, TextButton button){
        String tableId = "" + System.currentTimeMillis();
        String url = "https://us-central1-pokergame-007.cloudfunctions.net/createTable?tableId=table" + tableId;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    Gdx.app.log("HTTP", "Lobby Created Successfully: " + tableId);
                    codeLabel.setText(tableId);
                    joinLobby(tableId,name,null,button);
                } else {
                    Gdx.app.log("HTTP", "Error creating lobby: " + statusCode);
                    codeLabel.setText("Failed to create lobby");
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", "Request failed: " + t.getMessage());
                codeLabel.setText("Connection Error");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });

    }

    public void performAction(String playerId, String action, int amount, Window infoWindow, Label infoLabel){
        String url = "https://us-central1-pokergame-007.cloudfunctions.net/performAction?playerId="+playerId+"&action="+action+"&amount="+amount;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    Gdx.app.log("HTTP", "Action performed successfully");
                    infoWindow.setVisible(false);
                } else {
                    Gdx.app.log("HTTP", "Error performing action: " + statusCode);
                    infoLabel.setText("Error performing action");
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", "Request failed: " + t.getMessage());
                infoLabel.setText("Error performing action");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });
    }

    public void setPlayerStatus(String playerID, String status, TextButton button) {
        String url = "https://us-central1-pokergame-007.cloudfunctions.net/playerStatus?playerId=" + playerID + "&status=" + status;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    Gdx.app.log("HTTP", "Player status updated successfully to: " + status);
                    button.setText("READY!");
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            button.setDisabled(false);
                            GameScreen gameScreen = new GameScreen(InputManager.getInstance(null,null));
                            InputManager.getInstance(null,null).changeScreen(gameScreen);
                            currentTable.addObserver(gameScreen);
                        }
                    }, 1f);
                } else {
                    Gdx.app.log("HTTP", "Error updating player status: " + statusCode);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", "Failed to update player status: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Player status update request cancelled");
            }
        });
    }

}