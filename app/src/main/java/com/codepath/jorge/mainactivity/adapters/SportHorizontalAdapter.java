package com.codepath.jorge.mainactivity.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class SportHorizontalAdapter extends RecyclerView.Adapter<SportHorizontalAdapter.ViewHolder> {

    //Declaration
    //constants
    public static final String TAG = "SportHorizontalAdapter";

    //variables
    Context context;
    List<SportGame> sportGameList;
    List<SportGame> selectedSportsList;
    SportGame selectedSport;
    boolean multipleSelections;
    boolean firstSetUp = true;

    public SportHorizontalAdapter(Context context, List<SportGame> sportGameList, SportGame selectedSport) {
        this.context = context;
        this.sportGameList = sportGameList;
        this.selectedSport = selectedSport;
        multipleSelections = false;
    }

    public SportHorizontalAdapter(Context context, List<SportGame> sportGameList, List<SportGame> selectedSportsList) {
        this.context = context;
        this.sportGameList = sportGameList;

        if(firstSetUp) {
            this.selectedSportsList = selectedSportsList;
        }

        multipleSelections = true;
        firstSetUp = false;
    }

    public List<SportGame> getSelectedSportsList(){
        return selectedSportsList;
    }

    public SportGame getSelectedSport(){
        return selectedSport;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sport,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SportGame sportGame = sportGameList.get(position);

        holder.bind(sportGame);

    }

    @Override
    public int getItemCount() {
        return sportGameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //widget declaration
        ImageView ivSportImage;
        TextView tvSportName;
        RelativeLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //finding views by id
            ivSportImage = itemView.findViewById(R.id.ivSportImageItemSport);
            tvSportName = itemView.findViewById(R.id.tvSportNameItemSport);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(SportGame sportGame) {

            //getting image
            ParseFile sportImage = sportGame.getSportImage();

            //loading image
            Glide.with(context).load(sportImage.getUrl()).into(ivSportImage);

            //setting text
            tvSportName.setText(sportGame.getSportName());

            //check selection type
            if(multipleSelections){
                if(isSportSelected(sportGame)){
                    container.setBackgroundColor(context.getResources().getColor(R.color.teal_light));
                }
                else {
                    container.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
            }
            else {
                //check if item is the selected one
                if (selectedSport.getObjectId() != null && selectedSport.getObjectId().equals(sportGame.getObjectId())) {
                    container.setBackgroundColor(context.getResources().getColor(R.color.teal_light));
                } else {
                    container.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
            }

            //listener
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(multipleSelections){
                        if(isSportSelected(sportGame)){
                            removeSport(sportGame);
                        }
                        else{
                            selectedSportsList.add(sportGame);
                        }
                    }
                    else {
                        selectedSport = sportGame;
                    }

                    notifyDataSetChanged();
                }
            });
        }

        private void removeSport(SportGame sportGame) {

            if(selectedSportsList.isEmpty()){
                return;
            }

            for(int i = 0; i< selectedSportsList.size();i++){
                //if already check, uncheck
                if(sportGame.getObjectId().equals(selectedSportsList.get(i).getObjectId())){
                    selectedSportsList.remove(i);
                }
            }

        }

        private boolean isSportSelected(SportGame sportGame) {

            if(selectedSportsList.isEmpty()){
                return false;
            }

            //goes through selected list
            for(int i = 0; i< selectedSportsList.size();i++){
                //if already check, uncheck
                if(sportGame.getObjectId().equals(selectedSportsList.get(i).getObjectId())){
                    return true;
                }
            }

            //if not on the list added to selected list
            return false;
        }
    }
}
