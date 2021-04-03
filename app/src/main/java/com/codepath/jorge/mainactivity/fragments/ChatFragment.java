package com.codepath.jorge.mainactivity.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.Message;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ChatFragment extends Fragment {


    static final String TAG = ChatFragment.class.getSimpleName();

    //TODO: Verify order of this method (Getting unreachable statement before shift)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Register the Message Model
        ParseObject.registerSubclass(Message.class);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        //User Login
        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            login();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    // TODO: Previously anonymous login method/ Verify if method will work
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    ImageButton btSend;

    //Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    //Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        //Find the text field and button
        etMessage = (EditText) etMessage.findViewById(R.id.etMessage);
        btSend = (ImageButton) btSend.findViewById(R.id.btSend);

        //When send button is clicked, create message object on Parse
        btSend.setOnClickListener(v -> {
            String data = etMessage.getText().toString();
            Message message = new Message();
            message.setBody(data);
            message.setUserId(ParseUser.getCurrentUser().getObjectId());



            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        //TODO: Verify that get context method is valid

                        Toast.makeText(getContext(), "Successfully created message on Parse", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to save message", e);
                    }
                }
            });
            etMessage.setText(null);
        });
    }


}