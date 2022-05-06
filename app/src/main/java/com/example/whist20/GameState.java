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
    public String game_name; // this is name unique i.e., is used as a an ID for each game.
    public boolean is_active;

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
        this.is_active = false;
        this.game_name = "";
    }

    public String getGameId() {
        if (players_id.obj == null) return "";
        return (String) players_id.obj;
    }

    public void removeUserByUid(String uid) {
        if (this.is_active || this.players_id == null) return;
        if (this.players_id.obj == null) return;

        if (players_id.obj.equals(uid)) {
            this.players_id = this.players_id.next;
            this.players_usernames = this.players_usernames.next;
            if (this.players_id == null) this.players_id = new Node();
            if (this.players_usernames == null) this.players_usernames = new Node();
            return;
        }

        Node iterator1 = this.players_id, iterator2 = this.players_usernames;
        while (iterator1.next != null) {
            if (iterator1.next.obj != null && iterator1.next.obj.equals(uid)) break;
            iterator1 = iterator1.next;
            iterator2 = iterator2.next;
        }
        assert iterator1.next != null;
        iterator1.next = iterator1.next.next;
        iterator2.next = iterator2.next.next;
    }

    public String currentPlayerTurn() {
        Node iterator = players_id;
        int counter = 0;
        while (iterator != null) {
            if (counter == this.current_index) break;
            iterator = iterator.next;
            counter = counter + 1;
        }
        if (iterator == null) return "";
        return (String) iterator.obj;
    }

    public String nextPlayerTurn() {
        this.current_index = (current_index + 1) % 4;
        return this.currentPlayerTurn();
    }
}
