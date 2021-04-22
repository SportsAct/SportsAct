package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ManageEventActivity extends AppCompatActivity {

    //declaration
    //constants
    public static final String TAG = "ManageEventActivity";

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
    private Button btnMinusMax;
    private Button btnPlusMax;
    private TextView tvMaxAmountOfPlayers;
    private TextView tvCurrentAmountGoing;
    private Button btnSeeWhoIsGoing;
    private Button btnUpdateEvent;
    private Button btnDeleteEvent;

    //adapter
    SportHorizontalAdapter adapter;

    //variables
    List<SportGame> sportGamesList;
    SportEvent currentSportEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        //finding views by id
        rvSportsGames = findViewById(R.id.rvSports);
        btnUpdateEvent = findViewById(R.id.btnSaveEventManage);

        //initializing sport list
        sportGamesList = new ArrayList<>();
        //initializing sport event
        currentSportEvent = new SportEvent();

        //setting adapter
        adapter = new SportHorizontalAdapter(this,sportGamesList,false);
        rvSportsGames.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSportsGames.setLayoutManager(layoutManager);

        //listeners
        btnUpdateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ManageEventActivity.this,"selected sport " + adapter.getSelectedSport().getSportName(),Toast.LENGTH_SHORT).show();
            }
        });

        //getting sports
        getSports();
    }

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

            }
        });
    }
}