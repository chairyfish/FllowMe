package com.example.melvin.fllowme.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;

/**
 * Created by Melvin on 2016/8/12.
 */
public class TitleBuilder {
    private View v_title;
    private TextView titlebar_tv_right;
    private ImageView titlebar_iv_right;
    private TextView titlebar_tv_center;
    private TextView titlebar_tv_left;
    private ImageView titlebar_iv_left;

    public TitleBuilder(Activity context)
    {
        v_title=context.findViewById(R.id.ll_titlebar);
        titlebar_tv_right= (TextView) v_title.findViewById(R.id.titlebar_tv_right);
        titlebar_iv_right= (ImageView) v_title.findViewById(R.id.titlebar_iv_right);
        titlebar_tv_center= (TextView) v_title.findViewById(R.id.titlebar_tv_center);
        titlebar_tv_left= (TextView) v_title.findViewById(R.id.titlebar_tv_left);
        titlebar_iv_left= (ImageView) v_title.findViewById(R.id.titlebar_iv_left);
    }

    public TitleBuilder(View context)
    {
        v_title=context.findViewById(R.id.ll_titlebar);
        titlebar_tv_right= (TextView) v_title.findViewById(R.id.titlebar_tv_right);
        titlebar_iv_right= (ImageView) v_title.findViewById(R.id.titlebar_iv_right);
        titlebar_tv_center= (TextView) v_title.findViewById(R.id.titlebar_tv_center);
        titlebar_tv_left= (TextView) v_title.findViewById(R.id.titlebar_tv_left);
        titlebar_iv_left= (ImageView) v_title.findViewById(R.id.titlebar_iv_left);
    }

    //title
    public TitleBuilder setTitleBgRes(int resId)
    {
        v_title.setBackgroundResource(resId);
        return this;
    }

    public TitleBuilder setTitleText(String string)
    {
        titlebar_tv_center.setVisibility(
                TextUtils.isEmpty(string)?View.GONE:View.VISIBLE
        );
        titlebar_tv_center.setText(string);
        return this;
    }

    //left
    public TitleBuilder setLeftText(String string)
    {
        titlebar_tv_left.setVisibility(
                TextUtils.isEmpty(string)?View.GONE:View.VISIBLE
        );
        titlebar_tv_left.setText(string);
        return this;
    }

    public TitleBuilder setLeftImg(int resId)
    {
        titlebar_iv_left.setVisibility(
                resId>0?View.VISIBLE:View.GONE
        );
        titlebar_iv_left.setImageResource(resId);
        return this;
    }

    public TitleBuilder setLeftOnClickListener(View.OnClickListener clickListener)
    {
        if(titlebar_tv_left.getVisibility()==View.VISIBLE)
        {
            titlebar_tv_left.setOnClickListener(clickListener);
        }
        else if(titlebar_iv_left.getVisibility()==View.VISIBLE)
        {
            titlebar_iv_left.setOnClickListener(clickListener);
        }
        return this;
    }

    //right
    public TitleBuilder setRightText(String string)
    {
        titlebar_tv_right.setVisibility(
                TextUtils.isEmpty(string)?View.GONE:View.VISIBLE
        );
        titlebar_tv_right.setText(string);
        return this;
    }

    public TitleBuilder setRightImg(int resId)
    {
        titlebar_iv_right.setVisibility(
                resId>0?View.VISIBLE:View.GONE
        );
        titlebar_iv_right.setImageResource(resId);
        return this;
    }

    public TitleBuilder setRightOnClickListener(View.OnClickListener clickListener)
    {
        if(titlebar_tv_right.getVisibility()==View.VISIBLE)
        {
            titlebar_tv_right.setOnClickListener(clickListener);
        }
        else if(titlebar_iv_right.getVisibility()==View.VISIBLE)
        {
            titlebar_iv_right.setOnClickListener(clickListener);
        }
        return this;
    }
}