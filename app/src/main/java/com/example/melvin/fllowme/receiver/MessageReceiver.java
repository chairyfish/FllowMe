package com.example.melvin.fllowme.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.ReplyActivity;
import com.example.melvin.fllowme.activity.SplashActivity;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.ImageLoader;
import com.example.melvin.fllowme.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Melvin on 2016/8/19.
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "";
        int SmallIcon = 0;
        String ContentTitle = "", ContentText = "";
        final String[] id = {""};
        String code = "";
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            Log.e("Push", msg);
            ToastUtils.showToast(msg);
            JSONTokener jsonTokener = new JSONTokener(msg);
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonTokener.nextValue();
                message = jsonObject.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            code = message.substring(1, 3);
            id[0] = message.substring(4);
            Log.e("#", message.substring(1, 3));
        } else if (intent.getStringExtra("pushMsg") != null) {
            code = intent.getStringExtra("code");
            id[0] = intent.getStringExtra("senderId");
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);

        if (code.equals(CommonConstants.APPLY_FRIEND)) {
            SmallIcon = R.mipmap.ic_launcher;
            ContentTitle = "好友验证";
            ContentText = "我是" + id[0] + ",希望能添加你为好友！";
            Intent intent1 = new Intent(context, ReplyActivity.class);
            intent1.putExtra("applicantId", id[0]);
            Log.e("applicantId", id[0]);
            intent1.putExtra("app", "app");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else if (code.equals(CommonConstants.APPLY_AGREE)) {
            SmallIcon = R.mipmap.ic_launcher;
            ContentTitle = "同意申请";
            ContentText = "我是" + id[0] + ",已经同意你的好友申请！";

            final Users user = new Users();
            user.setObjectId(id[0]);
            Users userTable = new Users();
            userTable.setObjectId(BmobUser.getCurrentUser(Users.class).getObjectId());
            BmobRelation relation = new BmobRelation();
            relation.add(user);
            userTable.setContacts(relation);
            userTable.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.e("bmob", "多对多关联添加成功");

                        //add in local db
                        BmobQuery<Users> query = new BmobQuery<Users>();
                        Log.e("mark", "1");
                        query.getObject(id[0], new QueryListener<Users>() {
                            @Override
                            public void done(final Users users, BmobException e) {
                                if (e == null) {
                                    ImageLoader.showImageByAsyncTask(null, user.getHeadPic(), user.getUsername(), user.getNickname());
                                }
                            }
                        });
                    } else {
                        Log.e("mark", "5");
                        Log.i("bmob", "失败：" + e.getMessage());
                    }
                }
            });

        } else if (code.equals(CommonConstants.APPLY_REFUSE)) {
            SmallIcon = R.mipmap.ic_launcher;
            ContentTitle = "拒绝申请";
            ContentText = "我是" + id[0] + ",已经拒绝你的好友申请！";
        } else if (code.equals(CommonConstants.APPLY_OFFLINE)) {
            Users.logOut();   //清除缓存用户对象
            SharedPreferences sp = ContextUtils.getInstance().getSharedPreferences(CommonConstants.SP_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CommonConstants.HEADPIC_PATH, null);
            editor.putString(CommonConstants.HEADPIC_URL, null);
            editor.commit();
            CommonConstants.STATIC_HEADPIC_PATH = null;
            Intent i = new Intent(ContextUtils.getInstance(), SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextUtils.getInstance().startActivity(i);
//                finish();
            return;
        }
        builder.setSmallIcon(SmallIcon);
        builder.setContentTitle(ContentTitle);
        builder.setContentText(ContentText);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
        Notification notification = builder.build();
        manager.notify(R.mipmap.ic_launcher, notification);
    }
}
