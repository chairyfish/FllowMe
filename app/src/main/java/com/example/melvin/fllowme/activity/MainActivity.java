package com.example.melvin.fllowme.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.fragment.FragmentController;

public class MainActivity extends FragmentActivity {

    TextView[] rbs = new TextView[4];
    private RadioGroup rg_tab;
    private FragmentController fragmentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        rg_tab= (RadioGroup) findViewById(R.id.rg_tab);


//初始化控件，中间大个的，周围小弟
        rbs[0] = (TextView) findViewById(R.id.rb_moments);
        rbs[1] = (TextView) findViewById(R.id.rb_guide);
        rbs[2] = (TextView) findViewById(R.id.rb_contact);
        rbs[3] = (TextView) findViewById(R.id.rb_user);

        rbs[0].setOnClickListener(new MenuClick());
        rbs[1].setOnClickListener(new MenuClick());
        rbs[2].setOnClickListener(new MenuClick());
        rbs[3].setOnClickListener(new MenuClick());

/*
        Drawable[] drs=new Drawable[4];

        for (ImageView rb : rbs) {
            //挨着给每个RadioButton加入drawable限制边距以控制显示大小
            drs = rb.getCompoundDrawables();
            //获取drawables
            Rect r = new Rect(0, 0, drs[1].getMinimumWidth()*1/2, drs[1].getMinimumHeight()*1/2);
            //定义一个Rect边界
            drs[1].setBounds(r);
            rb.setCompoundDrawables(null,drs[1],null,null);
            //添加限制给控件
        }
*/


        fragmentController = FragmentController.getFragmentController(this, R.id.ly_content);
        rbs[0].setSelected(true);
        fragmentController.showFragment(0);
    }

    private void setSelected() {
        rbs[0].setSelected(false);
        rbs[1].setSelected(false);
        rbs[2].setSelected(false);
        rbs[3].setSelected(false);

    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentController.onDestroy();
    }

    private class MenuClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rb_moments:
                    setSelected();
                    rbs[0].setSelected(true);
                    fragmentController.showFragment(0);
                    break;
                case R.id.rb_guide:
                    setSelected();
                    rbs[1].setSelected(true);
                    fragmentController.showFragment(1);
                    break;
                case R.id.rb_contact:
                    setSelected();
                    rbs[2].setSelected(true);
                    fragmentController.showFragment(2);
                    break;
                case R.id.rb_user:
                    setSelected();
                    rbs[3].setSelected(true);
                    fragmentController.showFragment(3);
                    break;
                default:
                    break;
            }
        }
    }

}
