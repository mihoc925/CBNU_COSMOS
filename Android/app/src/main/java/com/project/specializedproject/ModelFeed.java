package com.project.specializedproject;

public class ModelFeed {

    private String uid;
    private String fid;
    private String reply;
    private String location;

    private String title;
    private String note;
    private String photo;

    private String contentCount;
    private String contentFollow;
    private String contentCompletion;

    private String contentDistance;
    private String contentTime;

    public ModelFeed(){}
    public ModelFeed(String uid, String fid, String reply, String location, String title, String note, String photo, String contentCount, String contentFollow, String contentCompletion, String contentDistance, String contentTime) {
        this.uid = uid;
        this.fid = fid;
        this.reply = reply;
        this.location = location;
        this.title = title;
        this.note = note;
        this.photo = photo;
        this.contentCount = contentCount;
        this.contentFollow = contentFollow;
        this.contentCompletion = contentCompletion;
        this.contentDistance = contentDistance;
        this.contentTime = contentTime;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getReply() {
        return reply;
    }
    public void setReply(String reply) {
        this.reply = reply;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getContentCount() {
        return contentCount;
    }
    public void setContentCount(String contentCount) {
        this.contentCount = contentCount;
    }
    public String getContentFollow() {
        return contentFollow;
    }
    public void setContentFollow(String contentFollow) {
        this.contentFollow = contentFollow;
    }
    public String getContentCompletion() {
        return contentCompletion;
    }
    public void setContentCompletion(String contentCompletion) {
        this.contentCompletion = contentCompletion;
    }
    public String getContentDistance() {
        return contentDistance;
    }
    public void setContentDistance(String contentDistance) {
        this.contentDistance = contentDistance;
    }
    public String getContentTime() {
        return contentTime;
    }
    public void setContentTime(String contentTime) {
        this.contentTime = contentTime;
    }
}
