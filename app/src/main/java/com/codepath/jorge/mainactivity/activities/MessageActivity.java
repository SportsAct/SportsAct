package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.adapters.MessageAdapter;
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

//todo make it look more like a chat
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
    Handler handler;

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

        //setting cursor to edit text
        etMessage.requestFocus();

        //listeners
        //send button
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendAMessage();
            }
        });

        //setting handler to get messages periodically
        // Create the Handler object (on the main thread by default)
         handler = new Handler();

        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }

    // Define the code block to be executed
   private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            getNewMessages();
            handler.postDelayed(this, 2000);
        }
    };

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

                tbToolbar.setTitle(currentChat.getEvent().getTitle() + " Chat");
                
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

    private void getNewMessages() {

        if(currentChat == null || messageList.isEmpty()){
            return;
        }

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_USER);
        query.whereEqualTo(Message.KEY_CHAT,currentChat);
        query.whereGreaterThan(Message.KEY_CREATED_AT, messageList.get(0).getCreatedAt());
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
                    messageList.add(0, objects.get(i));
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

        if( etMessage.getText().toString().isEmpty()){
            return;
        }

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

                messageList.add(0,message);

                //updating last message in chat
                updateLastMessage(message);

                adapter.notifyDataSetChanged();

            }
        });
        etMessage.setText(null);
    }

    private void updateLastMessage(Message message) {

        currentChat.setLastMessage(message);

        currentChat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem saving the  last message!", e);
                    Toast.makeText(MessageActivity.this, "There was a problem saving the last message!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Removes pending code execution
        handler.removeCallbacks(runnableCode);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set again
        handler.post(runnableCode);
    }
}





