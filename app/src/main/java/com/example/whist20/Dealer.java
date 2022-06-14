package com.example.whist20;

public class Dealer extends Player {
    public String DealerName;

    public Dealer() {
    }

    public Dealer(String Uid, String userName, String DealerName) {
        super(Uid, userName);
        this.DealerName = DealerName;
    }

    @Override
    public String userName() {
        return this.DealerName;
    }

    @Override
    public void openNewCard(Dict dict, GameState game) {
        Node iterator = game.players.head;
        while(iterator != null){
            if(((Player)iterator.obj).cards.sum() > this.cards.sum()){
                this.openNewCard(dict, game);
                return;
            }
        }
    }
}
