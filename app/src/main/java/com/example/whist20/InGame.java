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

import java.util.concurrent.TimeUnit;

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
    private TextView budget;
    private TextView bettingValue;
    private TextView turn;

    private Button hit;
    private Button miss;
    private Button exit;
    private Button continue_game;

    private GameState game = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        this.hit = (Button) findViewById(R.id.hitButton);
        this.miss = (Button) findViewById(R.id.missButton);
        this.exit = (Button) findViewById(R.id.exitButtonInGame);
        this.continue_game = (Button) findViewById(R.id.continueButtonInGame);
        this.hit.setClickable(false);
        this.miss.setClickable(false);
        this.exit.setVisibility(View.INVISIBLE);
        this.continue_game.setVisibility(View.INVISIBLE);

        this.myUserName = (TextView) findViewById(R.id.playerUsername1);
        this.userName2 = (TextView) findViewById(R.id.playerUsername2);
        this.userName3 = (TextView) findViewById(R.id.playerUsername3);
        this.userName4 = (TextView) findViewById(R.id.playerUsername4);
        this.budget = (TextView) findViewById(R.id.budgetInGame);
        this.bettingValue = (TextView) findViewById(R.id.bettingValueInGame);
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
        this.hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }

                Player my_player = (Player) game.currentPlayer.obj;
                my_player.openNewCard(game);
                if (my_player.cards.sum() == -1) {
                    Toast.makeText(InGame.this, "You Lost", Toast.LENGTH_LONG).show();
                    game.nextPlayerTurn();
                }
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

        this.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                game = GameState.convertSnapshotToGameState(snapshot);
                                if (game == null) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).child("current_game_id").setValue("");
                                    Profile.user.current_game_id = "";
                                    startActivity(new Intent(InGame.this, HomePage.class));
                                    return;
                                }

                                game.removePlayerByUid(Profile.user.uid);
                                String game_name = game.game_name;
                                if (game.numOfPlayers == 1) game = null;


                                FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).child("current_game_id").setValue("");
                                Profile.user.current_game_id = "";
                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).setValue(game);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        this.continue_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit.setVisibility(View.INVISIBLE);
                continue_game.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                game = GameState.convertSnapshotToGameState(snapshot);
                                assert game != null;
                                game.findPlayerByUid(Profile.user.uid).is_ready = true;
                                game.num_of_ready_players++;
                                FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid)
                                        .child("money").setValue(Profile.user.money - game.game_money);
                                Profile.user.money -= game.game_money;
                                Toast.makeText(InGame.this, String.valueOf(-game.game_money) , Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void clearCardImages() {
        for (int i = 0; i < 8; i++) {
            player1_cards[i].setImageResource(0);
            player2_cards[i].setImageResource(0);
            player3_cards[i].setImageResource(0);
            player4_cards[i].setImageResource(0);
        }
    }

    private void dealerPlay() {
        Dealer dealer = (Dealer) game.players.head.obj;
        Player player = game.findPlayerByUid(Profile.user.uid);
        if (dealer.cards.sum() > player.cards.sum()) Toast.makeText(InGame.this, "You Lost the Bet", Toast.LENGTH_LONG).show();
        else Toast.makeText(InGame.this, "You won the bet +" + String.valueOf(game.game_money * 2), Toast.LENGTH_LONG).show();
        if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;

        // TODO: show the rest of the players the cards that the dealer has opened
        ((Dealer) game.currentPlayer.obj).openNewCard(game);
        game.restartGame();
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
    }

    private void notFoundCase() {
        startActivity(new Intent(InGame.this, HomePage.class));
    }

    private void myPlayerNotReadyCase() {
        exit.setVisibility(View.VISIBLE);
        continue_game.setVisibility(View.VISIBLE);
        clearCardImages();
    }

    private void notAllPlayersReadyCase() {
        turn.setText("waiting");
    }

    private void gameIsNotActiveCase() {
        if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
        game.startRound();
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
    }

    private void isFirstOpenCase() {
        Node iterator = game.players.head;
        int index = 0;
        ImageView[][] players_cards = {player1_cards, player2_cards, player3_cards, player4_cards};
        while (iterator != null) {
            if (iterator.obj == null) break;

            Node tmp = ((Player) iterator.obj).cards.cards.head;
            Card card = (Card) tmp.obj;
            assert card != null;
            players_cards[index][0].setImageResource(convertCardToImage(card));

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iterator = iterator.next;
            index++;
        }

        iterator = game.players.head;
        index = 0;
        while (iterator != null) {
            if (iterator.obj == null) break;

            Node tmp = ((Player) iterator.obj).cards.cards.head.next;
            Card card = (Card) tmp.obj;
            assert card != null;
            players_cards[index][1].setImageResource(convertCardToImage(card));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iterator = iterator.next;
            index++;
        }
        game.first_open = false;
        if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
    }

    private boolean isLegalToContinue() {
        budget.setText("Budget: " + String.valueOf(Profile.user.money));
        turn.setText("");
        bettingValue.setText("Bet: " + String.valueOf(game.game_money));
        Player player = game.findPlayerByUid(Profile.user.uid);
        if (!player.is_ready) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myPlayerNotReadyCase();
            return false;
        }

        if (game.num_of_ready_players < game.numOfPlayers) {
            notAllPlayersReadyCase();
            return false;
        }

        if (!game.is_active) {
            gameIsNotActiveCase();
            return false;
        }

        if (game.first_open) {
            isFirstOpenCase();
            return false;
        }
        return true;
    }

    private void showPage() {
        String game_name = Profile.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = GameState.convertSnapshotToGameState(snapshot);
                if (game == null) {
                    snapshot.getRef().removeEventListener(this);
                    notFoundCase();
                    return;
                }
                if (game.findPlayerByUid(Profile.user.uid) == null) {
                    snapshot.getRef().removeEventListener(this);
                    notFoundCase();
                    return;
                }
                if (!isLegalToContinue()) return;

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


                if (!((Player)iterator.obj).uid.equals(Profile.user.uid)) {
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
                }


                if (!((Player)iterator.obj).uid.equals(Profile.user.uid)) {
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
                }

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