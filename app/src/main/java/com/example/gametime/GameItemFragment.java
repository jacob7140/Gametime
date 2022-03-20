package com.example.gametime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class GameItemFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";

    private static final String ARG_PARAM_AUTH = "ARG_PARAM_AUTH";
    private static final String ARG_PARAM_GAME = "ARG_PARAM_GAME";
    private Game mGame;

    public GameItemFragment() {
        // Required empty public constructor
    }

    public static GameItemFragment newInstance(Game game) {
        GameItemFragment fragment = new GameItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame = (Game) getArguments().getSerializable(ARG_PARAM_GAME);
        }
    }

    TextView gameName, location, gameDate, numPeople, seatsLeft, datePosted, postedBy;
    ImageButton buttonBack, shareIt;
    Button signUpForGame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_item, container, false);
        String numSeatsLeft = String.valueOf(Integer.parseInt(mGame.getNumberPeople()) - mGame.getSignedUp().size());

        gameName = view.findViewById(R.id.textViewItemGameName);
        location = view.findViewById(R.id.textViewItemLocation);
        gameDate = view.findViewById(R.id.textViewItemDate);
        numPeople = view.findViewById(R.id.textViewItemNumPeople);
        seatsLeft = view.findViewById(R.id.textViewItemSeatsLeft);
        datePosted = view.findViewById(R.id.textViewItemDatePosted);
        postedBy = view.findViewById(R.id.textViewItemPostedBy);
        buttonBack = view.findViewById(R.id.imageButtonGameItemBack);
        signUpForGame = view.findViewById(R.id.buttonGameItemSignUp);
        shareIt = view.findViewById(R.id.imageButtonShare);

        gameName.setText(mGame.getGameName());
        location.setText(mGame.getAddress());
        gameDate.setText(mGame.getGameDate());
        numPeople.setText(mGame.getNumberPeople());
        seatsLeft.setText(numSeatsLeft);
        // TODO: Fix format
        datePosted.setText(mGame.getCreatedAt().toString());
        postedBy.setText(mGame.getCreatedByName());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoGameList();
            }
        });

        shareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String message="Let's play this game!";

                // This should be changed to the real refferal link
                message = message+ "\n" + "https://play.google.com/store/apps/details?id=" +
                       BuildConfig.APPLICATION_ID;

                myIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(myIntent,"Share with"));
            }
        });


        if (mGame.getCreatedByUid().equals(user.getUid())) {
            signUpForGame.setText("Delete Post");
            signUpForGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteGame deleteGame = new DeleteGame();
                    deleteGame.deleteGamePost(mGame, getContext(), db);
                    mListener.gotoGameList();
                }
            });
        } else if (mGame.getSignedUp().contains(user.getUid())) {
            signUpForGame.setText("Withdraw");
            signUpForGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGame.signedUp.remove(user.getUid());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Are you sure you want withdraw from this game?");
                    builder.setMessage("By confirming, you will be removed from the list!");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HashMap<String, Object> updateSignedUp = new HashMap<>();
                                    updateSignedUp.put("signedUp", FieldValue.arrayRemove(user.getUid()));
                                    db.collection("games").document(mGame.getGameId()).update(updateSignedUp);

                                    mListener.gotoGameList();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        else {
            signUpForGame.setText("Sign Up");
            signUpForGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGame.signedUp.add(user.getUid());
                    HashMap<String, Object> updateSignedUp = new HashMap<>();
                    updateSignedUp.put("signedUp", FieldValue.arrayUnion(user.getUid()));
                    db.collection("games").document(mGame.getGameId()).update(updateSignedUp);

                    mListener.gotoGameList();
                }
            });
        }

        return view;
    }

    GameItemListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (GameItemListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface GameItemListener{
        void gotoGameList();
    }
}