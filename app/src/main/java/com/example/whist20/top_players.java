package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class top_players extends AppCompatActivity {

    private Button back;
    private User user = Profile.user;
    private User first = null;
    private User sec = null;
    private User third = null;
    private TextView first_text, sec_text, third_text;
    private Node friends;
    private Lock mutex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        back = (Button) findViewById(R.id.back_top_players);
        first_text = (TextView) findViewById(R.id.firstPlayer);
        sec_text = (TextView) findViewById(R.id.secondPlayer);
        third_text = (TextView) findViewById(R.id.thirdPlayer);
        mutex = new ReentrantLock();
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(top_players.this, Profile.class));
            }
        });
        friends = user.friends;
        Node iterator = friends;
        while (iterator != null) {
            if (iterator.obj == null) break;
            FirebaseDatabase.getInstance().getReference("Users").child((String) iterator.obj).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mutex.lock();
                    try {
                        User friend = snapshot.getValue(User.class);
                        assert friend != null;
                        if (first == null) {
                            first = new User();
                            first.username = friend.username;
                            first.uid = friend.uid;
                            first.rank = friend.rank;
                        }
                        if (sec == null) {
                            sec = new User();
                            sec.username = friend.username;
                            sec.uid = friend.uid;
                            sec.rank = friend.rank;
                        }
                        if (third == null) {
                            third = new User();
                            third.username = friend.username;
                            third.uid = friend.uid;
                            third.rank = friend.rank;
                        }
                        if (friend.rank > first.rank) {
                            first.username = friend.username;
                            first.uid = friend.uid;
                            first.rank = friend.rank;
                        } else if (friend.rank > sec.rank) {
                            sec.username = friend.username;
                            sec.uid = friend.uid;
                            sec.rank = friend.rank;
                        } else if (friend.rank > third.rank) {
                            third.username = friend.username;
                            third.uid = friend.uid;
                            third.rank = friend.rank;
                        }
                        first_text.setText(first.username);
                        sec_text.setText(sec.username);
                        third_text.setText(third.username);
                    } finally {
                        mutex.unlock();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            iterator = iterator.next;
        }

    }
}