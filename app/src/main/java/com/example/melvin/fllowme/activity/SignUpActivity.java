package com.example.melvin.fllowme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Installations;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SignUpActivity extends AppCompatActivity {

    public String newusername1;
    public String newpasswd1;
    public String repasswd1;
    Button btsignin;
    EditText newusername, newpasswd, repasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initView();
    }

    private void initView() {
        btsignin = (Button) findViewById(R.id.signin);
        newusername = (EditText) findViewById(R.id.newusername);
        newpasswd = (EditText) findViewById(R.id.newpasswd);
        repasswd = (EditText) findViewById(R.id.repasswd);

        btsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newusername1 = newusername.getText().toString();
                newpasswd1 = newpasswd.getText().toString();
                repasswd1 = repasswd.getText().toString();

                if (newusername.equals("") || newpasswd1.equals("")) {
                    Toast toast = Toast.makeText(SignUpActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    if (newpasswd1.equals(repasswd1)) {
                        register();
                    } else {
                        Toast toast1 = Toast.makeText(SignUpActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG);
                        toast1.show();
                    }
                }
            }
        });
    }

    public void register() {
        final Users user = new Users();
        user.setUsername(newusername1);
        user.setPassword(newpasswd1);
        user.setNickname(newusername1);
        user.setHeadPic("http://bmob-cdn-5915.b0.upaiyun.com/2016/09/01/e982e9b1338d41568a622fab5639a419.jpg");
        user.signUp(new SaveListener<Users>() {

            @Override
            public void done(Users users, BmobException e) {
                if (e == null) {
                    ToastUtils.showToast("SignUp Successfully");

                    user.login(new SaveListener<Users>() {
                        @Override
                        public void done(Users users, BmobException e) {
                            if (e == null) {
                                Users newUser = new Users();
                                final Users user = BmobUser.getCurrentUser(Users.class);
                                newUser.setOnline(true);
                                newUser.update(user.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.e("online", "true");
                                        } else {
                                            Log.e("online", "false");
                                        }
                                    }
                                });
                                ToastUtils.showToast("Login Successfully");

                                BmobQuery<Installations> query = new BmobQuery<Installations>();
                                query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(ContextUtils.getInstance()));
                                query.findObjects(new FindListener<Installations>() {

                                    @Override
                                    public void done(List<Installations> list, BmobException e) {
                                        if (e == null) {
                                            if (list.size() > 0) {
                                                Installations mbi = list.get(0);
                                                mbi.setUid(user.getObjectId());
                                                Log.e("user", user.getObjectId());
                                                mbi.update(mbi.getObjectId(), new UpdateListener() {

                                                    @Override
                                                    public void done(BmobException e) {
                                                        if (e == null)
                                                            Log.e("ins updat", "ins update success");
                                                        else
                                                            Log.e("ins updat", "ins update failure");
                                                    }

                                                });
                                            } else {
                                                Log.e("found", "not found");
                                            }
                                        }
                                    }
                                });
                                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();

                            } else
                                ToastUtils.showToast("Login failed");
                        }
                    });
                } else {
                    ToastUtils.showToast("SignUp failed");
                }
            }
        });
    }
}
