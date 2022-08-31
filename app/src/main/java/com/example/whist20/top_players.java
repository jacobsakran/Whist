package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class top_players extends AppCompatActivity {

    private Button back;
    private User user = Profile.user;
    private User first = null;
    private User sec = null;
    private User third = null;
    private Node friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        back = (Button) findViewById(R.id.back_top_players);

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
                    User friend = snapshot.getValue(User.class);
                    assert friend != null;
                    if (first != null) {
                        
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            iterator = iterator.next;
        }

        if (Objects.equals(user.uid, first.uid)) {
            Toast.makeText(top_players.this, "congrats you got the first place!", Toast.LENGTH_LONG).show();

        }
        if (Objects.equals(user.uid, sec.uid)) {
            Toast.makeText(top_players.this, "congrats you got the second place!", Toast.LENGTH_LONG).show();

        }
        if (Objects.equals(user.uid, third.uid)) {
            Toast.makeText(top_players.this, "congrats you got the third place!", Toast.LENGTH_LONG).show();
        }

    }
}