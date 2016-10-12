package com.example.melvin.fllowme.utils;

import android.util.Log;

import com.example.melvin.fllowme.bean.Installations;
import com.example.melvin.fllowme.bean.PushMessages;
import com.example.melvin.fllowme.bean.Users;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Melvin on 2016/9/8.
 */
public class PushMessageUtils {

    public static Boolean isUserOnline(String id) {
        final Boolean[] isOnline = {false};
        BmobQuery<Users> query = new BmobQuery<Users>();
        query.getObject(id, new QueryListener<Users>() {
            @Override
            public void done(Users users, BmobException e) {
                if (e == null) {
                    isOnline[0] = users.getOnline();
                }
            }
        });
        return isOnline[0];
    }

    public static void writeTable(final String code, String receiverId) {
        PushMessages message = new PushMessages();
        message.setMessage(code);
        message.setSenderId(BmobUser.getCurrentUser(Users.class).getObjectId());
        Users friend = new Users();
        friend.setObjectId(receiverId);
        message.setReceiver(new BmobPointer(friend));
        message.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.e(code, "save sussess");
                } else {
                    Log.e(code, "save failure");
                }
            }
        });
    }

    public static void pushMsg(final String code, String receiverId) {
        BmobPushManager pushManager = new BmobPushManager();
        BmobQuery<Installations> query1 = BmobInstallation.getQuery();
        query1.addWhereEqualTo("uid", receiverId);
        pushManager.setQuery(query1);
        pushManager.pushMessage("#" + code + "#" + Users.getCurrentUser(Users.class).getObjectId(), new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.showToast("已发送好友验证");
                    Log.e(code, "success!!!");
                } else {
                    ToastUtils.showToast("发送验证失败");
                    Log.e(code, "failure!!!");
                }
            }
        });
    }
}
