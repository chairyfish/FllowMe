package com.example.melvin.fllowme.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Melvin on 2016/8/26.
 */
public class ImageUtils {

    public static final int GET_IMAGE_FROM_PHONE = 5001;
    public static final int GET_IMAGE_BY_CAMERA = 5002;

    public static Uri imageUriFromCamera;

    public static void openLocalImage(final Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, GET_IMAGE_FROM_PHONE);
    }

    public static void openCameraImage(final Activity activity) {
        imageUriFromCamera = createImagePathUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
        activity.startActivityForResult(intent, GET_IMAGE_BY_CAMERA);
    }

    public static void openCameraImage(final Fragment fragment) {
        imageUriFromCamera = createImagePathUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
        fragment.startActivityForResult(intent, GET_IMAGE_BY_CAMERA);
    }

    public static Uri createImagePathUri() {
        Uri imageFilePath = null;
     /*   String status = Environment.getExternalStorageState();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName =timeFormatter.format(new Date(time));
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN,time);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        if(status.equals(Environment.MEDIA_MOUNTED)){
            imageFilePath = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        }else {
            imageFilePath = context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,values);
        }*/

        File f = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        currentPath=f.getAbsolutePath();
//        i.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(f));

        Log.e("imageFilePath", Uri.fromFile(f).getPath());
        return Uri.fromFile(f);
    }

    public static void deleteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);
    }


}
