package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.fragments.AccountFragment;
import com.codepath.jorge.mainactivity.fragments.ChatFragment;
import com.codepath.jorge.mainactivity.fragments.HomeFragment;
import com.codepath.jorge.mainactivity.models.AllStates;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Declaration
    //constants
    public static final String TAG = "MainActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar tbToolbar;

    //managers and adapters
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfUserIsLogged();

        //finding views by id
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tbToolbar = findViewById(R.id.tbToolbar);

        //setting bar
        setSupportActionBar(tbToolbar);

        //listeners
        //bottom navigation view listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment;

                //to check to which fragment this is going
                switch (item.getItemId()) {
                    case R.id.mnu_action_chat:
                        fragment = new ChatFragment();
                        break;
                    case R.id.mnu_action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mnu_action_account:
                        fragment = new AccountFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //loading home as main fragment
        bottomNavigationView.setSelectedItemId(R.id.mnu_action_home);
        fragmentManager.beginTransaction().replace(R.id.flContainer, new HomeFragment()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case    R.id.actLogout:
                //log user out
                ParseUser.logOut();
                //take them to log in page
                checkIfUserIsLogged();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkIfUserIsLogged() {

        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}