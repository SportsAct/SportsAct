package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {

    public static final String KEY_STATE = "state";
    public static final String KEY_CITY_NAME = "cityName";
    public static final String KEY_STATE_NAME = "stateName";

    public String getId(){return getObjectId();}

    public AllStates getState(){return (AllStates) getParseObject(KEY_STATE);}

    public void setState(AllStates state){put(KEY_STATE,state);}

    public String getCityName(){return getString(KEY_CITY_NAME);}

    public void setCityName(String name){put(KEY_CITY_NAME,name);}

    public String getStateName(){return getString(KEY_STATE_NAME);}

    public void setStateName(String name){put(KEY_STATE_NAME,name);}
}
