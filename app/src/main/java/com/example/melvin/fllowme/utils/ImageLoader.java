package com.example.melvin.fllowme.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.database.MyDB;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Melvin on 2016/9/2.
 */
public class ImageLoader {
    //    private boolean isDbNull;
    private static LruCache<String, Bitmap> mMemoryCaches;
    //    public String[] mUrls;
    Set<NewsAsyncTask> mTasks = new HashSet<NewsAsyncTask>();
    private ListView listView;
    private List<Users> contacts = new ArrayList<Users>();


    public ImageLoader(ListView listView, List<Users> contacts/*,boolean isDbNull*/) {
        this.listView = listView;
        this.contacts = contacts;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSizes = maxMemory / 5;

        mMemoryCaches = new LruCache<String, Bitmap>(cacheSizes) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
//        this.isDbNull = isDbNull;
    }

    public static void showImageByAsyncTask(ImageView imageView, String url, String username, String nickname) {
        new NewsAsyncTask(imageView, url).execute(url, username, nickname);
    }

    public static Bitmap getBitmapFromUrl(String urlString) {

        Bitmap bitmap;
        InputStream is = null;
        try {
            URL mUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
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

    public static Bitmap getImageThumbnail(String path, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth / width;
        int beHeight = options.outHeight / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void showImage(ImageView imageView, String url) {

        Bitmap bitmap = getBitmapFromLrucache(url);
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static Bitmap getBitmapFromLrucache(String url) {
        return mMemoryCaches.get(url);
    }

    public static void addBitmapToLrucaches(String url, Bitmap bitmap) {
        if (getBitmapFromLrucache(url) == null) {
            mMemoryCaches.put(url, bitmap);
        }
    }

    public void loadImages(int start, int end) {

      /*  ImageView imageView;
        String loadUrl;*/

        for (int i = start; i < end; i++) {
            String loadUrl = contacts.get(i).getHeadPic();
            ImageView imageView = (ImageView) listView
                    .findViewWithTag(loadUrl);
            if (getBitmapFromLrucache(loadUrl) != null) {
                imageView.setImageBitmap(getBitmapFromLrucache(loadUrl));
            } else {
                NewsAsyncTask mNewsAsyncTask = new NewsAsyncTask(imageView, loadUrl);
                mTasks.add(mNewsAsyncTask);
                mNewsAsyncTask.execute(loadUrl, contacts.get(i).getUsername(), contacts.get(i).getNickname());
            }
        }

        /*MyDB myDB = new MyDB(ContextUtils.getInstance());
        SQLiteDatabase dbReader = myDB.getReadableDatabase();
        Cursor cursor;
        if (isDbNull) {
            for (int i = start; i < end; i++){
                Users friend = contacts.get(i);
                cursor = dbReader.query(MyDB.TABLE_CONTACT_NAME,new String[]{MyDB.COLUMN_CONTACT_HP_PATH},MyDB.COLUMN_CONTACT_FRIEND+" = ?",new String[]{friend.getUsername()},null,null,null);
                if(cursor.moveToFirst()){
                    Log.e("acqure","a");
                    loadUrl = cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_CONTACT_HP_PATH));
//                    Bitmap bitmap = getImageThumbnail(loadUrl, 130, 130);
                    Bitmap bitmap = BitmapFactory.decodeFile(loadUrl);
//                    bitmap = toRoundCorner(bitmap, 360);
                    imageView = (ImageView) listView.findViewWithTag(friend.getHeadPic());
                    imageView.setImageBitmap(bitmap);

                }else {
                    loadUrl = friend.getHeadPic();
                    imageView = (ImageView) listView.findViewWithTag(loadUrl);
                    NewsAsyncTask newsAsyncTask = new NewsAsyncTask(imageView, loadUrl);
                    mTasks.add(newsAsyncTask);
                    newsAsyncTask.execute(loadUrl, friend.getUsername(), friend.getNickname(), friend.getUpdatedAt());
                }
                cursor.close();
            }
        } else {
            for (int i = start; i < end; i++) {
                if(mUrls[i] != null) {
                    loadUrl = mUrls[i];
                    Bitmap bitmap = getImageThumbnail(loadUrl, 130, 130);
                    bitmap = toRoundCorner(bitmap, 360);
                    imageView = (ImageView) listView.findViewWithTag(loadUrl);
                    imageView.setImageBitmap(bitmap);
                }else {
                    Users friend = contacts.get(i);
                    loadUrl = friend.getHeadPic();
                    imageView = (ImageView) listView.findViewWithTag(loadUrl);
                    NewsAsyncTask newsAsyncTask = new NewsAsyncTask(imageView, loadUrl);
                    mTasks.add(newsAsyncTask);
                    newsAsyncTask.execute(loadUrl, friend.getUsername(), friend.getNickname(), friend.getUpdatedAt());
                }
            *//*if (getBitmapFromLrucache(loadUrl) != null) {
                ImageView imageView = (ImageView) mListView
                        .findViewWithTag(loadUrl);

                imageView.setImageBitmap(getBitmapFromLrucache(loadUrl));
            } else {
                NewsAsyncTask mNewsAsyncTask = new NewsAsyncTask(loadUrl);
                mTasks.add(mNewsAsyncTask);
                mNewsAsyncTask.execute(loadUrl);
            }*//*
            }
        }*/
    }

    public void cancelAllAsyncTask() {
        if (mTasks != null) {
            for (NewsAsyncTask newsAsyncTask : mTasks) {
                newsAsyncTask.cancel(false);
            }
        }
    }

    static class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView myImageView;

        public NewsAsyncTask(ImageView imageView, String url) {
            myImageView = imageView;
        }

        //String...params是可变参数接受execute中传过来的参数
        @Override
        protected Bitmap doInBackground(String... params) {

            //String url=params[0];
            //这里同样调用我们的getBitmapFromeUrl
            Bitmap bitmap = getBitmapFromUrl(params[0]);

            //下载完成之后将其加入到LruCache中这样下次加载的时候，就可以直接从LruCache中直接读取
            if (bitmap != null) {
                addBitmapToLrucaches(params[0], bitmap);
            }

            /*String path = Environment.getExternalStorageDirectory() + "/followme/";
            File p = new File(path);
            if (!p.exists()) {
                p.mkdirs();
            }
            Log.e("path", path);
            File myCaptureFile = new File(path, System.currentTimeMillis() + ".jpg");
            path = myCaptureFile.getAbsolutePath();
            Log.e("path", path);
            if (!myCaptureFile.exists()) {
                try {
                    myCaptureFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 1, bos);
                bos.flush();
                bos.close();*/
            MyDB myDB = new MyDB(ContextUtils.getInstance());
            SQLiteDatabase dbWriter = myDB.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(MyDB.COLUMN_CONTACT_FRIEND, params[1]);
            cv.put(MyDB.COLUMN_CONTACT_REMARK, params[2]);
            dbWriter.insert(MyDB.TABLE_CONTACT_NAME, null, cv);
            Log.e("insert", "success");


          /*  } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
//            bitmap = getImageThumbnail(path, 130, 130);
//            bitmap = toRoundCorner(bitmap, 360);
            Log.e("return", "bitmap");
            return bitmap;
        }

        //这里的bitmap是从doInBackgroud中方法中返回过来的
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Log.e("show", "show");
            if (myImageView != null)
                myImageView.setImageBitmap(bitmap);
        }


    }

}
