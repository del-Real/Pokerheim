package io.github.G16.Model;

import java.util.ArrayList;

import io.github.G16.View.ObserverScreen;

public class PlayerTable implements Subject{
    private String tableId;
    int pot;
    private String currentTurn;
    private ArrayList<Card> communityCards=new ArrayList<>();
    private ArrayList<ObserverScreen> observers=new ArrayList<>();
    public PlayerTable(String tableId){
        this.tableId = tableId;
    }

    public void updateTable(int pot, String currentTurn, ArrayList<Card> communityCards){
        this.pot = pot;
        this.currentTurn=currentTurn;
        this.communityCards=communityCards;
        System.out.println("Update");
        notifyObservers();
    }
    public String getTableId() {
        return tableId;
    }

    public int getPot(){
        return this.pot;
    }

    public void addObserver(ObserverScreen observer){
        this.observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        System.out.println("Notifying");
        for (ObserverScreen o: observers){
            o.update(this);
        }
    }
}
