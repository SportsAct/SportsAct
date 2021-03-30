package com.codepath.jorge.mainactivity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.EventsAdapter;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    //declaration
    //constants
    public static final String TAG = "HomeFragment";

    //widgets
    private RecyclerView recyclerViewHome;

    //adapters
    private EventsAdapter adapter;

    //variables
    private List<SportEvent> sportEventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //finding views
        recyclerViewHome = view.findViewById(R.id.rvRecyclerViewHome);

        //initializing event list
        sportEventList = new ArrayList<>();

        //setting adapter
        adapter = new EventsAdapter(getContext(),sportEventList);
        recyclerViewHome.setAdapter(adapter);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        
        //getting events
        getHomeFeed();
    }

    private void getHomeFeed(){
        ParseQuery<SportEvent> query = ParseQuery.getQuery(SportEvent.class);
        query.include(SportEvent.KEY_SPORT);
        query.include(SportEvent.KEY_USER);
        query.findInBackground(new FindCallback<SportEvent>() {
            @Override
            public void done(List<SportEvent> events, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the events!!", e);
                    Toast.makeText(getContext(), "There was a problem loading the events", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG,"Success");

                //set posts
                sportEventList.addAll(events);

                //notify adapter
                adapter.notifyDataSetChanged();

            }
        });


        }
}