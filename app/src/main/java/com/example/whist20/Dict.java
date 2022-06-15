package com.example.whist20;

import androidx.annotation.NonNull;

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

    public static Dict convertSnapshotToDict(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) return null;
        Dict dict = new Dict();

        DataSnapshot iterator = snapshot.child("freeCards").child("head");
        while (iterator.exists()) {
            Card card = iterator.child("obj").getValue(Card.class);
            dict.freeCards.addNode(card);
            iterator = iterator.child("next");
        }

        iterator = snapshot.child("userCards").child("head");
        while (iterator.exists()) {
            Card card = iterator.child("obj").getValue(Card.class);
            dict.usedCards.addNode(card);
            iterator = iterator.child("next");
        }

        return dict;
    }
}
