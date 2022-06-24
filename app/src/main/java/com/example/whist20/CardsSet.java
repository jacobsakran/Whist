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
        if (card.value == 14) this.cardsSum++;
    }

    public int sum() {
        Node iterator = cards.head;
        int num_of_aces = 0;
        while (iterator != null) {
            if (iterator.obj == null) break;
            if (((Card) iterator.obj).value == 14) num_of_aces++;
            iterator = iterator.next;
        }

        int tmp = cardsSum;
        while (tmp > 21) {
            if (num_of_aces == 0) break;
            tmp = tmp - 10;
            num_of_aces--;
        }

        return tmp < 22 ? tmp : -1;
    }
}

