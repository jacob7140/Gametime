package com.example.gametime;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    String address, createdByName, gameName, gameDate, gameTime, numberPeople, createdByUid, gameId, gameType;
    Timestamp createdAt;
    ArrayList<String> likedBy = new ArrayList<>();
    ArrayList<String> signedUp = new ArrayList<>();

    public Game(String address, String createdByName, String gameName, String gameDate, String gameTime, String numberPeople, String createdByUid,
                String gameId, Timestamp createdAt, ArrayList<String> likedBy, ArrayList<String> signedUp, String gameType) {
        this.address = address;
        this.createdByName = createdByName;
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameTime = gameTime;
        this.numberPeople = numberPeople;
        this.createdByUid = createdByUid;
        this.gameId = gameId;
        this.createdAt = createdAt;
        this.likedBy = likedBy;
        this.signedUp = signedUp;
        this.gameType = gameType;
    }

    public Game(){

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(String numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public ArrayList<String> getSignedUp() {
        return signedUp;
    }

    public void setSignedUp(ArrayList<String> signedUp) {
        this.signedUp = signedUp;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "Game{" +
                "address='" + address + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", gameName='" + gameName + '\'' +
                ", gameDate='" + gameDate + '\'' +
                ", gameTime='" + gameTime + '\'' +
                ", numberPeople='" + numberPeople + '\'' +
                ", createdByUid='" + createdByUid + '\'' +
                ", gameId='" + gameId + '\'' +
                ", gameType='" + gameType + '\'' +
                ", createdAt=" + createdAt +
                ", likedBy=" + likedBy +
                ", signedUp=" + signedUp +
                '}';
    }
}
