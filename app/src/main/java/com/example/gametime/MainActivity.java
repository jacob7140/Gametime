/*
TODO: Add a section for people to comment on each game item.
TODO: Implement favorite games
TODO: Implement user profile fragments
TODO: Implement notifications - New game posted, new comment, change to game you registered for
TODO: Delete games if date is passed
 */

package com.example.gametime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements CreateAccountFragment.RegisterListener, OpeningFragment.OpeningListner, LoginFragment.LoginListener, HomeFragment.HomeListener, CreateGameFragment.CreateGameListener,
        GamesListFragment.GamesListFragmentListener, GameItemFragment.GameItemListener, ChatMessageFragment.ChatMessageListener, EditGameFragment.EditGameListener, CollectionInboxFragment.CollectionInboxListener,
        MessageListFragment.MessageListFragmentListener, NotificationListFragment.NotificationListFragmentListener, ProfileFragment.ProfileListener, AccountInfoFragment.AccountInfoListener {

    private static final String CHANNEL_ID = "Notification Channel";
    FirebaseAuth mAuth;
    public enum PreviousViewState {
        MAIN,
        GAMEITEM,
        GAMESLIST,
        INBOX
    }

    public MainActivity(){};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rootView, new OpeningFragment())
                    .commit();
        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new HomeFragment()).commit();
        }

    }


    //testing

    @Override
    public void gotoCreateAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new CreateAccountFragment())
                .commit();
    }

    @Override
    public void gotoCreateGame() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateGameFragment()).commit();
    }

    public void gotoEditGame(Game game) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new EditGameFragment(game)).commit();
    }

    @Override
    public void gotoEditGame(Game game, PreviousViewState viewState) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new EditGameFragment(game, viewState)).commit();
    }

    @Override
    public void gotoFindGame() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new GamesListFragment()).commit();
    }

    @Override
    public void gotoLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void gotoPersonalInfo() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new AccountInfoFragment()).commit();
    }

    @Override
    public void gotoHostedGames() {

    }

    @Override
    public void gotoUpcomingGames() {

    }

    @Override
    public void gotoHome() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new HomeFragment()).commit();
    }

    @Override
    public void gotoGameItem(Game game) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, GameItemFragment.newInstance(game)).commit();
    }

    @Override
    public void gotoOpening() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new OpeningFragment()).commit();
    }

    @Override
    public void gotoGameList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new GamesListFragment()).commit();
    }

    @Override
    public void gotoChatMessage(Game game, PreviousViewState viewState) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ChatMessageFragment(game, viewState)).commit();
    }

    public void gotoInbox(){
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CollectionInboxFragment()).commit();
    }

    @Override
    public void goToProfile() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ProfileFragment()).commit();
    }

    public void gotoMessageList(){
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new MessageListFragment()).commit();
    }

    public void gotoNotificationList(){
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new NotificationListFragment()).commit();
    }
}