package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PointerEncoder;

@ParseClassName("Requests")
public class Requests extends ParseObject {

    private static final String KEY_FROM_USER = "fromUser";
    public static final String KEY_TO_USER = "toUser";
    public static final String KEY_STATUS = "status";

    public String getId(){return getObjectId();}

    public ParseUser getFromUser(){
        return getParseUser(KEY_FROM_USER);
    }
    public void setFromUser(ParseUser parseUser){
        put(KEY_FROM_USER,parseUser);
    }

    public ParseUser getToUser(){
        return getParseUser(KEY_TO_USER);
    }
    public void setToUser(ParseUser parseUser){
        put(KEY_TO_USER,parseUser);
    }

    public ParseUser getStatus(){return (ParseUser) getParseObject(KEY_STATUS);}

    public void setStatus(ParseUser toUser){put(KEY_STATUS,toUser);}

}
