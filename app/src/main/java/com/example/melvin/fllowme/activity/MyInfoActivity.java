package com.example.melvin.fllowme.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.fragment.MomentsFragment;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.ImageUtils;
import com.example.melvin.fllowme.utils.TitleBuilder;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Melvin on 2016/8/27.
 */
public class MyInfoActivity extends FragmentActivity {

    private ImageView iv_photo;
    private EditText etAccount, etNickname, etSex;
    private Uri headPicUri;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static void isEdit(boolean value, EditText editText) {
        if (value) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return null;
                }
            }});
        } else {
//设置不可获取焦点
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
//输入框无法输入新的内容
            editText.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
                }
            }});
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setContentView(R.layout.activity_my_info_moments);
            MomentsFragment fragment = new MomentsFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.ll_my_info_container, fragment).commit();
        } else {
            setContentView(R.layout.activity_my_info);
            final Users user = BmobUser.getCurrentUser(Users.class);

            sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
            editor = sp.edit();

            headPicUri = null;

            iv_photo = (ImageView) findViewById(R.id.iv_photo);
            etAccount = (EditText) findViewById(R.id.etAccount);
            etNickname = (EditText) findViewById(R.id.etNickname);
            CommonConstants.STATIC_HEADPIC_PATH = sp.getString(CommonConstants.HEADPIC_PATH, null);
            if (CommonConstants.STATIC_HEADPIC_PATH != null) {
                File file = new File(CommonConstants.STATIC_HEADPIC_PATH);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(CommonConstants.STATIC_HEADPIC_PATH);
                    //将图片显示到ImageView中
                    iv_photo.setImageBitmap(bm);
                }
            } else {
                downLoad(user.getHeadPic());
            }

            etAccount.setText(user.getUsername());
            etNickname.setText(user.getNickname());
            // etSex.setText(user.getSex()?"男":"女");

            iv_photo.setEnabled(false);
            isEdit(false, etAccount);
            isEdit(false, etNickname);

            new TitleBuilder(MyInfoActivity.this).setRightText("修改").setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) findViewById(v.getId());
                    if (tv.getText().equals("修改")) {
                        tv.setText("完成");
                        //enable to click all the compoment
                        iv_photo.setEnabled(true);
//                    isEdit(true,etAccount);
                        isEdit(true, etNickname);
                        //isEdit(true,etSex);
                    } else if (tv.getText().equals("完成")) {
                        tv.setText("修改");
                        //unable to click
                        //upload the new information
                        final Users newUser = new Users();
                        final boolean[] isUpdate = {false};

                        iv_photo.setEnabled(false);
//                    isEdit(false, etAccount);
                        isEdit(false, etNickname);
                        if (!etNickname.getText().toString().equals(user.getNickname())) {
                            Log.e("updateNickName", "here");
                            user.setNickname(etNickname.getText().toString());
                            newUser.setNickname(etNickname.getText().toString());
                            isUpdate[0] = true;
                        }
                        isEdit(false, etSex);
                        if (headPicUri != null) {
                            Log.e("avdpath", headPicUri.getPath());
                            final BmobFile bmobFile = new BmobFile(new File(headPicUri.getPath()));
                            headPicUri = null;
                            bmobFile.uploadblock(new UploadFileListener() {

                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                        Log.e("上传文件成功", bmobFile.getFileUrl());

                                        editor.putString(CommonConstants.HEADPIC_URL, bmobFile.getFileUrl());
                                        editor.commit();
                                        Users newUser2 = new Users();
                                        newUser2.setNickname(user.getNickname());
                                        newUser2.setHeadPic(bmobFile.getFileUrl());
                                        newUser2.update(user.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    ToastUtils.showToast("更新用户头像成功");
                                                    downLoad(bmobFile.getFileUrl());
                                                } else {
                                                    ToastUtils.showToast("更新用户头像失败");
                                                }
                                            }
                                        });
//                                    CommonConstants.headPic_url = bmobFile.getFileUrl();


                                    } else {
                                        Log.e("上传文件失败", e.getMessage());
                                    }

                                }

                                @Override
                                public void onProgress(Integer value) {
                                    // 返回的上传进度（百分比）
                                }
                            });

                        }

                        if (isUpdate[0]) {
                            Log.e("update", "true");
                            newUser.update(user.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ToastUtils.showToast("更新用户信息成功");
                                    } else {
                                        ToastUtils.showToast("更新用户信息失败");
                                    }
                                }
                            });
                        } else {
                            Log.e("update", "false");
                        }
                    }
                }
            });

            iv_photo = (ImageView) findViewById(R.id.iv_photo);

            iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] items = {"拍照", "相册"};
                    new AlertDialog.Builder(MyInfoActivity.this)
                            .setTitle("更换头像")
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    switch (which) {
                                        case 0:
                                            ImageUtils.openCameraImage(MyInfoActivity.this);
                                            break;
                                        case 1:
                                            ImageUtils.openLocalImage(MyInfoActivity.this);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });

            findViewById(R.id.btnlogout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Users newUser = new Users();
                    newUser.setOnline(false);
                    newUser.update(BmobUser.getCurrentUser(Users.class).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e("online", "false");
                            } else {
                                Log.e("online", "true");
                            }
                        }
                    });
                    Users.logOut();   //清除缓存用户对象
                    editor.putString(CommonConstants.HEADPIC_PATH, null);
                    editor.putString(CommonConstants.HEADPIC_URL, null);
                    editor.commit();
                    CommonConstants.STATIC_HEADPIC_PATH = null;
                    Intent i = new Intent(ContextUtils.getInstance(), SplashActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final BmobFile bmobFile;
        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                    return;
                }
                headPicUri = ImageUtils.imageUriFromCamera;
                iv_photo.setImageURI(ImageUtils.imageUriFromCamera);
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (resultCode != RESULT_CANCELED) {
                    headPicUri = data.getData();
                    iv_photo.setImageURI(data.getData());
                }
                break;
        }
    }

    private void downLoad(String url) {
        BmobFile file = new BmobFile(System.currentTimeMillis() + ".jpg", null, url);
        file.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.e("download", "success");
//                    CommonConstants.headPic_path = s;
                    editor.putString(CommonConstants.HEADPIC_PATH, s);
                    editor.commit();
                    File file = new File(s);
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        //将图片显示到ImageView中
                        iv_photo.setImageBitmap(bm);
                    }
                } else {
                    Log.e("download", e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }
}
