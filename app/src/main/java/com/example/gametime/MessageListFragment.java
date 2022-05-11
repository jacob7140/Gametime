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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This MessageListFragment Class creates a reusable user interface for the Message List Page of the app.
 */
public class MessageListFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();                    //FirebaseAuth Instance
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();    //FirebaseUser Instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();             //FireBaseFireStore Instance - Database

    /**
     * This method is used to start the activity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;                                                  //RecyclerView Instance
    private RecyclerView.LayoutManager layoutManager;                                   //Recycler View LayoutManager Instance
    private GamesAdapter adapter;                                                       //GameAdapter Instance
    private ArrayList<Game> gameList = new ArrayList<Game>();                           //Game ArrayList - list of game events
    private SimpleDateFormat formatterDate = new SimpleDateFormat("MM-dd-yyyy"); //Date format
    /**
     * This creates visual on what is shown on the screen.
     * @param inflater this instantiate the contents of layout XML files
     * @param container this acts as a container
     * @param savedInstanceState - android activity
     * @return the view of all items that will be shown onto the screen of the user
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        recyclerView = view.findViewById(R.id.findMessageRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter();
        recyclerView.setAdapter(adapter);
        setupGamesListener(); //this will setup all game's group messages

        return view; //return view
    }

    /**
     * This method sets up the games that is retrieved from the FirebaseFireStore Database
     */
    private void setupGamesListener() {
        db.collection("userdata").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){                                                            //checks if the task is successful to retrieve the data
                    DocumentSnapshot doc = task.getResult();                                        //set results of the task document
                    if(doc.exists()) {                                                              //check if the document exists
                        if (doc.get("SignedGameID") != null) {                                      //checks if signedGameID is not equal to null
                            ArrayList<String> data = (ArrayList<String>) doc.get("SignedGameID");   //store signed game id list
                            if (!data.isEmpty()) {                                                  //checks if data is not empty
                                for (String id : data) { //loops through the data

                                    //get data from the database for games
                                    db.collection("games").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {                          //checks if it is successful
                                                DocumentSnapshot document = task.getResult();   //get document results
                                                if(document.exists()) {                         //Checks if the document exists
                                                    Game game = document.toObject(Game.class);  //convert document to game object and store it to game
                                                    game.setGameId(document.getId());           //set game id
                                                    gameList.add(game);                         //add game to list
                                                } else {
                                                    //If the document id does not exist then remove the id from userdata SignedGameID List
                                                    HashMap<String, Object> updateSignedGameIDList = new HashMap<>();   //declared HashMap object
                                                    updateSignedGameIDList.put("SignedGameID", FieldValue.arrayRemove(id)); //add document id to HashMap
                                                    db.collection("userdata").document(user.getUid()).update(updateSignedGameIDList); //update userdata database
                                                }
                                            }
                                            adapter.notifyDataSetChanged();                 //adapter - notify data change
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

    /**
     * This is GamesAdapter Class
     */
    class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {

        /**
         * This method is used to get a new view
         * @param parent this is a view group
         * @param viewType this is a integer value
         * @return
         */
        @NonNull
        @Override
        public GamesAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findmessage_recycler_layout, parent, false);
            return new GamesAdapter.GamesViewHolder(view);
        }

        /**
         * Returns the total items that are in the game list
         * @return the number of items from the game list
         */
        @Override
        public int getItemCount() {
            return gameList.size();
        }

        /**
         * This method is used to recycle view and bind it with new data
         * @param holder this is the new view
         * @param position the position of the game from the list
         */
        @Override
        public void onBindViewHolder(@NonNull GamesAdapter.GamesViewHolder holder, int position) {
            Game game = gameList.get(position);
            holder.setUpGameRow(game);
        }

        /**
         * This is a GamesViewHolder
         */
        class GamesViewHolder extends RecyclerView.ViewHolder {
            private Game mGame;                                                     //Game Instance
            private TextView textViewGameName, textViewGameDate, textViewGameTime;  //TextView Instances

            /**
             * GamesViewHolder Class Constructor
             * @param itemView - view
             */
            public GamesViewHolder(@NonNull View itemView) {
                super(itemView);

                //Initialized objects
                textViewGameName = itemView.findViewById(R.id.textViewNotificationGameName);
                textViewGameDate = itemView.findViewById(R.id.textViewNotificationTime);
                textViewGameTime = itemView.findViewById(R.id.textViewNotificationMsg);


                //set it as clickable itemView
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoChatMessage(mGame, PreviousViewState.INBOX); //go to inbox page
                    }
                });

            }

            /**
             * This method sets up game row
             * @param game the game object
             */
            public void setUpGameRow(Game game) {

                this.mGame = game;                              //set game to mGame
                textViewGameName.setText(mGame.getGameName());  //set game name to textView
                String gameDate = formatterDate.format(mGame.getGameDate().toDate());
                textViewGameDate.setText(gameDate);              //set game date to textView
                textViewGameTime.setText("Group Message");      //set "Group Message" to textView

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
