package com.example.melvin.fllowme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.melvin.fllowme.R;

/**
 * Created by Melvin on 2016/9/11.
 */
public class DialogImage extends Dialog {
    Context mContext;
    ImageView photo;
    String path;
    Bitmap mbitmap;

    public DialogImage(Context context) {
        super(context);
        mContext = context;
    }

    public DialogImage(Context context, String s) {
        super(context);
        mContext = context;
        path = s;
    }

    public DialogImage(Context context, Bitmap bitmap) {
        super(context);
        mContext = context;
        mbitmap = bitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int w = 1080;
        int h = 464;
        Log.i("scale", String.valueOf(width));
        Log.i("scale", String.valueOf(height));
        float bitScale = (float) w / h;
        float viewScale = (float) width / height;
        Log.i("scale", String.valueOf(bitScale) + "," + String.valueOf(viewScale));
        float scaleWidth, scaleHeight;
        Matrix matrix = new Matrix();
        if (bitScale > viewScale) {
            scaleWidth = ((float) w / width);
            scaleHeight = ((float) h / width * w / h);
        } else {
            scaleWidth = ((float) w / height * h / w);
            scaleHeight = ((float) h / height);
        }
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.dialog_image, null);
        photo = (ImageView) layout.findViewById(R.id.photo);
        /*
        Log.i("path", path);
        try {
            bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                Log.i("dialogbitmap", "null");
            } else {
                photo.setImageBitmap(bitmap);
            }
        }catch (ArithmeticException e){
            e.printStackTrace();
        }
        */
        Bitmap bitmap = zoomBitmap(mbitmap);
        photo.setImageBitmap(bitmap);
        this.setContentView(layout);
        //      mbitmap= BitmapFactory.decodeFile(photoURL);
        //     photo.setImageBitmap(mbitmap);

    }
}