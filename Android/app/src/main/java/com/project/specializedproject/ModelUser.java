package com.project.specializedproject;

public class ModelUser {

    private String uid;
    private String email;
    private String nick;
    private String permission;
    private String phone;
    private String profileImg;

    private String level;
    private String point;
    private String uLocation;

    public ModelUser() {}
    public ModelUser(String uid, String email, String nick, String permission, String phone, String profileImg, String level, String point, String uLocation) {
        this.uid = uid;
        this.email = email;
        this.nick = nick;
        this.permission = permission;
        this.phone = phone;
        this.profileImg = profileImg;
        this.level = level;
        this.point = point;
        this.uLocation = uLocation;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    public String getPermission() {
        return permission;
    }
    public void setPermission(String permission) {
        this.permission = permission;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getProfileImg() {
        return profileImg;
    }
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getPoint() {
        return point;
    }
    public void setPoint(String point) {
        this.point = point;
    }
    public String getULocation() {
        return uLocation;
    }
    public void setULocation(String location) {
        this.uLocation = location;
    }
}
