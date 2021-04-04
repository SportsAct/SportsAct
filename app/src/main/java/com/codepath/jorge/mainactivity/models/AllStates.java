package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Usabystate_States")
public class AllStates extends ParseObject {

    public static final String KEY_ABREVIATION = "postalAbreviation";
    public static final String KEY_NAME = "name";

    public String getId(){return getObjectId();}

    public String getAbreviation(){return getString(KEY_ABREVIATION);}

    public String getName(){return getString(KEY_NAME);}

}
