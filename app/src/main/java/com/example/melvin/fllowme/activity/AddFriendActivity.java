package com.example.melvin.fllowme.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.fragment.AddFriendFragment;


/**
 * Created by Melvin on 2016/8/21.
 */
public class AddFriendActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
//        getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.container,new AddFriendFragment()).commit();

        getFragmentManager().beginTransaction()
                .add(R.id.container, new AddFriendFragment()).commit();
    }
}
