package com.example.melvin.fllowme.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.melvin.fllowme.BaseFragment;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.MyInfoActivity;
import com.example.melvin.fllowme.activity.NoteActivity;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.TitleBuilder;

import java.io.File;

import cn.bmob.v3.BmobUser;

/**
 * Created by Melvin on 2016/8/11.
 */
public class MeFragment extends BaseFragment {
    private ImageView iv_me_photo;
    private Users user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(activity, R.layout.fragment_me, null);
        user = BmobUser.getCurrentUser(Users.class);

        new TitleBuilder(view).setTitleText("我");

        iv_me_photo = (ImageView) view.findViewById(R.id.iv_me_photo);

        if (CommonConstants.STATIC_HEADPIC_PATH == null) {
            SharedPreferences sp = getActivity().getSharedPreferences(CommonConstants.MY_DB, Context.MODE_PRIVATE);
            CommonConstants.STATIC_HEADPIC_PATH = sp.getString(CommonConstants.HEADPIC_PATH, null);
        }
        if (CommonConstants.STATIC_HEADPIC_PATH != null) {
            File file = new File(CommonConstants.STATIC_HEADPIC_PATH);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(CommonConstants.STATIC_HEADPIC_PATH);
//                bm = ImageLoader.getImageThumbnail(CommonConstants.STATIC_HEADPIC_PATH,50,50);
//                bm = ImageLoader.toRoundCorner(bm,360);
                //将图片显示到ImageView中
                iv_me_photo.setImageBitmap(bm);
            }
        } else {
            Log.e("pathstatic", "null");
            iv_me_photo.setImageResource(R.mipmap.ic_launcher);
        }

        view.findViewById(R.id.ll_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContextUtils.getInstance(), MyInfoActivity.class));
            }
        });

        view.findViewById(R.id.ll_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContextUtils.getInstance(), NoteActivity.class));
            }
        });

        view.findViewById(R.id.ll_me_moments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MomentsFragment fragment = new MomentsFragment();
                Intent intent = new Intent(view.getContext(), MyInfoActivity.class);

                Bundle bundle = new Bundle();
                Users me = BmobUser.getCurrentUser(Users.class);
                bundle.putString("friendId", me.getObjectId());
                bundle.putString("friendNick", me.getNickname());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("hidden", "flag");
        if (!hidden) {
            Log.e("hidden", "show");
            if (CommonConstants.STATIC_HEADPIC_PATH != null) {
                File file = new File(CommonConstants.STATIC_HEADPIC_PATH);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(CommonConstants.STATIC_HEADPIC_PATH);
//                bm = ImageLoader.getImageThumbnail(CommonConstants.STATIC_HEADPIC_PATH,50,50);
//                bm = ImageLoader.toRoundCorner(bm,360);
                    //将图片显示到ImageView中
                    iv_me_photo.setImageBitmap(bm);
                }
            } else
                Log.e("statPath", "null");
        } else
            Log.e("hidden", "hidden");
    }
}
