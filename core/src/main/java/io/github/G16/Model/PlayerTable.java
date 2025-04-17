package io.github.G16.Model;

import java.util.ArrayList;

import io.github.G16.View.Observer;

public class PlayerTable implements Subject{

    // This basically contains the information the player sees/needs
    private final String tableId;
    private final String playerId;
    private int pot;
    private int stack;
    private String currentTurn;
    private ArrayList<Card> communityCards=new ArrayList<>();
    private ArrayList<Card> playerHand = new ArrayList<>();
    private ArrayList<Observer> observers=new ArrayList<>();
    private String currentPlayer;
    private String lastAction;
    public PlayerTable(String tableId, String playerId){
        this.tableId = tableId;
        this.playerId = playerId;
    }

    public void updateTable(int pot, String currentTurn, ArrayList<Card> communityCards, ArrayList<Card> playerHand, int stack, String currentPlayer, String lastAction){
        boolean different = false;

        // Compare each field and update only if there's a change
        if (this.pot != pot) {
            this.pot = pot;
            different = true;
        }

        if (this.currentTurn == null || !this.currentTurn.equals(currentTurn)) {
            this.currentTurn = currentTurn;
            different = true;
        }

        if (this.communityCards == null || !this.communityCards.equals(communityCards)) {
            this.communityCards = communityCards;
            different = true;
        }

        if (this.playerHand == null || !this.playerHand.equals(playerHand)) {
            this.playerHand = playerHand;
            different = true;
        }

        if (this.stack != stack) {
            this.stack = stack;
            different = true;
        }

        if (this.currentPlayer == null || !this.currentPlayer.equals(currentPlayer)) {
            this.currentPlayer = currentPlayer;
            different = true;
        }

        if (this.lastAction == null || !this.lastAction.equals(lastAction)){
            this.lastAction=lastAction;
            different=true;
        }

        // If any field was different, notify observers
        if (different) {
            System.out.println("Update, last action now: "+this.lastAction);
            notifyObservers();
        }
    }

    public String getTableId() {
        return tableId;
    }

    public String getPlayerId(){
        return playerId;
    }

    public String getCurrentTurn(){
        return currentTurn;
    }

    public int getPot(){
        return pot;
    }

    public int getStack() {
        return stack;
    }
    public String getCurrentPlayer(){
        return currentPlayer;
    }

    public ArrayList<Card> getCommunityCards() {
        return communityCards;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }
    public String getLastAction(){
        return lastAction;
    }

    public void addObserver(Observer observer){
        this.observers.add(observer);
        observer.update(this);
    }

    @Override
    public void notifyObservers() {
        System.out.println("Notifying");
        for (Observer o: observers){
            o.update(this);
        }
    }
}
