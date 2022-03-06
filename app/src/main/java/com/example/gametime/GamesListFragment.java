package com.example.gametime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class GamesListFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games_list, container, false);

        recyclerView = view.findViewById(R.id.findGameRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter();
        recyclerView.setAdapter(adapter);

        setupGamesListener();

        view.findViewById(R.id.imageButtonFindBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoHome();
            }
        });

        return view;
    }

    private void setupGamesListener() {
        db1.collection("games").orderBy("gameTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            ImageView imageViewTrash, imageViewLike;

            public GamesViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewGameName = itemView.findViewById(R.id.recyclerGameName);
                textViewGameDate = itemView.findViewById(R.id.recyclerGameDate);
                textViewGameTime = itemView.findViewById(R.id.recyclerGameTime);
                imageViewTrash = itemView.findViewById(R.id.imageVireRecyclerTrash);
                imageViewLike = itemView.findViewById(R.id.imageViewLike);

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

                if (mGame.getCreatedByUid().equals(user.getUid())) {
                    imageViewTrash.setVisibility(View.VISIBLE);
                    imageViewTrash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(true);
                            builder.setTitle("Are you sure you want to delete this game?");
                            builder.setMessage("By confirming, you will remove this game post from everyone's list!");
                            builder.setPositiveButton("Confirm",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db1.collection("games").document(mGame.getGameId()).delete();
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
                } else {
                    imageViewTrash.setVisibility(View.INVISIBLE);
                }

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
    }

}