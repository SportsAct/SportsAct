package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.fragments.AccountFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "AccountFragment";

    EditText userNameId2;
    EditText bioTextId2;
    Button saveButtonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        userNameId2 = findViewById(R.id.userNameId2);
        bioTextId2 = findViewById(R.id.bioTextId2);
        saveButtonId = findViewById(R.id.saveButtonId);

        bioTextId2.setText((String) ParseUser.getCurrentUser().get("bio"));
        userNameId2.setText((String) ParseUser.getCurrentUser().get("username"));


        // click listener for Save
        saveButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
                return;
            }
        });


    }
    // To save edits for user name and bio
    private void savePost() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(userNameId2.getText().toString());
        parseUser.put("bio", bioTextId2.getText().toString());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e != null){
                    Log.e(TAG, "Error while saving", e);
                }
                Log.i(TAG, "post save was successful!");
            }
        });

        Intent i = new Intent();

    }

}