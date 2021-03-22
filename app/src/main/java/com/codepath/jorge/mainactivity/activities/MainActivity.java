package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.fragments.AccountFragment;
import com.codepath.jorge.mainactivity.fragments.ChatFragment;
import com.codepath.jorge.mainactivity.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    //Declaration
    //constants
    public static final String TAG = "MainActivity";
    //widgets
    private BottomNavigationView bottomNavigationView;

    //managers and adapters
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finding views by id
        bottomNavigationView = findViewById(R.id.bottom_navigation);

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
}