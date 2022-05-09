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

    public Card() {

    }

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }
}
