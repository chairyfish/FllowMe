package com.example.melvin.fllowme.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.POI;
import com.example.melvin.fllowme.bean.PlaceItem;
import com.example.melvin.fllowme.bean.Records;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RecordMapActivity extends AppCompatActivity {

    final SpatialReference mapSR = SpatialReference.create(102100);
    final int FIRST_CLICK = 1;
    final int SECOND_CLICK = 2;
    String RECORDID;//来自youjiquan
    String placeItemId;
    String province, city, place, name;
    String worldMapURL =
            "http://cache1.arcgisonline.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
    Point pointA = new Point();
    Point FirstPoint = new Point();
    String firstURL;
    Bitmap bitmap;
    MapView map = null;
    ImageView imageView;
    int CLICK_STATE = FIRST_CLICK;
    int Graphicexist = 1;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
// 在这里可以进行UI操作
                    if (bitmap == null) {
                        Log.i("handler", "bitmap null");
                    } else {
                        imageView.setImageBitmap(bitmap);
                        Log.i("handler", "bitmap not null");
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_map);


        RECORDID = getIntent().getStringExtra("recordid");


/***********************************addBasicMap*******************************************/

        map = (MapView) findViewById(R.id.recordmap);//获取map实例
        ArcGISDynamicMapServiceLayer dynamicLayer = new ArcGISDynamicMapServiceLayer(worldMapURL);//地图服务
        //Adds layer into the 'MapView'
        map.addLayer(dynamicLayer); //添加地图服务到map中
        final GraphicsLayer graphicsLayer = new GraphicsLayer();
        final SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.rgb(93, 218, 249), 15, SimpleMarkerSymbol.STYLE.CIRCLE);

        final Callout callout = map.getCallout();
        callout.setStyle(R.xml.calloutphotostyle);  //callout的样式
        callout.setOffset(0, -15);
        map.setOnSingleTapListener(new OnSingleTapListener() {
            private static final long servialVersionUID = 1L;

            @Override
            public void onSingleTap(float x, float y) {
                if (Graphicexist == 0) {
                    Toast.makeText(RecordMapActivity.this, "no point exist", Toast.LENGTH_SHORT);
                } else {
                    int[] graphicIDs = graphicsLayer.getGraphicIDs(x, y, 20);
                    if (graphicIDs != null && graphicIDs.length > 0) {
                        Log.i("pointid", String.valueOf(graphicIDs[0]));
                        //加载callout的布局
                        LayoutInflater inflater = LayoutInflater.from(RecordMapActivity.this);
                        View view = inflater.inflate(R.layout.calloutphoto, null);
                        imageView = (ImageView) view.findViewById(R.id.LBSphoto);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra("placeitemid", placeItemId);
                                intent.setClass(RecordMapActivity.this, RecordItemActivity.class);
                                startActivity(intent);
                            }
                        });

                        //                    TextView context = (TextView) view.findViewById(R.id.context);
                        //                     context.setText("XXXXXX");  //获得文本
                        //                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
//                    imageView.setImageBitmap(concept1.getBitmap());//获得图片
                        //                      ImageButton imageButton = (ImageButton) view.findViewById(R.id.goon);
//                    imageButton.setOnClickListener(new View.OnClickListener() {   //图像按钮的点击事件
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    });

                        Graphic gr = graphicsLayer.getGraphic(graphicIDs[0]);
                        Point pt = map.toMapPoint(x, y);    //将屏幕坐标转变成投影坐标
                        Point wgsPoint = (Point) GeometryEngine.project(pt, map.getSpatialReference(), SpatialReference.create(4326));
                        getfirstURL(wgsPoint);
                        switch (CLICK_STATE) {
                            case FIRST_CLICK:    //第一次点击，显示callout
                                callout.show(pt, view);
                                CLICK_STATE = SECOND_CLICK;
                                break;
                            case SECOND_CLICK:  //第二次点击，隐藏callout
                                callout.hide();
                                CLICK_STATE = FIRST_CLICK;
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (CLICK_STATE == SECOND_CLICK) {
                            callout.hide();
                            CLICK_STATE = FIRST_CLICK;
                        }
                    }
                }
            }
        });

/*********************************query*****************************************/


        final BmobQuery<PlaceItem> placeItemBmobQuery = new BmobQuery<PlaceItem>();
        Records records = new Records();
        records.setObjectId(RECORDID);
        placeItemBmobQuery.order("time");
        placeItemBmobQuery.addWhereEqualTo("host", new BmobPointer(records));
        placeItemBmobQuery.setLimit(10);
        placeItemBmobQuery.findObjects(new FindListener<PlaceItem>() {
            @Override
            public void done(List<PlaceItem> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        Log.d("PlaceItem", String.valueOf(list.size()));
                        graphicsLayer.removeAll();
                        int n = 0;
                        for (PlaceItem placeItem : list) {
                            BmobGeoPoint point = placeItem.getPoint();
                            queryPOI(point);
                            pointA.setX(placeItem.getPoint().getLongitude());
                            pointA.setY(placeItem.getPoint().getLatitude());
                            //地理坐标转投影坐标
                            Point mapPoint1 = (Point) GeometryEngine.project(pointA, SpatialReference.create(4326), mapSR);
                            Graphic graphic1 = new Graphic(mapPoint1, symbol);
                            graphicsLayer.addGraphic(graphic1);
                            if (n == 0) {
                                FirstPoint = mapPoint1;
                            }
                            n++;
                        }
                        map.zoomToResolution(FirstPoint, 19);
                        map.addLayer(graphicsLayer);
                    } else {
                        Toast toast = Toast.makeText(RecordMapActivity.this, " 无匹配数据 ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(RecordMapActivity.this, "placeItem查询失败", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        });
    }

    //////////////////////////////////获取照片地址
    public String getfirstURL(final Point p) {
        firstURL = null;


        BmobGeoPoint geoPoint = new BmobGeoPoint();
        geoPoint.setLongitude(p.getX());
        geoPoint.setLatitude(p.getY());

        Records recordsA = new Records();
        recordsA.setObjectId(RECORDID);

        BmobQuery<PlaceItem> placeItemBmobQuery = new BmobQuery<PlaceItem>();
        placeItemBmobQuery.addWhereNear("point", geoPoint);
        placeItemBmobQuery.addWhereEqualTo("host", new BmobPointer(recordsA));
        placeItemBmobQuery.setLimit(1);
        placeItemBmobQuery.findObjects(new FindListener<PlaceItem>() {
            @Override
            public void done(List<PlaceItem> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (PlaceItem placeItem : list) {
                            placeItemId = placeItem.getObjectId();

                            List<String> listURL = new ArrayList<String>();
                            listURL = placeItem.getURL();
                            firstURL = listURL.get(0);
                            Log.i("placeItemurl", firstURL);

                            if (firstURL == null) {
                                Log.i("loadimage", "URL null");
                            } else {
                                TwoThread tt = new TwoThread();
                                tt.start();
                            }

                        }
                    } else {
                        Log.i("indentify placeItem", "nothing found");
                    }
                } else {
                    Log.i("indentify placeItem", e.getMessage());
                }
            }
        });
        return firstURL;
    }

    public void queryPOI(BmobGeoPoint point) {
        BmobQuery<POI> poiBmobQuery = new BmobQuery<POI>();

        poiBmobQuery.addWhereNear("pointP", point);
        poiBmobQuery.setLimit(1);
        poiBmobQuery.findObjects(new FindListener<POI>() {
            @Override
            public void done(List<POI> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (POI poi : list) {
                            province = poi.getProvince();
                            city = poi.getCity();
                            place = poi.getPlace();
                            name = poi.getName();
                        }
                        Log.i("address", province + city + place + name);
                        Graphicexist = 1;
                    } else {
                        Toast toast = Toast.makeText(RecordMapActivity.this, " 空", Toast.LENGTH_SHORT);
                        toast.show();
                        Graphicexist = 0;
                    }
                } else {
                    Toast toast = Toast.makeText(RecordMapActivity.this, "POI查询失败", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.i("POI", e.getMessage() + e.getErrorCode());
                }
            }
        });
    }


    ///////////////////////////////定位器

    ////////////////////////////////downLoad
    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL mUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl
                    .openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            Log.i("downLoad", "finish");
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

    public class TwoThread extends Thread {
        public void run() {
            bitmap = getBitmapFromUrl(firstURL);
            if (bitmap == null) {
                Log.i("imagedwnLoad", "异步出错");
            } else {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }
}
