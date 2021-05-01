package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LocationDialog;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.fragments.AccountFragment;
import com.codepath.jorge.mainactivity.models.AllStates;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.codepath.jorge.mainactivity.models.SportPreference;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity  implements LocationDialog.LocationDialogListener{

    //declaration

    //constants
    public static final String TAG = "AccountFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    //widgets
    private Toolbar toolbar;
    private ImageView ivProfilePic;
    private Button btnChangePicture;
    private EditText etActualName;
    private EditText userNameId2;
    private EditText bioTextId2;
    private Button btnLocation;
    private RecyclerView imagesSports;

    //variables
    List<SportGame> sportList;
    List<SportGame> selectedSportList;
    List<SportGame> oldOnes;
    ParseUser currentUser;
    private ArrayList<AllStates> allStates;
    //picture related
    public String photoFileName = "photo.jpg";
    private File photoFile;

    //adapter
    private SportHorizontalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //finding views by id
        userNameId2 = findViewById(R.id.userNameId2);
        bioTextId2 = findViewById(R.id.bioTextId2);
        toolbar = findViewById(R.id.tbToolbar);
        ivProfilePic = findViewById(R.id.profilePic);
        btnChangePicture = findViewById(R.id.btnChangeProfilePicture);
        etActualName = findViewById(R.id.etActualNameEditProfile);
        btnLocation = findViewById(R.id.btnChangeLocation);
        imagesSports = findViewById(R.id.rvSports);

        //initializing arrays
        sportList = new ArrayList<>();
        selectedSportList = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
        allStates = new ArrayList<>();

        //getting states
        getStates();

        // SETTING ADAPTER FOR FAVORITE SPORT ON PROFILE
        adapter = new SportHorizontalAdapter(this, sportList, selectedSportList);
        imagesSports.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagesSports.setLayoutManager(layoutManager);
        getSports();

        //setting bar
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.cancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting data
        ParseFile profilePicture = (ParseFile) currentUser.get("profilePicture");
        Glide.with(this).load(profilePicture.getUrl()).placeholder(R.drawable.empty_profile).into(ivProfilePic);

        etActualName.setText((String) currentUser.get("name"));
        bioTextId2.setText((String) currentUser.get("bio"));
        userNameId2.setText(currentUser.getUsername());

        Location userLocation = (Location) currentUser.get("location");
        btnLocation.setText(userLocation.getCityName() + ", " + userLocation.getStateName());

        //listeners

        //changing picture button
        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        //opening location dialog
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_changes,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ((item.getItemId())){
            //save changes
            case R.id.mnu_action_save:
                saveSportPreferences();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void saveLocation(Location location) {

        checkIfLocationIsDuplicate(location);

    }

    private void checkIfLocationIsDuplicate(Location location) {

        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo(Location.KEY_STATE_NAME, location.getStateName());
        query.whereEqualTo(Location.KEY_CITY_NAME, location.getCityName());
        query.getFirstInBackground(new GetCallback<Location>() {
            @Override
            public void done(Location object, ParseException e) {

                if(e != null){
                    //location not found
                    currentUser.put("location", location);
                    btnLocation.setText(location.getCityName() + ", " + location.getStateName());

                    return;
                }

                //location found
                currentUser.put("location", object);

                btnLocation.setText(object.getCityName() + ", " + object.getStateName());

            }
        });
    }

    private void getStates(){

        ParseQuery<AllStates> query = ParseQuery.getQuery(AllStates.class);
        query.findInBackground(new FindCallback<AllStates>() {
            @Override
            public void done(List<AllStates> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the states!!", e);
                    Toast.makeText(EditProfile.this, "There was a problem loading the states", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0 ; i < objects.size() ; i++){
                    allStates.add(objects.get(i));
                }

                btnLocation.setClickable(true);

            }
        });

    }

    private void openDialog(){

        if(allStates == null || allStates.isEmpty()){
            return;
        }

        LocationDialog locationDialog = new LocationDialog(allStates);
        locationDialog.show(getSupportFragmentManager(),TAG);
    }


    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    //On activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfilePic.setImageBitmap(takenImage);

                savePost();

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost() {
        currentUser.put("profilePicture", new ParseFile(photoFile));
    }

    // To save edits for user name and bio
    private void saveEdits() {

        currentUser.setUsername(userNameId2.getText().toString());
        currentUser.put("bio", bioTextId2.getText().toString());
        currentUser.put("name", etActualName.getText().toString());
        getSportPreferenceOfUser();

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(EditProfile.this,"There was a problem updating the profile", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "post save was successful!");
                Toast.makeText(EditProfile.this,"Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
                    Toast.makeText(EditProfile.this, "There was a problem loading the Sports", Toast.LENGTH_SHORT).show();
                    return;
                }
                getSportPreferenceOfUser();

                sportList.addAll(sportGameList);

                //notifying adapter
                adapter.notifyDataSetChanged();
            }
        });
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

    private void saveSportPreferences(){

        final List<SportGame> toDeleteSports = new ArrayList<>();
        final List<SportGame> toCreateSports = new ArrayList<>();

        boolean found;

        //finding new selected sports
        for(int i = 0; i < selectedSportList.size(); i++){
            found = false;
            for(int j = 0; j < oldOnes.size();j++){
                if(oldOnes.get(j).getObjectId().equals(selectedSportList.get(i).getObjectId())){
                    found = true;
                    break;
                }
            }
            if(!found){

                toCreateSports.add(selectedSportList.get(i));
            }
        }

        //finding sports that where unselected
        for(int i = 0; i < oldOnes.size();i++){
            found = false;
            for(int j = 0; j < selectedSportList.size(); j++){

                if(oldOnes.get(i).getObjectId().equals(selectedSportList.get(j).getObjectId())){
                    Log.i(TAG, "Found " + oldOnes.get(i).getSportName());
                    found = true;
                }
            }

            if(!found){
                Log.i(TAG, "deleting:  " + oldOnes.get(i).getSportName());
                toDeleteSports.add(oldOnes.get(i));
            }
        }

        saveSportPreferenceQuery(toCreateSports);

        deleteUnselectedSports(toDeleteSports);
    }

    private void deleteUnselectedSports(List<SportGame> toDeleteSports) {

        for(SportGame sportGame : toDeleteSports){

            ParseQuery<SportPreference> query = ParseQuery.getQuery(SportPreference.class);
            query.whereEqualTo(SportPreference.KEY_USER,ParseUser.getCurrentUser());
            query.whereEqualTo(SportPreference.KEY_SPORT,sportGame);
            query.getFirstInBackground(new GetCallback<SportPreference>() {
                @Override
                public void done(SportPreference deletedObject, ParseException e) {

                    if( e != null){
                        Log.e(TAG, "There was a problem saving the preferences!", e);
                        Toast.makeText(EditProfile.this, "There was a problem deleting the unselected!", Toast.LENGTH_SHORT).show();
                    }

                    deletedObject.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if( e != null){
                                Log.e(TAG, "There was a problem saving the preferences!", e);
                                Toast.makeText(EditProfile.this, "There was a problem deleting the unselected 2!", Toast.LENGTH_SHORT).show();
                            }

                            saveEdits();
                        }
                    });

                }
            });
        }

    }

    private void saveSportPreferenceQuery(List<SportGame> toCreateSports) {

        oldOnes.clear();
        oldOnes.addAll(selectedSportList);

        for(SportGame sportGame : toCreateSports){
            Log.i(TAG, "Just Checking");
            SportPreference newSportPreference = new SportPreference();
            newSportPreference.setSport(sportGame);
            newSportPreference.setUser(ParseUser.getCurrentUser());
            newSportPreference.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if( e != null){
                        Log.e(TAG, "There was a problem saving the preferences!", e);
                        Toast.makeText(EditProfile.this, "There was a problem saving the preferences!", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(EditProfile.this, "Sport preferences save successfully!", Toast.LENGTH_SHORT).show();

                }
            });

            saveEdits();
        }
    }

}