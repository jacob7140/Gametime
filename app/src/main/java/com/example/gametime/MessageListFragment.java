package com.example.gametime;

import com.example.gametime.MainActivity.PreviousViewState;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MessageListFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GamesAdapter adapter;
    private ArrayList<Game> gameList = new ArrayList<Game>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        recyclerView = view.findViewById(R.id.findMessageRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter();
        recyclerView.setAdapter(adapter);
        setupGamesListener();



        return view;
    }

    private void setupGamesListener() {
        db.collection("userdata").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        if (doc.get("SignedGameID") != null) {
                            ArrayList<String> data = (ArrayList<String>) doc.get("SignedGameID");
                            if (!data.isEmpty()) {
                                for (String id : data) {
                                    db.collection("games").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            if (task.isSuccessful()) {
                                                Game game = document.toObject(Game.class);
                                                game.setGameId(document.getId());
                                                gameList.add(game);

                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {

        @NonNull
        @Override
        public GamesAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findmessage_recycler_layout, parent, false);
            return new GamesAdapter.GamesViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return gameList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull GamesAdapter.GamesViewHolder holder, int position) {
            Game game = gameList.get(position);
            holder.setUpGameRow(game);
        }

        class GamesViewHolder extends RecyclerView.ViewHolder {
            Game mGame;
            TextView textViewGameName, textViewGameDate, textViewGameTime;

            public GamesViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewGameName = itemView.findViewById(R.id.textViewNotificationGameName);
                textViewGameDate = itemView.findViewById(R.id.textViewNotificationTime);
                textViewGameTime = itemView.findViewById(R.id.textViewNotificationMsg);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoChatMessage(mGame, PreviousViewState.INBOX);
                    }
                });

            }

            public void setUpGameRow(Game game) {
                this.mGame = game;
                textViewGameName.setText(mGame.getGameName());
                textViewGameDate.setText(mGame.gameDate.toString());
                textViewGameTime.setText("Group Message");
            }
        }
    }


    MessageListFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (MessageListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface MessageListFragmentListener{
        void gotoGameItem(Game game);
        void gotoChatMessage(Game game, PreviousViewState viewState);
    }
}
