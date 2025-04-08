package io.github.G16.Model;

public enum Suit {
    HEARTS("h"), DIAMONDS("d"), CLUBS("c"), SPADES("s");

    private final String shortCode;

    Suit(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public static Suit fromString(String code) {
        for (Suit suit : Suit.values()) {
            if (suit.shortCode.equalsIgnoreCase(code)) {
                return suit;
            }
        }
        throw new IllegalArgumentException("Invalid suit: " + code);
    }
}
