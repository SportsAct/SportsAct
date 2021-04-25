package com.codepath.jorge.mainactivity.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.AutoCompleteUserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    List<ParseUser> userList;

    public static final String TAG = "SearchActivity";
    AutoCompleteTextView autoCompleteTextView;


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
                    Log.e(TAG, "Test");
                }
                autoCompleteTextView.setEnabled(true);

                AutoCompleteTextView editText = findViewById(R.id.friendSearch);
                AutoCompleteUserAdapter adapter = new AutoCompleteUserAdapter(SearchActivity.this, userList);
                editText.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        autoCompleteTextView = findViewById(R.id.friendSearch);
        userList = new ArrayList<>();
        getUsers();


    }
}





