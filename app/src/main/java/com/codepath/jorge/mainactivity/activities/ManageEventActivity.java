package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
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

    //adapter
    SportHorizontalAdapter adapter;

    //variables
    List<SportGame> sportGamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        //finding views by id
        rvSportsGames = findViewById(R.id.rvSports);

        //initializing sport list
        sportGamesList = new ArrayList<>();

        //setting adapter
        adapter = new SportHorizontalAdapter(this,sportGamesList);
        rvSportsGames.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSportsGames.setLayoutManager(layoutManager);

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