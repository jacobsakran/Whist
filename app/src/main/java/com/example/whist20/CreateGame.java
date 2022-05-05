package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        GameState game = new GameState();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        game.players_id.obj = uid;
        game.players_usernames.obj = HomePage.user.username;
        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(uid)
                .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("current_game_id").setValue(uid);
                    HomePage.user.current_game_id = uid;
                    startActivity(new Intent(CreateGame.this, WaitingSession.class));
                }
                else Toast.makeText(CreateGame.this, "Failed to create game, try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}