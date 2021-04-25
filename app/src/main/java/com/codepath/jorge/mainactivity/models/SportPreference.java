package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("SportPreference")
public class SportPreference extends ParseObject {

    public static final String KEY_ID = "objectId";
    public static final String KEY_USER = "user";
    public static final String KEY_SPORT = "sport";

    public String getId(){return getObjectId();}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public SportGame getSport(){return (SportGame) getParseObject(KEY_SPORT);}

    public void setSport(SportGame sport){put(KEY_SPORT,sport);}
}
