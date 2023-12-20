package com.example.projek;

public class Task {
    String title, uid, desc, imageURL, imageUID;

    public Task(String title, String uid, String desc, String imageURL, String imageUID) {
        this.title = title;
        this.uid = uid;
        this.desc = desc;
        this.imageURL = imageURL;
        this.imageUID = imageUID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageUID() {
        return imageUID;
    }

    public void setImageUID(String imageUID) {
        this.imageUID = imageUID;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Task() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
