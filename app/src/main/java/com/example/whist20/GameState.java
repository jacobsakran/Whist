package com.example.whist20;

import android.widget.Toast;

import java.util.Vector;

public class GameState {
    public Node players_id; // the player in 0 sits in front of the player 2, and the player in 1 sits in front of the player in 3.
    public Node players_usernames;
    public Node players_cards; // size of 4, each node has a node as an object i.e. list of lists such that each inner list is size of 13 (4x13).
    public Team team1, team2;
    public Round last_round;
    public Suit trump;
    public int current_index;
    public int num_of_players;

    public GameState() {
        this.players_id = new Node();
        this.players_cards = new Node();
        this.players_usernames = new Node();
        this.num_of_players = 0;
        this.team1 = null;
        this.team2 = null;
        this.last_round = new Round();
        this.current_index = 0;
        this.trump = Suit.Hearts;

    }

    public String currentPlayerTurn() {
        Node iterator = players_id;
        int counter = 0;
        while (iterator != null) {
            if (counter == this.current_index) break;
            iterator = iterator.next;
        }
        if (iterator == null) return "";
        return (String) iterator.obj;
    }

    public String nextPlayerTurn() {
        this.current_index = (current_index + 1) % 4;
        return this.currentPlayerTurn();
    }
}
