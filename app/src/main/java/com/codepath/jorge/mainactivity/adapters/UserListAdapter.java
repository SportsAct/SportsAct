package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
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

        private TextView tvName, tvUserName;
        private ImageView ivProfilePic;
        private Button btnAcceptButton;
        private ImageButton btnDeclineRequest;

        public FriendRequestViewHolder(View v) {
            super(v);
            tvName =  v.findViewById(R.id.tvActualNameUserItemRequest);
            tvUserName =  v.findViewById(R.id.tvUserNameUserItemRequest);
            ivProfilePic = v.findViewById(R.id.ivParticipantUserItemRequest);
            btnAcceptButton = v.findViewById(R.id.btnAcceptFriendRequest);
            btnDeclineRequest = v.findViewById(R.id.btnCancelFriendRequest);
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
                   //todo implement accept
               }
           });

           //decline
           btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //todo implement decline
               }
           });

       }
    }
}
