package com.example.whist20;


import java.util.Vector;

public class CardsSet {
    MyList cards;
    int cardsSum;
    public CardsSet(){
        this.cardsSum = 0;
        this.cards = new MyList();
    }
    public void addCard(Card card){
        this.cards.addNode(card);
        this.cardsSum += card.value;
    }
    public int getSum(){
        return this.cardsSum < 22 ? this.cardsSum : -1;
    }
}

