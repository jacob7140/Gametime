package com.example.gametime;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HostedGamesFragment extends Fragment {

    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public HostedGamesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_hosted_games, container, false);
        recyclerView = view.findViewById(R.id.upcomingGameRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter();
        recyclerView.setAdapter(adapter);
        setupGamesListener();

        view.findViewById(R.id.imageButtonHostedBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToProfile();
            }
        });

        return view;
    }

    private void setupGamesListener() {
        db1.collection("games").whereEqualTo("createdByUid", user.getUid()).whereGreaterThan("gameDate", Timestamp.now())
                .orderBy("gameDate", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            return new GamesAdapter.GamesViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return gamesList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull GamesAdapter.GamesViewHolder holder, int position) {
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

                SimpleDateFormat formatterDateAndTime= new SimpleDateFormat("MM-dd-yyyy");
                Date createdAt = mGame.getGameDate().toDate();
                String formattedDate = formatterDateAndTime.format(createdAt);
                textViewGameDate.setText(formattedDate);

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
                                    mListener.gotoEditGame(mGame, MainActivity.PreviousViewState.GAMESLIST);
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

                        } else {
                            mGame.likedBy.add(mAuth.getCurrentUser().getUid());
                            imageViewLike.setImageResource(R.drawable.like_favorite);
                            HashMap<String, Object> updateLikedBy = new HashMap<>();
                            updateLikedBy.put("likedBy", FieldValue.arrayUnion(user.getUid()));
                            db1.collection("games").document(mGame.getGameId()).update(updateLikedBy);
                        }
                    }
                });
            }
        }
    }

    HostedGamesFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (HostedGamesFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }


    interface HostedGamesFragmentListener{
        void goToProfile();
        void gotoGameItem(Game game);
        void gotoEditGame(Game game, MainActivity.PreviousViewState viewState);
    }
}