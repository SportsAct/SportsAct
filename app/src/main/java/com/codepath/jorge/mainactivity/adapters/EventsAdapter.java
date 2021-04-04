package com.codepath.jorge.mainactivity.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.activities.EventParticipantsActivity;
import com.codepath.jorge.mainactivity.models.EventParticipant;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    //declaration
    //constants
    public static final String TAG = "EventAdapter";

    private Context context;
    private List<SportEvent> sportEventList;

    public EventsAdapter(Context context, List<SportEvent> sportEventList) {
        this.context = context;
        this.sportEventList = sportEventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SportEvent sportEvent = sportEventList.get(position);
        holder.bind(sportEvent);
    }

    @Override
    public int getItemCount() {
        return sportEventList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //declaring item widgets
        TextView tvEventTitle;
        TextView tvTimeOfEvent;
        ImageView ivUserProfilePic;
        TextView tvUserName;
        TextView tvLocation;
        ImageView ivSportImage;
        TextView tvSportPlayed;
        TextView tvParticipantGoing;
        TextView tvRemainingSpots;
        Button btnSeeWhoIsGoing;
        Button btnChatWithGroup;
        Button btnJoinEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //finding views by id
            tvEventTitle = itemView.findViewById(R.id.tvEventTitleHome);
            tvTimeOfEvent = itemView.findViewById(R.id.tvTimeOfEventHome);
            ivUserProfilePic = itemView.findViewById(R.id.ivHostProfilePictureHome);
            tvUserName = itemView.findViewById(R.id.tvHostNameHome);
            tvLocation = itemView.findViewById(R.id.tvLocationHome);
            ivSportImage = itemView.findViewById(R.id.ivSportIconHome);
            tvSportPlayed = itemView.findViewById(R.id.tvSportPlayHome);
            tvParticipantGoing = itemView.findViewById(R.id.tvParticipantsGoingHome);
            tvRemainingSpots = itemView.findViewById(R.id.tvRemainingSpotsHome);
            btnSeeWhoIsGoing = itemView.findViewById(R.id.btnSeeParticipantsHome);
            btnChatWithGroup = itemView.findViewById(R.id.btnChatWithGroupHome);
            btnJoinEvent = itemView.findViewById(R.id.btnJoinEventHome);
        }

        public void bind(SportEvent sportEvent) {

            //setting date pattern
            String pattern = "EEE MMM dd, yyyy 'at' hh:mm aaa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(sportEvent.getEventDate());

            //getting user profile image
            ParseFile profileImage = (ParseFile) sportEvent.getUser().get("profilePicture");

            //getting sport image
            ParseFile sportImage = sportEvent.getSport().getSportImage();

            //binding the view
            tvEventTitle.setText(sportEvent.getTitle());
            tvTimeOfEvent.setText(date);
            tvUserName.setText((String) sportEvent.getUser().get("name"));
            tvLocation.setText(sportEvent.getLocation().getCityName() + ", " + sportEvent.getLocation().getStateName());
            tvSportPlayed.setText(sportEvent.getSport().getSportName());
            tvParticipantGoing.setText(Integer.toString(sportEvent.getCurrentNumberOfParticipants()));
            int remainingSpots = sportEvent.getMaxNumberOfParticipants() - sportEvent.getCurrentNumberOfParticipants();
            tvRemainingSpots.setText(remainingSpots + " more spots open.");

            //loading user picture
            if(profileImage != null){
                Glide.with(context).load(profileImage.getUrl()).into(ivUserProfilePic);
            }
            else{
                ivUserProfilePic.setImageResource(R.drawable.empty_profile);
            }
            //loading sport image
            if(sportEvent!= null){
                Glide.with(context).load(sportImage.getUrl()).into(ivSportImage);
            }

            //seting buttons listeners
            btnSeeWhoIsGoing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EventParticipantsActivity.class);
                    intent.putExtra("event_id",sportEvent.getId());
                    context.startActivity(intent);
                }
            });

            //todo chat with group
            btnChatWithGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,sportEvent.getObjectId(),Toast.LENGTH_SHORT).show();
                }
            });

            btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   checkIfUserIsAParticipant(sportEvent);
                }
            });
        }

        //check if user is in the event already
        private void checkIfUserIsAParticipant(SportEvent sportEvent) {

            ParseQuery<EventParticipant> query = ParseQuery.getQuery(EventParticipant.class);
            query.whereEqualTo(EventParticipant.KEY_USER, ParseUser.getCurrentUser());
            query.whereEqualTo(EventParticipant.KEY_EVENT, sportEvent);
            query.getFirstInBackground(new GetCallback<EventParticipant>() {
                @Override
                public void done(EventParticipant user, ParseException e) {

                    //something went wrong
                    if(e != null){

                        if(user == null){
                            //check if event is full
                            if(sportEvent.getCurrentNumberOfParticipants() >= sportEvent.getMaxNumberOfParticipants()){
                                Toast.makeText(context, "Event is full!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            enrollUserInEvent(sportEvent);
                        }
                        else {
                            Toast.makeText(context, "You are already enrolled in this event!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "You are already enrolled in this event!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        //enroll an user into an event
        private void enrollUserInEvent(SportEvent sportEvent) {

            //todo enroll user in chat

            //enroll user in event
            EventParticipant newParticipant = new EventParticipant();
            newParticipant.setUser(ParseUser.getCurrentUser());
            newParticipant.setEvent(sportEvent);
            newParticipant.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    //something went wrong
                    if(e != null){
                        Log.e(TAG,"There was a problem enrolling user", e);
                        Toast.makeText(context, "There was a problem enrolling user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //update event to have one more
                    updateEvent(sportEvent);

                }
            });

        }

        //update the event to have 1 more participant
        private void updateEvent(SportEvent sportEvent) {

            sportEvent.setCurrentNumberOfParticipants(sportEvent.getCurrentNumberOfParticipants() + 1);
            sportEvent.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    //something went wrong
                    if(e != null){
                        Log.e(TAG,"There was a problem updating the event", e);
                        Toast.makeText(context, "There was a problem updating the event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(context, "Joining event was a success!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    //todo take to chat activity

                }
            });
        }
    }
}
