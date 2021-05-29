package com.project.specializedproject;

import android.app.Application;

public class ModelFeed extends Application {

    private String fid;
    private String uid;
    private String c_date;
    private String u_date;
    private String feedW_distance;
    private String feedW_time;
    private String feedW_count;

    private String feedW_title0;
    private String feedW_note0;
    private String feedW_photo0;
    private String feedW_location0;

    private String feedW_title;
    private String feedW_note;
    private String feedW_photo;
    private String feedW_location;

    public ModelFeed(){}
    public ModelFeed(String fid, String uid, String c_date, String u_date, String feedW_distance, String feedW_time, String feedW_count, String feedW_title0, String feedW_note0, String feedW_photo0, String feedW_location0, String feedW_title, String feedW_note, String feedW_photo, String feedW_location) {
        this.fid = fid;
        this.uid = uid;
        this.c_date = c_date;
        this.u_date = u_date;
        this.feedW_distance = feedW_distance;
        this.feedW_time = feedW_time;
        this.feedW_count = feedW_count;
        this.feedW_title0 = feedW_title0;
        this.feedW_note0 = feedW_note0;
        this.feedW_photo0 = feedW_photo0;
        this.feedW_location0 = feedW_location0;
        this.feedW_title = feedW_title;
        this.feedW_note = feedW_note;
        this.feedW_photo = feedW_photo;
        this.feedW_location = feedW_location;
    }

    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getC_date() {
        return c_date;
    }
    public void setC_date(String c_date) {
        this.c_date = c_date;
    }
    public String getU_date() {
        return u_date;
    }
    public void setU_date(String u_date) {
        this.u_date = u_date;
    }
    public String getFeedW_distance() {
        return feedW_distance;
    }
    public void setFeedW_distance(String feedW_distance) {
        this.feedW_distance = feedW_distance;
    }
    public String getFeedW_time() {
        return feedW_time;
    }
    public void setFeedW_time(String feedW_time) {
        this.feedW_time = feedW_time;
    }
    public String getFeedW_count() {
        return feedW_count;
    }
    public void setFeedW_count(String feedW_count) {
        this.feedW_count = feedW_count;
    }
    public String getFeedW_title0() {
        return feedW_title0;
    }
    public void setFeedW_title0(String feedW_title0) {
        this.feedW_title0 = feedW_title0;
    }
    public String getFeedW_note0() {
        return feedW_note0;
    }
    public void setFeedW_note0(String feedW_note0) {
        this.feedW_note0 = feedW_note0;
    }
    public String getFeedW_photo0() {
        return feedW_photo0;
    }
    public void setFeedW_photo0(String feedW_photo0) {
        this.feedW_photo0 = feedW_photo0;
    }
    public String getFeedW_location0() {
        return feedW_location0;
    }
    public void setFeedW_location0(String feedW_location0) {
        this.feedW_location0 = feedW_location0;
    }
    public String getFeedW_title() {
        return feedW_title;
    }
    public void setFeedW_title(String feedW_title) {
        this.feedW_title = feedW_title;
    }
    public String getFeedW_note() {
        return feedW_note;
    }
    public void setFeedW_note(String feedW_note) {
        this.feedW_note = feedW_note;
    }
    public String getFeedW_photo() {
        return feedW_photo;
    }
    public void setFeedW_photo(String feedW_photo) {
        this.feedW_photo = feedW_photo;
    }
    public String getFeedW_location() {
        return feedW_location;
    }
    public void setFeedW_location(String feedW_location) {
        this.feedW_location = feedW_location;
    }
}
