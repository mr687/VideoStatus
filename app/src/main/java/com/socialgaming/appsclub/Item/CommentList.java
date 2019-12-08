package com.socialgaming.appsclub.Item;

import java.io.Serializable;

/**
 * Created by admin on 19-03-2018.
 */

public class CommentList implements Serializable {

    private String comment_id,user_id,user_name,user_image,video_id,comment_text,comment_date;

    public CommentList(String comment_id, String user_id, String user_name, String user_image, String video_id, String comment_text, String comment_date) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_image = user_image;
        this.video_id = video_id;
        this.comment_text = comment_text;
        this.comment_date = comment_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }
}
