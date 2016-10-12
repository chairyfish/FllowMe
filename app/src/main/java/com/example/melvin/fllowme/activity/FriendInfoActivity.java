package com.example.melvin.fllowme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.fragment.FriendInfoFragment;

/**
 * Created by Melvin on 2016/8/31.
 */
public class FriendInfoActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        FriendInfoFragment friendInfoFragment = new FriendInfoFragment();
        friendInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_friend_info_container, friendInfoFragment).commit();
    }
}
