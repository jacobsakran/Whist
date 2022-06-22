package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InGame extends AppCompatActivity {
    private final int[] cards_of_hearts = {R.drawable.two_of_hearts, R.drawable.three_of_hearts, R.drawable.four_of_hearts,
            R.drawable.five_of_hearts, R.drawable.six_of_hearts, R.drawable.seven_of_hearts, R.drawable.eight_of_hearts,
            R.drawable.nine_of_hearts, R.drawable.ten_of_hearts, R.drawable.jack_of_hearts, R.drawable.queen_of_hearts,
            R.drawable.king_of_hearts, R.drawable.ace_of_hearts};

    private final int[] cards_of_diamonds = {R.drawable.two_of_diamonds, R.drawable.three_of_diamonds, R.drawable.four_of_diamonds,
            R.drawable.five_of_diamonds, R.drawable.six_of_diamonds, R.drawable.seven_of_diamonds, R.drawable.eight_of_diamonds,
            R.drawable.nine_of_diamonds, R.drawable.ten_of_diamonds, R.drawable.jack_of_diamonds, R.drawable.queen_of_diamonds,
            R.drawable.king_of_diamonds, R.drawable.ace_of_diamonds};

    private final int[] cards_of_spades = {R.drawable.two_of_spades, R.drawable.three_of_spades, R.drawable.four_of_spades,
            R.drawable.five_of_spades, R.drawable.six_of_spades, R.drawable.seven_of_spades, R.drawable.eight_of_spades,
            R.drawable.nine_of_spades, R.drawable.ten_of_spades, R.drawable.jack_of_spades, R.drawable.queen_of_spades,
            R.drawable.king_of_spades, R.drawable.ace_of_spades};

    private final int[] cards_of_clubs = {R.drawable.two_of_clubs, R.drawable.three_of_clubs, R.drawable.four_of_clubs,
            R.drawable.five_of_clubs, R.drawable.six_of_clubs, R.drawable.seven_of_clubs, R.drawable.eight_of_clubs,
            R.drawable.nine_of_clubs, R.drawable.ten_of_clubs, R.drawable.jack_of_clubs, R.drawable.queen_of_clubs,
            R.drawable.king_of_clubs, R.drawable.ace_of_clubs};

    private final int[][] convert_card_to_image = {cards_of_hearts, cards_of_diamonds, cards_of_spades, cards_of_clubs};

    private ImageView[] player1_cards;
    private ImageView[] player2_cards;
    private ImageView[] player3_cards;
    private ImageView[] player4_cards;

    private TextView myUserName;
    private TextView userName2;
    private TextView userName3;
    private TextView userName4;
    private TextView turn;

    private Button hit, miss, exit;

    private GameState game = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        this.hit = (Button) findViewById(R.id.hitButton);
        this.miss = (Button) findViewById(R.id.missButton);
        this.exit = (Button) findViewById(R.id.in_game_exit);
        this.hit.setClickable(false);
        this.miss.setClickable(false);

        this.myUserName = (TextView) findViewById(R.id.playerUsername1);
        this.userName2 = (TextView) findViewById(R.id.playerUsername2);
        this.userName3 = (TextView) findViewById(R.id.playerUsername3);
        this.userName4 = (TextView) findViewById(R.id.playerUsername4);
        this.turn = (TextView) findViewById(R.id.playersTurn);

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

        initOnClicks();
        showPage();
    }

    private int convertCardToImage(Card card) {
        return this.convert_card_to_image[Card.convertSuitToInt(card.suit)][card.value - 2];
    }

    private void initOnClicks() {
        this.exit.setOnClickListener(new View.OnClickListener() {
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
                                    startActivity(new Intent(InGame.this, Profile.class));
                                } else Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        this.hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }

                Player my_player = (Player) game.currentPlayer.obj;
                my_player.openNewCard(game);
                game.nextPlayerTurn();
                if (my_player.cards.sum() == -1) Toast.makeText(InGame.this, "You Lost", Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id)
                        .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful())
                                    Toast.makeText(InGame.this, "Failed to update game on FireBase", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        this.miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }

                game.nextPlayerTurn();
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id)
                        .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful())
                                    Toast.makeText(InGame.this, "Failed to update game on FireBase", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void dealerPlay() {
        if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;

        ((Dealer) game.currentPlayer.obj).openNewCard(game);
        game.nextPlayerTurn();
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
    }

    private void showPage() {
        String game_name = Profile.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = GameState.convertSnapshotToGameState(snapshot);
                if (game == null) {
                    snapshot.getRef().removeEventListener(this);
                    startActivity(new Intent(InGame.this, HomePage.class));
                    return;
                }
                Node players = game.players.head;
                Node iterator = players;
                Player current_player = ((Player) game.currentPlayer.obj);

                while (iterator != null) {
                    String uid = ((Player) iterator.obj).uid;
                    if (uid.equals(Profile.user.uid)) break;
                    iterator = iterator.next;
                }

                Node card_iterator = null;
                int index = 0;


                myUserName.setText(((Player) iterator.obj).userName);
                card_iterator = ((Player) iterator.obj).cards.cards.head;
                while (card_iterator != null) {
                    Card card = (Card) card_iterator.obj;
                    if (card == null) break;
                    player1_cards[index].setImageResource(convertCardToImage(card));
                    card_iterator = card_iterator.next;
                    index++;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;


                index = 0;
                userName2.setText(((Player) iterator.obj).userName);
                card_iterator = ((Player) iterator.obj).cards.cards.head;
                while (card_iterator != null) {
                    Card card = (Card) card_iterator.obj;
                    if (card == null) break;
                    player2_cards[index].setImageResource(convertCardToImage(card));
                    card_iterator = card_iterator.next;
                    index++;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;


                index = 0;
                userName3.setText(((Player) iterator.obj).userName);
                card_iterator = ((Player) iterator.obj).cards.cards.head;
                while (card_iterator != null) {
                    Card card = (Card) card_iterator.obj;
                    if (card == null) break;
                    player3_cards[index].setImageResource(convertCardToImage(card));
                    card_iterator = card_iterator.next;
                    index++;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                /*
                index = 0;
                userName4.setText(((Player) iterator.obj).userName);
                card_iterator = ((Player) iterator.obj).cards.cards.head;
                while (card_iterator != null) {
                    Card card = (Card) card_iterator.obj;
                    if (card == null) break;
                    player4_cards[index].setImageResource(convertCardToImage(card));
                    card_iterator = card_iterator.next;
                    index++;
                }
                */
                if (current_player.uid.equals(Profile.user.uid)) {
                    hit.setClickable(true);
                    miss.setClickable(true);
                    turn.setText("Your Turn");
                } else {
                    hit.setClickable(false);
                    miss.setClickable(false);
                    turn.setText("");
                    if (current_player.uid.equals("")) dealerPlay(); // the current player is the dealer
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}