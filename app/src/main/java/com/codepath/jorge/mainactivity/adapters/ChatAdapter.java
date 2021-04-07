package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
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
import com.codepath.jorge.mainactivity.models.Chat;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    //Declaration
    //constants
    public static final String TAG = "ChatAdapter";

    //variables
    Context context;
    List<Chat> chatList;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        holder.bind(chat);

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //declaring item widgets
        ImageView ivGroupImage;
        TextView tvEventName;
        TextView tvLastMessage;
        TextView tvUpdatedAt;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);

           //finding views by id
           ivGroupImage = itemView.findViewById(R.id.ivGroupImageChat);
           tvEventName = itemView.findViewById(R.id.tvChatNameChat);
           tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
           tvUpdatedAt = itemView.findViewById(R.id.tvTimeChat);
       }

        public void bind(Chat chat) {

            //getting image
            ParseFile groupImage = (ParseFile) chat.getGroupImage();

            //loading user picture
            if(groupImage != null){
                Glide.with(context).load(groupImage.getUrl()).into(ivGroupImage);
            }
            else{
                ivGroupImage.setImageResource(R.drawable.group);
            }

            //setting date pattern
            String pattern = "hh:mm aaa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(chat.getUpdatedTime());

                    //setting the texts
                    tvEventName.setText(chat.getEvent().getTitle());
                    tvLastMessage.setText(chat.getLastMessage().getBody());
                    tvUpdatedAt.setText(date);

                }
        }
    }
