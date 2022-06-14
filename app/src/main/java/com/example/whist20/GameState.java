package com.example.whist20;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Vector;

public class GameState {
    public MyList players;
    public Node currentPlayer;
    public int numOfPlayers;
    public String game_name;
    public boolean is_active;
    public int maxPlayersNum;
    public Dict dict;

    public GameState() {
    }

    public GameState(String game_name) {
        this.players = new MyList();
        this.currentPlayer = new Node();
        this.numOfPlayers = 0;
        this.maxPlayersNum = 4;
        this.is_active = false;
        this.game_name = game_name;
        this.dict = new Dict();
    }

    public void addPlayer(Player player) {
        this.players.addNode(player);
        this.numOfPlayers = this.numOfPlayers + 1;
        this.currentPlayer = this.players.head;
    }

    public void removePlayerByUid(String uid) {
        if (this.is_active) return;
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

    public GameState convertSnapshotToGameState(@NonNull DataSnapshot snapshot) {
        GameState tmp = snapshot.getValue(GameState.class);
        GameState game = new GameState(tmp.game_name);

        // Converting players
        Node iterator = tmp.players.head;
        while (iterator != null) {
            HashMap<String, Object> convert = (HashMap<String, Object>) iterator.obj;
            Player player = new Player();
            player.userName = convert.get("userName").toString();
            player.uid = convert.get("uid").toString();

            CardsSet card_set = (CardsSet) convert.get("cards");
            Node cards_iterator = (Node) card_set.cards.head;
            player.cards = null;

            game.players.addNode(player);
            iterator = iterator.next;
        }

        // Converting currentPlayer
        /*
        HashMap<String, Object> convert = (HashMap<String, Object>) tmp.currentPlayer.obj;
        String current_player_uid = convert.get("uid").toString();
        while (iterator != null) {
            if (((Player) iterator.obj).uid.equals(current_player_uid)) {
                game.currentPlayer = iterator;
                break;
            }
            iterator = iterator.next;
        }
        */
        // Converting dict
        game.dict = null; //game.dict.convertSnapshotToDict(snapshot.child("dict"));
        game.numOfPlayers = tmp.numOfPlayers;
        game.is_active = tmp.is_active;
        game.maxPlayersNum = tmp.maxPlayersNum;
        return game;
    }
}
