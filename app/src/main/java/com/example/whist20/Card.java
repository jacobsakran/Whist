package com.example.whist20;

enum Suit {
    Hearts,
    Diamonds,
    Spades,
    Clubs
}


public class Card {
    public Suit suit;
    public int value;

    public Card() {}

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public static Suit convertInToSuit(int val) {
        if (val == 0) return Suit.Hearts;
        if (val == 1) return Suit.Diamonds;
        if (val == 2) return Suit.Spades;
        return Suit.Clubs;
    }

    public static int convertSuitToInt(Suit suit) {
        if (suit == Suit.Hearts) return 0;
        if (suit == Suit.Diamonds) return 1;
        if (suit == Suit.Spades) return 2;
        return 3;
    }
}
