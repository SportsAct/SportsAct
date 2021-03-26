package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.jorge.mainactivity.R;

public class CreateEventActivity extends AppCompatActivity {

    //declaration

    //constants
    public static final String TAG = "CreateEventActivity";

    //widgets
    EditText etEventTitle;
    Switch swtPrivacy;
    ImageView ivCalendar;
    TextView tvDateSelected;
    ImageView ivClock;
    TextView tvTimeSelected;
    EditText etLocation;
    NumberPicker npAmountOfParticipants;
    NumberPicker npSportsToBePlayed;
    Button btnCreateEvent;

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
    }
}