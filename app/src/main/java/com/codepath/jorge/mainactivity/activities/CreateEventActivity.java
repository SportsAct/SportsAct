package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//todo set up all progress bars

class Event{

    Date dateTime;
    int hour;
    int minutes;
    String eventTitle;
    boolean privacy;
    String location;
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

public class CreateEventActivity extends AppCompatActivity {

    //declaration

    //constants
    public static final String TAG = "CreateEventActivity";

    //widgets
    private EditText etEventTitle;
    private Switch swtPrivacy;
    private ImageView ivCalendar;
    private TextView tvDateSelected;
    private ImageView ivClock;
    private TextView tvTimeSelected;
    private EditText etLocation;
    private NumberPicker npAmountOfParticipants;
    private NumberPicker npSportsToBePlayed;
    private Button btnCreateEvent;

    //variable
    private List<SportGame> sportGames;
    private Event eventBeingCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //finding views by id
        etEventTitle = findViewById(R.id.etEventTitleCreateEvent);
        swtPrivacy = findViewById(R.id.swtPrivacyCreateEvent);
        ivCalendar = findViewById(R.id.ivCalendarCreateEvent);
        tvDateSelected = findViewById(R.id.tvSelectedDateCreateEvent);
        ivClock = findViewById(R.id.ivTimePickerCreateEvent);
        tvTimeSelected = findViewById(R.id.tvTimeSelectedCreateEvent);
        etLocation = findViewById(R.id.etLocationCreateEvent);
        npAmountOfParticipants = findViewById(R.id.npAmountofPlayersCreateEvent);
        npSportsToBePlayed = findViewById(R.id.npSportPickerCreateEvent);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        //initialising variables
        sportGames = new ArrayList<>();
        eventBeingCreated = new Event();

        //setting the Number Pickers
       getSportData();
       
       //listeners
        
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

    }

    //gather all informetion to create event
    private void createEvent() {
        //validate data
        validateData();

        // get data
        eventBeingCreated.eventTitle = etEventTitle.getText().toString();
        eventBeingCreated.privacy = swtPrivacy.isChecked();
        eventBeingCreated.location = etLocation.getText().toString();
        eventBeingCreated.maxParticipants = npAmountOfParticipants.getValue();
        eventBeingCreated.sportGame = getPickedSport();
        eventBeingCreated.getFullDate();

        //create event
        createEventQuery();

        //send user to confirmation screen
        //todo send user to confirmation screen

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
                    return;
                }

                //todo create chat
                //todo join author to event
                //todo anything else to do with the event

                Toast.makeText(CreateEventActivity.this, "Event Created Successfully", Toast.LENGTH_SHORT).show();
                finish();

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
    private void validateData() {

        if(etEventTitle.getText().toString().isEmpty()){
            Toast.makeText(this,"Missing an Event Title", Toast.LENGTH_SHORT).show();
            etEventTitle.requestFocus();
            return;
        }

        if(eventBeingCreated.dateTime == null){
            Toast.makeText(this,"Need to Select a Date", Toast.LENGTH_SHORT).show();
            showDatePicker();
            return;
        }

        if(eventBeingCreated.hour == -1){
            Toast.makeText(this,"Need to Select a Time for the Event", Toast.LENGTH_SHORT).show();
            showTimePicker();
            return;
        }

        if(etLocation.getText().toString().isEmpty()){
            Toast.makeText(this,"Missing a Location for the Event", Toast.LENGTH_SHORT).show();
            etLocation.requestFocus();
            return;
        }
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