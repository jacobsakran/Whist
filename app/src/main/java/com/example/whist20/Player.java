package com.example.whist20;

import androidx.annotation.NonNull;

public class Player extends Object{
    public String uid;
    public String userName;
    public boolean is_ready;
    public CardsSet cards;

    public Player() {
    }

    public Player(String Uid, String userName) {
        this.uid = Uid;
        this.is_ready = true;
        this.userName = userName;
        this.cards = new CardsSet();
    }

    public void openNewCard(GameState game) {
        this.cards.addCard(game.dict.randomCard());
    }
    public void clearCardsSet() { this.cards = new CardsSet(); }

    public int playerScore() {
        return this.cards.sum();
    }

    public String userName() {
        return this.userName;
    }

    @Override
    public boolean equals(@NonNull Object p) {
        return this.uid.equals(((Player) p).uid);
    }
}
