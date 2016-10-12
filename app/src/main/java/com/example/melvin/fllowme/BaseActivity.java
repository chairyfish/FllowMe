package com.example.melvin.fllowme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.utils.ToastUtils;

/**
 * Created by Melvin on 2016/8/11.
 */
public abstract class BaseActivity extends Activity {

    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
    }

    protected void Intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    protected void showToast(String string) {
        ToastUtils.showToast(string);
    }
}
