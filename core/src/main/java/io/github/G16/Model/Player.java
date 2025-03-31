package io.github.G16.Model;

public class Player {
    PlayerHand playerHand;
    int chips;
    public Player(int chips){
        this.playerHand=new PlayerHand();
        this.chips=chips;
    }

    public PlayerHand getPlayerHand() {
        return this.playerHand;
    }

    public int getChips() {
        return this.chips;
    }
}
