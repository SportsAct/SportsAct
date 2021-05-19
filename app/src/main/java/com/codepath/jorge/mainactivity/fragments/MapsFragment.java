package com.codepath.jorge.mainactivity.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportPreference;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.parse.Parse.getApplicationContext;

public class MapsFragment extends Fragment {

    //declaration

    //constants
    public static final String TAG = "MapsFragment";
    private final static String KEY_LOCATION = "location";
    public static final float DEFAULT_ZOOM = 11f;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //widgets
    private SupportMapFragment mapFragment;
    private ImageView ivGPS;
    //bottom view widgets
    private RelativeLayout rlBottomSheet;
    private TextView tvTitleBottom;
    private TextView tvLocationBottom;
    private TextView tvTimeEstimate;
    private LinearLayout llBtnDirectionBottom;
    private LinearLayout llBtnWebsiteBottom;
    private LinearLayout llBtnSeeEventBottom;
    private ImageButton ibCloseBottom;

    //variables
    private List<SportEvent> sportEventList;
    private List<ParseQuery<SportEvent>> sportPreferencesQueries;
    private HashMap<String,String> markersEvents;
    // Retrieve and cache the system's default "short" animation time.
    int shortAnimationDuration ;

    //map related
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean locationLoaded;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.WEBSITE_URI);


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;

            if (map != null) {
                // Map is ready
                Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

                //getting events
                getSportPreferenceOfUser();

                //get location
                getUserLocation();

                //set marker click listener
                setMapClickListener();

                //todo setting the map
                // map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setMapToolbarEnabled(false);

                //initializing everything
                init();
            }
        }
    };

    private void setMapClickListener() {

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                //event that we will populate
                SportEvent selectedSportEvent = new SportEvent();

                //getting event id
                String eventId = markersEvents.get(marker.getId());

                marker.showInfoWindow();

                //getting event
                for(int i = 0;i < sportEventList.size();i++){

                    if(sportEventList.get(i).getId().equals(eventId)){
                        selectedSportEvent = sportEventList.get(i);
                        break;
                    }

                }

                if(selectedSportEvent == null){
                    Toast.makeText(getContext(),"Event not found!",Toast.LENGTH_SHORT).show();
                    return false;
                }

                //getting estimated time

                //setting the bottom sheet
                tvTitleBottom.setText(selectedSportEvent.getTitle());
                tvLocationBottom.setText(selectedSportEvent.getPlace().getName());

                //setting click listeners
                //todo set direction intent
                llBtnDirectionBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Open Google Maps?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        String latitude = String.valueOf(marker.getPosition().latitude);
                                        String longitude = String.valueOf(marker.getPosition().longitude);
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");

                                        try{
                                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                startActivity(mapIntent);
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                            Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                // set website intent
                SportEvent finalSelectedSportEvent = selectedSportEvent;
                llBtnWebsiteBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToWebsite(finalSelectedSportEvent.getPlace().getURL());
                    }
                });
                //todo send to event screen
                llBtnSeeEventBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                //close the bottom sheet
                ibCloseBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        animateBootomSheetOut();

                    }
                });

                //setting view visible
                loadBottomSheet();

                return true;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                animateBootomSheetOut();
            }
        });
    }

    private void goToWebsite(String url) {

        //if it doesnt have url return and tell user
        if(url.isEmpty()){
            Toast.makeText(getActivity(),"Not website available for this place!",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(urlIntent);
    }

    private void animateBootomSheetOut() {
        rlBottomSheet.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) { }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        rlBottomSheet.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) { }
                    @Override
                    public void onAnimationRepeat(Animator animator) { }
                });
    }

    private void loadBottomSheet() {
        rlBottomSheet.setAlpha(0f);
        rlBottomSheet.setVisibility(View.VISIBLE);

        rlBottomSheet.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

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
                    Log.e(TAG,"There was a problem loading the events!!", e);
                    return;
                }


                //clearing list first
                sportEventList.clear();

                //set posts within mile range
                int milesRange = (int) ParseUser.getCurrentUser().get("travel_miles");

                //location
                com.codepath.jorge.mainactivity.models.Location userLocation = (com.codepath.jorge.mainactivity.models.Location) ParseUser.getCurrentUser().getParseObject("location");

                ParseGeoPoint userLocationLatLon = userLocation.getLatLon();

                Log.i(TAG,"User Lat Lon: " + userLocationLatLon.getLatitude() + ", " + userLocationLatLon.getLongitude());

                //adding events withing a range to the home feed
                for(int i = 0; i < events.size(); i++){

                    double distance = distance(userLocationLatLon.getLatitude(),userLocationLatLon.getLongitude(),events.get(i).getPlace().getLatLon().getLatitude(),events.get(i).getPlace().getLatLon().getLongitude(),'M');

                    Log.i(TAG,"Event: " + events.get(i).getTitle() + " is " + distance + " miles of user location");

                    if(distance < milesRange){

                        sportEventList.add(events.get(i));

                        addMarker(events.get(i));
                    }


                }

                //if data is empty
                if(sportEventList.isEmpty()) {

                   //todo say there is not event nearby
                }

            }
        });

    }

    private void addMarker(SportEvent sportEvent) {

        // Define color of marker icon
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

        //getting event info
        String title = sportEvent.getTitle();
        String sport = sportEvent.getSport().getSportName();
        ParseGeoPoint parseGeoPoint = sportEvent.getPlace().getLatLon();
        LatLng point = new LatLng(parseGeoPoint.getLatitude(),parseGeoPoint.getLongitude());

        // Creates and adds marker to the map
        Marker marker = map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(sport)
                .icon(defaultMarker));

        //setting a ling between the event and the marker
        markersEvents.put(marker.getId(),sportEvent.getId());

        // Animate marker using drop effect
        // --> Call the dropPinEffect method here
        dropPinEffect(marker);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //ask user for permission if it doesnt have access
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else {
            Toast.makeText(getActivity(), "location succeed", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);

            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                onLocationChanged(location);
                                moveCamera(new LatLng(location.getLatitude(),location.getLongitude()));
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });

        }
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        // Report to the UI that the location was updated

        mCurrentLocation = location;
        locationLoaded = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //finding views by id
        ivGPS = view.findViewById(R.id.gpsID);
        rlBottomSheet = view.findViewById(R.id.rlBottomSheet);
        tvTitleBottom = view.findViewById(R.id.tvTitleBottomSheet);
        tvLocationBottom = view.findViewById(R.id.tvPlaceBottomSheet);
        tvTimeEstimate = view.findViewById(R.id.tvTimeExpectedBottomSheet);
        ibCloseBottom = view.findViewById(R.id.ivCloseBottomSheet);
        llBtnDirectionBottom = view.findViewById(R.id.llDirectionBottomSheet);
        llBtnWebsiteBottom = view.findViewById(R.id.llWebsiteBottomSheet);
        llBtnSeeEventBottom = view.findViewById(R.id.llSeeEventBottomSheet);

        //initializing event list
        sportEventList = new ArrayList<>();
        sportPreferencesQueries = new ArrayList<>();
        markersEvents = new HashMap<>();
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        //setting variables
        locationLoaded = false;

        //getting own queries
        ParseQuery<SportEvent> ownEventsQury = ParseQuery.getQuery(SportEvent.class);
        ownEventsQury.whereEqualTo(SportEvent.KEY_USER,ParseUser.getCurrentUser());

        sportPreferencesQueries.add(ownEventsQury);

        //check if location was loaded already
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        else {
            Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {

        //if the places are not initialize yet, initialize them
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(fields);

        //set autocomplete to give a response
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {

                //move camera to place search
                moveCamera(place.getLatLng());

            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //listener for gps takes user to its location
        ivGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
            }
        });
    }

    private void moveCamera(LatLng latLng) {
        Log.i(TAG, "Moving camera to: " + latLng.latitude + ", " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
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