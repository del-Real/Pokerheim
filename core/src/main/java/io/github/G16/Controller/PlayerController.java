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
import io.github.G16.View.ScreenStates.MainMenuScreen;

public class PlayerController {
    private static final String BASE_URL = "https://us-central1-pokergame-007.cloudfunctions.net";
    private static PlayerController instance;
    private PlayerTable currentTable;

    private PlayerController() {}

    public static PlayerController getInstance() {
        if (instance == null) {
            instance = new PlayerController();
        }
        return instance;
    }

    public PlayerTable getCurrentTable() {
        return currentTable;
    }

    public void joinLobby(String code, String name, TextField codeField, TextButton button, Label errorLabel) {
        String url = BASE_URL + "/joinTable?tableId=table" + code + "&name=" + name;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);
        if (button != null) button.setDisabled(true);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    String responseText = httpResponse.getResultAsString();
                    currentTable = new PlayerTable(code, extractPlayerId(responseText));
                    InputManager.getInstance(null, null).listenForUpdates(currentTable);

                    if (codeField != null) codeField.setText("Joined lobby successfully");
                    if (errorLabel != null) errorLabel.setText("");

                    button.clearListeners();
                    button.setText("READY?");
                    button.addListener(new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            button.setDisabled(true);
                            setPlayerStatus(currentTable.getPlayerId(), "ready", button);
                        }
                    });
                    button.setDisabled(false);
                } else {
                    if (codeField != null) codeField.setText("Failed to join lobby");
                    if (errorLabel != null) errorLabel.setText("Unable to join lobby. Please try again.");
                    if (button != null) button.setDisabled(false);
                }
            }

            @Override
            public void failed(Throwable t) {
                if (codeField != null) codeField.setText("Connection Error");
                if (errorLabel != null) errorLabel.setText("Network error. Please check your connection.");
                if (button != null) button.setDisabled(false);
            }

            @Override
            public void cancelled() {
                if (errorLabel != null) errorLabel.setText("Request Cancelled");
                if (button != null) button.setDisabled(false);
            }
        });
    }

    private String extractPlayerId(String responseText) {
        String[] parts = responseText.split(": ");
        return parts.length > 1 ? parts[1] : null;
    }

    public void createLobby(Label codeLabel, String name, TextButton button, Label errorLabel) {
        tryCreateLobby(generateRandomCode(), codeLabel, name, button, errorLabel);
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + (int) (Math.random() * 900000));
    }

    private void tryCreateLobby(String tableId, Label codeLabel, String name, TextButton button, Label errorLabel) {
        String url = BASE_URL + "/createTable?tableId=table" + tableId;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);
        if (button != null) button.setDisabled(true);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseText = httpResponse.getResultAsString();

                if (statusCode == HttpStatus.SC_OK) {
                    codeLabel.setText(tableId);
                    if (errorLabel != null) errorLabel.setText("");
                    joinLobby(tableId, name, null, button, errorLabel);
                } else if (responseText != null && responseText.contains("Table already exists")) {
                    tryCreateLobby(generateRandomCode(), codeLabel, name, button, errorLabel);
                } else {
                    codeLabel.setText("Failed to create lobby");
                    if (errorLabel != null) errorLabel.setText("Could not create lobby.");
                    if (button != null) button.setDisabled(false);
                }
            }

            @Override
            public void failed(Throwable t) {
                codeLabel.setText("Connection Error");
                if (errorLabel != null) errorLabel.setText("Network error. Please try again.");
                if (button != null) button.setDisabled(false);
            }

            @Override
            public void cancelled() {
                if (errorLabel != null) errorLabel.setText("Request Cancelled");
                if (button != null) button.setDisabled(false);
            }
        });
    }

    public void performAction(String playerId, String action, int amount, Window infoWindow, Label errorLabel) {
        String url = BASE_URL + "/performAction?playerId=" + playerId + "&action=" + action + "&amount=" + amount;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);
        infoWindow.setTouchable(null);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                int statusCode = httpResponse.getStatus().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    errorLabel.setText("");
                } else {
                    String responseText = httpResponse.getResultAsString();
                    if (responseText != null && !responseText.isEmpty()) {
                        errorLabel.setText(responseText
                                .replace("player " + currentTable.getPlayerId() + "'s", "your")
                                .replace("Player " + currentTable.getPlayerId(), "You"));
                    } else {
                        errorLabel.setText("Action failed. Please try again.");
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                errorLabel.setText("Network error occurred");
                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            }

            @Override
            public void cancelled() {
                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            }
        });
    }

    public void setPlayerStatus(String playerId, String status, TextButton button) {
        String url = BASE_URL + "/playerStatus?playerId=" + playerId + "&status=" + status;

        button.setText("WAIT");
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                    button.setText("READY!");
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            button.setDisabled(false);
                            GameScreen gameScreen = new GameScreen(InputManager.getInstance(null, null));
                            InputManager.getInstance(null, null).changeScreen(gameScreen);
                            currentTable.addObserver(gameScreen);
                        }
                    }, 1f);
                } else {
                    button.setDisabled(false);
                }
            }

            @Override
            public void failed(Throwable t) {
                button.setDisabled(false);
                button.setText("TRY AGAIN");
            }

            @Override
            public void cancelled() {
                button.setDisabled(false);
            }
        });
    }

    public void leaveTable(String playerId, Window infoWindow, Label errorLabel) {
        String url = BASE_URL + "/leaveTable?playerId=" + playerId;

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setTimeOut(5000);

        infoWindow.setTouchable(null);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("LeaveTable - Status: " + statusCode);
                System.out.println("LeaveTable - Response: " + httpResponse.getResultAsString());

                if (statusCode == HttpStatus.SC_OK) {
                    currentTable = null;
                    errorLabel.setText("Leaving...");

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            MainMenuScreen mainMenuScreen = new MainMenuScreen(InputManager.getInstance(null, null));
                            InputManager.getInstance(null, null).changeScreen(mainMenuScreen);
                        }
                    }, 1f);
                } else {
                    errorLabel.setText("Unable to leave the table.");
                }
            }

            @Override
            public void failed(Throwable t) {
                errorLabel.setText("Network error. Try again.");
                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            }

            @Override
            public void cancelled() {
                errorLabel.setText("Request cancelled.");
                infoWindow.setVisible(false);
                infoWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            }
        });
    }

}
