package com.codepath.jorge.mainactivity.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Chat")
public class Chat extends ParseObject implements Comparable<Chat>{

    public static final String KEY_EVENT = "event";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_GROUP_IMAGE = "group_image";

    public String getId(){return getObjectId();}

    public SportEvent getEvent(){
        return (SportEvent) getParseObject(KEY_EVENT);
    }

    public void setEvent(SportEvent sportEvent){
        put(KEY_EVENT,sportEvent);
    }

    public Message getLastMessage(){return (Message) getParseObject(KEY_LAST_MESSAGE);}

    public void setLastMessage(Message lastMessage){put(KEY_LAST_MESSAGE,lastMessage);}

    public Date getUpdatedTime(){return getUpdatedAt();}

    public ParseFile getGroupImage(){return getParseFile(KEY_GROUP_IMAGE);}

    public void setGroupImage(ParseFile parseFile){put(KEY_GROUP_IMAGE,parseFile);}

    @Override
    public int compareTo(Chat chat) {
        return (int) (chat.getUpdatedTime().getTime() - getUpdatedTime().getTime());
    }
}
