package com.example.whist20;

import androidx.annotation.NonNull;

public class Player extends Object{
    public String uid;
    public String userName;
    public int budget;
    public int num_of_wins;
    public int rank;
    public int streak;
    public int num_of_rounds;
    public boolean is_ready;
    public CardsSet cards;
    public boolean isDouble;

    public Player() {
    }

    public Player(String Uid, String userName, int initial_budget, int rank) {
        this.num_of_wins = 0;
        this.rank = rank;
        this.streak = 0;
        this.num_of_rounds = 0;
        this.uid = Uid;
        this.budget = initial_budget;
        this.is_ready = true;
        this.userName = userName;
        this.cards = new CardsSet();
        this.isDouble = false;
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
