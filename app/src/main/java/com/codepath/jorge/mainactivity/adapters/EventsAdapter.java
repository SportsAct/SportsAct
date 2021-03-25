package com.codepath.jorge.mainactivity.adapters;

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

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.SportEvent;

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
            //binding the view
            tvEventTitle.setText(sportEvent.getTitle());
            tvTimeOfEvent.setText("Wed Feb 24, 2018 at 8:00 PM"); //TODO fix time
            //todo put image of user
            tvUserName.setText(sportEvent.getUser().getUsername()); //todo get name
            tvLocation.setText(sportEvent.getLocation());
            //todo sport image here
            tvSportPlayed.setText("Soccer");//todo get sport
            tvParticipantGoing.setText(Integer.toString(sportEvent.getCurrentNumberOfParticipants()));
            int remainingSpots = sportEvent.getMaxNumberOfParticipants() - sportEvent.getCurrentNumberOfParticipants();
            tvRemainingSpots.setText(remainingSpots + " more spots open.");

            //seting buttons listeners
            //todo See who is going
            btnSeeWhoIsGoing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,sportEvent.getObjectId(),Toast.LENGTH_SHORT).show();
                }
            });

            //todo chat with group
            btnChatWithGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,sportEvent.getObjectId(),Toast.LENGTH_SHORT).show();
                }
            });

            //todo join an event
            btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,sportEvent.getObjectId(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
