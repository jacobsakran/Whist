package com.example.whist20;

import android.widget.Toast;

import java.util.Vector;

public class GameState {
    public MyList players;
    public Node currentPlayer;
    public int numOfPlayers;
    public String game_name;
    public boolean is_active;
    public int maxPlayersNum;
    public Dict dict;

    public GameState() {}
    public GameState(String game_name) {
        this.players = new MyList();
        this.currentPlayer = new Node();
        this.numOfPlayers = 0;
        this.maxPlayersNum = 4;
        this.is_active = false;
        this.game_name = game_name;
        this.dict = new Dict();
    }

    public void addPlayer(Player player){
        this.players.addNode(player);
        this.numOfPlayers = this.numOfPlayers + 1;
        this.currentPlayer = this.players.head;
    }

    public void removePlayerByUid(String uid) {
        //if (this.is_active) return;
        Player to_remove = new Player();
        to_remove.uid = uid;
        this.players.removeByValue(to_remove);
        this.numOfPlayers--;
    }

    public Player currentPlayerTurn() {
        return (Player) this.currentPlayer.obj;
    }

    public Player nextPlayerTurn() {
        this.currentPlayer = this.currentPlayer.next;
        if (this.currentPlayer == null) this.currentPlayer = this.players.head;
        return this.currentPlayerTurn();
    }
}
