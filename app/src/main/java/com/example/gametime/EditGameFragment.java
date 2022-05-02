package com.example.gametime;

import static com.example.gametime.MainActivity.PreviousViewState;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditGameFragment extends Fragment {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String TAG = "data";
    private Game game;
    private PreviousViewState viewState;

    public EditGameFragment(Game game) { this.game = game; }

    public EditGameFragment(Game game, PreviousViewState viewState) {
        this.game = game;
        this.viewState = viewState;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView title;
    EditText editTextName, editTextAddress, editTextNumberPeople, editTextTime;
    CalendarView calendarView;
    ImageButton imageBack;
    Button updateButton;
    Spinner dropdown;
    String gameDate;
    String preferenceSelection;
    ArrayList<String> likedBy = new ArrayList<>();

    Calendar calendar, now;
    int year, month, day;
    long differenceInTime, differenceInDays;
    String[] dateString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);
        title = view.findViewById(R.id.textView2);
        editTextName = view.findViewById(R.id.editTextCreateName);
        editTextAddress = view.findViewById(R.id.editTextCreateAddress);
        editTextNumberPeople = view.findViewById(R.id.editTextCreateNumberPeople);
        editTextTime = view.findViewById(R.id.editTextCreateTime);

        calendarView = view.findViewById(R.id.calendarViewCreate);
        imageBack  =view.findViewById(R.id.imageButtonCreateAccountBack);
        updateButton = view.findViewById(R.id.buttonCreatePost);
        dropdown = view.findViewById(R.id.pickerPreferenceCreateGame);

        title.setText("Edit Game");
        editTextName.setText(game.getGameName());
        editTextAddress.setText(game.getAddress());
        editTextNumberPeople.setText(game.getNumberPeople());
        editTextTime.setText(game.getGameTime());

        dateString = game.getGameDate().split("/");
        year = Integer.parseInt(dateString[0]);
        month = Integer.parseInt(dateString[1]);
        day = Integer.parseInt(dateString[2]);

        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendarView.setDate(calendar.getTimeInMillis());
        updateButton.setText("Update Post");

        if(gameDate == null) {
            updateCalendarInfo();
            gameDate = year + "/" + month + "/" + day;
        }

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewState == PreviousViewState.GAMEITEM){
                    mListener.gotoGameItem(game);
                } else if(viewState == PreviousViewState.GAMESLIST){
                    mListener.gotoFindGame();
                } else {
                    mListener.gotoHome();
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String setYear = String.valueOf(year);
                String setMonth = String.valueOf(month + 1);
                String setDay = String.valueOf(dayOfMonth);

                gameDate = setYear + "/" + setMonth + "/" + setDay;
                calendar.set(year, month, dayOfMonth);
                updateCalendarInfo();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.preferences_array_changed, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(adapter.getItemViewType(0));
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

        switch(game.getGameType()){
            case "Strategy":
                dropdown.setSelection(0);
                break;
            case "Co-op":
                dropdown.setSelection(1);
                break;
            case "Mystery":
                dropdown.setSelection(2);
                break;
            case "Combat":
                dropdown.setSelection(3);
                break;
            case "Legacy":
                dropdown.setSelection(4);
                break;
            default:
                break;
        }

        view.findViewById(R.id.buttonCreatePost).setOnClickListener(new View.OnClickListener() {
            private DocumentReference doc;

            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss z");
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
                gamePost.put("likedBy", likedBy);

                db.collection("games").document(game.getGameId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        gamePost.put("signedUp", documentSnapshot.get("signedUp"));
                    }
                });

                gamePost.put("gameType", preferenceSelection);

                if (gameName.isEmpty() | address.isEmpty() | numberPeople.isEmpty() | time.isEmpty() | gameDate == null | preferenceSelection.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Can not be empty", Toast.LENGTH_SHORT).show();
                } else if (gameDate != null) {
                    if (differenceInDays <= 0) {
                        Toast.makeText(getActivity(), "Date needs to be after the current date", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("games").document(game.getGameId()).update(gamePost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                new Notification(user.getDisplayName(), user.getUid(), gameName).sendNotificationTo(Notification.Notification_Type.UPDATED);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Could not update game post", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });

                        if(viewState == PreviousViewState.GAMEITEM){
                            mListener.gotoGameItem(game);
                        } else if(viewState == PreviousViewState.GAMESLIST) {
                            mListener.gotoFindGame();
                        } else {
                            mListener.gotoHome();
                        }

                    }
                }
            }

        });

        return view;
    }

    private void updateCalendarInfo(){
        Calendar now = Calendar.getInstance();
        long currentDay = now.getTimeInMillis() / (24 * 60 * 60 * 1000);
        long selectedDay = calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
        differenceInDays = selectedDay - currentDay;
    }

    EditGameListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (EditGameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface EditGameListener{
        void gotoHome();
        void gotoGameItem(Game game);
        void gotoFindGame();
    }
}