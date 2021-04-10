package com.codepath.jorge.mainactivity.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LocationDialog;
import com.codepath.jorge.mainactivity.models.AllStates;
import com.codepath.jorge.mainactivity.models.Chat;
import com.codepath.jorge.mainactivity.models.ChatUserJoin;
import com.codepath.jorge.mainactivity.models.EventParticipant;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class Event{

    Date dateTime;
    int hour;
    int minutes;
    String eventTitle;
    boolean privacy;
    Location location;
    int maxParticipants;
    SportGame sportGame;
    Date fullDate;

    public Event() {
        dateTime = null;
        hour = -1;
        minutes = -1;
        eventTitle = null;
        privacy = false;
        location = null;
        maxParticipants = 2;
        sportGame = null;
        fullDate = null;
    }

    public void getFullDate(){
        Calendar cal = Calendar.getInstance();// creates calendar
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(dateTime);               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, hour + 4);      // adds the hours selected
        cal.add(Calendar.MINUTE, minutes);
        fullDate = cal.getTime();
    }
}

public class CreateEventActivity extends AppCompatActivity implements LocationDialog.LocationDialogListener {

    //declaration

    //constants
    public static final String TAG = "CreateEventActivity";
    private final int MAX_COUNT = 25;

    //widgets
    private EditText etEventTitle;
    private TextView tvMaxCount;
    private Switch swtPrivacy;
    private LinearLayout ivCalendar;
    private TextView tvDateSelected;
    private LinearLayout ivClock;
    private TextView tvTimeSelected;
    private TextView tvLocation;
    private Button btnSelectLocation;
    private NumberPicker npAmountOfParticipants;
    private NumberPicker npSportsToBePlayed;
    private Button btnCreateEvent;
    ProgressBar progressBar;

    //variable
    private List<SportGame> sportGames;
    private Event eventBeingCreated;
    private ArrayList<AllStates> allStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //finding views by id
        etEventTitle = findViewById(R.id.etEventTitleCreateEvent);
        tvMaxCount = findViewById(R.id.tvTitleMaxCount);
        swtPrivacy = findViewById(R.id.swtPrivacyCreateEvent);
        ivCalendar = findViewById(R.id.ivCalendarCreateEvent);
        tvDateSelected = findViewById(R.id.tvSelectedDateCreateEvent);
        ivClock = findViewById(R.id.ivTimePickerCreateEvent);
        tvTimeSelected = findViewById(R.id.tvTimeSelectedCreateEvent);
        btnSelectLocation = findViewById(R.id.btnLocationCreateEvent);
        tvLocation = findViewById(R.id.tvLocationCreateEvent);
        npAmountOfParticipants = findViewById(R.id.npAmountofPlayersCreateEvent);
        npSportsToBePlayed = findViewById(R.id.npSportPickerCreateEvent);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        progressBar = findViewById(R.id.progressBarCreatingEvent);

        //initialising variables
        sportGames = new ArrayList<>();
        eventBeingCreated = new Event();
        allStates = new ArrayList<>();

        //getting states
        getStates();

        //setting the Number Pickers
       getSportData();
       
       //listeners

        //title max count
        etEventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int counter = etEventTitle.length();

                if(counter <= MAX_COUNT){
                    tvMaxCount.setText(Integer.toString(MAX_COUNT - counter));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
                int counter = etEventTitle.length();
                tvMaxCount.setVisibility(View.VISIBLE);
                if(counter == 0){
                    tvMaxCount.setVisibility(View.INVISIBLE);
                }
            }
        });
        
        //picking a date
        ivCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        //picking the time
        ivClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        //creating the event
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        //select location
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

    }

    @Override
    public void saveLocation(Location location) {
        eventBeingCreated.location = location;
        tvLocation.setText(location.getCityName() + ", " + location.getState().getName());
    }

    //gather all informetion to create event
    private void createEvent() {

        progressBar.setVisibility(View.VISIBLE);

        if(!validateData()){
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        // get data
        eventBeingCreated.eventTitle = etEventTitle.getText().toString();
        eventBeingCreated.privacy = swtPrivacy.isChecked();
        eventBeingCreated.maxParticipants = npAmountOfParticipants.getValue();
        eventBeingCreated.sportGame = getPickedSport();
        eventBeingCreated.getFullDate();

        //create event
        createEventQuery();

    }

    private void createEventQuery() {

        //create event
        SportEvent sportEvent = new SportEvent();
        sportEvent.setLocation(eventBeingCreated.location);
        sportEvent.setUser(ParseUser.getCurrentUser());
        sportEvent.setEventDate(eventBeingCreated.fullDate);
        sportEvent.setSport(eventBeingCreated.sportGame);
        sportEvent.setTitle(eventBeingCreated.eventTitle);
        sportEvent.setCurrentNumberOfParticipants(1);
        sportEvent.setMaxNumberOfParticipants(eventBeingCreated.maxParticipants);
        sportEvent.setPrivacy(eventBeingCreated.privacy);

        //save to database
        sportEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem creating the event!!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem creating the event", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                //join author to event
                joinHostToEvent(sportEvent);

                Toast.makeText(CreateEventActivity.this, "Event Created Successfully", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getStates(){

        ParseQuery<AllStates> query = ParseQuery.getQuery(AllStates.class);
        query.findInBackground(new FindCallback<AllStates>() {
            @Override
            public void done(List<AllStates> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the states!!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem loading the states", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0 ; i < objects.size() ; i++){
                    allStates.add(objects.get(i));
                }

                btnSelectLocation.setEnabled(true);

            }
        });

    }

    private void openDialog(){

        if(allStates == null || allStates.isEmpty()){
            return;
        }

        LocationDialog locationDialog = new LocationDialog(allStates);
        locationDialog.show(getSupportFragmentManager(),TAG);
    }

    private void joinHostToEvent(SportEvent sportEvent) {

        EventParticipant hostParticipant = new EventParticipant();
        hostParticipant.setUser(ParseUser.getCurrentUser());
        hostParticipant.setEvent(sportEvent);
        hostParticipant.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem joining user to event!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem joining user to event!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                //create chat and send user to chat screen
                //create chat
                createChat(sportEvent);

            }
        });
    }

    private void createChat(SportEvent sportEvent) {

        Chat chat = new Chat();
        chat.setEvent(sportEvent);

        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem creating the chat!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem creating the chat!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                //joining user to chat
                joinUserToCHat(chat);

                //send user to message screen
                Intent intent = new Intent(CreateEventActivity.this, MessageActivity.class);
                intent.putExtra("chat_id",chat.getObjectId());
                CreateEventActivity.this.startActivity(intent);

                finish();

            }
        });
    }

    private void joinUserToCHat(Chat chat) {

        ChatUserJoin chatUserJoin = new ChatUserJoin();
        chatUserJoin.setChat(chat);
        chatUserJoin.setUser(ParseUser.getCurrentUser());

        chatUserJoin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem joining user to the chat!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem joining user to the chat!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

            }
        });
    }

    //gets the sport from the number picker
    private SportGame getPickedSport() {

        SportGame pickedGame = null;

        for(int i = 0; i < sportGames.size(); i++){

            if(sportGames.get(i).getSportName().equals(getSportString()[npSportsToBePlayed.getValue()])){
                pickedGame = sportGames.get(i);
            }
        }
        return pickedGame;
    }

    //make sure user fill data fields that they must fill
    private boolean validateData() {

        if(etEventTitle.getText().toString().isEmpty()){
            Toast.makeText(this,"Missing an Event Title", Toast.LENGTH_SHORT).show();
            etEventTitle.requestFocus();
            return false;
        }

        if(eventBeingCreated.dateTime == null){
            Toast.makeText(this,"Need to Select a Date", Toast.LENGTH_SHORT).show();
            showDatePicker();
            return false;
        }

        if(eventBeingCreated.hour == -1){
            Toast.makeText(this,"Need to Select a Time for the Event", Toast.LENGTH_SHORT).show();
            showTimePicker();
            return false;
        }

       if(eventBeingCreated.location == null){
            Toast.makeText(this,"Missing a Location for the Event", Toast.LENGTH_SHORT).show();
            openDialog();
            return false;
        }

        return true;
    }

    //shows the time picker
    private void showTimePicker() {

        //creating time picker
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select the time of the event")
                .build();

        //show the time picker
        picker.show(getSupportFragmentManager(),TAG);

        //when user hit ok in the dialog
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int minute = picker.getMinute();
                final int hour = picker.getHour();
                saveTime(minute,hour);
            }
        });
    }

    //save the time and update the time edit text
    private void saveTime(int minute, int hour) {

        //save date and time in event
        eventBeingCreated.minutes = minute;
        eventBeingCreated.hour = hour;

        //getting am or pm and making it 12 hour format
        String AM_PM ;
        String hourString = Integer.toString(hour);
        if(hour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
            hour -= 12;
            hourString = Integer.toString(hour);
        }

        //change display textview
        tvTimeSelected.setText("Selected Time: " + hourString + ":" + pad(minute) + " " + AM_PM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvTimeSelected.setTextColor(getColor(R.color.black));
        }
    }

    //helper function to make an integer return with two places if is less than 10 Ex (2 --> 02) (10 --> 10)
    private String pad(int minute) {
        return (minute < 10 ) ? ("0" + minute) : Integer.toString(minute);
    }

    //shows date picker
    private void showDatePicker() {

        //creating date constraint
        final long todayDate = MaterialDatePicker.todayInUtcMilliseconds();
        CalendarConstraints.Builder constrainDate = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        //creating date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choose Event Date")
                .setCalendarConstraints(constrainDate.build())
                .setSelection(todayDate)
                .build();

        datePicker.show(getSupportFragmentManager(),TAG);

        // Respond to positive button click.
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                //get date and save it
                Date d = new Date(selection);
                saveDateSelected(d);
            }
        }) ;

    }

    //saves the date and update his edit text
    private void saveDateSelected(Date d) {
        //'at' hh:mm aaa"
        //save the date
        eventBeingCreated.dateTime = d;
        //setting date pattern
        String pattern = "E MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = simpleDateFormat.format(d);
        //change text view
        tvDateSelected.setText("Selected date: " + date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvDateSelected.setTextColor(getColor(R.color.black));
        }
    }

    //set the number pickers with the appropiate data
    private void setNumberPickers() {

        //setting amount of players min and max value
        npAmountOfParticipants.setMaxValue(50);
        npAmountOfParticipants.setMinValue(2);

        //setting sports
        npSportsToBePlayed.setMaxValue(sportGames.size() - 1);
        npSportsToBePlayed.setMinValue(0);
        String[] sportVals = getSportString();
        npSportsToBePlayed.setDisplayedValues(sportVals);
        npSportsToBePlayed.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker1 = npSportsToBePlayed.getValue();
                Log.d("picker value", sportVals[valuePicker1]);
            }
        });

    }

    //gets the sport data from the database
    private void getSportData() {

        //query to get Sport Data
        ParseQuery<SportGame> query = ParseQuery.getQuery(SportGame.class);
        query.findInBackground(new FindCallback<SportGame>() {
            @Override
            public void done(List<SportGame> sportGameList, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the Sports!!", e);
                    Toast.makeText(CreateEventActivity.this, "There was a problem loading the Sports", Toast.LENGTH_SHORT).show();
                    return;
                }

                sportGames.addAll(sportGameList);

                //setting the number pickers
                setNumberPickers();

            }
        });
    }

    //helper function to extract all the sport names of the SportGame objects returned from the database
    private String[] getSportString() {

        String[] sportNames = new String[sportGames.size()];

        //goes throug all the sports and gets their name
        for(int i = 0; i < sportGames.size(); i++){
            sportNames[i] = sportGames.get(i).getSportName();
        }

        Arrays.sort(sportNames,0,sportNames.length);

        return sportNames;
    }
}