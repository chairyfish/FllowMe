package com.example.melvin.fllowme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.melvin.fllowme.BaseActivity;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Installations;
import com.example.melvin.fllowme.bean.PushMessages;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.receiver.MessageReceiver;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Melvin on 2016/8/11.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {


    EditText username1, passwd1;
    private Button bnLogin, bnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        bnLogin = (Button) findViewById(R.id.bnLogin);
        bnSignin = (Button) findViewById(R.id.bnSignin);
        bnLogin.setOnClickListener(this);
        bnSignin.setOnClickListener(this);
        username1 = (EditText) findViewById(R.id.username);
        passwd1 = (EditText) findViewById(R.id.passwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bnLogin:
                login();
                break;
            case R.id.bnSignin:
                Intent intent = new Intent();
                intent.setClass(this, SignUpActivity.class);
                startActivity(intent);

                break;
        }
    }

    public void login() {
        String username = username1.getText().toString();
        String passwd = passwd1.getText().toString();
        if (username.isEmpty() || passwd.isEmpty()) {
            Toast toast = Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Log.e("username", username);
        Users user4login = new Users();
        user4login.setUsername(username);
        user4login.setPassword(passwd);
        user4login.login(new SaveListener<Users>() {
            @Override
            public void done(Users users, BmobException e) {
                if (e == null) {
                    if (users.getOnline() == false) {
                        Users newUser = new Users();
                        final Users user = BmobUser.getCurrentUser(Users.class);
                        newUser.setOnline(true);
                        newUser.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e("online", "true");

                                    BmobQuery<PushMessages> query = new BmobQuery<PushMessages>();
                                    query.addWhereEqualTo("receiver", new BmobPointer(user));
                                    query.findObjects(new FindListener<PushMessages>() {
                                        @Override
                                        public void done(List<PushMessages> list, BmobException e) {
                                            for (int i = 0; i < list.size(); i++) {
                                                Intent intent = new Intent(LoginActivity.this, MessageReceiver.class);
                                                intent.putExtra("pushMsg", "pushMsg");
                                                intent.putExtra("code", list.get(i).getMessage());
                                                intent.putExtra("senderId", list.get(i).getSenderId());
                                                sendBroadcast(intent);
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("online", "false");
                                }
                            }
                        });
                    } else {
                        BmobPushManager pushManager = new BmobPushManager();
                        BmobQuery<Installations> query1 = BmobInstallation.getQuery();
                        query1.addWhereEqualTo("uid", users.getObjectId());
                        pushManager.setQuery(query1);
                        pushManager.pushMessage("#" + CommonConstants.APPLY_OFFLINE + "#" + Users.getCurrentUser(Users.class).getObjectId(), new PushListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e(CommonConstants.APPLY_OFFLINE, "success!!!");

                                    BmobQuery<Installations> query = new BmobQuery<Installations>();
                                    query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(ContextUtils.getInstance()));
                                    query.findObjects(new FindListener<Installations>() {

                                        @Override
                                        public void done(List<Installations> list, BmobException e) {
                                            if (e == null) {
                                                if (list.size() > 0) {
                                                    Installations mbi = list.get(0);
                                                    mbi.setUid(BmobUser.getCurrentUser(Users.class).getObjectId());
                                                    mbi.update(mbi.getObjectId(), new UpdateListener() {

                                                        @Override
                                                        public void done(BmobException e) {
                                                            if (e == null) {
                                                                Log.e("installtion", "update success");
                                                            } else {
                                                                Log.e("installtion", "update failure");
                                                            }
                                                        }

                                                    });
                                                } else {
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(CommonConstants.APPLY_OFFLINE, "failure!!!");
                                }
                            }
                        });
                    }
                    ToastUtils.showToast("Login Successfully");


                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    ToastUtils.showToast("Login failed");
                    Log.e("error", e.toString());
                }
            }
        });
    }
}
