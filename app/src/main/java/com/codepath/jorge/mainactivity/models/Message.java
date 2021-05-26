package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.Date;

@ParseClassName("Messages")
public class Message extends ParseObject {


    public static final String KEY_MESSAGE_BODY = "body";
    public static final String KEY_USER = "user";
    public static final String KEY_CHAT = "chat";
    public static final String KEY_ATTACHMENT = "attachment";

    public String getBody(){
        try {
            return fetchIfNeeded().getString(KEY_MESSAGE_BODY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Not found";
    }

    public void setBody(String message){put(KEY_MESSAGE_BODY,message);}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public Chat getChat(){
        return (Chat) getParseObject(KEY_CHAT);
    }

    public void setChat(Chat chat){
        put(KEY_CHAT,chat);
    }

    public Date getTime(){return getCreatedAt();}

    public ParseFile getFile(){return getParseFile(KEY_ATTACHMENT);}

    public void setParseFile(ParseFile parseFile){put(KEY_ATTACHMENT,parseFile);}


}
