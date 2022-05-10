package com.example.gametime;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * This Message Class
 */
public class Message implements Serializable {

    //Declared Variables of this class
    private String textMsg, createdByName, createdByUid, msgId;
    private long msgTime;

    /**
     * Default Message Class Constructor
     */
    public Message(){ }

    /**
     * Message Class Constructor
     * @param textMsg text message of a user
     * @param createdByName the message is created under this name
     * @param createdByUid the message is created under this user id number
     */
    public Message(String textMsg, String createdByName, String createdByUid){
        this.textMsg = textMsg;                 //set textMsg to textMsg
        this.createdByName = createdByName;     //set createdByName to createdByName
        this.createdByUid = createdByUid;       //set createdByUid to createdByUid
        this.msgId = "";                        //initialized empty string
        msgTime = new Date().getTime();         //set current time to msgTime
    }

    /**
     * Returns the text message
     * @return text message
     */
    public String getTextMsg() {
        return textMsg;
    }

    /**
     * Set the text message
     * @param textMsg the text message that is being set
     */
    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    /**
     * Returns the user's name that created the message
     * @return name of the user
     */
    public String getCreatedByName() {
        return createdByName;
    }

    /**
     * Set the name of the user of the message
     * @param createdByName the name of the user that is being set
     */
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * Returns the user's id that created the message
     * @return user identification number
     */
    public String getCreatedByUid() {
        return createdByUid;
    }

    /**
     * Set user identification number of the message
     * @param createdByUid the user identification number
     */
    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    /**
     * Returns the message time as long value
     * @return long value of message time
     */
    public long getMsgTime(){ return msgTime; }

    /**
     * Set message time of the message
     * @param msgTime
     */
    public void setMsgTime(long msgTime) { this.msgTime = msgTime; }

    /**
     * Returns the message identification
     * @return message id
     */
    public String getMsgId() { return msgId; }

    /**
     * Set the message identification
     * @param msgId message identification
     */
    public void setMsgId(String msgId) { this.msgId = msgId; }

    /**
     * Returns a string of information of this class
     * @return a string of text
     */
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
