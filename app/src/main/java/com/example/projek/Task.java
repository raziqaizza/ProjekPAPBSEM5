package com.example.projek;

public class Task {
    String title, uid, desc;

    public Task(String title, String uid, String desc) {
        this.title = title;
        this.uid = uid;
        this.desc = desc;
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
