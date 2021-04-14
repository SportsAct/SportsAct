package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Sport")
public class SportGame extends ParseObject {

    public static final String KEY_SPORT_NAME = "sportName";
    public static final String KEY_SPORT_IMAGE = "sportImage";

    public String getId(){return getObjectId();}

    public String getSportName(){return getString(KEY_SPORT_NAME);}

    public void setSportName(String sportName){put(KEY_SPORT_NAME,sportName);}

    public ParseFile getSportImage(){return getParseFile(KEY_SPORT_IMAGE);}

    public void setSportImage(ParseFile parseFile){put(KEY_SPORT_IMAGE,parseFile);}
}
