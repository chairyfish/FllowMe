package com.example.melvin.fllowme.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by chairyfish on 2016/8/24.
 */
public class PlaceItem extends BmobObject {
    private static final long serialVersionUID = 1L;
    private BmobGeoPoint point;
    private BmobDate time;
    private String text;
    private BmobPointer host;//这是指针，指向Records
    private List<String> URL;

    public PlaceItem() {

    }

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public BmobDate getTime() {
        return time;
    }

    public void setTime(BmobDate time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BmobPointer getHost() {
        return host;
    }

    public void setHost(BmobPointer host) {
        this.host = host;
    }

    public List<String> getURL() {
        return URL;
    }

    public void setURL(List<String> URL) {
        this.URL = URL;
    }

}