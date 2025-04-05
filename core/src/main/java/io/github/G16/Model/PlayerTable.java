package io.github.G16.Model;

import java.util.ArrayList;

import io.github.G16.View.ObserverScreen;

public class PlayerTable implements Subject{
    private final String tableId;
    private final String playerId;
    private int pot;
    private String currentTurn;
    private ArrayList<Card> communityCards=new ArrayList<>();
    private ArrayList<Card> playerHand = new ArrayList<>();
    private ArrayList<ObserverScreen> observers=new ArrayList<>();
    public PlayerTable(String tableId, String playerId){
        this.tableId = tableId;
        this.playerId = playerId;
    }

    public void updateTable(int pot, String currentTurn, ArrayList<Card> communityCards, ArrayList<Card> playerHand){
        this.pot = pot;
        this.currentTurn=currentTurn;
        this.communityCards=communityCards;
        this.playerHand=playerHand;
        System.out.println("Update");
        notifyObservers();
    }
    public String getTableId() {
        return tableId;
    }

    public String getPlayerId(){
        return playerId;
    }

    public int getPot(){
        return pot;
    }

    public ArrayList<Card> getCommunityCards() {
        return communityCards;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public void addObserver(ObserverScreen observer){
        this.observers.add(observer);
        observer.update(this);
    }

    @Override
    public void notifyObservers() {
        System.out.println("Notifying");
        for (ObserverScreen o: observers){
            o.update(this);
        }
    }
}
