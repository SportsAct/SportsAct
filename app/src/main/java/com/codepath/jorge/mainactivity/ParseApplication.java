package com.codepath.jorge.mainactivity;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("VR96MYDQPZyF7dgNGvwSV4d9XuAFPNGlTtsnrfjb")
                .clientKey("AMFYSy1jTGDkj12fjtFW4moolfu22QsMJsZHVtSd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
    }

