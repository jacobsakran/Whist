package com.example.whist20;

public class Round {
    public Node players_turns;
    public String current_players_turn;
    public int turn_number;

    public Round() {
        this.current_players_turn = "";
        this.players_turns = new Node();
        this.turn_number = 0;
    }
}
