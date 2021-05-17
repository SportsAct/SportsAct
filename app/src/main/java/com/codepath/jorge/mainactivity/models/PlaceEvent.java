package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Place")
public class PlaceEvent extends ParseObject {

    public static final String KEY_LATLON = "LatLon";
    public static final String KEY_URL = "URL";
    public static final String KEY_NAME = "name";
    public static final String KEY_GOOGLE_ID = "google_id";

    public String getId(){return getObjectId();}

    public ParseGeoPoint getLatLon(){return getParseGeoPoint(KEY_LATLON);}

    public void setLatLon(ParseGeoPoint geoPoint){put(KEY_LATLON, geoPoint);}

    public String getURL(){return getString(KEY_URL);}

    public void setURL(String URL){ put(KEY_URL,URL);}

    public String getName(){return getString(KEY_NAME);}

    public void setName(String name){put(KEY_NAME,name);}

    public String getGoogleId(){return getString(KEY_GOOGLE_ID);}

    public void setKeyGoogleId(String id){put(KEY_GOOGLE_ID,id);}
}
