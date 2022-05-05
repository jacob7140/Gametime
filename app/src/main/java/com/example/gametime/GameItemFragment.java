package com.example.gametime;

import static com.example.gametime.MainActivity.PreviousViewState;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    TextView gameName, location, gameDate, numPeople, seatsLeft, datePosted, postedBy, gameType;
    ImageButton buttonBack;
    ImageView imageViewEdit;
    Button signUpForGame, messageUserForGame;

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
        imageViewEdit = view.findViewById(R.id.imageViewEditIconGameItem);
        signUpForGame = view.findViewById(R.id.buttonGameItemSignUp);
        gameType = view.findViewById(R.id.textViewItemGameType);

        messageUserForGame = view.findViewById(R.id.buttonGameItemSendMessage);

        gameName.setText(mGame.getGameName());
        location.setText(mGame.getAddress());
        gameDate.setText(mGame.getGameDate());
        numPeople.setText(mGame.getNumberPeople());
        seatsLeft.setText(numSeatsLeft);

        SimpleDateFormat formatter= new SimpleDateFormat("MM-dd-yyyy 'at' hh:mm aa");
        Date createdAt = mGame.getCreatedAt().toDate();
        String formattedDate = formatter.format(createdAt);
        datePosted.setText(formattedDate);
        postedBy.setText(mGame.getCreatedByName());

        gameType.setText(mGame.getGameType());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoGameList();
            }
        });

        db.collection("userdata").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userRole = "";
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("Role")){
                        userRole = (String) documentSnapshot.get("Role");
                    }
                }
                if (mGame.getCreatedByUid().equals(user.getUid()) || userRole.equals("Admin")) {

                    signUpForGame.setText("Delete Post");
                    signUpForGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DeleteGame deleteGame = new DeleteGame();
                            deleteGame.deleteGamePost(mGame, getContext(), db);
                            mListener.gotoGameList();
                        }
                    });
                    imageViewEdit.setVisibility(View.VISIBLE);
                    imageViewEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.gotoEditGame(mGame, PreviousViewState.GAMEITEM);
                        }
                    });
                    messageUserForGame.setVisibility(View.VISIBLE);
                    messageUserForGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.gotoChatMessage(mGame, PreviousViewState.GAMEITEM);
                        }

                    });
                }
            }
        });

        if (mGame.getSignedUp().contains(user.getUid())) {
            imageViewEdit.setVisibility(View.INVISIBLE);
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

                                    HashMap<String, Object> userdata = new HashMap<>();
                                    userdata.put("SignedGameID", FieldValue.arrayRemove(mGame.getGameId()));
                                    db.collection("userdata").document(user.getUid()).update(userdata);
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
            messageUserForGame.setVisibility(View.VISIBLE);
            messageUserForGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.gotoChatMessage(mGame, PreviousViewState.GAMEITEM);
                }

            });
        }
        else {
            signUpForGame.setText("Sign Up");
            messageUserForGame.setVisibility(View.INVISIBLE);
            imageViewEdit.setVisibility(View.INVISIBLE);
            signUpForGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGame.signedUp.add(user.getUid());
                    HashMap<String, Object> updateSignedUp = new HashMap<>();
                    updateSignedUp.put("signedUp", FieldValue.arrayUnion(user.getUid()));
                    db.collection("games").document(mGame.getGameId()).update(updateSignedUp);

                    HashMap<String, Object> userdata = new HashMap<>();
                    userdata.put("SignedGameID", FieldValue.arrayUnion(mGame.getGameId()));
                    db.collection("userdata").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                db.collection("userdata").document(user.getUid()).update(userdata);
                            } else {
                                db.collection("userdata").document(user.getUid()).set(userdata);
                            }
                        }
                    });
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
        void gotoChatMessage(Game game, PreviousViewState viewState);
        void gotoEditGame(Game game, PreviousViewState viewState);
    }
}