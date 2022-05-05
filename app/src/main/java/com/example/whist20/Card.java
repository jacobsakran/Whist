package com.example.whist20;

enum Suit {
    Hearts,
    Diamonds,
    Spade,
    Clubs
}
public class Card {
    public Suit suit;
    public int value;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }
}
