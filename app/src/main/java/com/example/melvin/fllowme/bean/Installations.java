package com.example.melvin.fllowme.bean;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Melvin on 2016/8/22.
 */
public class Installations extends BmobInstallation {

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
