package com.example.whist20;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.core.SnapshotHolder;

import java.util.HashMap;
import java.util.Vector;

public class Dict {
    public MyList freeCards;
    public MyList usedCards;
    public MyList suitList;

    public Dict() {
        this.usedCards = new MyList();
        this.freeCards = new MyList();
        this.suitList = new MyList();
        this.suitList.addNode(Suit.Hearts);
        this.suitList.addNode(Suit.Diamonds);
        this.suitList.addNode(Suit.Spades);
        this.suitList.addNode(Suit.Clubs);

    }

    public Card randomCard() {
        if (this.freeCards.size < 1) {
            return null;
        }
        int index = ((int) Math.random()) % (this.freeCards.size);
        Card tmp = (Card) this.freeCards.findByIndex(index);
        this.usedCards.addNode(tmp);
        this.freeCards.removeByIndex(index);
        return tmp;
    }

    public Dict convertSnapshotToDict(DataSnapshot snapshot) {
        Dict tmp = snapshot.getValue(Dict.class);
        Dict dict = new Dict();
        Node cards_iterator = (Node) tmp.freeCards.head;

        while (cards_iterator != null) {
            HashMap<String, Object> convert_card = (HashMap<String, Object>) cards_iterator.obj;
            Card card = new Card();
            card.value = (int) convert_card.get("value");
            card.suit = (Suit) convert_card.get("suit");

            dict.freeCards.addNode(card);
            cards_iterator = cards_iterator.next;
        }

        cards_iterator = (Node) tmp.usedCards.head;
        while (cards_iterator != null) {
            HashMap<String, Object> convert_card = (HashMap<String, Object>) cards_iterator.obj;
            Card card = new Card();
            card.value = (int) convert_card.get("value");
            card.suit = (Suit) convert_card.get("suit");

            dict.usedCards.addNode(card);
            cards_iterator = cards_iterator.next;
        }

        cards_iterator = (Node) tmp.suitList.head;
        while (cards_iterator != null) {
            dict.suitList.addNode((Suit) cards_iterator.obj);
            cards_iterator = cards_iterator.next;
        }

        return dict;
    }
}
