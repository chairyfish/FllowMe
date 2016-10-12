package com.example.melvin.fllowme.fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by Melvin on 2016/8/11.
 */
public class FragmentController {
    private static FragmentController fragmentController;
    private int containerId;
    private android.support.v4.app.FragmentManager fragmentManager;
    private ArrayList<android.support.v4.app.Fragment> fragments;

    private FragmentController(FragmentActivity fragmentActivity, int containerId) {
        this.containerId = containerId;
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        init();
    }

    public static FragmentController getFragmentController(FragmentActivity fragmentActivity, int containerId) {
        if (fragmentController == null) {
            fragmentController = new FragmentController(fragmentActivity, containerId);
        }
        return fragmentController;
    }

    public static void onDestroy() {
        fragmentController = null;
    }

    private void init() {
        fragments = new ArrayList<android.support.v4.app.Fragment>();
        fragments.add(new MomentsFragment());
        fragments.add(new GuideFragment());
        fragments.add(new ContactFragment());
        fragments.add(new MeFragment());

        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (android.support.v4.app.Fragment fragment : fragments) {
            ft.add(containerId, fragment);
        }
        System.err.println("------------------------This is the first commit");
        ft.commit();
    }

    public void hideFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        for (android.support.v4.app.Fragment fragment : fragments) {
            if (fragment != null)
                ft.hide(fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        android.support.v4.app.Fragment fragment = fragments.get(position);
        ft.show(fragment);
        ft.commit();
    }
}
