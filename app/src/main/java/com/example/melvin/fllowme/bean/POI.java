package com.example.melvin.fllowme.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by chairyfish on 2016/8/23.
 */
public class POI extends BmobObject {
    private static final long serialVersionUID = 1L;
    private String province;
    private String city;
    private String place;
    private String name;
    private BmobGeoPoint pointP;

    /***********************
     * province
     ***********************************/
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobGeoPoint getPointP() {
        return pointP;
    }

    public void setPointP(BmobGeoPoint pointP) {
        this.pointP = pointP;
    }


}
