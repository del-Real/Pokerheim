package io.github.G16.Model;

import java.util.ArrayList;

public class Player {

    private final String playerID;
    private final String name;
    private final ArrayList<Card> cards;

    private int stack;

    private boolean folded=false;
    private boolean allIn=false;
    public Player(String playerID, String name, ArrayList<Card> cards, int stack){
        this.playerID=playerID;
        this.name=name;
        this.cards=cards;
        this.stack=stack;
    }

    public void updatePlayer(int stack, boolean folded, boolean allIn){
        this.stack = stack;
        this.folded = folded;
        this.allIn = allIn;
    }
}
