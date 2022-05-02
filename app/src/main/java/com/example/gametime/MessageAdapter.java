package com.example.gametime;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private ArrayList<Message> messagesList;

    public MessagesAdapter() {

    }

    public MessagesAdapter(ArrayList<Message> messageList) {
        this.messagesList = messageList;
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.findmessage_recycler_layout, parent, false);
        return new MessagesAdapter.MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {
        Message msg = messagesList.get(position);
        holder.setUpMessageRow(msg);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {
        private Message msg;
        private TextView textViewTextMsg, textViewMsgTime, textViewPostedByName;
        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTextMsg = itemView.findViewById(R.id.textViewNotificationMsg);
            textViewMsgTime = itemView.findViewById(R.id.textViewNotificationTime);
            textViewPostedByName = itemView.findViewById(R.id.textViewNotificationGameName);
        }

        public void setUpMessageRow(Message msg){
            this.msg = msg;
            textViewTextMsg.setText(msg.getTextMsg());
            textViewMsgTime.setText(DateFormat.format("MM-dd-yyyy (h:mm aa)",
                    msg.getMsgTime()));
            textViewPostedByName.setText(msg.getCreatedByName());
        }
    }
}
