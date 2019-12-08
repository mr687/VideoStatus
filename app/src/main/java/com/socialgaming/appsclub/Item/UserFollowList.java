package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class UserFollowList implements Serializable {

    private String follow_user_id,follow_user_name,user_image,is_verified;

    public UserFollowList(String follow_user_id, String follow_user_name, String user_image, String is_verified) {
        this.follow_user_id = follow_user_id;
        this.follow_user_name = follow_user_name;
        this.user_image = user_image;
        this.is_verified = is_verified;
    }

    public String getFollow_user_id() {
        return follow_user_id;
    }

    public void setFollow_user_id(String follow_user_id) {
        this.follow_user_id = follow_user_id;
    }

    public String getFollow_user_name() {
        return follow_user_name;
    }

    public void setFollow_user_name(String follow_user_name) {
        this.follow_user_name = follow_user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }
}
