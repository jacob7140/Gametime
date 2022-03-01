package com.example.gametime;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Game implements Serializable {
    String address, createdByName, gameName, gameDate, gameTime, numberPeople, createdByUid, gameId;
    Timestamp createdAt;

    public Game(String address, Timestamp createdAt, String createdByName, String createdByUid, String gameDate, String gameName, String gameTime, String numberPeople, String gameId) {
        this.address = address;
        this.createdByName = createdByName;
        this.gameName = gameName;
        this.gameDate = gameDate;
        this.gameTime = gameTime;
        this.numberPeople = numberPeople;
        this.createdByUid = createdByUid;
        this.gameId = gameId;
        this.createdAt = createdAt;
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
                ", createdAt=" + createdAt +
                '}';
    }
}
