package com.example.melvin.fllowme.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.example.melvin.fllowme.BaseFragment;
import com.example.melvin.fllowme.Poisearch.PoiAroundSearchActivity;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.NoteActivity;
import com.example.melvin.fllowme.bean.POI;
import com.example.melvin.fllowme.followme.FollowMeActivity;
import com.example.melvin.fllowme.routing.LinkRouteActivity;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Melvin on 2016/8/11.
 */
public class GuideFragment extends BaseFragment {

    public static Point mLocation = null;
    final SpatialReference mapSR = SpatialReference.create(102100);    //空间参照
    final SpatialReference routSR = SpatialReference.create(4326);
    final int FIRST_CLICK = 1;
    final int SECOND_CLICK = 2;
    public LocationManager manager;
    public String placename;
    public Double X, Y;
    String worldMapURL =
            "http://cache1.arcgisonline.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
    LocationDisplayManager ldm;
    ImageButton bnLocation;
    ImageButton bnFollowme, bnlinkroute;
    EditText searchplace;
    TextView calloutplacename;
    int CLICK_STATE = FIRST_CLICK;
    private MapView map = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(activity, R.layout.fragment_guide, null);

//        new TitleBuilder(view).setTitleText("导航");
        bindviews(view);
        showmap(view);

        ldm = map.getLocationDisplayManager();
        ldm.setLocationListener(new MyLocationListener());
        ldm.start();
        ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.OFF);

        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        final GraphicsLayer graphicsLayer = new GraphicsLayer();
        final SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.rgb(255, 244, 143), 15, SimpleMarkerSymbol.STYLE.CROSS);


        final Callout callout = map.getCallout();
        callout.setStyle(R.xml.calloutstyle);  //callout的样式
        callout.setOffset(0, -15);
        map.setOnSingleTapListener(new OnSingleTapListener() {
            private static final long servialVersionUID = 1L;

            @Override
            public void onSingleTap(float x, float y) {

                //加载callout的布局
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.callout_addpoint, null);
                calloutplacename = (TextView) view.findViewById(R.id.placename);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.addpoint);

                imageButton.setOnClickListener(new View.OnClickListener() {   //图像按钮的点击事件
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("X", X);
                        intent.putExtra("Y", Y);
                        Log.i("X+Y", String.valueOf(X) + String.valueOf(Y));
                        intent.setClass(getActivity(), NoteActivity.class);
                        startActivity(intent);
                    }
                });
                Point pt = map.toMapPoint(x, y);    //将屏幕坐标转变成投影坐标
                Point mapPoint = (Point) GeometryEngine.project(pt, map.getSpatialReference(), SpatialReference.create(4326));

                switch (CLICK_STATE) {
                    case FIRST_CLICK:    //第一次点击，显示callout
                        DecimalFormat df = new DecimalFormat("#.000000");//保留六位
                        X = Double.parseDouble(df.format(mapPoint.getX()));
                        Y = Double.parseDouble(df.format(mapPoint.getY()));
                        Log.i("pt", String.valueOf(X) + "," + String.valueOf(Y));
                        BmobGeoPoint bmobpoint = new BmobGeoPoint();
                        bmobpoint.setLongitude(X);
                        bmobpoint.setLatitude(Y);
                        queryPOI(view, bmobpoint);
                        Graphic graphic1 = new Graphic(pt, symbol);
                        graphicsLayer.addGraphic(graphic1);
                        map.addLayer(graphicsLayer);
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

            }
        });


        return view;
    }


    private void bindviews(View view) {
        searchplace = (EditText) view.findViewById(R.id.searchplace);
        searchplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PoiAroundSearchActivity.class);
                startActivity(intent);
            }
        });
        searchplace.setCursorVisible(false);
        bnLocation = (ImageButton) view.findViewById(R.id.menu_location);
        bnFollowme = (ImageButton) view.findViewById(R.id.menu_followme);
        bnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point p = (Point) GeometryEngine.project(mLocation, routSR, mapSR);
                map.zoomToResolution(p, 20);
                Toast toast = Toast.makeText(getActivity(), "定位到你在的位置", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        bnFollowme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FollowMeActivity.class);
                startActivity(intent);
            }
        });

        bnlinkroute = (ImageButton) view.findViewById(R.id.menu_linkroute);
        bnlinkroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LinkRouteActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showmap(View view) {
        map = (MapView) view.findViewById(R.id.ditu);//获取map实例


        //Creates a dynamic layer using service URL

        ArcGISDynamicMapServiceLayer dynamicLayer = new ArcGISDynamicMapServiceLayer(worldMapURL);//地图服务
        //Adds layer into the 'MapView'

        map.addLayer(dynamicLayer); //添加地图服务到map中
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please enable your GPS before proceeding")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /***************************
     * POI
     ******************/
    public void queryPOI(View view, BmobGeoPoint point) {
        BmobQuery<POI> poiBmobQuery = new BmobQuery<POI>();

        poiBmobQuery.addWhereNear("pointP", point);
        poiBmobQuery.setLimit(1);
        poiBmobQuery.findObjects(new FindListener<POI>() {
            @Override
            public void done(List<POI> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (POI poi : list) {
                            String province = poi.getProvince();
                            String city = poi.getCity();
                            String place = poi.getPlace();
                            String name = poi.getName();
                            placename = province + city + place + name;
                            calloutplacename.setText("位置：" + placename);
                        }
                    } else {
                        Log.i("POI", "found nothing!");
                    }
                } else {
                    Log.i("POI", e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    private class MyLocationListener implements LocationListener {

        public MyLocationListener() {
            super();
        }

        /**
         * If location changes, update our current location. If being found for
         * the first time, zoom to our current position with a resolution of 20
         */
        public void onLocationChanged(Location loc) {
            if (loc == null)
                return;
            boolean zoomToMe = (mLocation == null);
            mLocation = new Point(loc.getLongitude(), loc.getLatitude());
            if (zoomToMe) {
                Point p = (Point) GeometryEngine.project(mLocation, routSR, mapSR);
                map.zoomToResolution(p, 20.0);
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "GPS定位失败",
                    Toast.LENGTH_SHORT).show();
            buildAlertMessageNoGps();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "GPS定位成功",
                    Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
