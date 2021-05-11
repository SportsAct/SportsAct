package com.codepath.jorge.mainactivity.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.activities.CreateEventActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


//todo different empty fragment depending the need
public class EmptyFragment extends Fragment {

    //declaration
    //constants
    public static final String TAG = "EmptyFragment";

    //widgets
    private Button btnCreateEvent;
    private Button btnChangePreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //finding views by id
        btnCreateEvent = view.findViewById(R.id.idCreateNewEventEmptyActivity);
        btnChangePreferences = view.findViewById(R.id.idTakeToProfileEmptyActivity);

        //listeners

        //send to create activity
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToCreateActivity();
            }
        });

        //send to account fragment
        btnChangePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToAccountFragment();
            }
        });
    }

    private void sendToAccountFragment() {

        BottomNavigationView bottom = getActivity().findViewById(R.id.bottom_navigation);

        bottom.setSelectedItemId(R.id.mnu_action_account);

        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.flContainer, new AccountFragment()).commit();
    }

    private void sendToCreateActivity() {
        Intent i = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(i);
    }
}