package com.example.whist20;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;

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
    public boolean first_open;
    public boolean is_private;
    public int num_of_ready_players;
    public int game_money;

    public GameState() {
    }

    public GameState(String game_name, int game_money) {
        this.players = new MyList();
        this.currentPlayer = new Node();
        this.numOfPlayers = 0;
        this.maxPlayersNum = 4;
        this.is_active = false;
        this.is_private = false;
        this.num_of_ready_players = 0;
        this.game_name = game_name;
        this.dict = new Dict();
        this.first_open = true;
        this.game_money = game_money;
    }

    public void addPlayer(Player player) {
        this.players.addNode(player);
        this.numOfPlayers = this.numOfPlayers + 1;
        this.num_of_ready_players += player.is_ready ? 1 : 0;
        this.currentPlayer = this.players.head.next;
    }

    public void clearCards() {
        Node iterator = players.head;

        while (iterator != null) {
            if (iterator.obj == null) break;
            Player my_player = (Player) iterator.obj;
            my_player.clearCardsSet();
            iterator = iterator.next;
        }
    }

    public Player findPlayerByUid(String uid) {
        Player to_find = new Player();
        to_find.uid = uid;
        return (Player) this.players.findByValue(to_find);
    }

    public void removePlayerByUid(String uid) {
        if (this.is_active) return;
        Player to_remove = new Player();
        to_remove.uid = uid;
        this.num_of_ready_players -= ((Player) this.players.findByValue(to_remove)).is_ready ? 1 : 0;
        this.players.removeByValue(to_remove);
        this.currentPlayer = this.players.head.next;
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

    public void startRound() {
        if (is_active) return;
        Node iterator = players.head;

        while (iterator != null) {
            if (iterator.obj == null) break;
            Player my_player = (Player) iterator.obj;
            my_player.cards.addCard(this.dict.randomCard());
            my_player.cards.addCard(this.dict.randomCard());
            iterator = iterator.next;
        }

        is_active = true;
    }

    public void restartGame() {
        // TODO: update the players' money in the FireBase
        int dealer_sum = ((Dealer) this.players.head.obj).cards.sum();
        Node iterator = this.players.head.next;
        while (iterator != null) {
            if (iterator.obj == null) break;
            Player player = (Player) iterator.obj;
            if (player.cards.sum() >= dealer_sum && player.cards.sum() > -1) {
                if (player.isDouble) FirebaseDatabase.getInstance().getReference("Users").child(player.uid).child("money").setValue(player.budget + game_money * 4);
                else FirebaseDatabase.getInstance().getReference("Users").child(player.uid).child("money").setValue(player.budget + game_money * 2);

                player.budget += player.isDouble ? game_money * 4 : game_money * 2;
                player.streak++;
                player.num_of_rounds++;
                player.num_of_wins++;
                player.rank += player.streak / 2 + ((int) ((player.num_of_wins / player.num_of_rounds) * 2)) * 2;
                player.rank = Math.max(player.rank, 0);
                FirebaseDatabase.getInstance().getReference("Users").child(player.uid).child("rank").setValue(player.rank);
            } else FirebaseDatabase.getInstance().getReference("Users").child(player.uid).child("rank").setValue(Math.max(player.rank - 3, 0));


            iterator = iterator.next;
            player.is_ready = false;
            player.isDouble = false;
        }
        this.currentPlayer = this.players.head.next;
        this.num_of_ready_players = 1;
        this.is_active = false;
        this.first_open = true;
        this.clearCards();
        this.dict = new Dict();
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
        game.first_open = snapshot.child("first_open").getValue(boolean.class);
        game.num_of_ready_players = snapshot.child("num_of_ready_players").getValue(int.class);
        game.maxPlayersNum = snapshot.child("maxPlayersNum").getValue(int.class);
        return game;
    }

    public static GameState convertMutableDataToGameState(@NonNull MutableData currentData){
        if (!currentData.hasChildren()) return null;
        GameState game = new GameState(currentData.child("game_name").getValue(String.class), currentData.child("game_money").getValue(int.class));

        // Converting players
        MutableData players_iterator =  currentData.child("players").child("head");
        boolean is_dealer = true;
        Dealer dealer = null;
        while (players_iterator.hasChildren()) {
            if (is_dealer) {
                dealer = players_iterator.child("obj").getValue(Dealer.class);
                assert dealer != null;
                dealer.cards = new CardsSet();
            }

            Player player = players_iterator.child("obj").getValue(Player.class);
            assert player != null;
            player.cards = new CardsSet();

            MutableData cards_iterator = players_iterator.child("obj").child("cards").child("cards").child("head");
            while (cards_iterator.hasChildren()) {
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
        String current_player_uid = currentData.child("currentPlayer").child("obj").child("uid").getValue(String.class);
        Node iterator = game.players.head;
        while (iterator != null) {
            if (((Player) iterator.obj).uid.equals(current_player_uid)) {
                game.currentPlayer = iterator;
                break;
            }
            iterator = iterator.next;
        }

        // Converting dict
        game.dict = Dict.convertMutableDataToDict(currentData.child("dict"));
        game.is_active = currentData.child("is_active").getValue(boolean.class);
        game.first_open = currentData.child("first_open").getValue(boolean.class);
        game.num_of_ready_players = currentData.child("num_of_ready_players").getValue(int.class);
        game.maxPlayersNum = currentData.child("maxPlayersNum").getValue(int.class);
        return game;
    }
}
