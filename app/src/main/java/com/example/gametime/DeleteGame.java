package com.example.gametime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class DeleteGame implements Serializable {

    public void deleteGamePost(Game mGame, Context context, FirebaseFirestore db) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Are you sure you want to delete this game?");
        builder.setMessage("By confirming, you will remove this game post from everyone's list!");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("games").document(mGame.getGameId()).delete();
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
}
