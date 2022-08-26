package com.example.whist20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    private TextView budget;
    private TextView bettingValue;
    private TextView turn;
    private TextView playerCardBudget;
    private TextView playerCardView;
    private TextView playerCardUsername;
    private Button hit;
    private Button miss;
    private Button exit;
    private Button continue_game;
    private Button addFriend;
    private Button playerCardExit;

    private GameState game = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_in_game);

        this.hit = (Button) findViewById(R.id.hitButton);
        this.miss = (Button) findViewById(R.id.missButton);
        this.exit = (Button) findViewById(R.id.exitButtonInGame);
        this.continue_game = (Button) findViewById(R.id.continueButtonInGame);

        this.addFriend = (Button) findViewById(R.id.player_card_add);
        this.playerCardExit = (Button) findViewById(R.id.player_card_exit);
        this.playerCardBudget = (TextView) findViewById(R.id.player_card_budget);
        this.playerCardView = (TextView) findViewById(R.id.playerCard);
        this.playerCardUsername = (TextView) findViewById(R.id.player_card_username);

        this.addFriend.setVisibility(View.INVISIBLE);
        this.addFriend.setClickable(false);
        this.playerCardUsername.setVisibility(View.INVISIBLE);
        this.playerCardView.setVisibility(View.INVISIBLE);
        this.playerCardExit.setVisibility(View.INVISIBLE);
        this.playerCardExit.setClickable(false);
        this.playerCardBudget.setVisibility(View.INVISIBLE);

        this.hit.setClickable(false);
        this.miss.setClickable(false);
        this.exit.setVisibility(View.INVISIBLE);
        this.continue_game.setVisibility(View.INVISIBLE);

        this.myUserName = (TextView) findViewById(R.id.playerUsername1);
        this.userName2 = (TextView) findViewById(R.id.playerUsername2);
        this.userName3 = (TextView) findViewById(R.id.playerUsername3);
        this.userName4 = (TextView) findViewById(R.id.playerUsername4);

        this.myUserName.setClickable(false);
        this.userName2.setClickable(true);
        this.userName3.setClickable(true);
        this.userName4.setClickable(true);

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



        handleDisconnection();
        initOnClicks();
        showPage();
    }

    private void handleDisconnection() {
        String game_name = Profile.user.current_game_id;
        FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(game_name).child(Profile.user.uid).onDisconnect().setValue(Profile.user.username);
        FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(game_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GameState tmp = GameState.convertSnapshotToGameState(snapshot);
                        assert tmp != null;
                        if (!snapshot.hasChild(tmp.currentPlayerTurn().uid)) return;
                        while (snapshot.hasChild(tmp.currentPlayerTurn().uid)) tmp.nextPlayerTurn();

                        if (!tmp.currentPlayerTurn().uid.equals(Profile.user.uid)) return;
                        FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).setValue(tmp);
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

    private int convertCardToImage(Card card) {
        return this.convert_card_to_image[Card.convertSuitToInt(card.suit)][card.value - 2];
    }

    private void initOnClicks() {
        this.userName2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                playerCardView.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.VISIBLE);
                playerCardUsername.setVisibility(View.VISIBLE);
                playerCardExit.setVisibility(View.VISIBLE);
                playerCardBudget.setVisibility(View.VISIBLE);

                playerCardExit.setClickable(true);
                addFriend.setClickable(true);

                Node players = game.players.head;
                Node iterator = players;
                while (iterator != null) {
                    String uid = ((Player) iterator.obj).uid;
                    if (uid.equals(Profile.user.uid)) break;
                    iterator = iterator.next;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                Player player2 = (Player) iterator.obj;
                playerCardUsername.setText(player2.userName);
                playerCardBudget.setText("Budget: " + player2.budget);

                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Users").child(player2.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user2 = snapshot.getValue(User.class);
                                if (user2.requested == null) user2.requested = new Node();
                                user2.requested.addValue(Profile.user.uid);
                                FirebaseDatabase.getInstance().getReference("Users").child(player2.uid)
                                        .child("requested").setValue(user2.requested);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        addFriend.setClickable(false);
                        addFriend.setText("Friend Request sent");
                        //Todo send a friend request

                    }
                });
                playerCardExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addFriend.setVisibility(View.INVISIBLE);
                        addFriend.setClickable(false);
                        playerCardUsername.setVisibility(View.INVISIBLE);
                        playerCardView.setVisibility(View.INVISIBLE);
                        playerCardExit.setVisibility(View.INVISIBLE);
                        playerCardExit.setClickable(false);
                        playerCardBudget.setVisibility(View.INVISIBLE);
                    }
                });



            }
        });

        this.userName3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                playerCardView.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.VISIBLE);
                playerCardUsername.setVisibility(View.VISIBLE);
                playerCardExit.setVisibility(View.VISIBLE);
                playerCardBudget.setVisibility(View.VISIBLE);

                playerCardExit.setClickable(true);
                addFriend.setClickable(true);

                Node players = game.players.head;
                Node iterator = players;
                while (iterator != null) {
                    String uid = ((Player) iterator.obj).uid;
                    if (uid.equals(Profile.user.uid)) break;
                    iterator = iterator.next;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                Player player3 = (Player) iterator.obj;
                playerCardUsername.setText(player3.userName);
                playerCardBudget.setText("Budget: " + player3.budget);

                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Users").child(player3.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user2 = snapshot.getValue(User.class);
                                if (user2 == null) {
                                    Toast.makeText(InGame.this, "something went wrong", Toast.LENGTH_LONG).show();
                                }
                                user2.requested.addValue(Profile.user.uid);
                                FirebaseDatabase.getInstance().getReference("Users").child(player3.uid)
                                        .child("requested").setValue(user2.requested);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        addFriend.setClickable(false);
                        addFriend.setText("Friend Request sent");
                        //Todo send a friend request

                    }
                });
                playerCardExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addFriend.setVisibility(View.INVISIBLE);
                        addFriend.setClickable(false);
                        playerCardUsername.setVisibility(View.INVISIBLE);
                        playerCardView.setVisibility(View.INVISIBLE);
                        playerCardExit.setVisibility(View.INVISIBLE);
                        playerCardExit.setClickable(false);
                        playerCardBudget.setVisibility(View.INVISIBLE);
                    }
                });



            }
        });
        this.userName4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game == null) {
                    Toast.makeText(InGame.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                playerCardView.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.VISIBLE);
                playerCardUsername.setVisibility(View.VISIBLE);
                playerCardExit.setVisibility(View.VISIBLE);
                playerCardBudget.setVisibility(View.VISIBLE);

                playerCardExit.setClickable(true);
                addFriend.setClickable(true);

                Node players = game.players.head;
                Node iterator = players;
                while (iterator != null) {
                    String uid = ((Player) iterator.obj).uid;
                    if (uid.equals(Profile.user.uid)) break;
                    iterator = iterator.next;
                }
                iterator = iterator.next;
                if (iterator == null) iterator = players;
                iterator = iterator.next;
                if (iterator == null) iterator = players;
                iterator = iterator.next;
                if (iterator == null) iterator = players;

                Player player4 = (Player) iterator.obj;
                playerCardUsername.setText(player4.userName);
                playerCardBudget.setText("Budget: " + player4.budget);

                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference("Users").child(player4.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user2 = snapshot.getValue(User.class);
                                user2.requested.addValue(Profile.user.uid);
                                FirebaseDatabase.getInstance().getReference("Users").child(player4.uid)
                                        .child("requested").setValue(user2.requested);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        addFriend.setClickable(false);
                        addFriend.setText("Friend Request sent");
                        //Todo send a friend request

                    }
                });
                playerCardExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        addFriend.setVisibility(View.INVISIBLE);
                        addFriend.setClickable(false);
                        playerCardUsername.setVisibility(View.INVISIBLE);
                        playerCardView.setVisibility(View.INVISIBLE);
                        playerCardExit.setVisibility(View.INVISIBLE);
                        playerCardExit.setClickable(false);
                        playerCardBudget.setVisibility(View.INVISIBLE);
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

                FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(game.game_name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        game.nextPlayerTurn();
                        if (snapshot.exists()) while (snapshot.hasChild(game.currentPlayerTurn().uid) && !game.currentPlayerTurn().uid.equals("")) game.nextPlayerTurn();
                        Toast.makeText(InGame.this, game.currentPlayerTurn().userName, Toast.LENGTH_LONG).show();

                        FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id)
                                .setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful())
                                            Toast.makeText(InGame.this, "Failed to update game on FireBase", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        this.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).addListenerForSingleValueEvent(new ValueEventListener() {
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


                                        FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).child("current_game_id").setValue("");Profile.user.current_game_id = "";
                                        FirebaseDatabase.getInstance().getReference("ActiveGames").child(game_name).setValue(game);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                return null;
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                            }
                        });
            }
        });

        this.continue_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit.setVisibility(View.INVISIBLE);
                continue_game.setVisibility(View.INVISIBLE);
                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(game.game_name).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                game = GameState.convertSnapshotToGameState(snapshot);
                                                assert game != null;
                                                Player tmp = game.findPlayerByUid(Profile.user.uid);
                                                tmp.is_ready = true;
                                                game.num_of_ready_players++;
                                                FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                return null;
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

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
        // TODO: show the rest of the players the cards that the dealer has opened
        Dealer dealer = (Dealer) game.players.head.obj;
        if (dealer.cards.cards.size < 2) {
            if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dealer.cards.addCard(game.dict.randomCard());
            game.nextPlayerTurn();
            game.is_active = true;
            FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
            return;
        }

        if (!game.is_active) {
            if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(game.game_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Toast.makeText(InGame.this, "XXXX", Toast.LENGTH_LONG).show();

                    Node iterator = game.players.head;
                    while (iterator != null) {
                        if (snapshot.hasChild(((Player) iterator.obj).uid) && !((Player) iterator.obj).uid.equals("")) {
                            game.removePlayerByUid(((Player) iterator.obj).uid);
                            Toast.makeText(InGame.this, ((Player) iterator.obj).userName, Toast.LENGTH_LONG).show();
                            iterator = game.players.head;
                        }
                        iterator = iterator.next;
                    }
                    game.restartGame();
                    FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
                    Toast.makeText(InGame.this, "YYYY", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            dealer.openNewCard(game);
            Player player = game.findPlayerByUid(Profile.user.uid);
            if (dealer.cards.sum() > player.cards.sum() || player.cards.sum() == -1) Toast.makeText(InGame.this, "You Lost the Bet", Toast.LENGTH_LONG).show();
            else Toast.makeText(InGame.this, "You won the bet +" + String.valueOf(game.game_money * 2), Toast.LENGTH_LONG).show();
            if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
            game.is_active = false;
            //if (!((Player) game.players.head.next.obj).uid.equals(Profile.user.uid)) return;
            FirebaseDatabase.getInstance().getReference("ActiveGames").child(Profile.user.current_game_id).setValue(game);
        }

    }

    private void notFoundCase() {
        FirebaseDatabase.getInstance().getReference("DisconnectedUsers").child(game.game_name).setValue(null);
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
                Thread.sleep(1000);
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
                Thread.sleep(3000);
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

        /*if (!game.is_active) {
            gameIsNotActiveCase();
            return false;
        }

        if (game.first_open) {
            isFirstOpenCase();
            return false;
        }*/
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
                FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Profile.user = snapshot.getValue(User.class);
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
                        myUserName.setClickable(false);
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
                        if (((Player) iterator.obj).uid.equals("")) {
                            userName2.setClickable(false);
                        }
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
                            if (((Player) iterator.obj).uid.equals("")) {
                                userName3.setClickable(false);
                            }
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
                        else{
                            //userName3.setVisibility(View.un)
                            userName3.setClickable(false);
                        }


                        if (!((Player)iterator.obj).uid.equals(Profile.user.uid)) {
                            if (((Player) iterator.obj).uid.equals("")) {
                                userName4.setClickable(false);
                            }
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
                        else{
                            userName4.setClickable(false);
                        }


                        if (current_player.uid.equals(Profile.user.uid)) {
                            hit.setClickable(true);
                            miss.setClickable(true);
                            if (current_player.cards.cards.size == 0) {
                                current_player.budget -= game.game_money;
                                Profile.user.money -= game.game_money;
                                FirebaseDatabase.getInstance().getReference("Users").child(Profile.user.uid)
                                        .child("money").setValue(Profile.user.money).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(InGame.this, String.valueOf(-game.game_money), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            if (current_player.cards.cards.size < 2) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Player my_player = (Player) game.currentPlayer.obj;
                                my_player.openNewCard(game);
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
                            else {
                                turn.setText("Your Turn");
                            }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}