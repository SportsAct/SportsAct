package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LocationDialog;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//todo might need to just delete those
//todo give more space, separate sections
public class ManageEventActivity extends AppCompatActivity implements LocationDialog.LocationDialogListener {

    //declaration
    //constants
    public static final String TAG = "ManageEventActivity";
    private final int MAX_COUNT = 25;

    //widgets
    private RecyclerView rvSportsGames;
    private TextView tvNameOfEvent;
    private RelativeLayout btnChangeTitle;
    private Switch swtPrivacy;
    private Button btnChangeLocation;
    private TextView tvSelectedLocation;
    private LinearLayout btnSelectDate;
    private TextView tvDate;
    private LinearLayout btnSelectTime;
    private TextView tvTime;
    private ImageButton btnMinusMax;
    private ImageButton btnPlusMax;
    private TextView tvMaxAmountOfPlayers;
    private TextView tvCurrentAmountGoing;
    private Button btnSeeWhoIsGoing;
    private Button btnUpdateEvent;
    private Button btnDeleteEvent;
    private EditText etEditTitle;
    private TextView tvMaxCharactersTitle;
    private ImageView ivIconTitle;
    private Toolbar tbToolbar;
    ProgressBar progressBar;
    private TextView switchLabel;

    //adapter
    SportHorizontalAdapter adapter;

    //variables
    List<SportGame> sportGamesList;
    SportEvent currentSportEvent;
    SportGame selectedSport;
    Date dateTime; //to store the date if change
    int mHour = -1;
    int mMinutes;
    //todo private ArrayList<AllStates> allStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        //getting event id
        String eventId = getIntent().getStringExtra("event_id");

        //todo getting states maybe not needed
        //getStates();

        //get event
        getEvent(eventId);

        //finding views by id
        rvSportsGames = findViewById(R.id.rvSports);
        btnUpdateEvent = findViewById(R.id.btnSaveEventManage);
        tvNameOfEvent = findViewById(R.id.tvEventTitleManage);
        btnChangeTitle = findViewById(R.id.btnEditTitleManage);
        swtPrivacy = findViewById(R.id.swtPrivacyManageEvent);
        btnChangeLocation = findViewById(R.id.btnLocationManageEvent);
        tvSelectedLocation = findViewById(R.id.tvLocationManageEvent);
        btnSelectDate = findViewById(R.id.dateLayoutManage);
        tvDate = findViewById(R.id.tvDateManage);
        btnSelectTime = findViewById(R.id.timeLayoutManage);
        tvTime = findViewById(R.id.tvTimeManage);
        btnMinusMax = findViewById(R.id.btnMinus);
        btnPlusMax = findViewById(R.id.btnPlus);
        tvMaxAmountOfPlayers = findViewById(R.id.tvMaxAmountManage);
        tvCurrentAmountGoing = findViewById(R.id.tvCurrentEnroll);
        btnSeeWhoIsGoing = findViewById(R.id.btnSeeParticipantsManage);
        btnDeleteEvent = findViewById(R.id.btnDeleteEventmanage);
        etEditTitle = findViewById(R.id.etEventTitleManage);
        tvMaxCharactersTitle = findViewById(R.id.tvTitleMaxCountManage);
        ivIconTitle = findViewById(R.id.ibIconInTitle);
        tbToolbar = findViewById(R.id.tbToolbar);
        progressBar = findViewById(R.id.progressBarManageEventEvent);
        switchLabel = findViewById(R.id.switchTextManageEvent);

        progressBar.setVisibility(View.VISIBLE);

        //initializing sport list
        sportGamesList = new ArrayList<>();
        //initializing sport event
        currentSportEvent = new SportEvent();
        selectedSport = new SportGame();
       //todo allStates = new ArrayList<>();

        //setting bar
        tbToolbar.setTitle("Manage Your Event");
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listeners

        //switch change label
        swtPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    switchLabel.setText("Public");
                }
                else {
                    switchLabel.setText("Private");
                }
            }
        });

        //update event listener
        btnUpdateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                updateEvent();
            }
        });

        //see who is going
        btnSeeWhoIsGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventActivity.this, EventParticipantsActivity.class);
                intent.putExtra("event_id",currentSportEvent.getId());
                startActivity(intent);
            }
        });

        //plus minus buttons
        btnPlusMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSportEvent.setMaxNumberOfParticipants(currentSportEvent.getMaxNumberOfParticipants() + 1);
                tvMaxAmountOfPlayers.setText(Integer.toString(currentSportEvent.getMaxNumberOfParticipants()));
            }
        });

        //minus button
        btnMinusMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSportEvent.getMaxNumberOfParticipants() == currentSportEvent.getCurrentNumberOfParticipants()){
                    Toast.makeText(ManageEventActivity.this,"The maximum number of participants can't be lower than the current participants!", Toast.LENGTH_LONG).show();
                    return;
                }

                currentSportEvent.setMaxNumberOfParticipants(currentSportEvent.getMaxNumberOfParticipants() - 1);
                tvMaxAmountOfPlayers.setText(Integer.toString(currentSportEvent.getMaxNumberOfParticipants()));

            }
        });

        //show date picker
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        //show time picker
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        //show location picker
        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo openDialog(); open new search
            }
        });

        //title max count
        etEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int counter = etEditTitle.length();

                if(counter <= MAX_COUNT){
                    tvMaxCharactersTitle.setText(Integer.toString(MAX_COUNT - counter));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
                int counter = etEditTitle.length();
                tvMaxCharactersTitle.setVisibility(View.VISIBLE);
                if(counter == 0){
                    tvMaxCharactersTitle.setVisibility(View.INVISIBLE);
                }
            }
        });

        //change event title
        btnChangeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //changing views to show the new views
                tvNameOfEvent.setVisibility(View.GONE);
                etEditTitle.setVisibility(View.VISIBLE);
                etEditTitle.setText(currentSportEvent.getTitle());
                etEditTitle.requestFocus();
                etEditTitle.selectAll();

                tvMaxCharactersTitle.setVisibility(View.VISIBLE);
                ivIconTitle.setImageResource(R.drawable.checkmark);
                ivIconTitle.setClickable(true);

                ivIconTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etEditTitle.getText().toString().isEmpty()){
                            Toast.makeText(ManageEventActivity.this,"Title can not be empty",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currentSportEvent.setTitle(etEditTitle.getText().toString());

                        //change views to normal
                        tvNameOfEvent.setVisibility(View.VISIBLE);
                        tvNameOfEvent.setText(currentSportEvent.getTitle());

                        etEditTitle.setVisibility(View.GONE);

                        tvMaxCharactersTitle.setVisibility(View.GONE);

                        ivIconTitle.setClickable(false);
                        ivIconTitle.setImageResource(R.drawable.edit);
                    }
                });


            }
        });

        //todo better deletion
        //delete button
        btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(ManageEventActivity.this)
                .setTitle("Attention:")
                .setMessage("Event will be deleted!")
                        .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        deleteEvent();
                    }
                }).show();
            }
        });

    }

    private void deleteEvent() {
        currentSportEvent.setActive(false);

        currentSportEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem deleting the event!!", e);
                    Toast.makeText(ManageEventActivity.this, "There was a problem deleting the event", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ManageEventActivity.this, "There was a problem deleting the event", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    private void updateEvent() {

        //setting date
        if(dateTime != null){
            getFullDate();
        }

        if(mHour != -1){
            getFullDate();
        }

        currentSportEvent.setPrivacy(swtPrivacy.isChecked());
        currentSportEvent.setSport(adapter.getSelectedSport());

        currentSportEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem updating the event!!", e);
                    Toast.makeText(ManageEventActivity.this, "There was a problem updating the event", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ManageEventActivity.this, "Event Updated Successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

    }

    private void getFullDate(){
        Calendar cal = Calendar.getInstance();// creates calendar
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(dateTime != null) {
            cal.setTime(dateTime);
        }
        else {
            cal.setTime(currentSportEvent.getEventDate());
        }
        // sets calendar time/date
        cal.set(Calendar.HOUR_OF_DAY, mHour + 4);      // adds the hours selected
        cal.set(Calendar.MINUTE, mMinutes);
        currentSportEvent.setEventDate(cal.getTime());

        mHour = -1;
    }

    @Override
    public void saveLocation(Location location) {
        checkIfLocationIsDuplicate(location);
    }

    //todo not  needed maybe
    /*
    private void getStates(){

        ParseQuery<AllStates> query = ParseQuery.getQuery(AllStates.class);
        query.findInBackground(new FindCallback<AllStates>() {
            @Override
            public void done(List<AllStates> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the states!!", e);
                    Toast.makeText(ManageEventActivity.this, "There was a problem loading the states", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0 ; i < objects.size() ; i++){
                    allStates.add(objects.get(i));
                }

                btnChangeLocation.setEnabled(true);

            }
        });

    }
     */

    //todo open states selection dialog, change to the intent or activity
   /* private void openDialog(){

        if(allStates == null || allStates.isEmpty()){
            return;
        }

        LocationDialog locationDialog = new LocationDialog(allStates);
        locationDialog.show(getSupportFragmentManager(),TAG);
    }
*/
    //shows the time picker
    private void showTimePicker() {

        //creating time picker
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Update the time of the event")
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
       mMinutes = minute;
       mHour = hour;

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
        tvTime.setText(hourString + ":" + pad(minute) + " " + AM_PM);
    }

    //helper function to make an integer return with two places if is less than 10 Ex (2 --> 02) (10 --> 10)
    private String pad(int minute) {
        return (minute < 10 ) ? ("0" + minute) : Integer.toString(minute);
    }

    //shows date picker
    private void showDatePicker() {

        //creating date constraint
        final long selectedDate = currentSportEvent.getEventDate().getTime();
        CalendarConstraints.Builder constrainDate = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());

        //creating date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choose Event Date")
                .setCalendarConstraints(constrainDate.build())
                .setSelection(selectedDate)
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
        dateTime = d;
        //setting date pattern
        String pattern = "EEE MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = simpleDateFormat.format(d);
        //change text view
        tvDate.setText(date);

        showTimePicker();

    }

    private void getEvent(String eventId) {

        ParseQuery<SportEvent> query = ParseQuery.getQuery(SportEvent.class);
        query.include(SportEvent.KEY_SPORT);
        query.include(SportEvent.KEY_LOCATION);
        query.whereEqualTo(SportEvent.KEY_ID,eventId);
        query.getFirstInBackground(new GetCallback<SportEvent>() {
            @Override
            public void done(SportEvent object, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the event!!", e);
                    Toast.makeText(ManageEventActivity.this, "There was a problem loading the event", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentSportEvent = object;

                loadEventData();

            }
        });
    }

    //setting the UI
    private void loadEventData() {

        //setting date pattern
        String patternDate = "EEE MMM dd, yyyy";
        String patternTime = "hh:mm aaa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternDate);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(patternTime);
        String date = simpleDateFormat.format(currentSportEvent.getEventDate());
        String time = simpleTimeFormat.format(currentSportEvent.getEventDate());

        tvNameOfEvent.setText(currentSportEvent.getTitle());
        swtPrivacy.setChecked(currentSportEvent.getPrivacy());
        //todo tvSelectedLocation.setText(currentSportEvent.getLocation().getState().getName() + ", " + currentSportEvent.getLocation().getCityName());
        tvDate.setText(date);
        tvTime.setText(time);
        tvMaxAmountOfPlayers.setText(Integer.toString( currentSportEvent.getMaxNumberOfParticipants()));
        tvCurrentAmountGoing.setText(Integer.toString( currentSportEvent.getCurrentNumberOfParticipants()));
        selectedSport = currentSportEvent.getSport();

        if(currentSportEvent.getPrivacy()){
            switchLabel.setText("Public");
        }
        else {
            switchLabel.setText("Private");
        }

        //setting adapter
        adapter = new SportHorizontalAdapter(this,sportGamesList, selectedSport);
        rvSportsGames.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSportsGames.setLayoutManager(layoutManager);

        //gettingsports
        getSports();

    }

    //todo need a different check
    private void checkIfLocationIsDuplicate(Location location) {

        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo(Location.KEY_STATE_NAME, location.getStateName());
        query.whereEqualTo(Location.KEY_CITY_NAME, location.getCityName());
        query.getFirstInBackground(new GetCallback<Location>() {
            @Override
            public void done(Location object, ParseException e) {

                if(e != null){
                    Log.e(TAG,"Location is new!", e);

                    //location not found
                    currentSportEvent.setLocation(location);
                    //todo tvSelectedLocation.setText(location.getCityName() + ", " + location.getState().getName());

                    return;
                }

                //location found
                currentSportEvent.setLocation(object);
                //todo tvSelectedLocation.setText(object.getCityName() + ", " + object.getState().getName());

            }
        });
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
                    Toast.makeText(ManageEventActivity.this, "There was a problem loading the Sports", Toast.LENGTH_SHORT).show();
                    return;
                }

                sportGamesList.addAll(sportGameList);

                //notifying adapter
               adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}