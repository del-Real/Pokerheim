package io.github.G16.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;

import io.github.G16.Model.Player;

public class PlayerController {
    private Player player;
    private String joinLobbyUrl;
    void joinLobby(String code, String name){
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
                } else {
                    Gdx.app.log("HTTP", "Error joining lobby: "+statusCode);

                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", "Request failed: " + t.getMessage());

            }

            @Override
            public void cancelled() {
                Gdx.app.log("HTTP", "Request cancelled");
            }
        });


    }
    private String extractPlayerId(String responseText) {
        // La risposta sarà del tipo "playerId: <id>"
        String[] parts = responseText.split(": ");
        if (parts.length > 1) {
            return parts[1];  // Restituisce solo l'ID del giocatore
        }
        return null;
    }
}