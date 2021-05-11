package com.codepath.jorge.mainactivity.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.adapters.ChatAdapter;
import com.codepath.jorge.mainactivity.adapters.LoadingDialog;
import com.codepath.jorge.mainactivity.models.Chat;
import com.codepath.jorge.mainactivity.models.ChatUserJoin;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//todo dates show if more than one day
public class ChatFragment extends Fragment {

    //declaration
    //constants
    static final String TAG = "ChatFragment";

    //widgets
    private RecyclerView recyclerViewChats;
    LoadingDialog loadingDialog;

    //adapter
    ChatAdapter adapter;

    //variables
    List<Chat> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //finding views
        recyclerViewChats = view.findViewById(R.id.rvChat);

        //progress indicator creation
        loadingDialog = new LoadingDialog(getActivity());
        //starting the loading dialog
        loadingDialog.startLoadingDialog();

        //initializing event list
        chatList = new ArrayList<>();

        //recycler view performance
        recyclerViewChats.setHasFixedSize(true);

        //setting adapter
        adapter = new ChatAdapter(getContext(),chatList);
        recyclerViewChats.setAdapter(adapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

        //get the chats
        getUserChats();

    }

    @Override
    public void onResume() {
        super.onResume();

        //so when it leaves a chat is updated immediately
        getUserChats();
    }

    private void getUserChats() {

        ParseQuery<ChatUserJoin> query = ParseQuery.getQuery(ChatUserJoin.class);
        query.include(ChatUserJoin.KEY_CHAT);
        query.whereEqualTo(ChatUserJoin.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ChatUserJoin>() {
            @Override
            public void done(List<ChatUserJoin> objects, ParseException e) {

                //something went wrong
                if(e != null){
                    loadingDialog.dismissDialog();
                    Log.e(TAG,"There was a problem loading the chats!!", e);
                    Toast.makeText(getContext(), "There was a problem loading the chats", Toast.LENGTH_SHORT).show();
                    return;
                }

                //clearing the list for not duplicates
                chatList.clear();

                //adding chats
                for(int i = 0;i < objects.size();i++){

                    chatList.add(objects.get(i).getChat());
                }

                Collections.sort(chatList);

                //if data is empty
                if(chatList.isEmpty()) {

                    Fragment fragment = new EmptyFragment();

                    final FragmentManager fragmentManager = getFragmentManager();

                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                }

                //notify adapter
                adapter.notifyDataSetChanged();

                //dismissing loading dialog
                loadingDialog.dismissDialog();
            }
        });
    }

}