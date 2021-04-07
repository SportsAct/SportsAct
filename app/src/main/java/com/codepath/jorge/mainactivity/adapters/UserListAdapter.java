package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    //Declaration
    //constants
    public static final String TAG = "UserListAdapter";

    //variables
    Context context;
    List<ParseUser> userList;

    public  UserListAdapter(Context context, List<ParseUser> list){
        this.context = context;
        this.userList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ParseUser user = userList.get(position);

        holder.bind(user);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

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
}
