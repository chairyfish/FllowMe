package com.example.melvin.fllowme.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.MyInfoActivity;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.database.MyDB;
import com.example.melvin.fllowme.utils.ImageLoader;
import com.example.melvin.fllowme.utils.PushMessageUtils;
import com.example.melvin.fllowme.utils.ToastUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Melvin on 2016/8/31.
 */
public class FriendInfoFragment extends android.support.v4.app.Fragment {
    private Bundle bundle;
    private MyDB myDB;
    private SQLiteDatabase dbWrite, dbReader;
    private Cursor cursor;

    private String id, account, nickname, headpic_url;
    private Boolean sex;

    private TextView tv_account, tv_nickname, tv_sex, tv_friend_info_modify;
    private EditText et_friend_info_remark;
    private ImageView iv_photo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_info, null);

        myDB = new MyDB(view.getContext());
        dbReader = myDB.getReadableDatabase();
        dbWrite = myDB.getWritableDatabase();

        bundle = getArguments();
        id = bundle.getString("id");
        account = bundle.getString("account");
        nickname = bundle.getString("nickname");
        headpic_url = bundle.getString("headpic_url");
        sex = bundle.getBoolean("sex");

        iv_photo = (ImageView) view.findViewById(R.id.iv_friend_info_photo);
        tv_account = (TextView) view.findViewById(R.id.tv_friend_info_Account);
        tv_nickname = (TextView) view.findViewById(R.id.tv_friend_info_Nickname);
        tv_sex = (TextView) view.findViewById(R.id.tv_friend_info_Sex);
        tv_friend_info_modify = (TextView) view.findViewById(R.id.tv_friend_info_remark_modify);
        et_friend_info_remark = (EditText) view.findViewById(R.id.et_friend_info_remark);

        cursor = dbReader.query(MyDB.TABLE_CONTACT_NAME, new String[]{MyDB.COLUMN_CONTACT_REMARK}, MyDB.COLUMN_CONTACT_FRIEND + "= ? ", new String[]{account}, null, null, null);
        if (cursor.moveToFirst())
            et_friend_info_remark.setText(cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_CONTACT_REMARK)));
        MyInfoActivity.isEdit(false, et_friend_info_remark);


        tv_friend_info_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_friend_info_modify.getText().equals("修改")) {
                    tv_friend_info_modify.setText("完成");
                    MyInfoActivity.isEdit(true, et_friend_info_remark);
                } else {
                    tv_friend_info_modify.setText("修改");
                    MyInfoActivity.isEdit(false, et_friend_info_remark);
                    ContentValues cv = new ContentValues();
                    cv.put(MyDB.COLUMN_CONTACT_REMARK, et_friend_info_remark.getText().toString());
                    dbWrite.update(MyDB.TABLE_CONTACT_NAME, cv, MyDB.COLUMN_CONTACT_FRIEND + "= ? ", new String[]{account});
                }
            }
        });

        Bitmap bitmap = ImageLoader.getBitmapFromLrucache(headpic_url);
        iv_photo.setImageBitmap(bitmap);
        tv_nickname.setText(nickname);
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MomentsFragment fragment = new MomentsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("friendId", id);
                bundle.putString("friendNick", nickname);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.ll_friend_info_container, fragment).commit();
            }
        });
        tv_account.setText(account);
        tv_sex.setText(sex ? "男" : "女");

        final Users user = BmobUser.getCurrentUser(Users.class);


        view.findViewById(R.id.btnDeleteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Users friend = new Users();
                friend.setObjectId(id);

                BmobRelation relation = new BmobRelation();
                relation.remove(friend);
                user.setContacts(relation);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtils.showToast("delete successfully");

                            dbWrite.delete(MyDB.TABLE_CONTACT_NAME, MyDB.COLUMN_CONTACT_FRIEND + " = ?", new String[]{bundle.getString("account")});
                        } else {
                            ToastUtils.showToast("delete failure");
                            Log.e("delete", e.getMessage());
                        }
                    }
                });

                final Boolean[] isOnline = {false};
                isOnline[0] = PushMessageUtils.isUserOnline(id);
                if (isOnline[0]) {
                    PushMessageUtils.pushMsg(CommonConstants.APPLY_DELETE, id);
                } else {
                    PushMessageUtils.writeTable(CommonConstants.APPLY_DELETE, id);
                }
            }
        });

        return view;
    }


}
