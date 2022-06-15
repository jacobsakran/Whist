package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ProgressBar progressBar;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_session);
        game = null;
        exit = (Button) findViewById(R.id.exitWaitingSession);
        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        player3 = (TextView) findViewById(R.id.player3);
        player4 = (TextView) findViewById(R.id.player4);
        progressBar = (ProgressBar) findViewById(R.id.waitingSessionProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.removePlayerByUid(HomePage.user.uid);
                if (game.players.head.obj == null) game = null;

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

        ShowPage();
    }

    protected void StartGame() {
        progressBar.setVisibility(View.VISIBLE);

        if (!HomePage.user.uid.equals(((Player) game.players.head.obj).uid)) {
            FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(WaitingSession.this, InGame.class));
                                snapshot.getRef().removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            return;
        }

        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game.game_name).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(WaitingSession.this, "Failed to start game", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).setValue(game)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(WaitingSession.this, InGame.class));
                                }
                                else Toast.makeText(WaitingSession.this, "Failed to start game", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    protected void ShowPage() {
        String game_id = HomePage.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = GameState.convertSnapshotToGameState(snapshot);

                if (game == null) {
                    snapshot.getRef().removeEventListener(this);
                    startActivity(new Intent(WaitingSession.this, HomePage.class));
                    return;
                }

                Node players = game.players.head;
                if (players != null) {
                    player1.setText(((Player) players.obj).userName);
                    players = players.next;
                } else player1.setText("Waiting For Player 1...");

                if (players != null) {
                    player2.setText(((Player) players.obj).userName);
                    players = players.next;
                } else player2.setText("Waiting For Player 2...");

                if (players != null) {
                    player3.setText(((Player) players.obj).userName);
                    players = players.next;
                } else player3.setText("Waiting For Player 3...");

                if (players != null) {
                    player4.setText(((Player) players.obj).userName);
                    snapshot.getRef().removeEventListener(this);
                    StartGame();
                }
                else  player4.setText("Waiting For Player 4...");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}