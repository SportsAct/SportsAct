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
import android.view.View;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.fragments.AccountFragment;
import com.codepath.jorge.mainactivity.fragments.ChatFragment;
import com.codepath.jorge.mainactivity.fragments.HomeFragment;
import com.codepath.jorge.mainactivity.fragments.MapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    //Declaration
    //constants
    public static final String TAG = "MainActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar tbToolbar;
    private FloatingActionButton fabCreateEvent;

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
        fabCreateEvent = findViewById(R.id.fbAddEventButton);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

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
                    case R.id.mnu_action_map:
                        fragment = new MapsFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //listener create event
        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(i);
            }
        });

        //loading home as main fragment
        bottomNavigationView.setSelectedItemId(R.id.mnu_action_home);
        fragmentManager.beginTransaction().replace(R.id.flContainer, new HomeFragment()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_search,menu);
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
            case   R.id.search:
                //User searches for friends
               Intent intent = new Intent(this, SearchActivity.class);
               this.startActivity(intent);
               break;
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