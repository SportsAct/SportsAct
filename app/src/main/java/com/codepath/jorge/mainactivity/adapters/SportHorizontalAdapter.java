package com.codepath.jorge.mainactivity.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
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
import com.codepath.jorge.mainactivity.models.SportGame;
import com.parse.ParseFile;
import java.util.List;

public class SportHorizontalAdapter extends RecyclerView.Adapter<SportHorizontalAdapter.ViewHolder> {

    //Declaration
    //constants
    public static final String TAG = "SportHorizontalAdapter";

    //variables
    Context context;
    List<SportGame> sportGameList;
    String selectedSport;

    public SportHorizontalAdapter(Context context, List<SportGame> sportGameList) {
        this.context = context;
        this.sportGameList = sportGameList;
        selectedSport = "";
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

            //check if item is the selected one
            if(selectedSport.equals(sportGame.getObjectId())){
                container.setBackgroundColor(context.getResources().getColor(R.color.selected_item));
            }
            else
            {
                container.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            //listener
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedSport = sportGame.getObjectId();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
