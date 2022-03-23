package com.example.gametime;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Date;


public class Message implements Serializable {

    private String textMsg, createdByName, createdByUid, msgId;
    private long msgTime;
    public Message(){ }

    public Message(String textMsg, String createdByName, String createdByUid){
        this.textMsg = textMsg;
        this.createdByName = createdByName;
        this.createdByUid = createdByUid;
        this.msgId = "";
        msgTime = new Date().getTime();
    }

    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
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

    public long getMsgTime(){ return msgTime; }

    public void setMsgTime(long msgTime) { this.msgTime = msgTime; }

    public String getMsgId() { return msgId; }

    public void setMsgId(String msgId) { this.msgId = msgId; }

    @Override
    public String toString() {
        return "Message{" +
                "textMsg='" + textMsg + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", createdByUid='" + createdByUid + '\'' +
                ", msgTime=" + msgTime +
                '}';
    }
}
