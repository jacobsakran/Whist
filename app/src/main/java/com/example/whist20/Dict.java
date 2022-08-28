package com.example.whist20;

import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.core.SnapshotHolder;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class Dict {
    public MyList usedCards;

    public Dict() {
        this.usedCards = new MyList();
    }

    public Card randomCard() {
        if (this.usedCards.size > 51) return null;

        boolean[][] is_used = new boolean[4][13];

        Node iterator = usedCards.head;
        while (iterator != null) {
            Card card = (Card) iterator.obj;
            if (card == null) break;
            is_used[Card.convertSuitToInt(card.suit)][card.value - 2] = true;
            iterator = iterator.next;
        }

        Card[] unused_cards = new Card[52];
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                if (!is_used[i][j]) {
                    unused_cards[counter] = new Card(Card.convertInToSuit(i), j + 2);
                    counter++;
                }
            }
        }

        Random rand = new Random();
        int index = rand.nextInt(counter);
        Card tmp = unused_cards[index];
        this.usedCards.addNode(tmp);
        return tmp;
    }

    public static Dict convertSnapshotToDict(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) return null;
        Dict dict = new Dict();

        DataSnapshot iterator = snapshot.child("usedCards").child("head");
        while (iterator.exists()) {
            Card card = iterator.child("obj").getValue(Card.class);
            dict.usedCards.addNode(card);
            iterator = iterator.child("next");
        }

        return dict;
    }

    public static Dict convertMutableDataToDict(@NonNull MutableData currentData) {
        if (!currentData.hasChildren()) return null;
        Dict dict = new Dict();

        MutableData iterator = currentData.child("usedCards").child("head");
        while (iterator.hasChildren()) {
            Card card = iterator.child("obj").getValue(Card.class);
            dict.usedCards.addNode(card);
            iterator = iterator.child("next");
        }

        return dict;
    }
}
