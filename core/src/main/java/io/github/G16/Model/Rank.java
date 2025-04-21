package io.github.G16.Model;

public enum Rank {

    // Rank enum used by cards
    TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"),
    SEVEN("7"), EIGHT("8"), NINE("9"), TEN("T"),
    JACK("J"), QUEEN("Q"), KING("K"), ACE("A");

    private final String shortCode;

    Rank(String shortCode) {
        this.shortCode = shortCode;
    }

    public static Rank fromString(String code) {
        for (Rank rank : Rank.values()) {
            if (rank.shortCode.equalsIgnoreCase(code)) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Invalid rank: " + code);
    }
}
