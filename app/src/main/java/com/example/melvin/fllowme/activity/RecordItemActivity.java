package com.example.melvin.fllowme.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.POI;
import com.example.melvin.fllowme.bean.PlaceItem;
import com.example.melvin.fllowme.dialog.DialogImage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class RecordItemActivity extends AppCompatActivity {
    String ID;
    String picUrl1, picUrl2, picUrl3;
    TextView felling, locate, time;
    ImageView img_1, img_2, img_3;
    String URL1 = null, URL2 = null, URL3 = null, text, placename, timeshow;
    Bitmap b1, b2, b3;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    locate.setText(placename);
                    break;
                case 2:
                    img_1.setImageBitmap(b1);
                    break;
                case 3:
                    img_2.setImageBitmap(b2);
                    break;
                case 4:
                    img_3.setImageBitmap(b3);
                    break;
                default:
                    break;

            }
        }
    };

    public static Bitmap zoomBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int w = 200;
        int h = 200;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_item);


        initview();
        ID = getIntent().getStringExtra("placeitemid");

        Log.i("RecordItem_id", ID);

        BmobQuery<PlaceItem> query = new BmobQuery<PlaceItem>();
        query.getObject(ID, new QueryListener<PlaceItem>() {
            @Override
            public void done(PlaceItem placeItem, BmobException e) {
                if (e == null) {
                    List<String> listURL = new ArrayList<String>();
                    listURL = placeItem.getURL();
                    int i = listURL.size();
                    for (int k = 0; k < i; k++) {
                        if (k == 0) {
                            URL1 = listURL.get(k);
                        } else if (k == 1) {
                            URL2 = listURL.get(k);
                        } else if (k == 2) {
                            URL3 = listURL.get(k);
                        }
                    }
                    Log.i("URL", String.valueOf(i) + URL1 + URL2 + URL3);
                    /*
                    if(URL1==null){Log.i("URL1","null");}else{getBitmapfromBomb1(URL1);}
                    if(URL2==null){Log.i("URL2","null");}else{getBitmapfromBomb2(URL2);}
                    if(URL3==null){Log.i("URL3","null");}else{getBitmapfromBomb3(URL3);}
*/

                    if (URL1 == null) {
                        Log.i("URL1", "null");
                    } else {
                        DownloadPic1 d1 = new DownloadPic1();
                        d1.start();
                    }
                    if (URL2 == null) {
                        Log.i("URL2", "null");
                    } else {
                        DownloadPic2 d2 = new DownloadPic2();
                        d2.start();
                    }
                    if (URL3 == null) {
                        Log.i("URL3", "null");
                    } else {
                        DownloadPic3 d3 = new DownloadPic3();
                        d3.start();
                    }

                    timeshow = placeItem.getTime().getDate();
                    time.setText(timeshow);
                    text = placeItem.getText();
                    felling.setText(text);
                    BmobGeoPoint point = placeItem.getPoint();
                    queryPOI(point);

                }
            }
        });
    }

    private void initview() {
        felling = (TextView) findViewById(R.id.felling);
        locate = (TextView) findViewById(R.id.place);
        time = (TextView) findViewById(R.id.time);
        img_1 = (ImageView) findViewById(R.id.img_1);
        img_2 = (ImageView) findViewById(R.id.img_2);
        img_3 = (ImageView) findViewById(R.id.img_3);
        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogImage dialogImage = new DialogImage(RecordItemActivity.this, b1);
                dialogImage.show();
            }
        });
        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogImage dialogImage = new DialogImage(RecordItemActivity.this, b2);
                dialogImage.show();
            }
        });
        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogImage dialogImage = new DialogImage(RecordItemActivity.this, b3);
                dialogImage.show();
            }
        });
    }

    public void queryPOI(BmobGeoPoint point) {

        BmobQuery<POI> poiBmobQuery = new BmobQuery<POI>();

        poiBmobQuery.addWhereNear("pointP", point);
        poiBmobQuery.setLimit(1);
        poiBmobQuery.findObjects(new FindListener<POI>() {
            @Override
            public void done(List<POI> list, BmobException e) {
                if (e == null) {
                    String province = null, city = null, place = null, name = null;
                    if (list != null && list.size() > 0) {
                        for (POI poi : list) {
                            province = poi.getProvince();
                            city = poi.getCity();
                            place = poi.getPlace();
                            name = poi.getName();
                        }
                        placename = province + city + place + name;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        Log.i("address", province + city + place + name);
                    } else {
                        Toast toast = Toast.makeText(RecordItemActivity.this, " 空", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(RecordItemActivity.this, "POI查询失败", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.i("POI", e.getMessage() + e.getErrorCode());
                }
            }
        });
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

    //*****************************图片异步***************************************/
    public void getBitmapfromBomb1(String url) {
        BmobFile file = new BmobFile("a.jpg", "", url);
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Log.i("开始下载", "pic1");
            }

            @Override
            public void onProgress(Integer integer, long l) {
                Log.i("下载", "下载进度：" + integer + "," + l);
            }

            @Override
            public void done(String savepath, BmobException e) {
                if (e == null) {
                    Log.i("下载成功", savepath);
                    File file = new File(savepath);
                    if (file.exists()) {
                        b1 = BitmapFactory.decodeFile(savepath);
                        picUrl1 = savepath;
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);

                    }
                } else {
                    Log.i("pic1", "下载失败");
                }
            }
        });
    }

    public void getBitmapfromBomb2(String url) {

        BmobFile file = new BmobFile("b.jpg", "", url);
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Log.i("开始下载", "pic2");
            }

            @Override
            public void onProgress(Integer integer, long l) {
                Log.i("下载", "下载进度：" + integer + "," + l);
            }

            @Override
            public void done(String savepath, BmobException e) {
                if (e == null) {
                    Log.i("下载成功", savepath);
                    File file = new File(savepath);
                    if (file.exists()) {
                        b2 = BitmapFactory.decodeFile(savepath);
                        picUrl2 = savepath;
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);

                    }
                } else {
                    Log.i("pic2", "下载失败");
                }
            }
        });
    }

    public void getBitmapfromBomb3(String url) {

        BmobFile file = new BmobFile("c.jpg", "", url);
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Log.i("开始下载", "pic3");
            }

            @Override
            public void onProgress(Integer integer, long l) {
                Log.i("下载", "下载进度：" + integer + "," + l);
            }

            @Override
            public void done(String savepath, BmobException e) {
                if (e == null) {
                    Log.i("下载成功", savepath);
                    File file = new File(savepath);
                    if (file.exists()) {
                        b3 = BitmapFactory.decodeFile(savepath);
                        picUrl3 = savepath;
                        Message message = new Message();
                        message.what = 4;
                        handler.sendMessage(message);

                    }
                } else {
                    Log.i("pic3", "下载失败");
                }
            }
        });
    }

    public class DownloadPic1 extends Thread {
        public void run() {
            b1 = getBitmapFromUrl(URL1);
            if (b1 == null) {
                Log.i("imagedwnLoad", "异步出错");
            } else {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }

    public class DownloadPic2 extends Thread {
        public void run() {
            b2 = getBitmapFromUrl(URL2);
            if (b2 == null) {
                Log.i("imagedwnLoad", "异步出错");
            } else {
                Message message = new Message();
                message.what = 3;
                handler.sendMessage(message);
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////////

    public class DownloadPic3 extends Thread {
        public void run() {
            b3 = getBitmapFromUrl(URL3);
            if (b3 == null) {
                Log.i("imagedwnLoad", "异步出错");
            } else {
                Message message = new Message();
                message.what = 4;
                handler.sendMessage(message);
            }
        }
    }

}
