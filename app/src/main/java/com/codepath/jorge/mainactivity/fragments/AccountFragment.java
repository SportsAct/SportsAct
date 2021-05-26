package com.codepath.jorge.mainactivity.fragments;


import android.content.Intent;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.activities.EditProfile;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.codepath.jorge.mainactivity.models.SportPreference;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

//todo change it to look more like open sports
public class AccountFragment extends Fragment {

    public static final String TAG = "AccountFragment";

    private RecyclerView imagesSports;
    private ImageView profilePic;
    private Button editText;
    private TextView userNameId;
    private TextView bioTextId;
    private TextView realNameId;
    private TextView tvLocation;
    private TextView tvWillingToTravel;

    private SportHorizontalAdapter adapter;
    List<SportGame> sportList;
    List<SportGame> selectedSportList;
    List<SportGame> oldOnes;

    ParseUser currentUser;

    @Override
    public void onResume() {
        super.onResume();

        loadUserData();
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
                    Toast.makeText(getActivity(), "There was a problem loading the Sports", Toast.LENGTH_SHORT).show();
                    return;
                }
                getSportPreferenceOfUser();

                sportList.addAll(sportGameList);

                //notifying adapter
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePic = view.findViewById(R.id.profilePic);
        editText = view.findViewById(R.id.editText);
        userNameId = view.findViewById(R.id.userNameId);
        bioTextId = view.findViewById(R.id.bioTextId);
        realNameId = view.findViewById(R.id.realNameId);
        imagesSports = view.findViewById(R.id.rvSports);
        tvLocation = view.findViewById(R.id.tvLocationAccountFragment);
        tvWillingToTravel = view.findViewById(R.id.tvWillingToTravel);

        //initializing arrays
        sportList = new ArrayList<>();
        selectedSportList = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();

        // SETTING ADAPTER FOR FAVORITE SPORT ON PROFILE
        adapter = new SportHorizontalAdapter(getActivity(), sportList, selectedSportList);
        imagesSports.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imagesSports.setLayoutManager(layoutManager);
        getSports();


        loadUserData();

        // CLICK LISTENER TO
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                getActivity().startActivity(i);
            }
        });
    }

    private void loadUserData() {

        //Gets the username, real name, and bio from database
        userNameId.setText((String) currentUser.get("username"));
        bioTextId.setText((String) currentUser.get("bio"));
        realNameId.setText((String) currentUser.get("name"));
        tvWillingToTravel.setText("Willing to travel " + (int) currentUser.get("travel_miles") + " miles.");

        ParseFile profilePicture = (ParseFile) currentUser.get("profilePicture");
        Glide.with(getActivity()).load(profilePicture.getUrl()).placeholder(R.drawable.empty_profile).into(profilePic);

        Location userLocation = (Location) currentUser.get("location");

        if(userLocation != null) {
            tvLocation.setText(userLocation.getCityName() + ", " + userLocation.getStateName());
        }
        else {
            tvLocation.setText("Not Location Selected");
        }
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
                for( int i = 0 ; i < objects.size() ; i++){
                    selectedSportList.add(objects.get(i).getSport());
                }

                oldOnes = new ArrayList<>();

                oldOnes.addAll(selectedSportList);

                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

}



