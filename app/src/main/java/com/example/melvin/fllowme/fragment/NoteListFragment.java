package com.example.melvin.fllowme.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.PlaceItem;
import com.example.melvin.fllowme.bean.Records;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.database.MyDB;
import com.example.melvin.fllowme.utils.DbUtils;
import com.example.melvin.fllowme.utils.TitleBuilder;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by Melvin on 2016/9/4.
 */
public class NoteListFragment extends ListFragment {
    String title;
    private SimpleCursorAdapter adapter;
    private MyDB myDB;
    private SQLiteDatabase dbReader, dbWriter;
    private Cursor cursor;
    private Double CurLong, CurLat;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note_list, null);

        Bundle bundle = getArguments();
        CurLong = bundle.getDouble("Long");
        CurLat = bundle.getDouble("Lat");

        myDB = new MyDB(getActivity());
        dbReader = myDB.getReadableDatabase();
        dbWriter = myDB.getWritableDatabase();

        new TitleBuilder(view).setTitleText("游记本").setLeftText("返回").setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        }).setRightText("添加").setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNoteFragment fragment = new EditNoteFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putDouble("Long", CurLong);
                bundle1.putDouble("Lat", CurLat);
                fragment.setArguments(bundle1);
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.ll_note_container, fragment).commit();
            }
        });
///////////////////////////////////////////////////////////////////////////
        view.findViewById(R.id.btn_upload_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final EditText et = new EditText(getActivity());

                new AlertDialog.Builder(getActivity()).setTitle("给你的游记取个标题吧：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                title = et.getText().toString();
                                UpLoad(view);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        adapter = new SimpleCursorAdapter(view.getContext(), R.layout.adapter_note, null, new String[]{MyDB.COLUMN_NOTE_CONTENT, MyDB.COLUMN_NOTE_DATE}, new int[]{R.id.tvContent, R.id.tvDate});
        setListAdapter(adapter);

        refreshList();

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);

        Bundle bundle = new Bundle();
        bundle.putInt(EditNoteFragment.BUNDLE_NOTE_ID, cursor.getInt(cursor.getColumnIndex(MyDB.COLUMN_NOTE_ID)));
        bundle.putString(EditNoteFragment.BUNDLE_NOTE_CONTENT, cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_NOTE_CONTENT)));
        bundle.putString(EditNoteFragment.BUNDLE_NOTE_PIC_PATH[0], cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[0])));
        bundle.putString(EditNoteFragment.BUNDLE_NOTE_PIC_PATH[1], cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[1])));
        bundle.putString(EditNoteFragment.BUNDLE_NOTE_PIC_PATH[2], cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[2])));
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.ll_note_container, fragment).commit();
    }

    private void refreshList() {
        adapter.changeCursor(dbReader.query(MyDB.TABLE_NOTE_NAME, null, null, null, null, null, null));
    }


    public void UpLoad(View view) {
        Log.e("mark", "111");


        cursor = dbReader.query(MyDB.TABLE_NOTE_NAME, null, null, null, null, null, null);
        double longitude, latitude;
        String note_content, date;
        final String[] note_path = new String[3];
        int indexLong = cursor.getColumnIndex(MyDB.COLUMN_NOTE_LONG);
        int indexLai = cursor.getColumnIndex(MyDB.COLUMN_NOTE_LAI);
        int indexContent = cursor.getColumnIndex(MyDB.COLUMN_NOTE_CONTENT);
        int indexPath1 = cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[0]);
        int indexPath2 = cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[1]);
        int indexPath3 = cursor.getColumnIndex(MyDB.COLUMN_NOTE_PIC_PATH[2]);
        int indexDate = cursor.getColumnIndex(MyDB.COLUMN_NOTE_DATE);
        int picNum;
//                int Size = cursor.getCount();
        int i = 0;

        Users user = BmobUser.getCurrentUser(Users.class);
        final String[] coverUrl = {""};
        final int cursor_count = cursor.getCount();
        if (cursor_count > 0) {
            while (cursor.moveToNext()) {
                Log.e("upload", "1");
                longitude = cursor.getDouble(indexLong);
                latitude = cursor.getDouble(indexLai);
                note_content = cursor.getString(indexContent);
                note_path[0] = cursor.getString(indexPath1);
                note_path[1] = cursor.getString(indexPath2);
                note_path[2] = cursor.getString(indexPath3);
                date = cursor.getString(indexDate);

                if (!note_path[2].equals("")) {
                    picNum = 3;
                } else if (!note_path[1].equals("")) {
                    picNum = 2;
                } else if (!note_path[0].equals("")) {
                    picNum = 1;
                    Log.e("note_path", note_path[0]);
                } else {
                    picNum = 0;
                }

                final PlaceItem placeItem = new PlaceItem();
                placeItem.setText(note_content);
                placeItem.setPoint(new BmobGeoPoint(longitude, latitude));

                Date date1 = DbUtils.transformdate(date);
                BmobDate bmobDate = new BmobDate(date1);
                placeItem.setTime(bmobDate);
                Log.e("date", date);

                Log.e("upload", "2");
                if (i == 0) {
                    i++;
                    Log.e("upload", "3");
                    Records records = new Records();
                    records.setTitle(title);
                    records.setCoverURL(coverUrl[0]);
                    records.setAuthor(user.getNickname());
                    records.setHost(new BmobPointer(user));
                    final int finalPicNum = picNum;
                    records.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Log.e("done", "done");
                            if (e == null) {
                                Log.e("record", "upload success");
                                Log.e("author", s);
                                CommonConstants.RECORD_OBJ_ID = s;


                                Records record2 = new Records();
                                record2.setObjectId(s);
                                placeItem.setHost(new BmobPointer(record2));

                                if (finalPicNum != 0) {
                                    Log.e("picNum", finalPicNum + "");
                                    final String[] filePaths = new String[finalPicNum];
                                    for (int j = 0; j < finalPicNum; j++) {
                                        filePaths[j] = note_path[j];
                                    }
                                    BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

                                        @Override
                                        public void onSuccess(List<BmobFile> files, List<String> urls) {
                                            if (urls.size() == filePaths.length) {
                                                //do something

                                                if (coverUrl[0].equals("")) {
                                                    coverUrl[0] = urls.get(0);

                                                    Records records = new Records();
                                                    records.setTitle("123");
                                                    records.setCoverURL(coverUrl[0]);
                                                    Log.e("url", coverUrl[0]);
                                                    records.setObjectId(CommonConstants.RECORD_OBJ_ID);
                                                    Log.e("id", CommonConstants.RECORD_OBJ_ID + "  2222");
                                                    records.update(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if (e == null)
                                                                Log.e("record", "update1 success");
                                                            else
                                                                Log.e("record", e.getMessage());
                                                        }
                                                    });
                                                }
                                                Log.e("pic", "success");
                                                placeItem.setURL(urls);

                                                placeItem.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if (e == null) {
                                                            Log.e("placeItem", "upload success");

                                                            if (cursor_count == 1) {
                                                                dbWriter.delete(MyDB.TABLE_NOTE_NAME, MyDB.COLUMN_NOTE_ID + " > -1", null);
                                                                getActivity().finish();
                                                            }
                                                        } else {
                                                            Log.e("placeItem", "upload failure");
                                                            Log.e("error", e.getMessage() + "," + e.getErrorCode());
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onError(int statuscode, String errormsg) {
                                            ToastUtils.showToast("错误码" + statuscode + ",错误描述：" + errormsg);
                                        }

                                        @Override
                                        public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                                        }
                                    });
                                } else {
                                    placeItem.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Log.e("placeItem", "upload success");

                                                if (cursor_count == 1) {
                                                    dbWriter.delete(MyDB.TABLE_NOTE_NAME, MyDB.COLUMN_NOTE_ID + " > -1", null);
                                                    getActivity().finish();
                                                }
                                            }
                                        }
                                    });
                                }

                            } else {
                                Log.e("error", e.getMessage());
                            }
                        }
                    });
                    Log.e("i", i + "");
                } else {
                    i++;
                    Log.e("i", i + "");

                    while (true) {
                        if (!CommonConstants.RECORD_OBJ_ID.equals("")) {
                            break;
                        }
                    }

                    Records record2 = new Records();
                    record2.setObjectId(CommonConstants.RECORD_OBJ_ID);
                    placeItem.setHost(new BmobPointer(record2));

                    if (i == cursor_count) {
                        CommonConstants.RECORD_OBJ_ID = "";
                    }

                    if (picNum != 0) {
                        Log.e("picNum", picNum + "");
                        final String[] filePaths = new String[picNum];
                        for (int j = 0; j < picNum; j++) {
                            filePaths[j] = note_path[j];
                        }
                        final int finalI = i;
                        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

                            @Override
                            public void onSuccess(List<BmobFile> files, List<String> urls) {
                                if (urls.size() == filePaths.length) {
                                    //do something

                                    if (coverUrl[0].equals("")) {
                                        coverUrl[0] = urls.get(0);

                                        Records records = new Records();
                                        records.setTitle(title);
                                        records.setCoverURL(coverUrl[0]);
                                        records.setObjectId(CommonConstants.RECORD_OBJ_ID);
                                        records.update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                Log.e("record", "update2 success");
                                            }
                                        });
                                    }
                                    Log.e("pic", "success");
                                    placeItem.setURL(urls);

                                    placeItem.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Log.e("placeItem", "upload success");

                                                if (finalI == cursor_count) {
                                                    dbWriter.delete(MyDB.TABLE_NOTE_NAME, MyDB.COLUMN_NOTE_ID + " > -1", null);
                                                    getActivity().finish();
                                                }
                                            } else {
                                                Log.e("placeItem", "upload failure");
                                                Log.e("error", e.getMessage() + "," + e.getErrorCode());
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(int statuscode, String errormsg) {
                                ToastUtils.showToast("错误码" + statuscode + ",错误描述：" + errormsg);
                            }

                            @Override
                            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                            }
                        });
                    } else {
                        final int finalI = i;
                        placeItem.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Log.e("placeItem", "upload success");

                                    if (finalI == cursor_count) {
                                        dbWriter.delete(MyDB.TABLE_NOTE_NAME, MyDB.COLUMN_NOTE_ID + " > -1", null);
                                        getActivity().finish();
                                    }
                                }
                            }
                        });
                    }
                }


                Log.e("upload", "5");
            }

        } else {
            ToastUtils.showToast("您的游记内容为空哦");
        }
    }
}
