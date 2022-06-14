package com.example.whist20;

public class Dealer extends Player{
    String DealerName;
    public Dealer(){}
    public Dealer(String Uid, String userName, String DealerName){
        super(Uid, userName);
        this.DealerName = DealerName;
    }
    @Override
    public String getName(){
        return this.DealerName;
    }

    @Override
    public void openNewCard(Dict dict, GameState game) {
        Node iterator = game.players.head;
        while(iterator != null){
            if(((Player)iterator.obj).cards.getSum() > this.cards.getSum()){
                this.openNewCard(dict, game);
                return;
            }
        }
    }
}
