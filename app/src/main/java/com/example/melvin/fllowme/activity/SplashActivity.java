package com.example.melvin.fllowme.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.melvin.fllowme.BaseActivity;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

/**
 * Created by Melvin on 2016/8/11.
 */
public class SplashActivity extends BaseActivity {

    private static final int WHAT_INTENT2MAIN = 1;
    private static final int WHAT_INTENT2LOGIN = 2;
    private static final long SPLASH_DUR_TIME = 1000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_INTENT2LOGIN:
                    Intent2Activity(LoginActivity.class);
                    finish();
                    break;
                case WHAT_INTENT2MAIN:
                    Intent2Activity(MainActivity.class);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bmob.initialize(this, CommonConstants.BMOB_APPLICATION_ID);
        BmobInstallation.getCurrentInstallation().save();
        BmobPush.startWork(this);


        Users user = BmobUser.getCurrentUser(Users.class);
        if (user != null) {
            handler.sendEmptyMessageDelayed(WHAT_INTENT2MAIN, SPLASH_DUR_TIME);
        } else {
            handler.sendEmptyMessageDelayed(WHAT_INTENT2LOGIN, SPLASH_DUR_TIME);
        }
    }
}
