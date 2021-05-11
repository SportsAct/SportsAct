package com.codepath.jorge.mainactivity.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.Location;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

//todo whole rework, do everything here, maybe new view
//todo maybe change to a new activity, or fragment
public class LocationDialog extends AppCompatDialogFragment {

    //tags
    public static final String TAG = "LocationDialog";

    //widgets
    private AutoCompleteTextView actvStateSelection;
    private AutoCompleteTextView actvCitySelection;

    //variables
    //todo private ArrayList<AllStates> stateList;
    private ArrayList<String> cityList;

    //listener
    private LocationDialogListener listener;

    //todo constuctor
    /*
   public LocationDialog(ArrayList<AllStates> states){
        this.stateList = states;
        cityList = new ArrayList<>();
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

       //creating dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.location_dialog, null);

        //finding views
        actvCitySelection = view.findViewById(R.id.actvCityLocationDialog);
        actvStateSelection = view.findViewById(R.id.actvStateLocationDialog);

        //setting text view adapters
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getStatesStrings());
        actvStateSelection.setAdapter(stateAdapter);

        //when user toauch one of the text views
        actvStateSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                actvStateSelection.showDropDown();
                return false;
            }
        });

        actvCitySelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                actvCitySelection.showDropDown();
                return false;
            }
        });

        //when an user selects an item in the state text view
        actvStateSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //if something is on the city text box, clear it
                actvCitySelection.setText("");

                //checking which state the user selected
               // todo AllStates allState = getState();

                //get the cities for the state
               // todo getCities(allState.getAbreviation());
            }
        });

        //alert Dialog click listener for the buttons
        builder.setView(view)
                .setTitle("Enter your Location")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing to do
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //checking tv are not empty
                        if(actvStateSelection.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(),"You must select a State and a City",Toast.LENGTH_SHORT).show();
                            return;
                        }else  if(actvCitySelection.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(),"City can not be empty",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //getting text strings
                        String city = actvCitySelection.getText().toString();

                        //sending back the location
                       //todo AllStates allState = getState();

                        Location location = new Location();
                        // todo location.setState(allState);
                        location.setCityName(city);
                        // todo location.setStateName(allState.getName());

                        listener.saveLocation(location);
                    }
                });

        return builder.create();
        }


        //todo this
   /* private AllStates getState() {

       AllStates state = new AllStates();

        for(int x = 0; x < stateList.size(); x++){

            if(stateList.get(x).getName().equals(actvStateSelection.getText().toString())){
                state = stateList.get(x);
                break;
            }
        }

        return state;
    }
*/

    //todo get a string list of the states
    private ArrayList<String> getStatesStrings(){

        //if(stateList.isEmpty())
            return null;

        //ArrayList<String> statesStrings = new ArrayList<>();

       // for(int i = 0; i < stateList.size();i++){
       //     statesStrings.add(stateList.get(i).getName());
       // }

        //return statesStrings;
    }

    //get the cities from the state and loads them into the city text view
    private void getCities(String abreviation) {

        final String initTag = "Usabystate_";

        if(abreviation.isEmpty()){
            Toast.makeText(getActivity(), "Select an State", Toast.LENGTH_SHORT).show();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(initTag + abreviation);
        query.setLimit(1100);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e != null){
                    Log.e(TAG,"There was a problem loading the cities!!", e);
                    Toast.makeText(getActivity(), "There was a problem loading the states", Toast.LENGTH_SHORT).show();
                    return;
                }

                cityList.clear();

                for(int i = 0 ; i < objects.size() ; i++){
                    cityList.add(objects.get(i).getString("name"));
                }

                //setting text view adapters
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cityList);
                actvCitySelection.setAdapter(cityAdapter);

                actvCitySelection.setEnabled(true);

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (LocationDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LocationDialogListener");
        }
    }

    //to be able to return data
    public interface LocationDialogListener{
       void saveLocation(Location location);
    }

    }

