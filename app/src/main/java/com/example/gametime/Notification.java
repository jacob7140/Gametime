package com.example.gametime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification implements Serializable{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";

    private String notificationMsg, createdByName, createdByUid, notificationId, gameName;
    private long notificationTime;

    public enum Notification_Type {
        CREATED,
        DELETED,
        UPDATED,
        CANCELED
    }

    public Notification(){ }

    public Notification(String createdByName, String createdByUid, String gameName){
        this.createdByName = createdByName;
        this.createdByUid = createdByUid;
        this.gameName = gameName;
        this.notificationId = "";
        this.notificationMsg = "";
        notificationTime = new Date().getTime();

    }

    public Notification(String createdByName, String createdByUid, String gameName, String notificationMsg, long notificationTime){
        this.createdByName = createdByName;
        this.createdByUid = createdByUid;
        this.gameName = gameName;
        this.notificationId = "";
        this.notificationTime = notificationTime;
        this.notificationMsg = notificationMsg;
    }

    public String getGameNotification(Notification_Type type){
        String notificationMsg = "";
        switch(type){
            case CREATED:
                notificationMsg = "You created a game event successfully!";
                break;
            case DELETED:
                notificationMsg = "You deleted the game event successfully!";
                break;
            case UPDATED:
                notificationMsg = "The game event information is updated. Check out the changes.";
                break;
            case CANCELED:
                notificationMsg = "The game event is canceled!";
                break;
            default:
                notificationMsg = "Error: Something went wrong!";
                break;
        }
        return notificationMsg;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String textMsg) {
        this.notificationMsg = notificationMsg;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public long getNotificationTime(){ return notificationTime; }

    public void setNotificationTime(long notifiedTime) { this.notificationTime = notificationTime; }

    public String getNotificationId() { return notificationId; }

    public void setNotificationId(String msgId) { this.notificationId = notificationId; }

    public String getGameName(){
        return gameName;
    }

    public void sendNotificationTo(Notification_Type type){
        notificationMsg = getGameNotification(type);
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("gameName", gameName);
        notificationData.put("createdByName", createdByName);
        notificationData.put("createdByUid", createdByUid);
        notificationData.put("notificationTime", notificationTime);
        notificationData.put("notificationMsg", notificationMsg);
        db.collection("userdata").document(createdByUid).collection("notifications").add(notificationData);
    }
    public void sendNotificationTo(String userId, Notification_Type type){
        notificationMsg = getGameNotification(type);
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("gameName", gameName);
        notificationData.put("createdByName", createdByName);
        notificationData.put("createdByUid", createdByUid);
        notificationData.put("notificationTime", notificationTime);
        notificationData.put("notificationMsg", notificationMsg);
        db.collection("userdata").document(userId).collection("notifications").add(notificationData);
    }

    @Override
    public String toString() {
        return "Message{" +
                "gameName='" + gameName + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", createdByUid='" + createdByUid + '\'' +
                ", notificationMsg='" + notificationMsg + '\'' +
                ", notificationTime=" + notificationTime + '\'' +
                '}';
    }
}
