package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.HashMap;
import java.util.Vector;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    private Button log_out, create_game, refresh, back;
    private DatabaseReference reference;
    private ProgressBar progress_bar;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_home_page);
        progress_bar = (ProgressBar) findViewById(R.id.homePageProgressBar);
        layout = (LinearLayout) findViewById(R.id.homePageLinearLayout);

        refresh = (Button) findViewById(R.id.homePageRefreshButton);
        refresh.setOnClickListener(this);

        log_out = (Button) findViewById(R.id.logOutButton);
        log_out.setOnClickListener(this);

        create_game = (Button) findViewById(R.id.createGameButton);
        create_game.setOnClickListener(this);

        back = (Button) findViewById(R.id.back_home_page);
        back.setOnClickListener(this);
        viewHomePage();
    }

    @Override
    public void onClick(View view) {
        if (!Profile.user.current_game_id.equals("")) ForwardUserToCurrentGame();

        switch (view.getId()) {
            case R.id.logOutButton:
                logOut();
                break;
            case R.id.createGameButton:
                startActivity(new Intent(HomePage.this, CreateGame.class));
                break;
            case R.id.back_home_page:
                startActivity(new Intent(HomePage.this, Profile.class));
                break;
            case R.id.homePageRefreshButton:
                viewHomePage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void viewGameServers() {
        FirebaseDatabase.getInstance().getReference("WaitingSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layout.removeAllViews();
                for (DataSnapshot child : snapshot.getChildren()) {
                    GameState game = child.getValue(GameState.class);
                    assert game != null;

                    TextView text = new TextView(HomePage.this);
                    text.setTextSize(20);
                    text.setHint((String) game.game_name);
                    String text_to_show = "Click to join " + (String) game.game_name + " game (" + game.game_money +")";
                    text.setText(text_to_show);
                    text.setTextColor(Color.WHITE);
                    layout.addView(text);

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!Profile.user.current_game_id.equals("")) {
                                String msg = "You are already in this game, exit to join a new one";
                                Toast.makeText(HomePage.this, msg, Toast.LENGTH_LONG).show();
                                ForwardUserToCurrentGame();
                                return;
                            }
                            if (game.game_money > Profile.user.money) {
                                String msg = "You can not afford this game.";
                                Toast.makeText(HomePage.this, msg, Toast.LENGTH_LONG).show();
                                return;
                            }

                            //String game_name = ((TextView) view).getHint().toString().trim();
                            Profile.user.current_game_id = game.game_name;
                            //user.money -= game.game_money;
                            FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game.game_name)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            GameState game = snapshot.getValue(GameState.class);
                                            if (game == null) {
                                                Toast.makeText(HomePage.this, "The game is unavailable, refresh for better options", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            game.addPlayer(new Player(Profile.user.uid, Profile.user.username, Profile.user.money));
                                            FirebaseDatabase.getInstance().getReference("WaitingSessions").child(Profile.user.current_game_id)
                                                    .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(Profile.user.uid).child("current_game_id").setValue(Profile.user.current_game_id);
                                                        startActivity(new Intent(HomePage.this, WaitingSession.class));
                                                    }
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
    }

    private void ForwardUserToCurrentGame() {
        FirebaseDatabase.getInstance().getReference("WaitingSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(Profile.user.current_game_id)) {
                    startActivity(new Intent(HomePage.this, WaitingSession.class));
                    return;
                }
                FirebaseDatabase.getInstance().getReference("ActiveGames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(Profile.user.current_game_id)) {
                            startActivity(new Intent(HomePage.this, InGame.class));
                            return;
                        }

                        Profile.user.current_game_id = "";
                        FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).child("current_game_id").setValue("");
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

    private void viewHomePage() {
        progress_bar.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.INVISIBLE);
        log_out.setVisibility(View.INVISIBLE);
        create_game.setVisibility(View.INVISIBLE);

        FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        assert firebase_user != null;
        String id = firebase_user.getUid();

        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Profile.user != null) {
                    progress_bar.setVisibility(View.INVISIBLE);
                    create_game.setVisibility(View.VISIBLE);
                    log_out.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);

                    if (!Profile.user.current_game_id.equals("")) ForwardUserToCurrentGame();
                    viewGameServers();
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