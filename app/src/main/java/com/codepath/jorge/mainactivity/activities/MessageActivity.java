package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.fragments.ChatFragment;
import com.codepath.jorge.mainactivity.models.Message;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    /*static final String TAG = ChatFragment.class.getSimpleName();


    EditText etMessage;
    ImageButton btSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etMessage = view.findViewById(R.id.etMessage);
        btSend = view.findViewById(R.id.btSend);
        //When send button is clicked, create message object on Parse
        btSend.setOnClickListener(v -> {
            String data = etMessage.getText().toString();
            Message message = new Message();
            message.setBody(data);
            message.setUser(ParseUser.getCurrentUser());
            message.setChat(null);

            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "Successfully created message on Parse", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to save message", e);
                    }
                }
            });
            etMessage.setText(null);
        });
    }

     */
}

