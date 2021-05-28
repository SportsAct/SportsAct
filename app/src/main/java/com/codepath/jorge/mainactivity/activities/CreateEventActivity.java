package com.codepath.jorge.mainactivity.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.BetterActivityResult;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.models.Chat;
import com.codepath.jorge.mainactivity.models.ChatUserJoin;
import com.codepath.jorge.mainactivity.models.EventParticipant;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.PlaceEvent;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

class Event{

    Date dateTime;
    int hour;
    int minutes;
    String eventTitle;
    boolean privacy;
    PlaceEvent place;
    int maxParticipants;
    SportGame sportGame;
    Date fullDate;

    public Event() {
        dateTime = null;
        hour = -1;
        minutes = -1;
        place = new PlaceEvent();
        eventTitle = null;
        privacy = false;
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

public class CreateEventActivity extends AppCompatActivity{

    //declaration

    //constants
    public static final String TAG = "CreateEventActivity";
    private final int MAX_COUNT = 25;

    //widgets
    private RecyclerView rvSportsGames;
    private EditText etEventTitle;
    private TextView tvMaxCount;
    private Switch swtPrivacy;
    private LinearLayout ivCalendar;
    private TextView tvDateSelected;
    private LinearLayout ivClock;
    private TextView tvTimeSelected;
    private TextView tvLocation;
    private LinearLayout btnSelectLocation;
    private Button btnCreateEvent;
    ProgressBar progressBar;
    private Toolbar tbToolbar;
    private TextView switchText;
    private Slider sbParticipantsNumber;
    private TextView tvParticipantNumber;

    //adapter
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    SportHorizontalAdapter adapter;

    //variable
    private List<SportGame> sportGames;
    private SportGame selectedSport;
    private Event eventBeingCreated;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.WEBSITE_URI);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //finding views by id
        rvSportsGames = findViewById(R.id.rvSports);
        etEventTitle = findViewById(R.id.etEventTitleCreateEvent);
        tvMaxCount = findViewById(R.id.tvTitleMaxCount);
        swtPrivacy = findViewById(R.id.swtPrivacyCreateEvent);
        ivCalendar = findViewById(R.id.ivCalendarCreateEvent);
        tvDateSelected = findViewById(R.id.tvSelectedDateCreateEvent);
        ivClock = findViewById(R.id.ivTimePickerCreateEvent);
        tvTimeSelected = findViewById(R.id.tvTimeSelectedCreateEvent);
        btnSelectLocation = findViewById(R.id.btnLocationCreateEvent);
        tvLocation = findViewById(R.id.tvLocationCreateEvent);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        progressBar = findViewById(R.id.progressBarCreatingEvent);
        tbToolbar = findViewById(R.id.tbToolbar);
        switchText = findViewById(R.id.switchTextCreateEvent);
        sbParticipantsNumber = findViewById(R.id.sbChooseParticipants);
        tvParticipantNumber = findViewById(R.id.tvParticipantsNumberCreate);

        //initialising variables
        sportGames = new ArrayList<>();
        eventBeingCreated = new Event();
        selectedSport = new SportGame();

        //if the places are not initialize yet, initialize them
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        //setting bar
        tbToolbar.setTitle("Create Your Event");
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting adapter
        adapter = new SportHorizontalAdapter(this,sportGames, selectedSport);
        rvSportsGames.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSportsGames.setLayoutManager(layoutManager);

        //gettingsports
        getSports();
       
       //listeners
        //switch changing title
        swtPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    switchText.setText("Public");
                }
                else {
                    switchText.setText("Private");
                }
            }
        });

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

        //SeekBar
        sbParticipantsNumber.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(fromUser){
                    tvParticipantNumber.setText(String.format("%2.0f",value));
                    eventBeingCreated.maxParticipants = Math.round(value);
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //get the sports from db to populate horizontal rv
    private void getSports() {

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

                //notifying adapter
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void savePlace() {

        ParseQuery<PlaceEvent> query = ParseQuery.getQuery(PlaceEvent.class);
        query.whereEqualTo(PlaceEvent.KEY_GOOGLE_ID, eventBeingCreated.place.getGoogleId());
        query.getFirstInBackground(new GetCallback<PlaceEvent>() {
            @Override
            public void done(PlaceEvent object, ParseException e) {

                if(e != null){
                    Log.e(TAG,"Place is new!", e);

                    //location not found
                    tvLocation.setText(eventBeingCreated.place.getName());

                    return;
                }

                //location found
                eventBeingCreated.place = object;

                tvLocation.setText(eventBeingCreated.place.getName());

            }
        });

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
        eventBeingCreated.sportGame = adapter.getSelectedSport();
        eventBeingCreated.getFullDate();

        //create event
        createEventQuery();

    }

    private void createEventQuery() {

        //create event
        SportEvent sportEvent = new SportEvent();
        sportEvent.setUser(ParseUser.getCurrentUser());
        sportEvent.setEventDate(eventBeingCreated.fullDate);
        sportEvent.setPlace(eventBeingCreated.place);
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


    private void openDialog(){

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setHint("Choose the place of the event")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result)  {

                if (result.getResultCode() == EditProfile.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();

                    Place place = Autocomplete.getPlaceFromIntent(data);
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint();
                    parseGeoPoint.setLatitude(place.getLatLng().latitude);
                    parseGeoPoint.setLongitude(place.getLatLng().longitude);

                    //setting event place
                   eventBeingCreated.place.setLatLon(parseGeoPoint);
                    if(place.getWebsiteUri() != null)
                        eventBeingCreated.place.setURL(place.getWebsiteUri().toString());
                   eventBeingCreated.place.setName(place.getName());
                   eventBeingCreated.place.setKeyGoogleId(place.getId());

                    savePlace();
                }

            }
        });
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

       if( eventBeingCreated.place == null){
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
        tvTimeSelected.setText(hourString + ":" + pad(minute) + " " + AM_PM);
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
        tvDateSelected.setText(date);

    }




}