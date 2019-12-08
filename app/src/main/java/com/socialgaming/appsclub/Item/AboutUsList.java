package com.socialgaming.appsclub.Item;

import java.io.Serializable;

public class AboutUsList implements Serializable {

    private String app_name,app_logo,app_version,app_author,app_contact,app_email,app_website,app_description,app_developed_by,app_faq,app_privacy_policy,publisher_id,interstital_ad_id,interstital_ad_click,banner_ad_id,rewarded_video_ads_id,rewarded_video_click,redeem_currency,redeem_points,redeem_money,minimum_redeem_points,payment_method1,payment_method2,payment_method3,payment_method4,spinner_opt;
    private boolean interstital_ad=false;
    private boolean banner_ad=false;
    private boolean rewarded_video_ads=false;
    private boolean video_views_status_ad=false;
    private boolean video_add_status_ad=false;
    private boolean like_video_points_status_ad=false;
    private boolean download_video_points_status_ad=false;

    public AboutUsList(String app_name, String app_logo, String app_version, String app_author, String app_contact, String app_email, String app_website, String app_description, String app_developed_by, String app_faq, String app_privacy_policy, String publisher_id, String interstital_ad_id, String interstital_ad_click, String banner_ad_id, String rewarded_video_ads_id, String rewarded_video_click, String redeem_currency, String redeem_points, String redeem_money, String minimum_redeem_points, String payment_method1, String payment_method2, String payment_method3, String payment_method4, String spinner_opt, boolean interstital_ad, boolean banner_ad, boolean rewarded_video_ads, boolean video_views_status_ad, boolean video_add_status_ad, boolean like_video_points_status_ad, boolean download_video_points_status_ad) {
        this.app_name = app_name;
        this.app_logo = app_logo;
        this.app_version = app_version;
        this.app_author = app_author;
        this.app_contact = app_contact;
        this.app_email = app_email;
        this.app_website = app_website;
        this.app_description = app_description;
        this.app_developed_by = app_developed_by;
        this.app_faq = app_faq;
        this.app_privacy_policy = app_privacy_policy;
        this.publisher_id = publisher_id;
        this.interstital_ad_id = interstital_ad_id;
        this.interstital_ad_click = interstital_ad_click;
        this.banner_ad_id = banner_ad_id;
        this.rewarded_video_ads_id = rewarded_video_ads_id;
        this.rewarded_video_click = rewarded_video_click;
        this.redeem_currency = redeem_currency;
        this.redeem_points = redeem_points;
        this.redeem_money = redeem_money;
        this.minimum_redeem_points = minimum_redeem_points;
        this.payment_method1 = payment_method1;
        this.payment_method2 = payment_method2;
        this.payment_method3 = payment_method3;
        this.payment_method4 = payment_method4;
        this.spinner_opt = spinner_opt;
        this.interstital_ad = interstital_ad;
        this.banner_ad = banner_ad;
        this.rewarded_video_ads = rewarded_video_ads;
        this.video_views_status_ad = video_views_status_ad;
        this.video_add_status_ad = video_add_status_ad;
        this.like_video_points_status_ad = like_video_points_status_ad;
        this.download_video_points_status_ad = download_video_points_status_ad;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_author() {
        return app_author;
    }

    public void setApp_author(String app_author) {
        this.app_author = app_author;
    }

    public String getApp_contact() {
        return app_contact;
    }

    public void setApp_contact(String app_contact) {
        this.app_contact = app_contact;
    }

    public String getApp_email() {
        return app_email;
    }

    public void setApp_email(String app_email) {
        this.app_email = app_email;
    }

    public String getApp_website() {
        return app_website;
    }

    public void setApp_website(String app_website) {
        this.app_website = app_website;
    }

    public String getApp_description() {
        return app_description;
    }

    public void setApp_description(String app_description) {
        this.app_description = app_description;
    }

    public String getApp_developed_by() {
        return app_developed_by;
    }

    public void setApp_developed_by(String app_developed_by) {
        this.app_developed_by = app_developed_by;
    }

    public String getApp_faq() {
        return app_faq;
    }

    public void setApp_faq(String app_faq) {
        this.app_faq = app_faq;
    }

    public String getApp_privacy_policy() {
        return app_privacy_policy;
    }

    public void setApp_privacy_policy(String app_privacy_policy) {
        this.app_privacy_policy = app_privacy_policy;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getInterstital_ad_id() {
        return interstital_ad_id;
    }

    public void setInterstital_ad_id(String interstital_ad_id) {
        this.interstital_ad_id = interstital_ad_id;
    }

    public String getInterstital_ad_click() {
        return interstital_ad_click;
    }

    public void setInterstital_ad_click(String interstital_ad_click) {
        this.interstital_ad_click = interstital_ad_click;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public void setBanner_ad_id(String banner_ad_id) {
        this.banner_ad_id = banner_ad_id;
    }

    public String getRewarded_video_ads_id() {
        return rewarded_video_ads_id;
    }

    public void setRewarded_video_ads_id(String rewarded_video_ads_id) {
        this.rewarded_video_ads_id = rewarded_video_ads_id;
    }

    public String getRewarded_video_click() {
        return rewarded_video_click;
    }

    public void setRewarded_video_click(String rewarded_video_click) {
        this.rewarded_video_click = rewarded_video_click;
    }

    public String getRedeem_currency() {
        return redeem_currency;
    }

    public void setRedeem_currency(String redeem_currency) {
        this.redeem_currency = redeem_currency;
    }

    public String getRedeem_points() {
        return redeem_points;
    }

    public void setRedeem_points(String redeem_points) {
        this.redeem_points = redeem_points;
    }

    public String getRedeem_money() {
        return redeem_money;
    }

    public void setRedeem_money(String redeem_money) {
        this.redeem_money = redeem_money;
    }

    public String getMinimum_redeem_points() {
        return minimum_redeem_points;
    }

    public void setMinimum_redeem_points(String minimum_redeem_points) {
        this.minimum_redeem_points = minimum_redeem_points;
    }

    public String getPayment_method1() {
        return payment_method1;
    }

    public void setPayment_method1(String payment_method1) {
        this.payment_method1 = payment_method1;
    }

    public String getPayment_method2() {
        return payment_method2;
    }

    public void setPayment_method2(String payment_method2) {
        this.payment_method2 = payment_method2;
    }

    public String getPayment_method3() {
        return payment_method3;
    }

    public void setPayment_method3(String payment_method3) {
        this.payment_method3 = payment_method3;
    }

    public String getPayment_method4() {
        return payment_method4;
    }

    public void setPayment_method4(String payment_method4) {
        this.payment_method4 = payment_method4;
    }

    public String getSpinner_opt() {
        return spinner_opt;
    }

    public void setSpinner_opt(String spinner_opt) {
        this.spinner_opt = spinner_opt;
    }

    public boolean isInterstital_ad() {
        return interstital_ad;
    }

    public void setInterstital_ad(boolean interstital_ad) {
        this.interstital_ad = interstital_ad;
    }

    public boolean isBanner_ad() {
        return banner_ad;
    }

    public void setBanner_ad(boolean banner_ad) {
        this.banner_ad = banner_ad;
    }

    public boolean isRewarded_video_ads() {
        return rewarded_video_ads;
    }

    public void setRewarded_video_ads(boolean rewarded_video_ads) {
        this.rewarded_video_ads = rewarded_video_ads;
    }

    public boolean isVideo_views_status_ad() {
        return video_views_status_ad;
    }

    public void setVideo_views_status_ad(boolean video_views_status_ad) {
        this.video_views_status_ad = video_views_status_ad;
    }

    public boolean isVideo_add_status_ad() {
        return video_add_status_ad;
    }

    public void setVideo_add_status_ad(boolean video_add_status_ad) {
        this.video_add_status_ad = video_add_status_ad;
    }

    public boolean isLike_video_points_status_ad() {
        return like_video_points_status_ad;
    }

    public void setLike_video_points_status_ad(boolean like_video_points_status_ad) {
        this.like_video_points_status_ad = like_video_points_status_ad;
    }

    public boolean isDownload_video_points_status_ad() {
        return download_video_points_status_ad;
    }

    public void setDownload_video_points_status_ad(boolean download_video_points_status_ad) {
        this.download_video_points_status_ad = download_video_points_status_ad;
    }
}
