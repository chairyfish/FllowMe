package com.example.melvin.fllowme.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.utils.ImageLoader;
import com.example.melvin.fllowme.utils.PushMessageUtils;

/**
 * Created by Melvin on 2016/8/21.
 */
public class DetailInfoFragment extends Fragment {
    private TextView tvDetailAccount, tvDetailNick;
    private String uid, username, nickname, headpic_url;
    private ImageView ivSearchPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_info, null);

        tvDetailAccount = (TextView) view.findViewById(R.id.tvDetailAccount);
        tvDetailNick = (TextView) view.findViewById(R.id.tvDetailNick);
        ivSearchPhoto = (ImageView) view.findViewById(R.id.ivSearchPhoto);

        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        username = bundle.getString("username");
        nickname = bundle.getString("nickname");
        headpic_url = bundle.getString("headpic_url");

        tvDetailAccount.setText(username);
        tvDetailNick.setText(nickname);
        Bitmap bitmap = ImageLoader.getBitmapFromUrl(headpic_url);
        ivSearchPhoto.setImageBitmap(bitmap);


        view.findViewById(R.id.btnDetailAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Boolean[] isOnline = {false};
                isOnline[0] = PushMessageUtils.isUserOnline(uid);
                if (isOnline[0]) {
                    PushMessageUtils.pushMsg(CommonConstants.APPLY_FRIEND, uid);
                } else {
                    PushMessageUtils.writeTable(CommonConstants.APPLY_FRIEND, uid);
                }
            }
        });

        return view;
    }
}
