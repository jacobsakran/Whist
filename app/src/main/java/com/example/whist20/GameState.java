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
    public int game_money;

    public GameState() {
    }

    public GameState(String game_name, int game_money) {
        this.players = new MyList();
        this.currentPlayer = new Node();
        this.numOfPlayers = 0;
        this.maxPlayersNum = 4;
        this.is_active = false;
        this.game_name = game_name;
        this.dict = new Dict();
        this.game_money = game_money;
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
        this.numOfPlayers = this.numOfPlayers - 1;
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
        GameState game = new GameState(snapshot.child("game_name").getValue(String.class), snapshot.child("game_money").getValue(int.class));

        // Converting players
        DataSnapshot players_iterator =  snapshot.child("players").child("head");
        boolean is_dealer = true;
        Dealer dealer = null;
        while (players_iterator.exists()) {
            if (is_dealer) {
                dealer = players_iterator.child("obj").getValue(Dealer.class);
                assert dealer != null;
                dealer.cards = new CardsSet();
            }

            Player player = players_iterator.child("obj").getValue(Player.class);
            assert player != null;
            player.cards = new CardsSet();

            DataSnapshot cards_iterator = players_iterator.child("obj").child("cards").child("cards").child("head");
            while (cards_iterator.exists()) {
                Card card = cards_iterator.child("obj").getValue(Card.class);
                player.cards.addCard(card);
                cards_iterator = cards_iterator.child("next");
            }

            if (is_dealer) {
                dealer.cards = player.cards;
                game.addPlayer(dealer);
            }
            else game.addPlayer(player);

            is_dealer = false;
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
