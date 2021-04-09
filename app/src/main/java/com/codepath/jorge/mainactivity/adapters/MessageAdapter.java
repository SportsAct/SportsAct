package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.models.Message;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    //declaration
    //constants
    public static final String TAG = "MessageAdapter";
    public static final int MESSAGE_OUTGOING = 123;
    public static final int MESSAGE_INCOMING = 321;

    //variables
    private List<Message> mMessages;
    private Context mContext;
    private ParseUser mUser;

    //constructor for adapter
    public MessageAdapter(Context context, ParseUser user, List<Message> messages) {
        mMessages = messages;
        this.mUser = user;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    //see which message it is
    @Override
    public int getItemViewType(int position) {

        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    //checks to see if a message is yours
    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getUser().getObjectId().equals(mUser.getObjectId());
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;
        TextView tvTime;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = itemView.findViewById(R.id.ivProfileOther);
            body = itemView.findViewById(R.id.tvBody);
            name = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTimeMI);
        }

        @Override
        public void bindMessage(Message message) {

            //get image
            ParseFile parseFile = (ParseFile) message.getUser().get("profilePicture");

            if(parseFile != null) {
                Glide.with(mContext)
                        .load(parseFile.getUrl())
                        .into(imageOther);
            }
            else {
                imageOther.setImageResource(R.drawable.empty_profile);
            }

            name.setText(message.getUser().getUsername()); // in addition to message show user username
            body.setText(message.getBody());

            //getting date
            String pattern = "h:mm aaa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(message.getCreatedAt());
            tvTime.setText(date);

        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;
        TextView tvTime;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = itemView.findViewById(R.id.ivProfileMe);
            body = itemView.findViewById(R.id.tvBody);
            tvTime = itemView.findViewById(R.id.tvTimeMO);
        }

        @Override
        public void bindMessage(Message message) {

            //get image
            ParseFile parseFile = (ParseFile) message.getUser().get("profilePicture");

            if(parseFile != null) {
                Glide.with(mContext)
                        .load(parseFile.getUrl())
                        .into(imageMe);
            }
            else {
                imageMe.setImageResource(R.drawable.empty_profile);
            }

            body.setText(message.getBody());

            //getting date
            String pattern = "h:mm aaa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(message.getCreatedAt());
            tvTime.setText(date);
        }
    }



}
