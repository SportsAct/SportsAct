package com.codepath.jorge.mainactivity.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.UserListAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    List<ParseUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AutoCompleteTextView editText = findViewById(R.id.friendSearch);
        ArrayAdapter<ParseUser> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, userList);
        editText.setAdapter(adapter);
    }
}





