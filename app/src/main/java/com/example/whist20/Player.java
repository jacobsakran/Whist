package com.example.whist20;

public class Player {
    String uid;
    String userName;
    CardsSet cards;

    public Player(){}
    public Player(String Uid, String userName){
        this.uid = Uid;
        this.userName = userName;
        this.cards = new CardsSet();
    }
    public void openNewCard(Dict dict, GameState game){
        this.cards.addCard(dict.getRandomCard());
    }
    public int getPlayerScore(){
        return this.cards.getSum();
    }
    public String getName(){
        return this.userName;
    }
    public boolean equals(Player p){return this.uid.equals(p.uid);}
}
