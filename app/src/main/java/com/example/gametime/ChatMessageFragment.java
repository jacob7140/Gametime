package com.example.gametime;

import com.example.gametime.MainActivity.PreviousViewState;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class ChatMessageFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";
    private Game game;
    private PreviousViewState viewState;
    public ChatMessageFragment(Game game){ this.game = game;}

    public ChatMessageFragment(Game game, PreviousViewState viewState) { this.game = game; this.viewState = viewState;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Message> messagesList = new ArrayList<Message>();
    private MessagesAdapter adapter;

    private EditText editTextInput;
    private Button sendMessageButton;
    private ImageButton backButton;

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

        setupMessagesListener();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("createdByName", user.getDisplayName());
                messageData.put("createdByUid", mAuth.getCurrentUser().getUid());
                messageData.put("msgTime", (long) new Date().getTime());
                messageData.put("textMsg", editTextInput.getText().toString());
                db.collection("games").document(game.getGameId()).collection("ChatRoom")
                        .add(messageData);
                editTextInput.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewState == PreviousViewState.INBOX){
                    mListener.gotoInbox();
                } else {
                    mListener.gotoGameItem(game);
                }
            }
        });

        return view;
    }

    private void setupMessagesListener() {
        db.collection("games").document(game.getGameId()).collection("ChatRoom").orderBy("msgTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    messagesList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Message msg = document.toObject(Message.class);
                        msg.setMsgId(document.getId());
                        messagesList.add(msg);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    error.printStackTrace();
                }
            }
        });
    }

    ChatMessageListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ChatMessageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface ChatMessageListener{
        void gotoGameItem(Game game);
        void gotoInbox();
    }

}
