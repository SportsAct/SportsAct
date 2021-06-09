package com.codepath.jorge.mainactivity.models;

import android.content.Intent;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_FRIENDS_NUMBER = "friends_number";
    public static final String KEY_EVENT_NUMBER = "event_number";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public Integer getFriendNumber(){return getInt(KEY_FRIENDS_NUMBER);}

    public void setFriendsNumber1(){put(KEY_FRIENDS_NUMBER,getFriendNumber() + 1);}

    public Integer getEventNumber(){return getInt(KEY_EVENT_NUMBER);}

    public void setEventNumber1(){put(KEY_EVENT_NUMBER,getEventNumber() + 1);}
}
