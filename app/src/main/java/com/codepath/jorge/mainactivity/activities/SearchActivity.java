package com.codepath.jorge.mainactivity.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.AutoCompleteUserAdapter;
import com.codepath.jorge.mainactivity.models.Location;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    List<ParseUser> userList;

    public static final String TAG = "SearchActivity";
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteUserAdapter adapter;

    private TextView userName;
    private TextView bio;
    private TextView fullName;
    private ImageView profilePic;
    private TextView location;


    private void getUsers(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG,"There was a problem loading the users!!", e);
                    Toast.makeText(SearchActivity.this, "There was a problem loading the users!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i < objects.size(); i++){
                    userList.add(objects.get(i));
                }
                autoCompleteTextView.setEnabled(true);

                AutoCompleteTextView editText = findViewById(R.id.friendSearch);
                 adapter = new AutoCompleteUserAdapter(SearchActivity.this, userList);
                editText.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadUserProfile(ParseUser current) {

        userName.setText((String) current.get("username"));
        bio.setText((String) current.get("bio"));
        fullName.setText((String) current.get("name"));

        ParseFile profilePicture = (ParseFile) current.get("profilePicture");
        Glide.with(SearchActivity.this).load(profilePicture.getUrl()).placeholder(R.drawable.empty_profile).into(profilePic);

        Location userLocation = (Location) current.get("location");
        location.setText(userLocation.getCityName() + ", " + userLocation.getStateName());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        fullName = findViewById(R.id.fullName);
        profilePic = findViewById(R.id.profilePic);
        location = findViewById(R.id.tvlocation);

        autoCompleteTextView = findViewById(R.id.friendSearch);
        userList = new ArrayList<>();
        getUsers();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ParseUser current = adapter.getSelectedUser();

                Log.i(TAG, "Selected user: " + current.getUsername());
                loadUserProfile(current);
            }
        });

    }

}





