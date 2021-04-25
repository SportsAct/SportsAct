package com.codepath.jorge.mainactivity;

import java.util.List;

public class UserItem {
    private List<String> username;
    private int profileImage;

    public UserItem(List<String> username, int profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public List<String> getUsername() {
        return username;
    }

    public int getProfileImage() {
        return profileImage;
    }
}
