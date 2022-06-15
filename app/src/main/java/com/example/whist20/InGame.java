package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InGame extends AppCompatActivity {
    private ImageView[] player1_cards;
    private ImageView[] player2_cards;
    private ImageView[] player3_cards;
    private ImageView[] player4_cards;

    private TextView myUserName;
    private TextView userName2;
    private TextView userName3;
    private TextView userName4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        this.myUserName = (TextView) findViewById(R.id.playerUsername1);
        this.userName2 = (TextView) findViewById(R.id.playerUsername2);
        this.userName3 = (TextView) findViewById(R.id.playerUsername4);
        this.userName4 = (TextView) findViewById(R.id.playerUsername4);

        this.player1_cards = new ImageView[]{(ImageView) findViewById(R.id.card1), (ImageView) findViewById(R.id.card2),
                (ImageView) findViewById(R.id.card3), (ImageView) findViewById(R.id.card4),(ImageView) findViewById(R.id.card5),
                (ImageView) findViewById(R.id.card6), (ImageView) findViewById(R.id.card7), (ImageView) findViewById(R.id.card8)};

        this.player2_cards = new ImageView[]{(ImageView) findViewById(R.id.card9), (ImageView) findViewById(R.id.card10),
                (ImageView) findViewById(R.id.card11), (ImageView) findViewById(R.id.card12),(ImageView) findViewById(R.id.card13),
                (ImageView) findViewById(R.id.card14), (ImageView) findViewById(R.id.card15), (ImageView) findViewById(R.id.card16)};

        this.player3_cards = new ImageView[]{(ImageView) findViewById(R.id.card17), (ImageView) findViewById(R.id.card18),
                (ImageView) findViewById(R.id.card19), (ImageView) findViewById(R.id.card20),(ImageView) findViewById(R.id.card21),
                (ImageView) findViewById(R.id.card22), (ImageView) findViewById(R.id.card23), (ImageView) findViewById(R.id.card24)};

        this.player4_cards = new ImageView[]{(ImageView) findViewById(R.id.card25), (ImageView) findViewById(R.id.card26),
                (ImageView) findViewById(R.id.card27), (ImageView) findViewById(R.id.card28),(ImageView) findViewById(R.id.card29),
                (ImageView) findViewById(R.id.card30), (ImageView) findViewById(R.id.card31), (ImageView) findViewById(R.id.card32)};

        showPage();
    }

    private void showPage() {
        String game_name = HomePage.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameState game = GameState.convertSnapshotToGameState(snapshot);
                Node players = game.players.head;
                Node iterator = players;

                while (iterator != null) {
                    String uid = ((Player) iterator.obj).uid;
                    if (uid.equals(HomePage.user.uid)) break;
                    iterator = iterator.next;
                }

                myUserName.setText(((Player) iterator.obj).userName);
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                userName2.setText(((Player) iterator.obj).userName);
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                userName3.setText(((Player) iterator.obj).userName);
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                userName4.setText(((Player) iterator.obj).userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}