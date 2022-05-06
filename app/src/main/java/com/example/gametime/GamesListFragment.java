package com.example.gametime;

import com.example.gametime.MainActivity.PreviousViewState;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class GamesListFragment extends Fragment{

    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    final private String TAG = "data";

    public GamesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Game> gamesList = new ArrayList<Game>();
    GamesAdapter adapter;
    Spinner dropdown;
    String preferenceSelection;
    ArrayList<String> gamePreferenceList;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games_list, container, false);

        dropdown = view.findViewById(R.id.spinnerGameListPreference);
        gamePreferenceList = new ArrayList<>();
        gamePreferenceList.add("All");
        spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, gamePreferenceList);

        db1.collection("gamePreferences").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) {
                gamePreferenceList.clear();
                gamePreferenceList.add("All");
                gamePreferenceList.add("Loading...");
                for(QueryDocumentSnapshot documentSnapshot : querySnapshots){
                    gamePreferenceList.add((String) documentSnapshot.get("Type"));
                }
                gamePreferenceList.remove("Loading...");
                gamePreferenceList.add("Other");
                gamePreferenceList.add("<Add to List>");
                gamePreferenceList.add("<Remove from List>");
                Log.d(TAG, "onSuccess: " + spinnerAdapter.toString());
                spinnerAdapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " +  dropdown.getCount());
            }
        });

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropdown.setAdapter(spinnerAdapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int getId = parent.getSelectedItemPosition();
                preferenceSelection = String.valueOf(parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#FFFFFF"));
//                Log.d(TAG, preferenceSelection);
                setupGamesListener();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = view.findViewById(R.id.findGameRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter();
        recyclerView.setAdapter(adapter);

//        setupGamesListener();

        view.findViewById(R.id.imageButtonFindBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoHome();
            }
        });
        return view;
    }

    private void setupGamesListener() {
        if (preferenceSelection.equals("All")) {

            db1.collection("games").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error == null) {
                        gamesList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Game game = document.toObject(Game.class);
                            game.setGameId(document.getId());
                            gamesList.add(game);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        error.printStackTrace();
                    }
                }
            });
        } else if(preferenceSelection.equals("<Add to List>")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Add Game Preference");
            builder.setMessage("Enter the name of the game preference that you want to add to the list (Case-Sensitive).");
            EditText editTextGamePreference = new EditText(getContext());
            builder.setView(editTextGamePreference);

            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> gamePreferenceData = new HashMap<>();
                    gamePreferenceData.put("Type", editTextGamePreference.getText().toString());
                    gamePreferenceList.add(gamePreferenceList.size() - 2, editTextGamePreference.getText().toString());
                    db1.collection("gamePreferences").add(gamePreferenceData);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            preferenceSelection = gamePreferenceList.get(0);
            dropdown.setSelection(0);
        } else if(preferenceSelection.equals("<Remove from List>")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Add Game Preference");
            builder.setMessage("Enter the name of the game preference that you want to remove from the list (Case-Sensitive).");
            EditText editTextGamePreference = new EditText(getContext());
            builder.setView(editTextGamePreference);

            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    db1.collection("gamePreferences").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot : querySnapshots){
                                if(documentSnapshot.exists()){
                                    if(documentSnapshot.contains("Type")){
                                        String type = (String) documentSnapshot.get("Type");
                                        if(type.equals(editTextGamePreference.getText().toString())){
                                            db1.collection("gamePreferences").document(documentSnapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    gamePreferenceList.remove(editTextGamePreference.getText().toString());
                                                    Log.d(TAG, "onSuccess: to delete the item from the game preference list.");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: to delete the item from the game preference list.");
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            preferenceSelection = gamePreferenceList.get(0);
            dropdown.setSelection(0);
        } else {
            db1.collection("games").whereEqualTo("gameType", preferenceSelection).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error == null) {
                        gamesList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Game game = document.toObject(Game.class);
                            game.setGameId(document.getId());
                            gamesList.add(game);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        error.printStackTrace();
                    }
                }
            });
        }
    }


    class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {

        @NonNull
        @Override
        public GamesAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findgame_recycler_layout, parent, false);
            return new GamesViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return gamesList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
            Game game = gamesList.get(position);
            holder.setUpGameRow(game);
        }

        class GamesViewHolder extends RecyclerView.ViewHolder {
            Game mGame;
            TextView textViewGameName, textViewGameDate, textViewGameTime;
            ImageView imageViewTrash, imageViewLike, imageViewEdit;

            public GamesViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewGameName = itemView.findViewById(R.id.recyclerGameName);
                textViewGameDate = itemView.findViewById(R.id.recyclerGameDate);
                textViewGameTime = itemView.findViewById(R.id.recyclerGameTime);
                imageViewTrash = itemView.findViewById(R.id.imageVireRecyclerTrash);
                imageViewLike = itemView.findViewById(R.id.imageViewLike);
                imageViewEdit = itemView.findViewById(R.id.imageViewEditIcon);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoGameItem(mGame);
                    }
                });
            }

            public void setUpGameRow(Game game) {
                this.mGame = game;
                textViewGameName.setText(mGame.gameName);
                textViewGameDate.setText(mGame.gameDate);
                textViewGameTime.setText(mGame.gameTime);

                db1.collection("userdata").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userRole = "";
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.contains("Role")) {
                                userRole = (String) documentSnapshot.get("Role");
                            }
                        }

                        if (mGame.getCreatedByUid().equals(user.getUid()) || userRole.equals("Admin")) {
                            imageViewEdit.setVisibility(View.VISIBLE);
                            imageViewTrash.setVisibility(View.VISIBLE);

                            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.gotoEditGame(mGame, PreviousViewState.GAMESLIST);
                                }
                            });
                            imageViewTrash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DeleteGame deleteGame = new DeleteGame();
                                    deleteGame.deleteGamePost(mGame, getContext(), db1);
                                }
                            });
                        } else {
                            imageViewEdit.setVisibility(View.INVISIBLE);
                            imageViewTrash.setVisibility(View.INVISIBLE);
                        }
                    }
                });


                if (mGame.getLikedBy().contains(user.getUid())){
                    imageViewLike.setImageResource(R.drawable.like_favorite);
                } else {
                    imageViewLike.setImageResource(R.drawable.like_not_favorite);
                }

                imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mGame.getLikedBy().contains(mAuth.getCurrentUser().getUid())){
                            imageViewLike.setImageResource(R.drawable.like_not_favorite);
                            mGame.likedBy.remove(mAuth.getCurrentUser().getUid());

                            HashMap<String, Object> unlikeUpdate = new HashMap<>();
                            HashMap<String, Object> updateLikedBy = new HashMap<>();
                            updateLikedBy.put("likedBy", FieldValue.arrayRemove(user.getUid()));
                            db1.collection("games").document(mGame.getGameId()).update(updateLikedBy);

                            Log.d(TAG, "onClick: Unlike Post" + mGame.getLikedBy());

                        } else {
                            mGame.likedBy.add(mAuth.getCurrentUser().getUid());
                            imageViewLike.setImageResource(R.drawable.like_favorite);
                            HashMap<String, Object> updateLikedBy = new HashMap<>();
                            updateLikedBy.put("likedBy", FieldValue.arrayUnion(user.getUid()));
                            db1.collection("games").document(mGame.getGameId()).update(updateLikedBy);
                            Log.d(TAG, mGame.getLikedBy().toString());
                        }
                    }
                });
            }
        }
    }

    GamesListFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (GamesListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }


    interface GamesListFragmentListener{
        void gotoHome();
        void gotoGameItem(Game game);
        void gotoEditGame(Game game, PreviousViewState viewState);
    }

}