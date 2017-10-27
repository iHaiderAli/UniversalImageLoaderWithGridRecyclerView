package com.haider.universalImageloader.Model;

/**
 * Created by support on 10/27/17.
 */

public class UserModel {

    String imageUrl,userName;

    public UserModel(String userName, String imageUrl) {
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
