package com.example.melvin.fllowme.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.database.MyDB;
import com.example.melvin.fllowme.utils.ImageLoader;
import com.example.melvin.fllowme.utils.PushMessageUtils;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Melvin on 2016/8/24.
 */
public class ReplyActivity extends Activity implements View.OnClickListener {
    private String applicantId;
    private MyDB myDB;
    private SQLiteDatabase dbWriter;
    private String headpic_path;
    private TextView tvDetialAccount, tvDetialNick;
    private ImageView ivDetailPhoto;
    private Users applicant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        tvDetialAccount = (TextView) findViewById(R.id.tvDetailAccount);
        tvDetialNick = (TextView) findViewById(R.id.tvDetailNick);
        ivDetailPhoto = (ImageView) findViewById(R.id.ivDetailPhoto);

        myDB = new MyDB(this);
        dbWriter = myDB.getWritableDatabase();

        applicantId = getIntent().getStringExtra("applicantId");
        final String app = getIntent().getStringExtra("app");
        Log.e("applicantId", applicantId);
        Log.e("app", app);
        BmobQuery<Users> query = new BmobQuery<Users>();
        query.getObject(applicantId, new QueryListener<Users>() {
            @Override
            public void done(Users users, BmobException e) {
                if (e == null) {
                    applicant = users;
                    tvDetialAccount.setText(users.getUsername());
                    tvDetialNick.setText(users.getNickname());

                    ImageLoader.showImageByAsyncTask(ivDetailPhoto, applicant.getHeadPic(), applicant.getUsername(), applicant.getNickname());
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        final Boolean[] isOnline = {false};
        isOnline[0] = PushMessageUtils.isUserOnline(applicantId);

        switch (v.getId()) {
            case R.id.btn_reple_agree:
                //1.add relation
                //2.jump
                //3.send back message
                Users user = new Users();
                user.setObjectId(applicantId);
                Users userTable = new Users();
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                userTable.setContacts(relation);
                userTable.update(Users.getCurrentUser(Users.class).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.i("bmob", "多对多关联添加成功");

                            //reply
                            if (isOnline[0]) {
                                PushMessageUtils.pushMsg(CommonConstants.APPLY_AGREE, applicantId);
                            } else {
                                PushMessageUtils.writeTable(CommonConstants.APPLY_AGREE, applicantId);
                            }
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage());
                        }
                    }
                });
                finish();
                break;
            case R.id.btn_reple_refuse:
                //delete in local db
                dbWriter.delete(MyDB.TABLE_CONTACT_NAME, MyDB.COLUMN_CONTACT_FRIEND + " = ?", new String[]{applicant.getUsername()});

                //reply
                if (isOnline[0]) {
                    PushMessageUtils.pushMsg(CommonConstants.APPLY_AGREE, applicantId);
                } else {
                    PushMessageUtils.writeTable(CommonConstants.APPLY_AGREE, applicantId);
                }
                finish();
                break;
        }
    }
}
