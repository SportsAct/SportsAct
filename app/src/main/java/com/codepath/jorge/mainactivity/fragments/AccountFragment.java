package com.codepath.jorge.mainactivity.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.activities.CreateEventActivity;
import com.codepath.jorge.mainactivity.activities.EditProfile;
import com.codepath.jorge.mainactivity.activities.ManageEventActivity;
import com.codepath.jorge.mainactivity.adapters.SportHorizontalAdapter;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.codepath.jorge.mainactivity.models.SportPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    public static final String TAG = "AccountFragment";

    private RecyclerView imagesSports;
    private Button btnCaptureImage;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private ImageView profilePic;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private Button editText;
    private EditText userNameId;
    private EditText bioTextId;
    private TextView realNameId;
    private String strtext;



    private SportHorizontalAdapter adapter;
    List<SportGame> sportList;
    List<SportGame> selectedSportList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            String userNameId = getArguments().getString("params");
        }

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
                    Toast.makeText(getActivity(), "There was a problem loading the Sports", Toast.LENGTH_SHORT).show();
                    return;
                }
                getSportPreferenceOfUser();

                sportList.addAll(sportGameList);

                //notifying adapter
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePic = view.findViewById(R.id.profilePic);
        btnCaptureImage = view.findViewById(R.id.takePicId);
        editText = view.findViewById(R.id.editText);
        userNameId = view.findViewById(R.id.userNameId);
        bioTextId = view.findViewById(R.id.bioTextId);
        realNameId = view.findViewById(R.id.realNameId);
        imagesSports = view.findViewById(R.id.rvSports);
        sportList = new ArrayList<>();
        selectedSportList = new ArrayList<>();




        // SETTING ADAPTER FOR FAVORITE SPORT ON PROFILE
        adapter = new SportHorizontalAdapter(getActivity(), sportList, selectedSportList);
        imagesSports.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imagesSports.setLayoutManager(layoutManager);
        getSports();

        //Gets the username, real name, and bio from database
        userNameId.setText((String) ParseUser.getCurrentUser().get("username"));
        bioTextId.setText((String) ParseUser.getCurrentUser().get("bio"));
        realNameId.setText((String) ParseUser.getCurrentUser().get("name"));

        //Gets image from DataBase
        ParseFile profileImage = (ParseFile) ParseUser.getCurrentUser().get("profilePicture");
        Glide.with(getActivity()).load(profileImage.getUrl()).into(profilePic);

        // CLICK LISTENER TO LAUNCH CAMERA
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
                if (photoFile == null || profilePic.getDrawable() == null) {
                    Toast.makeText(getContext(), "Ready to Upload?", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        // CLICK LISTENER TO
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                getActivity().startActivity(i);
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
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    //On activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profilePic.setImageBitmap(takenImage);
                savePost(photoFile);
                profilePic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
        // To save post from submitting picture
        private void savePost(File photoFile) {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("profilePicture", new ParseFile(photoFile));
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e != null){
                    Log.e(TAG, "Error while saving", e);
                   Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
               }
               Log.i(TAG, "post save was successful!");
                Toast.makeText(getContext(), "Successful!", Toast.LENGTH_SHORT).show();
           }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }


}



