package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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
    private EditText game_money_edit_text;
    private Switch isPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_create_game);
        game_name_edit_text = (EditText) findViewById(R.id.createGameName);
        game_money_edit_text = (EditText) findViewById(R.id.createGameMoney);
        isPrivate = (Switch) findViewById(R.id.switchPrivate);

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
        int game_money = Integer.parseInt(game_money_edit_text.getText().toString().trim());
        if (game_money > Profile.user.money) {
            game_money_edit_text.setError("You can not afford this game.");
            return;
        }
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

                                GameState game = new GameState(game_name, game_money);
                                game.addPlayer(new Dealer("", "Dealer", 0));
                                game.addPlayer(new Player(Profile.user.uid, Profile.user.username, Profile.user.money, Profile.user.rank));
                                if (isPrivate.isChecked()) game.is_private = true;
                                FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_name)
                                        .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid)
                                                            .child("current_game_id").setValue(game_name);
                                                    Profile.user.current_game_id = game_name;
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

