package com.example.whist20;

import androidx.annotation.NonNull;

public class Player {
    public String uid;
    public String userName;
    public CardsSet cards;

    public Player() {
    }

    public Player(String Uid, String userName) {
        this.uid = Uid;
        this.userName = userName;
        this.cards = new CardsSet();
    }

    public void openNewCard(@NonNull Dict dict, GameState game) {
        this.cards.addCard(dict.randomCard());
    }

    public int playerScore() {
        return this.cards.sum();
    }

    public String userName() {
        return this.userName;
    }

    public boolean equals(@NonNull Player p) {
        return this.uid.equals(p.uid);
    }
}
