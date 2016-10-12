package com.example.melvin.fllowme.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by chairyfish on 2016/8/24.
 */
public class Records extends BmobObject {

    private String author;
    private BmobPointer host;
    private String coverURL;
    private String title;

    public BmobPointer getHost() {
        return host;
    }

    public void setHost(BmobPointer host) {
        this.host = host;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /************************
     * city
     *****************************/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
/*
    public BmobRelation getItem(){return  item;}
    public void setItem(BmobRelation item){this.item=item;}
*/
}
