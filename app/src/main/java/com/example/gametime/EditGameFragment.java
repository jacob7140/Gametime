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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This EditGameFragment Class creates a reusable user interface for the Edit Game Page of the app.
 */
public class EditGameFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();                    //FirebaseAuth Instance
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();    //FirebaseUser Instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();             //FireBaseFireStore Instance - Database
    final private String TAG = "data";                                          //String TAG - data
    private Game game;                                                          //Game Instance
    private PreviousViewState viewState;                                        //Enum PreviousViewState - previous view state


    /**
     * First EditGameFragment Class Constructor
     * @param game information of game
     */
    public EditGameFragment(Game game) { this.game = game; }

    /**
     * Second EditGameFragment Class Constructor
     * @param game This holds the information of a game
     * @param viewState This holds the previous view state
     */
    public EditGameFragment(Game game, PreviousViewState viewState) {
        this.game = game;
        this.viewState = viewState;
    }

    /**
     * This method is used to start the activity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TextView title;                                                             //TextView - title
    private EditText editTextName, editTextAddress, editTextNumberPeople, editTextTime; //EditView - Name, Address, Number of People, Time
    private CalendarView calendarView;                                                  //CalenderView - calendar
    private ImageButton imageBack;                                                      //ImageButton - back button
    private Button updateButton;                                                        //Button - update button
    private Spinner dropdown;                                                           //Spinner - dropdown object
    private String gameDate;                                                            //gameDate - the date when game event was created
    private String preferenceSelection;                                                 //preferenceSelection - current preference selected
    private ArrayList<String> likedBy = new ArrayList<>();                              //likedBy ArrayList - holds user id number
    private ArrayAdapter<String> adapter;                                               //adapter ArrayAdapter - used by dropdown object
    private ArrayList<String> gamePreferenceList;                                       //gamePreferenceList - a list of game genre

    private Calendar calendar, now;                                                     //Calendar - calendar and now
    private int year, month, day;                                                       //integer values - Year, Month, Day
    private long differenceInTime, differenceInDays;                                    //difference in time and in days between two different dates
    private String[] dateString;                                                        //dateString

    /**
     * This creates visual on what is shown on the screen.
     * @param inflater this instantiate the contents of layout XML files
     * @param container this acts as a container
     * @param savedInstanceState - android activity
     * @return the view of all items that will be shown onto the screen of the user
     */
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

        dateString = game.getGameDate().toString().split("/");
        year = Integer.parseInt(dateString[0]);
        month = Integer.parseInt(dateString[1]);
        day = Integer.parseInt(dateString[2]);

        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendarView.setDate(calendar.getTimeInMillis());
        updateButton.setText("Update Post");

        //checks if game date is equl to null
        if(gameDate == null) {
            updateCalendarInfo(); //call updateCalendarInfo method
            gameDate = year + "/" + month + "/" + day; //set date YYYY/MM/DD
        }

        //sets the back button as clickable button to go back to previous page
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks if the previous view state is equal to game time view state
                if(viewState == PreviousViewState.GAMEITEM){
                    mListener.gotoGameItem(game); //go to game item page

                    //checks if the previous view state is equal to game list view state
                } else if(viewState == PreviousViewState.GAMESLIST){
                    mListener.gotoFindGame(); //go to find game page

                } else { //otherwise
                    mListener.gotoHome(); //go to home page
                }
            }
        });

        //set the calender view as intractable calender
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String setYear = String.valueOf(year); //convert year to string
                String setMonth = String.valueOf(month + 1); //convert month to string
                String setDay = String.valueOf(dayOfMonth); //convert day to string

                gameDate = setYear + "/" + setMonth + "/" + setDay; //set date of YYYY/MM/DD
                calendar.set(year, month, dayOfMonth);//set calendar date
                updateCalendarInfo();//call updateCalendarInfo Method
            }
        });

        gamePreferenceList = new ArrayList<>(); //initialized game preference list
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, gamePreferenceList); //initialized adapter
        gamePreferenceList.add("Loading..."); //add first item to gamePreferenceList
        //call listner to FireBAseFireStore database for gamePreferenceList Data
        db.collection("gamePreferences").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) {
                gamePreferenceList.clear(); //clear list
                gamePreferenceList.add("Loading..."); //re-add first item
                int i = 0; //counter
                //loop through each document in query
                for(QueryDocumentSnapshot documentSnapshot : querySnapshots){
                    gamePreferenceList.add((String) documentSnapshot.get("Type")); //add game preference to list
                    if(game.getGameType().equals((String) documentSnapshot.get("Type"))){ //check if current game preference type is equal to game preference type from document data
                        preferenceSelection = game.getGameType(); //set current game preference selection
                        dropdown.setSelection(i); //set the dropdown selection of index of i
                    }
                    i++;//increment
                }
                gamePreferenceList.remove("Loading..."); //remove specified item from list
                gamePreferenceList.add("Other"); //add item to list
                adapter.notifyDataSetChanged(); //adapter notify data change
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);//set adapter drop down view resource
        dropdown.setAdapter(adapter);//set adapter to dropdown object
        dropdown.setSelection(adapter.getItemViewType(0)); //set selection

        //set interactive dropdown object
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int getId = parent.getSelectedItemPosition();
                preferenceSelection = String.valueOf(parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#FFFFFF"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //sets the create post button as clickable button
        view.findViewById(R.id.buttonCreatePost).setOnClickListener(new View.OnClickListener() {
            private DocumentReference doc; //declared document reference

            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy 'at' HH:mm:ss z"); //set format
                Date date = new Date(System.currentTimeMillis()); //set date
                String currentDate = formatter.format(date); //get current date

                String gameName = editTextName.getText().toString(); //set game name
                String address = editTextAddress.getText().toString(); //set address
                String numberPeople = editTextNumberPeople.getText().toString(); //set number of people
                String time = editTextTime.getText().toString(); //set time

                //gamePost Map<String, Object> holds data of the game event
                Map<String, Object> gamePost = new HashMap<>();
                gamePost.put("createdByName", user.getDisplayName());   //name
                gamePost.put("createdAt", Timestamp.now());             //time of now
                gamePost.put("createdByUid", user.getUid());            //user id
                gamePost.put("gameName", gameName);                     //game event name
                gamePost.put("address", address);                       //address
                gamePost.put("numberPeople", numberPeople);             //number of people
                gamePost.put("gameTime", time);                         //game event time when it will start
                gamePost.put("gameDate", gameDate);                     //game event date when it will start
                gamePost.put("likedBy", likedBy);                       //liked by which user list


                //retrieve data from FireBasFireStore database for game
                db.collection("games").document(game.getGameId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult(); //set results of the game
                        gamePost.put("signedUp", documentSnapshot.get("signedUp")); //add sign up list
                    }
                });

                gamePost.put("gameType", preferenceSelection); //add game preference selection to list

                //checks for empty or null values of these variables
                if (gameName.isEmpty() | address.isEmpty() | numberPeople.isEmpty() | time.isEmpty() | gameDate == null | preferenceSelection.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Can not be empty", Toast.LENGTH_SHORT).show(); //show message on screen

                    //checks if the game date is not equal to null
                } else if (gameDate != null) {

                    //checks difference in days is less than or equal to zero
                    if (differenceInDays <= 0) {
                        Toast.makeText(getActivity(), "Date needs to be after the current date", Toast.LENGTH_SHORT).show(); //show message on screen

                    } else { //otherwise
                        //retrieve data from FireBasFireStore database of games
                        db.collection("games").document(game.getGameId()).update(gamePost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //create notification type
                                new Notification(user.getDisplayName(), user.getUid(), gameName).sendNotificationTo(Notification.Notification_Type.UPDATED);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Could not update game post", Toast.LENGTH_SHORT).show(); //show message on screen
                                Log.d(TAG, e.toString());
                            }
                        });

                        //checks for previous view state
                        if(viewState == PreviousViewState.GAMEITEM){
                            mListener.gotoGameItem(game);   //go to game item page
                        } else if(viewState == PreviousViewState.GAMESLIST) {
                            mListener.gotoFindGame();   //go to find game page
                        } else {
                            mListener.gotoHome();   //go to home page
                        }

                    }
                }
            }

        });

        return view; //returns view
    }

    /**
     * This method updates calendar information
     */
    private void updateCalendarInfo(){
        Calendar now = Calendar.getInstance(); //get current calender information from calendar instance
        long currentDay = now.getTimeInMillis() / (24 * 60 * 60 * 1000); //set current day
        long selectedDay = calendar.getTimeInMillis() / (24 * 60 * 60 * 1000); //set selected day
        differenceInDays = selectedDay - currentDay; //difference between two different days
    }

    //Declared EditGameListener Interface
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

    /**
     * EditGameListener Interface
     */
    interface EditGameListener{
        void gotoHome();                //go to home page
        void gotoGameItem(Game game);   //go to game item
        void gotoFindGame();            //go to find game page
    }
}