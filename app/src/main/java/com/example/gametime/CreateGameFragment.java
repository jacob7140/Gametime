package com.example.gametime;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateGameFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText editTextName, editTextAddress, editTextNumberPeople, editTextTime;
    CalendarView calendarView;
    ImageButton imageBack;
    String gameDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
        editTextName = view.findViewById(R.id.editTextCreateName);
        editTextAddress = view.findViewById(R.id.editTextCreateAddress);
        editTextNumberPeople = view.findViewById(R.id.editTextCreateNumberPeople);
        editTextTime = view.findViewById(R.id.editTextCreateTime);

        calendarView = view.findViewById(R.id.calendarViewCreate);
        imageBack  =view.findViewById(R.id.imageButtonCreateAccountBack);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoHome();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String setYear = String.valueOf(year);
                String setMonth = String.valueOf(month + 1);
                String setDay = String.valueOf(dayOfMonth);

                gameDate = setYear + "/" + setMonth + "/" + setDay;

            }
        });


        view.findViewById(R.id.buttonCreatePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter= new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                String currentDate = formatter.format(date);

                String gameName = editTextName.getText().toString();
                String address = editTextAddress.getText().toString();
                String numberPeople = editTextNumberPeople.getText().toString();
                String time = editTextTime.getText().toString();


                Map<String, Object> gamePost = new HashMap<>();
                gamePost.put("createdByName", user.getDisplayName());
                gamePost.put("createdAt", Timestamp.now());
                gamePost.put("createdByUid", user.getUid());
                gamePost.put("gameName", gameName);
                gamePost.put("address", address);
                gamePost.put("numberPeople", numberPeople);
                gamePost.put("gameTime", time);
                gamePost.put("gameDate", gameDate);

                if (gameName.isEmpty() | address.isEmpty() | numberPeople.isEmpty() | time.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("games").add(gamePost).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(getActivity(), "Created game post", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Could not add game post", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
                }
                mListener.gotoHome();
            }

        });

        return view;
    }

    CreateGameListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateGameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface CreateGameListener{
        void gotoHome();
    }
}