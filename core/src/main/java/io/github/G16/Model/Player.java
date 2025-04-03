package io.github.G16.Model;

public class Player {

    private String playerID;
    private PlayerHand playerHand;
    private String name;
    private int chips;
    public Player(String playerID, String name){
        this.playerID=playerID;
        this.playerHand=new PlayerHand();
    }

    public PlayerHand getPlayerHand() {
        return this.playerHand;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public int getChips() {
        return this.chips;
    }
}
