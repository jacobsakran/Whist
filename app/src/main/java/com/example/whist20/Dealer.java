package com.example.whist20;

public class Dealer extends Player {
    public Dealer() {
    }

    public Dealer(String Uid, String userName, int initial_budget) {
        super(Uid, userName, initial_budget);
    }

    @Override
    public void openNewCard(GameState game) {
        Node iterator = game.players.head;
        while (iterator != null) {
            Player player = ((Player) iterator.obj);
            if (player == null) return;
            if (Math.max(((Player) iterator.obj).cards.sum(), 17) > this.cards.sum()) this.cards.addCard(game.dict.randomCard());
            iterator = iterator.next;
        }
        game.nextPlayerTurn();
    }
}
