package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    public static User user = null;
    private Button play_button;
    private Button log_out_button;
    private TextView profile_name_text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        play_button = (Button) findViewById(R.id.Play);
        log_out_button = (Button) findViewById(R.id.log_out_profile);
        profile_name_text = (TextView) findViewById(R.id.profile_name);

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, HomePage.class));
            }
        });

        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });
        viewProfilePage();
    }

    private void viewProfilePage() {
        //progress_bar.setVisibility(View.VISIBLE);
        play_button.setVisibility(View.INVISIBLE);
        log_out_button.setVisibility(View.INVISIBLE);
        profile_name_text.setVisibility(View.INVISIBLE);

        FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
        assert firebase_user != null;
        String id = firebase_user.getUid();

        FirebaseDatabase.getInstance().getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    //progress_bar.setVisibility(View.INVISIBLE);
                    profile_name_text.setVisibility(View.VISIBLE);
                    log_out_button.setVisibility(View.VISIBLE);
                    play_button.setVisibility(View.VISIBLE);
                    //refresh.setVisibility(View.VISIBLE);

                    if (!user.current_game_id.equals("")) ForwardUserToCurrentGame();
                    // the logged in user is loaded into the "user" parameter.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void ForwardUserToCurrentGame() {
        FirebaseDatabase.getInstance().getReference("WaitingSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(user.current_game_id)) {
                    startActivity(new Intent(Profile.this, WaitingSession.class));
                    return;
                }
                FirebaseDatabase.getInstance().getReference("ActiveGames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(user.current_game_id)) {
                            startActivity(new Intent(Profile.this, InGame.class));
                            return;
                        }

                        user.current_game_id = "";
                        FirebaseDatabase.getInstance().getReference("Users").child(user.uid).child("current_game_id").setValue("");
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