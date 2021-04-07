package com.codepath.jorge.mainactivity.parse;

import android.app.Application;

import com.codepath.jorge.mainactivity.models.AllStates;
import com.codepath.jorge.mainactivity.models.Chat;
import com.codepath.jorge.mainactivity.models.ChatUserJoin;
import com.codepath.jorge.mainactivity.models.EventParticipant;
import com.codepath.jorge.mainactivity.models.Message;
import com.codepath.jorge.mainactivity.models.Location;
import com.codepath.jorge.mainactivity.models.SportEvent;
import com.codepath.jorge.mainactivity.models.SportGame;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //register here your parse model
        ParseObject.registerSubclass(SportEvent.class);
        ParseObject.registerSubclass(SportGame.class);
        ParseObject.registerSubclass(EventParticipant.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(AllStates.class);
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(ChatUserJoin.class);

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("VR96MYDQPZyF7dgNGvwSV4d9XuAFPNGlTtsnrfjb") // should correspond to Application Id env variable
                .clientKey("AMFYSy1jTGDkj12fjtFW4moolfu22QsMJsZHVtSd")  // should correspond to Client key env variable
                        .server("https://parseapi.back4app.com").build());
    }
}

