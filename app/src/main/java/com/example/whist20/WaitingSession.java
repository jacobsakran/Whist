package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WaitingSession extends AppCompatActivity {
    private GameState game;
    private TextView player1, player2, player3, player4;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_session);
        game = null;
        exit = (Button) findViewById(R.id.exitWaitingSession);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.removeUserByUid(HomePage.user.uid);
                if (game.players_id.obj == null) game = null;

                FirebaseDatabase.getInstance().getReference("WaitingSessions").child(HomePage.user.current_game_id).setValue(game)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(HomePage.user.uid)
                                            .child("current_game_id").setValue("");
                                    startActivity(new Intent(WaitingSession.this, HomePage.class));
                                } else Toast.makeText(WaitingSession.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        player3 = (TextView) findViewById(R.id.player3);
        player4 = (TextView) findViewById(R.id.player4);

        String game_id = HomePage.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = snapshot.getValue(GameState.class);

                if (game == null) {
                    snapshot.getRef().removeEventListener(this);
                    return;
                }

                Node players = game.players_usernames;
                if (players != null) {
                    player1.setText((String) players.obj);
                    players = players.next;
                } else player1.setText("Waiting For Player 1...");

                if (players != null) {
                    player2.setText((String) players.obj);
                    players = players.next;
                } else player2.setText("Waiting For Player 2...");

                if (players != null) {
                    player3.setText((String) players.obj);
                    players = players.next;
                } else player3.setText("Waiting For Player 3...");

                if (players != null) player4.setText((String) players.obj);
                else  player4.setText("Waiting For Player 4...");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}