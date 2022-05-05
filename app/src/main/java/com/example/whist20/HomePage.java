package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Vector;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    public static User user = null;

    private Button log_out, create_game;
    private DatabaseReference reference;
    private String id;
    private ProgressBar progress_bar;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        progress_bar = (ProgressBar) findViewById(R.id.homePageProgressBar);
        progress_bar.setVisibility(View.VISIBLE);

        layout = (LinearLayout) findViewById(R.id.homePageLinearLayout);

        log_out = (Button) findViewById(R.id.logOutButton);
        log_out.setOnClickListener(this);

        create_game = (Button) findViewById(R.id.createGameButton);
        create_game.setOnClickListener(this);

        viewProfile();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logOutButton:
                logOut();
                break;
            case R.id.createGameButton:
                startActivity(new Intent(HomePage.this, CreateGame.class));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void viewProfile() {
        FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        id = firebase_user.getUid();

        FirebaseDatabase.getInstance().getReference("WaitingSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    GameState game = child.getValue(GameState.class);
                    TextView text = new TextView(HomePage.this);
                    text.setTextSize(20);

                    text.setText((String) game.players_id.obj);
                    layout.addView(text);
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String host_id = ((TextView) view).getText().toString().trim();
                            user.current_game_id = host_id;
                            FirebaseDatabase.getInstance().getReference("WaitingSessions").child(host_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            GameState game = snapshot.getValue(GameState.class);
                                            game.players_usernames.addValue(user.username);
                                            game.players_id.addValue(user.uid);
                                            FirebaseDatabase.getInstance().getReference("WaitingSessions").child(user.current_game_id)
                                                    .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                        startActivity(new Intent(HomePage.this, WaitingSession.class));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    progress_bar.setVisibility(View.INVISIBLE);
                    // the logged in user is loaded into the "user" parameter.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomePage.this, MainActivity.class));
    }
}