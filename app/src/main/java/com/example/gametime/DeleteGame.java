package com.example.gametime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                        deleteAllMessagesWithinTheGameChatRoom(mGame, db);
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

    private void deleteAllMessagesWithinTheGameChatRoom(Game mGame, FirebaseFirestore db) {
        db.collection("games").document(mGame.getGameId()).collection("ChatRoom")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(final QueryDocumentSnapshot doc : task.getResult()){
                        if(doc.exists()){
                            db.collection("games").document(mGame.getGameId())
                                    .collection("ChatRoom").document(doc.getId())
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) { /* I will add text log here later on */ }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { /* I will add text log here later on */}
                            });
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { /* I will add text log here later on */ }
        });
    }
}
