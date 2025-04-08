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
    public PlayerTable(String tableId, String playerId){
        this.tableId = tableId;
        this.playerId = playerId;
    }

    // This can be done better so that everything is updated only when something is different but its low priority
    public void updateTable(int pot, String currentTurn, ArrayList<Card> communityCards, ArrayList<Card> playerHand, int stack, String currentPlayer){
        this.pot = pot;
        this.currentTurn=currentTurn;
        this.communityCards=communityCards;
        this.playerHand=playerHand;
        this.stack=stack;
        this.currentPlayer=currentPlayer;
        System.out.println("Update");
        notifyObservers();
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
