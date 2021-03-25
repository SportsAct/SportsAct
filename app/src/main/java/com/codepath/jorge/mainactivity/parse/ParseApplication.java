package com.codepath.jorge.mainactivity.parse;

import android.app.Application;

import com.codepath.jorge.mainactivity.models.SportEvent;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //register here your parse model
        ParseObject.registerSubclass(SportEvent.class);

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("VR96MYDQPZyF7dgNGvwSV4d9XuAFPNGlTtsnrfjb") // should correspond to Application Id env variable
                .clientKey("AMFYSy1jTGDkj12fjtFW4moolfu22QsMJsZHVtSd")  // should correspond to Client key env variable
                        .server("https://parseapi.back4app.com").build());
    }
}

