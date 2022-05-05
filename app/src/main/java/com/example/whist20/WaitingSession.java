package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingSession extends AppCompatActivity {
    private GameState game;
    private TextView player1, player2, player3, player4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_session);

        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        player3 = (TextView) findViewById(R.id.player3);
        player4 = (TextView) findViewById(R.id.player4);

        String game_id = HomePage.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = snapshot.getValue(GameState.class);
                if (game == null) {
                    Toast.makeText(WaitingSession.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    return;
                }

                Node players = game.players_usernames;
                if (players != null) {
                    player1.setText((String) players.obj);
                    players = players.next;
                }
                if (players != null) {
                    player2.setText((String) players.obj);
                    players = players.next;
                }
                if (players != null) {
                    player3.setText((String) players.obj);
                    players = players.next;
                }
                if (players != null) player4.setText((String) players.obj);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}