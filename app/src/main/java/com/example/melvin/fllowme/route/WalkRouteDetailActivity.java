package com.example.melvin.fllowme.route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.utils.AmapUtil;


/**
 * Created by admin on 2016/8/16.
 */
public class WalkRouteDetailActivity extends Activity {
    private WalkPath mWalkPath;
    private TextView mTitle, mTitleWalkRoute;
    private ListView mWalkSegmentList;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        getIntentData();
        init();
    }

    private void init() {
        mTitle = (TextView) findViewById(R.id.title_center);
        mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
        mTitle.setText("步行路线详情");
        String dur = AmapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AmapUtil.getFriendlyLength((int) mWalkPath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");
        configureListView();
    }

    private void configureListView() {
        mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mWalkPath = intent.getParcelableExtra("walk_path");
    }

    public void onBackClick(View view) {
        this.finish();
    }

}
