package io.github.G16.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import io.github.G16.Model.Player;
import io.github.G16.View.ViewManager;

public class PlayerController {
    private Player player;
    private String joinLobbyUrl;
    private static PlayerController instance;
    private PlayerController(){}

    public static PlayerController getInstance(){
        if (instance == null){
            instance = new PlayerController();
        }
        return instance;
    }
    public void joinLobby(String code, String name, TextField codeField){
        joinLobbyUrl = "https://us-central1-pokergame-007.cloudfunctions.net/joinTable?tableId=table"+code+"&name="+name;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(joinLobbyUrl);
        request.setTimeOut(5000);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK){
                    Gdx.app.log("HTTP","Joined lobby successfully: "+code);

                    String responseText = httpResponse.getResultAsString();
                    player=new Player(extractPlayerId(responseText),name);
                    System.out.println(extractPlayerId(responseText));
                    codeField.setText("Joined lobby successfully");
                } else {
                    Gdx.app.log("HTTP", "Error joining lobby: "+statusCode);

                    codeField.setText("Failed to join lobby");
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

    public void createLobby(Label codeLabel){

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
}