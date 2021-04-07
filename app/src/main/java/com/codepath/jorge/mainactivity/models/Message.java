package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Messages")
public class Message extends ParseObject {

    public static final String KEY_ID = "objectId";
    public static final String KEY_CHAT = "chat";
    public static final String KEY_TIME ="time";
    public static final String KEY_ATTACHMENT = "attachment";
    public static final String KEY_USER = "user";
    public static final String KEY_BODY = "body";

    public String getId() {
        return getObjectId();
    }
    public String getBody() {
        return getString(KEY_BODY);
    }

    public Date getDate() {
        return getDate(KEY_TIME);
    }

    public void setUserId(ParseUser userId) {
        put(KEY_USER, userId);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public void setDate(Date time) { put(KEY_TIME, time); }

}
