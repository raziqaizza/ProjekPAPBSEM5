package com.example.projek;

public class Tugas {
    String title, uid, desc;

    public Tugas(String title, String uid, String desc) {
        this.title = title;
        this.uid = uid;
        this.desc = desc;
    }

    public Tugas() {
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
