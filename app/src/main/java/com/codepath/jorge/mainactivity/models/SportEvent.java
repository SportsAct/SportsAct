package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Events")
public class SportEvent extends ParseObject {

    public static final String KEY_ID = "objectId";
    public static final String KEY_TITLE = "eventTitle";
    public static final String KEY_DATE = "eventDate";
    public static final String KEY_USER = "user";
    public static final String KEY_PRIVACY = "eventPrivacy";
    public static final String KEY_SPORT = "sport";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_MAX_PARTICIPANTS = "eventMaxParticipants";
    public static final String KEY_CURRENT_PARTICIPANTS = "eventCurrentParticipants";

    public String getId(){return getObjectId();}

    public String getTitle(){return getString(KEY_TITLE);}

    public void setTitle(String title){put(KEY_TITLE,title);}

    public Date getEventDate(){return getDate(KEY_DATE);}

    public void setEventDate(Date date){put(KEY_DATE, date);}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public Boolean getPrivacy(){return getBoolean(KEY_PRIVACY);}

    public void setPrivacy(Boolean privacy){put(KEY_PRIVACY,privacy);}

    public SportGame getSport(){return (SportGame) getParseObject(KEY_SPORT);}

    public void setSport(SportGame sport){put(KEY_SPORT,sport);}

    //stretch for location to get parks from google
    public String getLocation(){return getString(KEY_LOCATION);}

    public void setLocation(String location){put(KEY_LOCATION,location);}

    public int getMaxNumberOfParticipants(){return getNumber(KEY_MAX_PARTICIPANTS).intValue();}

    public void setMaxNumberOfParticipants(int number){put(KEY_MAX_PARTICIPANTS,number);}

    public int getCurrentNumberOfParticipants(){return getNumber(KEY_CURRENT_PARTICIPANTS).intValue();}

    public void setCurrentNumberOfParticipants(int number){put(KEY_CURRENT_PARTICIPANTS,number);}
}
