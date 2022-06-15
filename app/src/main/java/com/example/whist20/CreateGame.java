package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateGame extends AppCompatActivity implements View.OnClickListener {
    private Button create_game;
    private EditText game_name_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        game_name_edit_text = (EditText) findViewById(R.id.createGameName);
        create_game = (Button) findViewById(R.id.createGameCreateButton);
        create_game.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createGameCreateButton:
                createGame();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void createGame() {
        String game_name = game_name_edit_text.getText().toString().trim();
        FirebaseDatabase.getInstance().getReference("WaitingSessions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(game_name)) {
                            game_name_edit_text.setError("Game name already exists");
                            return;
                        }

                        FirebaseDatabase.getInstance().getReference("ActiveGames").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(game_name)) {
                                    game_name_edit_text.setError("Game name already exists");
                                    return;
                                }

                                GameState game = new GameState(game_name);
                                game.addPlayer(new Dealer(game_name, "Dealer"));
                                game.addPlayer(new Player(HomePage.user.uid, HomePage.user.username));
                                FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_name)
                                        .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference("Users").child(HomePage.user.uid)
                                                            .child("current_game_id").setValue(game_name);
                                                    HomePage.user.current_game_id = game_name;
                                                    startActivity(new Intent(CreateGame.this, WaitingSession.class));
                                                } else Toast.makeText(CreateGame.this, "Failed to create game, try again", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

