package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("EventParticipant")
public class EventParticipant extends ParseObject {

    public static final String KEY_ID = "objectId";
    public static final String KEY_USER = "user";
    public static final String KEY_EVENT = "event";

    public String getId(){return getObjectId();}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public SportEvent getEvent(){
        return (SportEvent) getParseObject(KEY_EVENT);
    }

    public void setEvent(SportEvent sportEvent){
        put(KEY_EVENT,sportEvent);
    }
}
