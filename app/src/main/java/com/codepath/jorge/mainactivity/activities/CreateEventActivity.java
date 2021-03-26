package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.codepath.jorge.mainactivity.models.SportGame;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class Event{

    Date dateTime;

    public Event() {
        //todo set everything to null or default value
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
    }

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