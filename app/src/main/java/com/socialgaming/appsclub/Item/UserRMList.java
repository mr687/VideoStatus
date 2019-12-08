package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class UserRMList implements Serializable {

    private String redeem_id,user_points,redeem_price,request_date,status;

    public UserRMList(String redeem_id, String user_points, String redeem_price, String request_date, String status) {
        this.redeem_id = redeem_id;
        this.user_points = user_points;
        this.redeem_price = redeem_price;
        this.request_date = request_date;
        this.status = status;
    }

    public String getRedeem_id() {
        return redeem_id;
    }

    public void setRedeem_id(String redeem_id) {
        this.redeem_id = redeem_id;
    }

    public String getUser_points() {
        return user_points;
    }

    public void setUser_points(String user_points) {
        this.user_points = user_points;
    }

    public String getRedeem_price() {
        return redeem_price;
    }

    public void setRedeem_price(String redeem_price) {
        this.redeem_price = redeem_price;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
