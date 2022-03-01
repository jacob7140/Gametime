package com.example.gametime;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;

public class GameItemFragment extends Fragment {

    private static final String ARG_PARAM_AUTH = "ARG_PARAM_AUTH";
    private static final String ARG_PARAM_GAME = "ARG_PARAM_GAME";
    private Game mGame;

    public GameItemFragment() {
        // Required empty public constructor
    }

    public static GameItemFragment newInstance(Game game) {
        GameItemFragment fragment = new GameItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGame = (Game) getArguments().getSerializable(ARG_PARAM_GAME);
        }
    }

    TextView gameName, location, gameDate, numPeople, seatsLeft, datePosted, postedBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_item, container, false);

        gameName = view.findViewById(R.id.textViewItemGameName);
        location = view.findViewById(R.id.textViewItemLocation);
        gameDate = view.findViewById(R.id.textViewItemDate);
        numPeople = view.findViewById(R.id.textViewItemNumPeople);
        seatsLeft = view.findViewById(R.id.textViewItemSeatsLeft);
        datePosted = view.findViewById(R.id.textViewItemDatePosted);
        postedBy = view.findViewById(R.id.textViewItemPostedBy);

        gameName.setText(mGame.getGameName());
        location.setText(mGame.getAddress());
        gameDate.setText(mGame.getGameDate());
        numPeople.setText(mGame.getNumberPeople());
        // TODO: Figure this out
        seatsLeft.setText("Seats left");
        // TODO: Fix format
        datePosted.setText(mGame.getCreatedAt().toString());
        postedBy.setText(mGame.getCreatedByName());

        //TODO: Implement Sign up

        return view;
    }
}