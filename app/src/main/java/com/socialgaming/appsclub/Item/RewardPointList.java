package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class RewardPointList implements Serializable {

    private String video_id, video_title, video_thumbnail, user_id, activity_type, points, date,time;

    public RewardPointList(String video_id, String video_title, String video_thumbnail, String user_id, String activity_type, String points, String date, String time) {
        this.video_id = video_id;
        this.video_title = video_title;
        this.video_thumbnail = video_thumbnail;
        this.user_id = user_id;
        this.activity_type = activity_type;
        this.points = points;
        this.date = date;
        this.time = time;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
