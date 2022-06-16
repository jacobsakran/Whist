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
        this.cardsSum += Math.min(card.value, 10);
        if(card.value == 14) this.cardsSum++;
    }

    public int sum() {
        return this.cardsSum < 22 ? this.cardsSum : -1;
    }
}

