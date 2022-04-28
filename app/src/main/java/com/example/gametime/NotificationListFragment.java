package com.example.gametime;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationListFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Notification> notificationList = new ArrayList<Notification>();
    private NotificationsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        recyclerView = view.findViewById(R.id.findNotificationRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotificationsAdapter();
        recyclerView.setAdapter(adapter);
        setupNotificationsListener();
        return view;
    }

    private void setupNotificationsListener() {
        db.collection("userdata").document(user.getUid()).collection("notifications").orderBy("notificationTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String createdByName = String.valueOf(document.get("createdByName"));
                        String createdByUid = String.valueOf(document.get("createdByUid"));
                        String gameName = String.valueOf(document.get("gameName"));
                        String notificationMsg = String.valueOf(document.get("notificationMsg"));
                        Long notificationTime = Long.parseLong(document.get("notificationTime").toString());
                        Notification notification = new Notification(createdByName, createdByUid, gameName, notificationMsg, notificationTime);
                        notificationList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    error.printStackTrace();
                }
            }
        });
    }

    class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

        @NonNull
        @Override
        public NotificationsAdapter.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findnotification_recycler_layout, parent, false);
            return new NotificationsAdapter.NotificationsViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationsAdapter.NotificationsViewHolder holder, int position) {
            Notification notification = notificationList.get(position);
            holder.setUpNotificationRow(notification);
        }

        class NotificationsViewHolder extends RecyclerView.ViewHolder {
            Notification notification;
            TextView textViewGameName, textViewNotificationDate, textViewNotificationMsg, textViewHostedByName;

            public NotificationsViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewGameName = itemView.findViewById(R.id.textViewNotificationGameName);
                textViewNotificationDate = itemView.findViewById(R.id.textViewNotificationTime);
                textViewNotificationMsg = itemView.findViewById(R.id.textViewNotificationMsg);
                textViewHostedByName = itemView.findViewById(R.id.textViewHostedBy);
            }

            public void setUpNotificationRow(Notification notification) {
                this.notification = notification;
                String gameName = notification.getGameName();
                if(gameName.length() > 10)
                    gameName = gameName.substring(0,10) + "...";

                textViewGameName.setText(gameName);
                textViewNotificationDate.setText(DateFormat.format("MM-dd-yyyy (h:mm aa)",
                        notification.getNotificationTime()));
                textViewNotificationMsg.setText(notification.getNotificationMsg());

                if(notification.getCreatedByUid().equals(user.getUid()))
                    textViewHostedByName.setText("Hosted By: (You)");
                else
                    textViewHostedByName.setText("Hosted By: " + notification.getCreatedByName());
            }
        }
    }

    NotificationListFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (NotificationListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface NotificationListFragmentListener{
        void gotoGameItem(Game game);
        void gotoChatMessage(Game game, MainActivity.PreviousViewState viewState);
    }
}
