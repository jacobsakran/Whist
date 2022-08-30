package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Button timer_button, requestsProfileButton, back;
    private TextView profile_rank_text, profile_budget_text , numOfRequestes;
    private ScrollView requestLayout;
    private ImageView star, coin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_profile);
        star = (ImageView) findViewById(R.id.star);
        coin = (ImageView) findViewById(R.id.coin);
        play_button = (Button) findViewById(R.id.Play);
        back = (Button) findViewById(R.id.back_profile);
        log_out_button = (Button) findViewById(R.id.log_out_profile);
        profile_budget_text = (TextView) findViewById(R.id.profile_money);
        numOfRequestes = (TextView) findViewById(R.id.numOfRequestes);
        profile_rank_text = (TextView) findViewById(R.id.profile_rank);
        timer_button = (Button) findViewById(R.id.timerButton);
        requestsProfileButton = (Button) findViewById(R.id.requestsProfile);
        requestLayout = (ScrollView) findViewById(R.id.requestLayoutProfile);
        requestLayout.setVisibility(View.INVISIBLE);

        requestsProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                viewFriendsRequests();
            }
        });
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                viewProfilePage();
            }
        });
        timer_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                //Toast.makeText(Profile.this, String.valueOf(user.savedTime), Toast.LENGTH_LONG).show();
                if (time >= (user.savedTime + 24 * 60 * 60 * 1000)){
                    //Toast.makeText(Profile.this, String.valueOf(time - (user.savedTime + 24 * 60 * 60 * 1000)), Toast.LENGTH_LONG).show();
                    user.savedTime = time;
                    user.money += 1000;
                    FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
                            .child("money").setValue(user.money);
                    FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
                            .child("savedTime").setValue(user.savedTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Profile.this, "you earned 1000 coins", Toast.LENGTH_LONG).show();
                        }
                    });

                    profile_budget_text.setText("Budget: " + String.valueOf(user.money));
                }
                else {
                    Toast.makeText(Profile.this, "24 hours not passed to get your daily money", Toast.LENGTH_LONG).show();
                }
            }
        });

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
        star.setVisibility(View.INVISIBLE);
        coin.setVisibility(View.INVISIBLE);
        play_button.setVisibility(View.INVISIBLE);
        log_out_button.setVisibility(View.INVISIBLE);
        profile_budget_text.setVisibility(View.INVISIBLE);
        profile_rank_text.setVisibility(View.INVISIBLE);
        timer_button.setVisibility(View.INVISIBLE);
        requestLayout.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
        requestsProfileButton.setVisibility(View.INVISIBLE);
        numOfRequestes.setVisibility(View.INVISIBLE);
        FirebaseUser firebase_user = FirebaseAuth.getInstance().getCurrentUser();
        assert firebase_user != null;
        String id = firebase_user.getUid();

        FirebaseDatabase.getInstance().getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);

                if (user != null) {
                    //Toast.makeText(Profile.this, String.valueOf(user.savedTime), Toast.LENGTH_LONG).show();
                    star.setVisibility(View.VISIBLE);
                    coin.setVisibility(View.VISIBLE);
                    profile_rank_text.setVisibility(View.VISIBLE);
                    profile_budget_text.setVisibility(View.VISIBLE);
                    log_out_button.setVisibility(View.VISIBLE);
                    play_button.setVisibility(View.VISIBLE);
                    timer_button.setVisibility(View.VISIBLE);
                    profile_rank_text.setText("Rank: "+ String.valueOf(user.rank));
                    profile_budget_text.setText("Budget: " + String.valueOf(user.money));
                    requestsProfileButton.setVisibility(View.VISIBLE);
                    if (user.requested != null){
                        numOfRequestes.setVisibility(View.VISIBLE);
                        numOfRequestes.setText("1");
                    }

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


    private void viewFriendsRequests() {
        requestLayout.setVisibility(View.VISIBLE);
        timer_button.setVisibility(View.INVISIBLE);
        log_out_button.setVisibility(View.INVISIBLE);
        coin.setVisibility(View.INVISIBLE);
        star.setVisibility(View.INVISIBLE);
        play_button.setVisibility(View.INVISIBLE);
        requestsProfileButton.setVisibility(View.INVISIBLE);
        back.setVisibility(View.VISIBLE);
        Node friendRequests = user.requested;
        requestLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        requestLayout.setLayoutParams(layoutParams);

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);

        requestLayout.addView(linearLayout);
        //Toast.makeText(Profile.this, (String)friendRequests.obj, Toast.LENGTH_LONG).show();

        while (friendRequests != null){

            String uid = (String) friendRequests.obj;
            FirebaseDatabase.getInstance().getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User requestedUser = snapshot.getValue(User.class);
                    assert requestedUser != null;
                    TextView text = new TextView(Profile.this);
                    text.setTextSize(20);
                    //text.setHint((String) game.game_name);
                    String text_to_show = "Add " + (String) requestedUser.username;
                    text.setText(text_to_show);
                    text.setTextColor(Color.BLACK);
                    linearLayout.addView(text);
                    //Toast.makeText(Profile.this, text_to_show, Toast.LENGTH_LONG).show();

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (requestedUser.friends == null) requestedUser.friends = new Node();
                            requestedUser.friends.addValue(uid);
                            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                                    .child("friends").setValue(requestedUser.friends);

                            if (user.friends == null) user.friends = new Node();
                            user.friends.addValue(requestedUser.uid);
                            FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
                                    .child("friends").setValue(user.friends);
                            // TODO remove from requested
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            friendRequests = friendRequests.next;
        }
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
                            GameState game = GameState.convertSnapshotToGameState(snapshot.child(user.current_game_id));
                            if (game.numOfPlayers <= 1) {
                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(user.current_game_id).setValue(null);
                                return;
                            }
                            FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(user.current_game_id).child(user.uid).setValue(null);
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