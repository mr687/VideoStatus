package com.socialgaming.appsclub.Item;

import java.io.Serializable;
import java.util.List;

public class SubCategoryList implements Serializable {

    private String adView, id, cid, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name;
    private String already_like, user_id, user_name, user_image, already_follow,is_verified,total_comment,watermark_image,watermark_on_off;
    private List<SubCategoryList> relatedList;
    private List<CommentList> commentLists;

    public SubCategoryList() {
    }

    //download
    public SubCategoryList(String id, String cid, String video_title, String video_url, String video_thumbnail_b, String video_thumbnail_s, String category_name, String video_layout) {
        this.id = id;
        this.cid = cid;
        this.video_title = video_title;
        this.video_url = video_url;
        this.video_thumbnail_b = video_thumbnail_b;
        this.video_thumbnail_s = video_thumbnail_s;
        this.category_name = category_name;
        this.video_layout = video_layout;
    }

    //All other uses
    public SubCategoryList(String adView, String id, String cid, String video_title, String video_url, String video_layout, String video_thumbnail_b, String video_thumbnail_s, String total_viewer, String total_likes, String category_name, String already_like) {
        this.adView = adView;
        this.id = id;
        this.cid = cid;
        this.video_title = video_title;
        this.video_url = video_url;
        this.video_layout = video_layout;
        this.video_thumbnail_b = video_thumbnail_b;
        this.video_thumbnail_s = video_thumbnail_s;
        this.total_viewer = total_viewer;
        this.total_likes = total_likes;
        this.category_name = category_name;
        this.already_like = already_like;
    }

    //Sub UserActivity Detail
    public SubCategoryList(String adView, String id, String cid, String video_title, String video_url, String video_layout, String video_thumbnail_b, String video_thumbnail_s, String total_viewer, String total_likes, String category_name, String already_like, String user_id, String user_name, String user_image, String already_follow, String is_verified, String total_comment, String watermark_image, String watermark_on_off, List<SubCategoryList> relatedList, List<CommentList> commentLists) {
        this.adView = adView;
        this.id = id;
        this.cid = cid;
        this.video_title = video_title;
        this.video_url = video_url;
        this.video_layout = video_layout;
        this.video_thumbnail_b = video_thumbnail_b;
        this.video_thumbnail_s = video_thumbnail_s;
        this.total_viewer = total_viewer;
        this.total_likes = total_likes;
        this.category_name = category_name;
        this.already_like = already_like;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_image = user_image;
        this.already_follow = already_follow;
        this.is_verified = is_verified;
        this.total_comment = total_comment;
        this.watermark_image = watermark_image;
        this.watermark_on_off = watermark_on_off;
        this.relatedList = relatedList;
        this.commentLists = commentLists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_layout() {
        return video_layout;
    }

    public void setVideo_layout(String video_layout) {
        this.video_layout = video_layout;
    }

    public String getVideo_thumbnail_b() {
        return video_thumbnail_b;
    }

    public void setVideo_thumbnail_b(String video_thumbnail_b) {
        this.video_thumbnail_b = video_thumbnail_b;
    }

    public String getVideo_thumbnail_s() {
        return video_thumbnail_s;
    }

    public void setVideo_thumbnail_s(String video_thumbnail_s) {
        this.video_thumbnail_s = video_thumbnail_s;
    }

    public String getTotal_viewer() {
        return total_viewer;
    }

    public void setTotal_viewer(String total_viewer) {
        this.total_viewer = total_viewer;
    }

    public String getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(String total_likes) {
        this.total_likes = total_likes;
    }

    public String getAlready_like() {
        return already_like;
    }

    public void setAlready_like(String already_like) {
        this.already_like = already_like;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<SubCategoryList> getRelatedList() {
        return relatedList;
    }

    public void setRelatedList(List<SubCategoryList> relatedList) {
        this.relatedList = relatedList;
    }

    public List<CommentList> getCommentLists() {
        return commentLists;
    }

    public void setCommentLists(List<CommentList> commentLists) {
        this.commentLists = commentLists;
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

    public String getAlready_follow() {
        return already_follow;
    }

    public void setAlready_follow(String already_follow) {
        this.already_follow = already_follow;
    }

    public String getAdView() {
        return adView;
    }

    public void setAdView(String adView) {
        this.adView = adView;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }

    public String getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(String total_comment) {
        this.total_comment = total_comment;
    }

    public String getWatermark_image() {
        return watermark_image;
    }

    public void setWatermark_image(String watermark_image) {
        this.watermark_image = watermark_image;
    }

    public String getWatermark_on_off() {
        return watermark_on_off;
    }

    public void setWatermark_on_off(String watermark_on_off) {
        this.watermark_on_off = watermark_on_off;
    }
}
