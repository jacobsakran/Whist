package com.example.whist20;

public class Dealer extends Player {
    public Dealer() {
    }

    public Dealer(String Uid, String userName) {
        super(Uid, userName);
    }

    @Override
    public void openNewCard(Dict dict, GameState game) {
        Node iterator = game.players.head;
        while (iterator != null) {
            if (((Player) iterator.obj).cards.sum() > this.cards.sum()) {
                this.cards.addCard(dict.randomCard());
                return;
            }
            iterator = iterator.next;
        }
    }
}
