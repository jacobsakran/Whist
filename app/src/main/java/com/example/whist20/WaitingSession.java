package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import java.util.Objects;

public class WaitingSession extends AppCompatActivity {
    private GameState game;
    private TextView player1, player2, player3, player4;
    private ProgressBar progressBar;
    private Button exit;
    private ScrollView scrollViewInvites;
    private View background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_waiting_session);
        game = null;
        exit = (Button) findViewById(R.id.exitWaitingSession);
        player1 = (TextView) findViewById(R.id.player1);
        player2 = (TextView) findViewById(R.id.player2);
        player3 = (TextView) findViewById(R.id.player3);
        player4 = (TextView) findViewById(R.id.player4);
        scrollViewInvites = (ScrollView) findViewById(R.id.scrollViewInvites);
        scrollViewInvites.setVisibility(View.INVISIBLE);
        background = findViewById(R.id.waitingSessionBackground);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollViewInvites.setVisibility(View.INVISIBLE);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.waitingSessionProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.removePlayerByUid(Profile.user.uid);
                if (game.numOfPlayers == 1) game = null;

                FirebaseDatabase.getInstance().getReference("WaitingSessions").child(Profile.user.current_game_id).setValue(game)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid)
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
        if (!Profile.user.uid.equals(((Player) game.players.head.next.obj).uid)) return;
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).setValue(game)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) Toast.makeText(WaitingSession.this, "Failed to start game", Toast.LENGTH_LONG).show();

                                    FirebaseDatabase.getInstance().getReference("invitings").child(Profile.user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            DataSnapshot invited = snapshot;
                                            FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot child : snapshot.getChildren()) {
                                                        if (!invited.hasChild(Objects.requireNonNull(child.getKey()))) continue;
                                                        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(child.getKey()))
                                                                .child("invites").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (!snapshot.exists()) return;
                                                                        Node tmp = snapshot.getValue(Node.class);
                                                                        assert tmp != null;
                                                                        tmp.removeByValue(Profile.user.username);
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(child.getKey()))
                                                                                .child("invites").setValue(tmp);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            FirebaseDatabase.getInstance().getReference("invitings").child(Profile.user.uid).setValue(null);
                                            startActivity(new Intent(WaitingSession.this, InGame.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                

                                });
                            }

        });

    }

    protected void ShowPage() {
        String game_id = Profile.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("WaitingSessions").child(game_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = GameState.convertSnapshotToGameState(snapshot);

                if (game == null) {
                    snapshot.getRef().removeEventListener(this);
                    progressBar.setVisibility(View.INVISIBLE);
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
                    StartGame();
                    return;
                } else player3.setText("Waiting For Player 3...");

                if (players != null) {
                    player4.setText(((Player) players.obj).userName);
                    StartGame();
                }
                else  player4.setText("Waiting For Player 4...");

                FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;
                        Node friends = snapshot.getValue(Node.class);
                        while (friends != null) {
                            TextView friend_view = new TextView(WaitingSession.this);
                            scrollViewInvites.addView(friend_view);
                            friend_view.setTextSize(20);
                            friend_view.setHint((String) friends.obj);
                            FirebaseDatabase.getInstance().getReference("Users").child((String) friends.obj).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    friend_view.setText(snapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            friend_view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(friend_view.getHint()
                                            .toString().trim()).child("invites").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()) return;
                                            Node tmp = snapshot.getValue(Node.class);
                                            assert tmp != null;
                                            tmp.addValue(Profile.user.username);
                                            FirebaseDatabase.getInstance().getReference("Users").child(friend_view.getHint().toString().trim())
                                                    .child("invites").setValue(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference("invitings").child(Profile.user.uid).child(friend_view.getHint().toString().trim()).setValue("");
                                                    Toast.makeText(WaitingSession.this, friend_view.getText().toString().trim() + " has been invited", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                            friends = friends.next;
                        }
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