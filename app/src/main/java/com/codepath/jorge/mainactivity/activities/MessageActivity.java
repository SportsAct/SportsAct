package com.codepath.jorge.mainactivity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.adapters.MessageAdapter;
import com.codepath.jorge.mainactivity.fragments.ChatFragment;
import com.codepath.jorge.mainactivity.models.Chat;
import com.codepath.jorge.mainactivity.models.Message;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    //declaration
    //constants
    static final String TAG = "MessageActivity";

    //widgets
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btSend;
    private Toolbar tbToolbar;
    LoadingDialog loadingDialog;

    //variables
    MessageAdapter adapter;
    List<Message> messageList;
    Chat currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //get chat id
        String chatId = getIntent().getStringExtra("chat_id");

        //getting the chat
        getChat(chatId);

        //finding views by id
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        rvMessages = findViewById(R.id.rvMessages);
        tbToolbar = findViewById(R.id.tbToolbar);

        //setting bar
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //progress indicator creation
        loadingDialog = new LoadingDialog(this);
        //starting load dialog
        loadingDialog.startLoadingDialog();

        //initializing user list
        messageList = new ArrayList<>();

        //setting adapter
        adapter = new MessageAdapter(this,ParseUser.getCurrentUser(),messageList);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(false);
        rvMessages.setLayoutManager(linearLayoutManager);

        //listeners
        //send button
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendAMessage();
            }
        });
    }

    private void getChat(String chatId) {

        ParseQuery<Chat> query = ParseQuery.getQuery(Chat.class);
        query.whereEqualTo(Chat.KEY_OBJECT_ID,chatId);
        query.getFirstInBackground(new GetCallback<Chat>() {
            @Override
            public void done(Chat object, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the chat!!", e);
                    Toast.makeText(MessageActivity.this, "There was a problem loading the chat", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                    return;
                }
                
                currentChat = object;

                tbToolbar.setTitle(currentChat.getEvent().getTitle()); 
                
                //get messages
                getMessages();
                
            }
        });
        
    }

    private void getMessages() {

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_USER);
        query.orderByDescending(Message.KEY_CREATED_AT);
        query.whereEqualTo(Message.KEY_CHAT,currentChat);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the messages!!", e);
                    Toast.makeText(MessageActivity.this, "There was a problem loading the messages!!", Toast.LENGTH_SHORT).show();
                   loadingDialog.dismissDialog();
                    return;
                }

                //set messages
                for(int i = 0; i < objects.size(); i++){
                    messageList.add(objects.get(i));
                }

                //notify adapter
                adapter.notifyDataSetChanged();

                //hide bar
                loadingDialog.dismissDialog();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendAMessage() {

        String data = etMessage.getText().toString();
        Message message = new Message();
        message.setBody(data);
        message.setUser(ParseUser.getCurrentUser());
        message.setChat(currentChat);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem saving the message!", e);
                    Toast.makeText(MessageActivity.this, "There was a problem saving the message!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(MessageActivity.this, "Successfully created message on Parse", Toast.LENGTH_SHORT).show();

                messageList.add(0,message);

                adapter.notifyDataSetChanged();

            }
        });
        etMessage.setText(null);
    };
    }





