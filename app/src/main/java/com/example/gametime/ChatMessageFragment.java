package com.example.gametime;

import com.example.gametime.MainActivity.PreviousViewState;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This ChatMessageFragment Class creates a reusable user interface for the Chat Message Page of the app.
 */
public class ChatMessageFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();                    //FirebaseAuth Instance
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();    //FirebaseUser Instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();             //FireBaseFireStore Instance - Database
    private Game game;                                                          //Game Instance
    private PreviousViewState viewState;                                        //Enum PreviouseViewState - previous view state

    /**
     * First ChatMessageFragment Class Constructor
     * @param game information of game
     */
    public ChatMessageFragment(Game game){ this.game = game;}

    /**
     * Second ChatMessageFragment Class Constructor
     * @param game This holds the information of a game
     * @param viewState This holds the previous view state
     */
    public ChatMessageFragment(Game game, PreviousViewState viewState) {
        this.game = game;               //set game to game
        this.viewState = viewState;     //set viewState to viewState
    }

    /**
     * This method is used to start the activity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;                                      //RecyclerView Instance
    private RecyclerView.LayoutManager layoutManager;                       //RecyclerView LayoutManager Instance

    private ArrayList<Message> messagesList = new ArrayList<Message>();           //Message ArrayList - list of messages
    private MessagesAdapter adapter;                                        //MessageAdapter Instance

    private EditText editTextInput;                                         //EditText - for user input
    private Button sendMessageButton;                                       //Button - send message button
    private ImageButton backButton;                                         //ImageButton - back button

    /**
     * This creates visual on what is shown on the screen.
     * @param inflater this instantiate the contents of layout XML files
     * @param container this acts as a container
     * @param savedInstanceState - android activity
     * @return the view of all items that will be shown onto the screen of the user
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_chat_message, container, false);
        recyclerView = view.findViewById(R.id.findNotificationRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(messagesList);
        recyclerView.setAdapter(adapter);
        backButton = view.findViewById(R.id.imageBackButtonToGame);
        sendMessageButton = view.findViewById(R.id.send_message);
        editTextInput = view.findViewById(R.id.input);

        setupMessagesListener(); //This will setup all the messages

        //sets the send message button as clickable button to send messages
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> messageData = new HashMap<>();                     //Map<String, Object> of messaged data
                messageData.put("createdByName", user.getDisplayName());            //add name to message data map
                messageData.put("createdByUid", mAuth.getCurrentUser().getUid());   //add user id to message data map
                messageData.put("msgTime", (long) new Date().getTime());            //add message time to message data map
                messageData.put("textMsg", editTextInput.getText().toString());     //add text message to message data map

                //Add the message data to the FireBaseFireStore Database
                db.collection("games").document(game.getGameId()).collection("ChatRoom")
                        .add(messageData);

                editTextInput.setText("");                                              //EdiText - reset user input box
            }
        });

        //sets the back button as clickable button to go back to previous page
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //checks if the previous view state was a inbox
                if(viewState == PreviousViewState.INBOX){
                    mListener.gotoInbox();              //go to inbox page
                } else { //otherwise...
                    mListener.gotoGameItem(game);       //go to Game Item page
                }
            }
        });

        return view; //returns view
    }

    /**
     * This method sets up the messages that is retrieved from the FireBaseFireStore Database
     */
    private void setupMessagesListener() {
        db.collection("games").document(game.getGameId()).collection("ChatRoom").orderBy("msgTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error == null) {    //checks if exception error equals to null
                    messagesList.clear();   //clear message list
                    for (QueryDocumentSnapshot document : querySnapshot) {  //retrieve document that contains message data
                        Message msg = document.toObject(Message.class);     //convert document to message object
                        msg.setMsgId(document.getId());                     //set document id to message id
                        messagesList.add(msg);                              //add message to messsage list
                    }
                    adapter.notifyDataSetChanged();                         //notifies adapter of data set changed
                } else {
                    error.printStackTrace();                                //print stack trace
                }
            }
        });
    }

    /**
     * This is MessageAdapter Class
     */
    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

        private ArrayList<Message> messagesList;    //Message ArrayList - list of messages

        /**
         * Default MessageAdapter Class Constructor
         */
        public MessagesAdapter() { }

        /**
         * MessageAdapter Class Constructor
         * @param messageList this is a list of messages
         */
        public MessagesAdapter(ArrayList<Message> messageList) {
            this.messagesList = messageList; //set messagelist to messageList
        }

        /**
         * This method is used to get a new view
         * @param parent this is a view group
         * @param viewType this is a integer
         * @return
         */
        @NonNull
        @Override
        public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findmessage_recycler_layout, parent, false);
            return new MessagesAdapter.MessagesViewHolder(view);
        }

        /**
         * This method is used to recycle view and bind it with new data
         * @param holder this is the new view
         * @param position the position of the message from the list
         */
        @Override
        public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {
            Message msg = messagesList.get(position); //get the position of the message from the list
            holder.setUpMessageRow(msg); //new view sets up messages in rows
        }

        /**
         * Returns the total items that are in the message list
         * @return the number of items from the message list
         */
        @Override
        public int getItemCount() {
            return messagesList.size();
        }

        /**
         * This is the MessageViewHolder
         */
        class MessagesViewHolder extends RecyclerView.ViewHolder {
            private Message msg;                                                        //Message Instance
            private TextView textViewTextMsg, textViewMsgTime, textViewPostedByName;    //TextView Instances

            /**
             * MessageViewHolder Class Constructor
             * @param itemView the view item
             */
            public MessagesViewHolder(@NonNull View itemView) {
                super(itemView);

                //Initializing objects
                textViewTextMsg = itemView.findViewById(R.id.textViewNotificationMsg);
                textViewMsgTime = itemView.findViewById(R.id.textViewNotificationTime);
                textViewPostedByName = itemView.findViewById(R.id.textViewNotificationGameName);
            }

            /**
             * This method sets up message row
             * @param msg the message object
             */
            public void setUpMessageRow(Message msg){
                this.msg = msg;                                                 //set msg to msg
                textViewTextMsg.setText(msg.getTextMsg());                      //set msg to textView
                textViewMsgTime.setText(DateFormat.format("MM-dd-yyyy (h:mm aa)",
                        msg.getMsgTime()));                                     //set formatted message time to textView
                textViewPostedByName.setText(msg.getCreatedByName());           //set created by name to textView
            }
        }
    }

    ChatMessageListener mListener; //Declared ChatMessageListener Interface


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ChatMessageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    /**
     * ChatMessageListener Interface
     */
    interface ChatMessageListener{
        void gotoGameItem(Game game);   //go to game item page
        void gotoInbox();               //go to inbox page
    }

}
