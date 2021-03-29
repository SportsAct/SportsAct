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
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    static final String TAG = ChatFragment.class.getSimpleName();

    //TODO: Verify order of this method (Getting unreachable statement before shift)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //User Login
        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        } else {
            login();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

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
            ParseObject message = ParseObject.create("Messages");
            message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
            message.put(BODY_KEY, data);
            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null ) {

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




    public ChatFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ChatFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ChatFragment newInstance(String param1, String param2) {
//        ChatFragment fragment = new ChatFragment();
//        Bundle args = new Bundle();
//        args.putString(USER_ID_KEY, param1);
//        args.putString(BODY_KEY, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            etMessage = getArguments().getString(USER_ID_KEY);
//            btnSend = getArguments().getString(BODY_KEY);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chat, container, false);
//    }
}