package com.codepath.jorge.mainactivity.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Date;
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
    private List<ParseQuery<SportEvent>> sportPreferencesQueries;

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
        sportPreferencesQueries = new ArrayList<>();

        //getting own queries
        ParseQuery<SportEvent> ownEventsQury = ParseQuery.getQuery(SportEvent.class);
        ownEventsQury.whereEqualTo(SportEvent.KEY_USER,ParseUser.getCurrentUser());

        sportPreferencesQueries.add(ownEventsQury);

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
        getSportPreferenceOfUser();

    }

    private void getSportPreferenceOfUser() {

        ParseQuery<SportPreference> query = ParseQuery.getQuery(SportPreference.class);
        query.include(SportPreference.KEY_SPORT);
        query.include(SportPreference.KEY_USER);
        query.whereEqualTo(SportPreference.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<SportPreference>() {
            @Override
            public void done(List<SportPreference> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    loadingDialog.dismissDialog();
                    Log.e(TAG,"There was a problem loading the sport preference!!", e);
                    return;
                }

                //set sport queries
                for( int i = 0 ; i < objects.size() ; i++){

                    ParseQuery<SportEvent> sportQuery = ParseQuery.getQuery(SportEvent.class);
                    sportQuery.whereEqualTo(SportEvent.KEY_SPORT,objects.get(i).getSport());

                    sportPreferencesQueries.add(sportQuery);
                }

                getHomeFeed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

       getSportPreferenceOfUser();
    }

    //todo different tabs that show different events
    private void getHomeFeed(){

        Date today = new Date();

        ParseQuery<SportEvent> query = ParseQuery.or(sportPreferencesQueries);
        query.include(SportEvent.KEY_SPORT);
        query.include(SportEvent.KEY_USER);
        query.include(SportEvent.KEY_PLACE);
        query.whereGreaterThanOrEqualTo(SportEvent.KEY_DATE,today);
        query.whereEqualTo(SportEvent.KEY_ACTIVE, true);
        query.orderByAscending(SportEvent.KEY_DATE);
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

                //set posts within mile range
                int milesRange = (int) ParseUser.getCurrentUser().get("travel_miles");

                Location userLocation = (Location) ParseUser.getCurrentUser().getParseObject("location");

                ParseGeoPoint userLocationLatLon = userLocation.getLatLon();

                Log.i(TAG,"User Lat Lon: " + userLocationLatLon.getLatitude() + ", " + userLocationLatLon.getLongitude());

                //adding events withing a range to the home feed
                for(int i = 0; i < events.size(); i++){

                    double distance = distance(userLocationLatLon.getLatitude(),userLocationLatLon.getLongitude(),events.get(i).getPlace().getLatLon().getLatitude(),events.get(i).getPlace().getLatLon().getLongitude(),'M');

                    Log.i(TAG,"Event: " + events.get(i).getTitle() + " is " + distance + " miles of user location");

                    if(distance < milesRange){

                        sportEventList.add(events.get(i));
                    }


                }

                //notify adapter
                adapter.notifyDataSetChanged();

                //dismissing loading dialog
                loadingDialog.dismissDialog();

                //if data is empty
                if(sportEventList.isEmpty()) {

                    Fragment fragment = new EmptyFragment();

                    final FragmentManager fragmentManager = getFragmentManager();

                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                }

            }
        });

        }

        //calculates the distance between two points Lat Lon
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}