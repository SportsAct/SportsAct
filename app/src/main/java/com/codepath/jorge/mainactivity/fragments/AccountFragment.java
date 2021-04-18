package com.codepath.jorge.mainactivity.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.models.ChatUserJoin;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class AccountFragment extends Fragment {

    public static final String TAG = "AccountFragment";

    private Button btnCaptureImage;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private ImageView profilePic;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public ScrollView scrollerSlid;
    public ImageView basketballPic;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePic = view.findViewById(R.id.profilePic);
        btnCaptureImage = view.findViewById(R.id.takePicId);
        scrollerSlid = view.findViewById(R.id.scroller);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
                if (photoFile == null || profilePic.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

//        scrollerSlid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        basketballPic.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        v.setImageBitmap(res.getDrawable(R.drawable.img_down));
//                        break;
//                    }
//                    case MotionEvent.ACTION_CANCEL:{
//                        v.setImageBitmap(res.getDrawable(R.drawable.img_up));
//                        break;
//                    }
//                }
//                return false;
//            }
//        });

    }


    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "${applicationId}.fileprovider", photoFile);
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



