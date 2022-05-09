package com.example.gametime;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    Spinner dropdown;
    Date gameDate;
    String preferenceSelection;
    ArrayList<String> likedBy = new ArrayList<>();
    ArrayList<String> signedUp = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> gamePreferenceList;
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

        dropdown = view.findViewById(R.id.pickerPreferenceCreateGame);

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

                String gameDateS = setYear + "/" + setMonth + "/" + setDay + " at " + "11:59 PM UTC-4";
                try {
                    gameDate = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm aa").parse(gameDateS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        gamePreferenceList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, gamePreferenceList);
        gamePreferenceList.add("Loading...");
        db.collection("gamePreferences").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) {
                gamePreferenceList.clear();
                gamePreferenceList.add("Loading...");
                for(QueryDocumentSnapshot documentSnapshot : querySnapshots){
                    gamePreferenceList.add((String) documentSnapshot.get("Type"));
                }
                gamePreferenceList.remove("Loading...");
                gamePreferenceList.add("Other");
                Log.d(TAG, "onSuccess: " + adapter.toString());
                preferenceSelection = gamePreferenceList.get(0);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " +  dropdown.getCount());
            }
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int getId = parent.getSelectedItemPosition();
                preferenceSelection = String.valueOf(parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#FFFFFF"));
//                Log.d(TAG, preferenceSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

                signedUp.add(user.getUid());

                Map<String, Object> gamePost = new HashMap<>();
                gamePost.put("createdByName", user.getDisplayName());
                gamePost.put("createdAt", Timestamp.now());
                gamePost.put("createdByUid", user.getUid());
                gamePost.put("gameName", gameName);
                gamePost.put("address", address);
                gamePost.put("numberPeople", numberPeople);
                gamePost.put("gameTime", time);
                gamePost.put("gameDate", gameDate);
                gamePost.put("likedBy", likedBy);
                gamePost.put("signedUp", signedUp);
                gamePost.put("gameType", preferenceSelection);

                if (gameName.isEmpty() | address.isEmpty() | numberPeople.isEmpty() | time.isEmpty() | gameDate == null | preferenceSelection.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("games").add(gamePost).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            gamePost.put("gameID", documentReference.getId());
                            db.collection("games").document(documentReference.getId()).update(gamePost);

                            Map<String, Object> userdata = new HashMap<String, Object>();
                            userdata.put("SignedGameID", FieldValue.arrayUnion(documentReference.getId()));
                            db.collection("userdata").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        db.collection("userdata").document(user.getUid()).update(userdata);
                                    } else {
                                        db.collection("userdata").document(user.getUid()).set(userdata);
                                    }
                                }
                            });

                            new Notification(user.getDisplayName(), user.getUid(), gameName).sendNotificationTo(Notification.Notification_Type.CREATED);
//                            Toast.makeText(getActivity(), "Created game post", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Could not add game post", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
                    mListener.gotoHome();
                }
            }

        });

        return view;
    }

    //testing

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