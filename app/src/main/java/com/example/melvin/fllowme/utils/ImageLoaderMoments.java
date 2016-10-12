package com.example.melvin.fllowme.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Melvin on 2016/9/8.
 */
public class ImageLoaderMoments {


    public String mUrls[];
    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mMemoryCaches;
    private Set<RecordsBeanAsyncTask> mTasks;
    private ListView mListView;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }

        ;
    };

    public ImageLoaderMoments(ListView listView) {

        this.mListView = listView;

        mTasks = new HashSet<RecordsBeanAsyncTask>();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSizes = maxMemory / 5;

        mMemoryCaches = new LruCache<String, Bitmap>(cacheSizes) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    /////////////////////////////////////////比例缩放
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

    public Bitmap getBitmapFromLrucache(String url) {

        return mMemoryCaches.get(url);
    }

    public void addBitmapToLrucaches(String url, Bitmap bitmap) {

        if (getBitmapFromLrucache(url) == null) {
            mMemoryCaches.put(url, bitmap);
        }

    }

    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            String loadUrl = mUrls[i];
            if (getBitmapFromLrucache(loadUrl) != null) {
                ImageView imageView = (ImageView) mListView
                        .findViewWithTag(loadUrl);

                imageView.setImageBitmap(getBitmapFromLrucache(loadUrl));
            } else {

                RecordsBeanAsyncTask mRecordsBeanAsyncTask = new RecordsBeanAsyncTask(loadUrl);
                mTasks.add(mRecordsBeanAsyncTask);
                mRecordsBeanAsyncTask.execute(loadUrl);
            }
        }
    }

    public void showImage(ImageView imageView, String url) {

        Bitmap bitmap = getBitmapFromLrucache(url);
        if (bitmap == null) {

        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void cancelAllAsyncTask() {
        if (mTasks != null) {
            for (RecordsBeanAsyncTask recordsBeanAsyncTask : mTasks) {
                recordsBeanAsyncTask.cancel(false);
            }
        }

    }

    // 1.多线程的方法
    public void showImageByThead(ImageView iv, final String url) {
        mImageView = iv;
        mUrl = url;
        new Thread() {
            public void run() {
                Bitmap bitmap = getBitmapFromUrl(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
                Log.i("Handler", "send message");
            }

            ;
        }.start();
    }

    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        Log.i("downLoad", "start" + urlString);

        try {
            URL mUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl
                    .openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            Log.i("downLoad", "finish");
            Bitmap bitmapzoom = zoomBitmap(bitmap);
            return bitmapzoom;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageByAsyncTask(String url) {
        // 首先去LruCache中去找图片
        Bitmap bitmap = getBitmapFromLrucache(url);
        // 如果不为空，说明LruCache中已经缓存了该图片，则读取缓存直接显示，
        if (bitmap != null) {
            ImageView imageView = (ImageView) mListView.findViewWithTag(url);
            imageView.setImageBitmap(bitmap);
        } else {
            // 如果缓存中没有的话就开启异步任务去下载图片，
            new RecordsBeanAsyncTask(url).execute(url);
        }
    }

    class RecordsBeanAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;

        public RecordsBeanAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String url = params[0];
            Bitmap bitmap;

            bitmap = getBitmapFromUrl(url);
            // 下载完成之后将其加入到LruCache中这样下次加载的时候，就可以直接从LruCache中直接读取
            if (bitmap != null) {
                addBitmapToLrucaches(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // if (myImageView.getTag().equals(mUrl)) {
            // myImageView.setImageBitmap(bitmap);
            // }

            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);

            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
                Log.i("bitmap", "set");
            }

            mTasks.remove(this);

        }

    }

}
