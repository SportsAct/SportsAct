package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.EventsAdapter;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.adapters.UserListAdapter;
import com.codepath.jorge.mainactivity.models.EventParticipant;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class EventParticipantsActivity extends AppCompatActivity {

    //declaration
    //constants
    public static final String TAG = "EventParticipants";

    //widgets
    private RecyclerView rvEventParticipants;
    LoadingDialog loadingDialog;
    private TextView tvEventTitle;
    private TextView tvCount;
    private Toolbar tbToolbar;
    
    //adapter
    private UserListAdapter adapter;
    
    //variables
    private List<ParseUser> userList;
    private SportEvent sportEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participants);

        //get event id
        String eventId = getIntent().getStringExtra("event_id");

        //get event
        getEvent(eventId);
        
        //finding views
        rvEventParticipants = findViewById(R.id.rvRecyclerViewEventParticipants);
        tvEventTitle = findViewById(R.id.tvEventTitleEventParticipants);
        tvCount = findViewById(R.id.tvCountEventParticipants);
        tbToolbar = findViewById(R.id.tbToolbar);

        //setting bar
        tbToolbar.setTitle("Event Participants");
        setSupportActionBar(tbToolbar);
        
        //progress indicator creation
        loadingDialog = new LoadingDialog(this);
        //starting load dialog
        loadingDialog.startLoadingDialog();
        
        //initializing user list
        userList = new ArrayList<>();
        
        //setting adapter
        adapter = new UserListAdapter(this,userList);
        rvEventParticipants.setAdapter(adapter);
        rvEventParticipants.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getEvent(String id) {

        ParseQuery<SportEvent> query = ParseQuery.getQuery(SportEvent.class);
        query.whereEqualTo(SportEvent.KEY_ID,id);
        query.getFirstInBackground(new GetCallback<SportEvent>() {
            @Override
            public void done(SportEvent gettingSportEvent, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the event!!", e);
                    Toast.makeText(EventParticipantsActivity.this, "There was a problem loading the event", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                    return;
                }

                sportEvent = gettingSportEvent;

                tvEventTitle.setText(sportEvent.getTitle());
                tvCount.setText( Integer.toString(sportEvent.getCurrentNumberOfParticipants())  + " out of " + Integer.toString(sportEvent.getMaxNumberOfParticipants()) + " going");

                getUsers();

            }
        });
    }

    private void getUsers() {

        ParseQuery<EventParticipant> query = ParseQuery.getQuery(EventParticipant.class);
        query.include(EventParticipant.KEY_ID);
        query.include(EventParticipant.KEY_USER);
        query.whereEqualTo(EventParticipant.KEY_EVENT, sportEvent);
        query.findInBackground(new FindCallback<EventParticipant>() {
            @Override
            public void done(List<EventParticipant> eventParticipants, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the users!!", e);
                    Toast.makeText(EventParticipantsActivity.this, "There was a problem loading the users!!", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                    return;
                }

                Log.i(TAG, eventParticipants.get(0).getUser().getUsername());

                //set users
                for(int i = 0; i < eventParticipants.size(); i++){
                    userList.add(eventParticipants.get(i).getUser());
                }

                //notify adapter
                adapter.notifyDataSetChanged();

                //hide bar
                loadingDialog.dismissDialog();
            }
        });
    }
}