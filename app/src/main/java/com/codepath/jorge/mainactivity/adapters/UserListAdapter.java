package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.activities.SearchActivity;
import com.codepath.jorge.mainactivity.models.FriendsRequests;
import com.codepath.jorge.mainactivity.models.RequestStatus;
import com.codepath.jorge.mainactivity.models.UserInfo;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class UserListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Declaration
    //constants
    public static final String TAG = "UserListAdapter";

    //variables
    Context context;
    List<ParseUser> userList;
    private final int REQUEST = 0, FRIEND = 1;
    int viewType;

    public  UserListAdapter(Context context, List<ParseUser> list, int num){
        this.context = context;
        this.userList = list;
        this.viewType = num;
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
        return viewType;
    }

    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param parent ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case REQUEST:
                View v1 = inflater.inflate(R.layout.item_friend_request, parent, false);
                viewHolder = new FriendRequestViewHolder(v1);
                break;
            case FRIEND:
                View v2 = inflater.inflate(R.layout.user_item, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.user_item, parent, false);
                viewHolder = new ViewHolder(v);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case REQUEST:
                FriendRequestViewHolder vh1 = (FriendRequestViewHolder) viewHolder;
                vh1.bind(userList.get(position));
                break;
            case FRIEND:
                ViewHolder vh2 = (ViewHolder) viewHolder;
                vh2.bind(userList.get(position));
                break;
            default:
                ViewHolder v = (ViewHolder) viewHolder;
                v.bind(userList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //Different View Holders Below
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    //Default View Holder
    class ViewHolder extends RecyclerView.ViewHolder{

        //declaring item widgets
        ImageView ivUserProfilePic;
        TextView tvUsername;
        TextView tvActualName;
        Button btnActionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //finding views by id
            ivUserProfilePic = itemView.findViewById(R.id.ivParticipantUserItem);
            tvUsername = itemView.findViewById(R.id.tvUserNameUserItem);
            tvActualName = itemView.findViewById(R.id.tvActualNameUserItem);
            btnActionButton = itemView.findViewById(R.id.btnActionButton);
        }

        public void bind(ParseUser parseUser) {

            //getting image
            ParseFile  profileImage = (ParseFile) parseUser.get("profilePicture");

            //loading user picture
            if(profileImage != null){
                Glide.with(context).load(profileImage.getUrl()).into(ivUserProfilePic);
            }
            else {
                ivUserProfilePic.setImageResource(R.drawable.empty_profile);
            }

            //setting texts
            tvUsername.setText(parseUser.getUsername());
            tvActualName.setText(parseUser.getString("name"));

            //listeners
            btnActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo do something
                }
            });
        }
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        //declaration
        //widgets
        private TextView tvName, tvUserName;
        private ImageView ivProfilePic;
        private Button btnAcceptButton;
        private ImageButton btnDeclineRequest;
        RelativeLayout viewOverlay;
        ImageView ivCheckMark;

        //variables
        String CURRENT_STATE;

        public FriendRequestViewHolder(View v) {
            super(v);
            tvName =  v.findViewById(R.id.tvActualNameUserItemRequest);
            tvUserName =  v.findViewById(R.id.tvUserNameUserItemRequest);
            ivProfilePic = v.findViewById(R.id.ivParticipantUserItemRequest);
            btnAcceptButton = v.findViewById(R.id.btnAcceptFriendRequest);
            btnDeclineRequest = v.findViewById(R.id.btnCancelFriendRequest);
            viewOverlay = v.findViewById(R.id.unit_overlay);
            ivCheckMark = v.findViewById(R.id.ivCheckMarkFriendAdded);

            CURRENT_STATE = RequestStatus.TAG_STATUS_REQUEST_RECEIVED;
        }

       public void bind(ParseUser user){

           //getting image
           ParseFile  profileImage = (ParseFile) user.get("profilePicture");

            //setting stuff
           tvName.setText((String) user.get("name"));
           tvUserName.setText(user.getUsername());

           //loading user picture
           if(profileImage != null){
               Glide.with(context).load(profileImage.getUrl()).into(ivProfilePic);
           }
           else {
               ivProfilePic.setImageResource(R.drawable.empty_profile);
           }

           //setting the buttons

           //accept button
           btnAcceptButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   updatingFriendRequest(user,true);
               }
           });

           //decline
           btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   updatingFriendRequest(user,false);
               }
           });

           ((Animatable) ivCheckMark.getDrawable()).start();

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
                        Toast.makeText(context, "There was a problem declining the request!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "There was a problem declining the request!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //setting the button
                            if(accepting) {
                                CURRENT_STATE = RequestStatus.FRIENDS;
                                //update friend number
                                updateFriendNumbers(current);
                            }
                            else {
                                CURRENT_STATE = RequestStatus.TAG_STATUS_NOT_FRIENDS;
                            }

                            if(accepting) {
                                changeButtonAcept();
                            }
                            else {
                                removeFromRecycler();
                            }

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
                        Toast.makeText(context, "There was a problem updating the request!", Toast.LENGTH_SHORT).show();
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
                    //todo update button setButton(current);
                }
            });
        }

        private void removeFromRecycler() {
            userList.remove(getAdapterPosition());
            notifyDataSetChanged();
        }

        private void changeButtonAcept() {

            int shortAnimationDuration;
            shortAnimationDuration = context.getResources().getInteger(
                    android.R.integer.config_longAnimTime

            );

            //making view visible
            viewOverlay.setAlpha(0f);
            viewOverlay.setVisibility(View.VISIBLE);

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            viewOverlay.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);

            //animate
            ((Animatable) ivCheckMark.getDrawable()).start();

        }

        private void updateFriendNumbers(ParseUser current) {

            ParseQuery<UserInfo> queryOwn = ParseQuery.getQuery(UserInfo.class);
            queryOwn.whereEqualTo(UserInfo.KEY_USER, ParseUser.getCurrentUser());
            queryOwn.getFirstInBackground(new GetCallback<UserInfo>() {
                @Override
                public void done(UserInfo currentUserInfo, ParseException e) {

                    if(e != null){
                        Log.e(TAG,"Error updating current profile!", e);
                        Toast.makeText(context, "Error updating current profile!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Error updating user profile!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    otherUserInfo.setFriendsNumber1();
                    otherUserInfo.saveEventually();
                }
            });


        }
    }


}
