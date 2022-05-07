package com.example.whist20;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Vector;

public class User {
    public String email, username;
    public Node friends;
    public String current_game_id;
    public String uid;

    public User() {

    }

    public User(String email, String username, String uid) {
        this.email = email;
        this.username = username;
        this.friends = new Node();
        this.uid = uid;
        this.current_game_id = "";
    }

};

