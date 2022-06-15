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

    public static GameState convertSnapshotToGameState(@NonNull DataSnapshot snapshot) {
        if (!snapshot.exists()) return null;
        GameState game = new GameState(snapshot.child("game_name").getValue(String.class));

        // Converting players
        DataSnapshot players_iterator =  snapshot.child("players").child("head");
        while (players_iterator.exists()) {
            Player player = players_iterator.child("obj").getValue(Player.class);
            assert player != null;
            player.cards = new CardsSet();

            DataSnapshot cards_iterator = players_iterator.child("obj").child("cards").child("cards").child("head");
            while (cards_iterator.exists()) {
                Card card = cards_iterator.child("obj").getValue(Card.class);
                player.cards.addCard(card);
                cards_iterator = cards_iterator.child("next");
            }

            game.players.addNode(player);
            players_iterator = players_iterator.child("next");
        }

        // Converting currentPlayer
        String current_player_uid = snapshot.child("currentPlayer").child("obj").child("uid").getValue(String.class);
        Node iterator = game.players.head;
        while (iterator != null) {
            if (((Player) iterator.obj).uid.equals(current_player_uid)) {
                game.currentPlayer = iterator;
                break;
            }
            iterator = iterator.next;
        }

        // Converting dict
        game.dict = Dict.convertSnapshotToDict(snapshot.child("dict"));
        game.is_active = snapshot.child("is_active").getValue(boolean.class);
        game.maxPlayersNum = snapshot.child("maxPlayersNum").getValue(int.class);
        return game;
    }
}
