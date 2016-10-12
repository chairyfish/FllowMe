package com.example.melvin.fllowme.utils;

import android.widget.Toast;

/**
 * Created by Melvin on 2016/8/11.
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtils.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
