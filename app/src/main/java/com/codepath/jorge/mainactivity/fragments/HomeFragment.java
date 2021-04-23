package com.codepath.jorge.mainactivity.fragments;

import android.content.Intent;
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
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.activities.CreateEventActivity;
import com.codepath.jorge.mainactivity.adapters.EventsAdapter;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton fabCreateEvent;
    LoadingDialog loadingDialog;

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
        fabCreateEvent = view.findViewById(R.id.fbAddEventButton);

        //progress indicator creation
        loadingDialog = new LoadingDialog(getActivity());
        //starting the loading dialog
        loadingDialog.startLoadingDialog();

        //initializing event list
        sportEventList = new ArrayList<>();

        //recycler view performance
        recyclerViewHome.setHasFixedSize(true);

        //setting adapter
        adapter = new EventsAdapter(getContext(),sportEventList);
        recyclerViewHome.setAdapter(adapter);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        //listener create event
        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CreateEventActivity.class);
                startActivity(i);
            }
        });

        //getting events
      getHomeFeed();

    }

    @Override
    public void onResume() {
        super.onResume();

        getHomeFeed();
    }

    private void getHomeFeed(){

        ParseQuery<SportEvent> query = ParseQuery.getQuery(SportEvent.class);
        query.include(SportEvent.KEY_SPORT);
        query.include(SportEvent.KEY_USER);
        query.include(SportEvent.KEY_LOCATION);
        query.whereEqualTo(SportEvent.KEY_ACTIVE, true);
        query.findInBackground(new FindCallback<SportEvent>() {
            @Override
            public void done(List<SportEvent> events, ParseException e) {

                //something went wrong
                if(e != null){
                    loadingDialog.dismissDialog();
                    Log.e(TAG,"There was a problem loading the events!!", e);
                    return;
                }

                //clearing list first
                sportEventList.clear();

                //set posts
                sportEventList.addAll(events);

                //notify adapter
                adapter.notifyDataSetChanged();

                //dismissing loading dialog
                loadingDialog.dismissDialog();

            }
        });


        }
}