package com.example.whist20;

import java.util.Vector;

public class Dict {
    MyList freeCards;
    MyList usedCards;
    static Suit[] suitArray = {Suit.Hearts, Suit.Diamonds, Suit.Spades, Suit.Clubs};

    public Dict(){
        this.usedCards = new MyList();
        this.freeCards = new MyList();
        for(int i=0; i<52; i++){
            this.freeCards.addNode(new Card(suitArray[(int)Math.floor((double)i/13)], (i)%13+1 ));
        }
    }
    public Card getRandomCard(){
        if(this.freeCards.size < 1){
            return null;
        }
        int index = ((int)Math.random())%(this.freeCards.size);
        Card tmp = (Card) this.freeCards.getByIndex(index);
        this.usedCards.addNode(tmp);
        this.freeCards.removeByIndex(index);
        return tmp;
    }
}
