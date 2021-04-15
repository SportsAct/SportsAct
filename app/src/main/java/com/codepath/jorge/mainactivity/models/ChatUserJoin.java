package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ChatUserJoin")
public class ChatUserJoin extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_CHAT = "chat";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public Chat getChat(){
        return (Chat) getParseObject(KEY_CHAT);
    }

    public void setChat(Chat chat){
        put(KEY_CHAT,chat);
    }
}
