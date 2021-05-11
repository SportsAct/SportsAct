package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {

    public static final String KEY_CITY_NAME = "cityName";
    public static final String KEY_STATE_NAME = "stateName";
    public static final String KEY_LATLON = "LatLon";
    public static final String KEY_GOOGLE_ID = "google_id";

    public String getId(){return getObjectId();}

    public String getCityName(){
        try {
            return fetchIfNeeded().getString(KEY_CITY_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Not Found";
    }

    public void setCityName(String name){put(KEY_CITY_NAME,name);}

    public String getStateName(){
        try {
            return fetchIfNeeded().getString(KEY_STATE_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Not Found";
    }

    public void setStateName(String name){put(KEY_STATE_NAME,name);}

    public String getGoogleId(){return getString(KEY_GOOGLE_ID);}

    public void setKeyGoogleId(String id){put(KEY_GOOGLE_ID,id);}

    public ParseGeoPoint getLatLon(){return getParseGeoPoint(KEY_LATLON);}

    public void setLatLon(ParseGeoPoint geoPoint){put(KEY_LATLON, geoPoint);}
}
