package com.codepath.jorge.mainactivity.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.AutoCompleteUserAdapter;
import com.codepath.jorge.mainactivity.models.FriendsRequests;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.RequestStatus;
import com.codepath.jorge.mainactivity.models.UserInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

//todo add friend functionality
//todo change overall view, show person
public class SearchActivity extends AppCompatActivity {


//declaration
    //constants
    public static final String TAG = "SearchActivity";

    //widgets
    private TextView userName;
    private TextView bio;
    private TextView fullName;
    private ImageView profilePic;
    private TextView location;
    private Button btnFriendButton;
    AutoCompleteTextView autoCompleteTextView;

    //adapter
    AutoCompleteUserAdapter adapter;

    //variables
    private String CURRENT_STATE;
    List<ParseUser> userList;

    private void getUsers(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG,"There was a problem loading the users!!", e);
                    Toast.makeText(SearchActivity.this, "There was a problem loading the users!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i < objects.size(); i++){
                    userList.add(objects.get(i));
                }
                autoCompleteTextView.setEnabled(true);

                AutoCompleteTextView editText = findViewById(R.id.friendSearch);
                 adapter = new AutoCompleteUserAdapter(SearchActivity.this, userList);
                editText.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadUserProfile(ParseUser current) {

        String senderUserId = ParseUser.getCurrentUser().getObjectId();
        String receiverUserId = current.getObjectId();

        userName.setText((String) current.get("username"));
        bio.setText((String) current.get("bio"));
        fullName.setText((String) current.get("name"));

        ParseFile profilePicture = (ParseFile) current.get("profilePicture");
        Glide.with(SearchActivity.this).load(profilePicture.getUrl()).placeholder(R.drawable.empty_profile).into(profilePic);

        Location userLocation = (Location) current.get("location");
        location.setText(userLocation.getCityName() + ", " + userLocation.getStateName());

        //Friends - Sender cannot be receiver
        if (!senderUserId.equals(receiverUserId)) {
            //check if user is a friend
            checkFriendStatus(current);

        } else {
            //if it is your own profile, dont show button
            btnFriendButton.setVisibility(View.GONE);
        }
    }

    //checking friend status request
    private void checkFriendStatus(ParseUser current) {

        //set button visible
        btnFriendButton.setVisibility(View.VISIBLE);

        ParseQuery<FriendsRequests> query = ParseQuery.getQuery(FriendsRequests.class);
        query.whereEqualTo(FriendsRequests.KEY_FROM_USER,ParseUser.getCurrentUser());
        query.whereEqualTo(FriendsRequests.KEY_TO_USER,current);
        query.getFirstInBackground(new GetCallback<FriendsRequests>() {
            @Override
            public void done(FriendsRequests object, ParseException e) {

                if(e != null){
                    btnFriendButton.setText(RequestStatus.NOT_FRIENDS);
                    CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;
                    setButton(current);
                    return;
                }

                //if found the request
                switch (object.getStatus()){
                    case RequestStatus.TAG_STATUS_FRIENDS:
                      CURRENT_STATE = RequestStatus.TAG_STATUS_FRIENDS;
                        break;
                    case RequestStatus.TAG_STATUS_REQUEST_SENT:
                       CURRENT_STATE = RequestStatus.TAG_STATUS_REQUEST_SENT;
                        break;
                    case RequestStatus.TAG_STATUS_REQUEST_RECEIVED:
                        CURRENT_STATE = RequestStatus.TAG_STATUS_REQUEST_RECEIVED;
                        break;
                    default:
                        CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;
                }

                //set button listener
                setButton(current);
            }
        });
    }

    private void setButton(ParseUser current) {

        switch (CURRENT_STATE){
            case RequestStatus.TAG_STATUS_FRIENDS:
                btnFriendButton.setText(RequestStatus.FRIENDS);
                btnFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo, give option to go to profile or to message
                    }
                });
                break;
            case RequestStatus.TAG_STATUS_REQUEST_SENT:
                btnFriendButton.setText(RequestStatus.REQUEST_SENT);
                btnFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo, give option to cancel request, view profile, message
                    }
                });
                break;
            case RequestStatus.TAG_STATUS_REQUEST_RECEIVED:
                btnFriendButton.setText(RequestStatus.REQUEST_RECEIVED);
                btnFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //accept or decline friend request
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(SearchActivity.this);
                        alertDialogBuilder.setTitle("Friend Request!")
                                .setMessage("Accept or Decline Friend Request.")
                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //accept friend request
                                        updatingFriendRequest(current, true);
                                    }
                                })
                                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //decline friend request
                                        updatingFriendRequest(current, false);

                                    }
                                });
                        alertDialogBuilder.show();
                    }
                });
                break;
            default:
                btnFriendButton.setText(RequestStatus.NOT_FRIENDS);
                btnFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendFriendRequest(current);
                    }
                });
        }
    }

    private void updatingFriendRequest(ParseUser current,Boolean accepting) {

        ParseQuery<FriendsRequests> query = ParseQuery.getQuery(FriendsRequests.class);
        query.whereEqualTo(FriendsRequests.KEY_FROM_USER,current);
        query.whereEqualTo(FriendsRequests.KEY_TO_USER,ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<FriendsRequests>() {
            @Override
            public void done(FriendsRequests foundRequest, ParseException e) {

                if(e != null){
                    Log.e(TAG,"There was a problem declining the request!", e);
                    Toast.makeText(SearchActivity.this, "There was a problem declining the request!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //checking if accepting or declining
                if(accepting) {
                    foundRequest.setStatus(RequestStatus.TAG_STATUS_FRIENDS);
                    CURRENT_STATE = RequestStatus.FRIENDS;
                }
                else
                    foundRequest.setStatus(RequestStatus.TAG_STATUS_NOT_FRIENDS);

                    foundRequest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e != null){
                            Log.e(TAG,"There was a problem declining the request!", e);
                            Toast.makeText(SearchActivity.this, "There was a problem declining the request!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //setting the button
                        if(accepting) {
                            CURRENT_STATE = RequestStatus.FRIENDS;
                        //todo update friend number
                            updateFriendNumbers(current);
                        }
                        else {
                            CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;
                        }

                        setButton(current);

                    }
                });

            }
        });

        ParseQuery<FriendsRequests> secondQuery = ParseQuery.getQuery(FriendsRequests.class);
        secondQuery.whereEqualTo(FriendsRequests.KEY_TO_USER,current);
        secondQuery.whereEqualTo(FriendsRequests.KEY_FROM_USER,ParseUser.getCurrentUser());
        secondQuery.getFirstInBackground(new GetCallback<FriendsRequests>() {
            @Override
            public void done(FriendsRequests receiverRequest, ParseException e) {

                if(e != null){
                    Log.e(TAG,"There was a problem getting the second request!", e);
                    Toast.makeText(SearchActivity.this, "There was a problem updating the request!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //changing sender request
                if(accepting) {
                    CURRENT_STATE = RequestStatus.FRIENDS;
                    receiverRequest.setStatus(RequestStatus.TAG_STATUS_FRIENDS);
                }
                else {
                    receiverRequest.setStatus(RequestStatus.TAG_STATUS_NOT_FRIENDS);
                    CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;
                }

                receiverRequest.saveInBackground();
                setButton(current);
            }
        });
    }

    private void updateFriendNumbers(ParseUser current) {

        ParseQuery<UserInfo> queryOwn = ParseQuery.getQuery(UserInfo.class);
        queryOwn.whereEqualTo(UserInfo.KEY_USER, ParseUser.getCurrentUser());
        queryOwn.getFirstInBackground(new GetCallback<UserInfo>() {
            @Override
            public void done(UserInfo currentUserInfo, ParseException e) {

                if(e != null){
                    Log.e(TAG,"Error updating current profile!", e);
                    Toast.makeText(SearchActivity.this, "Error updating current profile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentUserInfo.setFriendsNumber1();
                currentUserInfo.saveEventually();
            }
        });

        ParseQuery<UserInfo> queryCurrent = ParseQuery.getQuery(UserInfo.class);
        queryCurrent.whereEqualTo(UserInfo.KEY_USER, current);
        queryCurrent.getFirstInBackground(new GetCallback<UserInfo>() {
            @Override
            public void done(UserInfo otherUserInfo, ParseException e) {

                if(e != null){
                    Log.e(TAG,"Error updating user profile!", e);
                    Toast.makeText(SearchActivity.this, "Error updating user profile!", Toast.LENGTH_SHORT).show();
                    return;
                }

                otherUserInfo.setFriendsNumber1();
                otherUserInfo.saveEventually();
            }
        });


    }

    //Method that sends a friend request
    private void sendFriendRequest(ParseUser current) {

        //check to see if there is already a request from this two users
        ParseQuery<FriendsRequests> query = ParseQuery.getQuery(FriendsRequests.class);
        query.whereEqualTo(FriendsRequests.KEY_FROM_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(FriendsRequests.KEY_TO_USER, current);
        query.getFirstInBackground(new GetCallback<FriendsRequests>() {
            @Override
            public void done(FriendsRequests foundRequest, ParseException e) {

                if(e != null){
                    Log.e(TAG,"Didnt find the request, creating a new one!", e);
                    sendRequest(current);
                    return;
                }

                //if it find it, update it
                foundRequest.setStatus(RequestStatus.TAG_STATUS_REQUEST_SENT);
                foundRequest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e != null){
                            Log.e(TAG,"Error on found First!", e);
                            Toast.makeText(SearchActivity.this, "An error has occurred!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //changing button to say it was sent
                        CURRENT_STATE = RequestStatus.TAG_STATUS_REQUEST_SENT;
                        setButton(current);

                        //change the other one
                        ParseQuery<FriendsRequests> otherQuery = ParseQuery.getQuery(FriendsRequests.class);
                        otherQuery.whereEqualTo(FriendsRequests.KEY_TO_USER, ParseUser.getCurrentUser());
                        otherQuery.whereEqualTo(FriendsRequests.KEY_FROM_USER, current);
                        otherQuery.getFirstInBackground(new GetCallback<FriendsRequests>() {
                            @Override
                            public void done(FriendsRequests otherSide, ParseException e) {

                                if(e != null){
                                    Log.e(TAG,"Error on otherQuery!", e);
                                    Toast.makeText(SearchActivity.this, "An error has occurred!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //changing the other person query
                                otherSide.setStatus(RequestStatus.TAG_STATUS_REQUEST_RECEIVED);
                                otherSide.saveInBackground();

                            }
                        });

                    }
                });

            }
        });




    }

    private void sendRequest(ParseUser current) {
        FriendsRequests newFriendRequest = new FriendsRequests();
        newFriendRequest.setFromUser(ParseUser.getCurrentUser());
        newFriendRequest.setToUser(current);
        newFriendRequest.setStatus(RequestStatus.TAG_STATUS_REQUEST_SENT);
        newFriendRequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e != null){
                    Log.e(TAG,"There was a problem sending the request!", e);
                    Toast.makeText(SearchActivity.this, "There was a problem sending the request!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //friend request sent

                //changing button to say it was sent
                CURRENT_STATE = RequestStatus.TAG_STATUS_REQUEST_SENT;
                setButton(current);


                //create the other point of view of the request
                FriendsRequests otherpointOfView = new FriendsRequests();
                otherpointOfView.setFromUser(current);
                otherpointOfView.setToUser(ParseUser.getCurrentUser());
                otherpointOfView.setStatus(RequestStatus.TAG_STATUS_REQUEST_RECEIVED);
                otherpointOfView.saveEventually();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        fullName = findViewById(R.id.fullName);
        profilePic = findViewById(R.id.profilePic);
        location = findViewById(R.id.tvlocation);
        btnFriendButton = findViewById(R.id.btnFriendRequestButtonSearch);

        autoCompleteTextView = findViewById(R.id.friendSearch);
        userList = new ArrayList<>();

        CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;

        getUsers();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ParseUser current = adapter.getSelectedUser();

                Log.i(TAG, "Selected user: " + current.getUsername());
                loadUserProfile(current);
            }
        });

    }
}





