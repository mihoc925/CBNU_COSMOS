package com.project.specializedproject;

public class ModelList {

    private String uid;
    private String email;
    private String nick;
    private String permission;
    private String phone;
    private String profileImg;

    public ModelList() {}
    public ModelList(String uid, String email, String nick, String permission, String phone, String profileImg) {
        this.uid = uid;
        this.email = email;
        this.nick = nick;
        this.permission = permission;
        this.phone = phone;
        this.profileImg = profileImg;
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
}
