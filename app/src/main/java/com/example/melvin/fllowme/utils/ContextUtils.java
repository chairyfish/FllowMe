package com.example.melvin.fllowme.utils;

import android.app.Application;

/**
 * Created by Melvin on 2016/8/17.
 */
public class ContextUtils extends Application {
    private static ContextUtils instance;

    public static ContextUtils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
