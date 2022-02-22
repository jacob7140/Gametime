package com.example.gametime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements CreateAccountFragment.RegisterListener, OpeningFragment.OpeningListner, LoginFragment.LoginListener {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new OpeningFragment()).commit();
    }

//    @Override
//    public void gotoOpening() {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.rootView, new OpeningFragment())
//                .commit();
//    }


    @Override
    public void gotoCreateAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rootView, new CreateAccountFragment())
                .commit();
    }

    @Override
    public void gotoLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void gotoHome() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new HomeFragment()).commit();
    }
}