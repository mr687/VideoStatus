package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class EarnPointList implements Serializable {

    private String title,point;

    public EarnPointList(String title, String point) {
        this.title = title;
        this.point = point;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
