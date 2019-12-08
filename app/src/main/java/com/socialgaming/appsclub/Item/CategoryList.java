package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class CategoryList implements Serializable {

    private String cid, category_name, category_image, category_image_thumb,cat_total_video;

    public CategoryList(String cid, String category_name, String category_image, String category_image_thumb, String cat_total_video) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.category_image_thumb = category_image_thumb;
        this.cat_total_video = cat_total_video;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }

    public void setCategory_image_thumb(String category_image_thumb) {
        this.category_image_thumb = category_image_thumb;
    }

    public String getCat_total_video() {
        return cat_total_video;
    }

    public void setCat_total_video(String cat_total_video) {
        this.cat_total_video = cat_total_video;
    }
}
