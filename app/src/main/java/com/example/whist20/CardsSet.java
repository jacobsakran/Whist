package com.example.whist20;


import java.util.Vector;

public class CardsSet {
    public MyList cards;
    public int cardsSum;

    public CardsSet() {
        this.cardsSum = 0;
        this.cards = new MyList();
    }

    public void addCard(Card card) {
        this.cards.addNode(card);
        this.cardsSum += card.value;
    }

    public int sum() {
        return this.cardsSum < 22 ? this.cardsSum : -1;
    }
}

