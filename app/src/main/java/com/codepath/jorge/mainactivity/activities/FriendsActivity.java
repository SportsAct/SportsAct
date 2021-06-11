package com.codepath.jorge.mainactivity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.adapters.UserListAdapter;
import com.codepath.jorge.mainactivity.models.FriendsRequests;
import com.codepath.jorge.mainactivity.models.RequestStatus;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    //declaration
    //constants
    public static final String TAG = "FriendsActivity";

    //widgets
    private EditText etSearchForFriends;
    private RecyclerView rvFriendRequestRecycler;
    private RecyclerView rvAllFriendsRecycler;
    private Toolbar tbToolbar;
    private LoadingDialog loadingDialog;
    private TextView tvNoFriendRequests;
    private TextView tvNoFriendsYet;
    private Button btnFindMoreFriends;

    //adapter
    private UserListAdapter friendsRequestAdapter;
    private UserListAdapter allFriendsAdapter;

    //variables
    private List<ParseUser> userRequestList;
    private List<ParseUser> friendsList;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //progress indicator creation
        loadingDialog = new LoadingDialog(this);
        //starting load dialog
        loadingDialog.startLoadingDialog();

        //finding views
        etSearchForFriends = findViewById(R.id.etSearchForFriendsFriendsScreen);
        rvFriendRequestRecycler = findViewById(R.id.rvFriendRequestsFriendScreen);
        rvAllFriendsRecycler = findViewById(R.id.rvAllFriendsFriendScreen);
        tbToolbar = findViewById(R.id.tbToolbar);
        tvNoFriendRequests = findViewById(R.id.tvNoFriendRequestFriendScreen);
        tvNoFriendsYet = findViewById(R.id.tvNoFriendTextFriendsScreen);
        btnFindMoreFriends = findViewById(R.id.btnFindFriends);

        //setting current user
        currentUser = ParseUser.getCurrentUser();

        //setting bar
        tbToolbar.setTitle(currentUser.getUsername());
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing user lists
        userRequestList = new ArrayList<>();
        friendsList = new ArrayList<>();

        //setting adapter
        LinearLayoutManager linearLayoutManagerRequest = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManagerFriend = new LinearLayoutManager(this);

        //friend request adapter
        friendsRequestAdapter = new UserListAdapter(this, userRequestList,0);
        rvFriendRequestRecycler.setAdapter(friendsRequestAdapter);
        rvFriendRequestRecycler.setLayoutManager(linearLayoutManagerRequest);

        //friends adapter
        allFriendsAdapter = new UserListAdapter(this,friendsList,1);
        rvAllFriendsRecycler.setAdapter(allFriendsAdapter);
        rvAllFriendsRecycler.setLayoutManager(linearLayoutManagerFriend);

        //getting user lists
        //getting request lists
        getRequestList();
        //getting friends
        getFriends();

        //find friends listener
        btnFindMoreFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo find more friends
            }
        });
    }

    private void getFriends() {

        ParseQuery<FriendsRequests> query = ParseQuery.getQuery(FriendsRequests.class);
        query.whereEqualTo(FriendsRequests.KEY_FROM_USER, currentUser);
        query.whereEqualTo(FriendsRequests.KEY_STATUS, RequestStatus.TAG_STATUS_FRIENDS);
        query.include(FriendsRequests.KEY_TO_USER);
        query.orderByAscending(FriendsRequests.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<FriendsRequests>() {
            @Override
            public void done(List<FriendsRequests> returnedFriendList, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the friend !", e);
                    //show there is no friends
                    btnFindMoreFriends.setVisibility(View.VISIBLE);
                    tvNoFriendsYet.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }

                //add users that are friends to the friend list
                for(int i = 0; i < returnedFriendList.size(); i++){
                    friendsList.add(returnedFriendList.get(i).getToUser());
                }

                //notifying that there is data in request list
                allFriendsAdapter.notifyDataSetChanged();

                btnFindMoreFriends.setVisibility(View.GONE);
                tvNoFriendsYet.setVisibility(View.GONE);

                if(friendsList.size() == 0){
                    btnFindMoreFriends.setVisibility(View.VISIBLE);
                    tvNoFriendsYet.setVisibility(View.VISIBLE);
                }

                //hide bar
                loadingDialog.dismissDialog();

            }
        });
    }

    private void getRequestList() {

        ParseQuery<FriendsRequests> query = ParseQuery.getQuery(FriendsRequests.class);
        query.whereEqualTo(FriendsRequests.KEY_FROM_USER, currentUser);
        query.whereEqualTo(FriendsRequests.KEY_STATUS, RequestStatus.TAG_STATUS_REQUEST_RECEIVED);
        query.include(FriendsRequests.KEY_TO_USER);
        query.orderByAscending(FriendsRequests.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<FriendsRequests>() {
            @Override
            public void done(List<FriendsRequests> friendRequests, ParseException e) {

                //something went wrong
                if(e != null){
                    Log.e(TAG,"There was a problem loading the friend requests!", e);
                   //show there is no request
                    tvNoFriendRequests.setVisibility(View.VISIBLE);
                    loadingDialog.dismissDialog();
                    return;
                }

                //add users that send request to request list
                for(int i = 0; i < friendRequests.size(); i++){
                    userRequestList.add(friendRequests.get(i).getToUser());
                }

                //notifying that there is data in request list
                friendsRequestAdapter.notifyDataSetChanged();

                tvNoFriendRequests.setVisibility(View.GONE);

                if(userRequestList.size() == 0){
                    tvNoFriendRequests.setVisibility(View.VISIBLE);
                }

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
}