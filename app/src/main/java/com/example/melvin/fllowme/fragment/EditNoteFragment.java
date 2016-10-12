package com.example.melvin.fllowme.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.adapter.NotePicAdapter;
import com.example.melvin.fllowme.database.MyDB;
import com.example.melvin.fllowme.utils.ImageUtils;
import com.example.melvin.fllowme.utils.TitleBuilder;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Melvin on 2016/9/4.
 */
public class EditNoteFragment extends Fragment {

    public static final String BUNDLE_NOTE_ID = "note_id";
    public static final String BUNDLE_NOTE_CONTENT = "note_content";
    public static final String[] BUNDLE_NOTE_PIC_PATH = {"note_path1", "note_path2", "note_path3"};

    private int note_id;
    private String note_content;
    private ArrayList<String> note_path;
    private MyDB myDB;
    private SQLiteDatabase dbReader, dbWriter;
    private EditText etEditNote;
    private GridView gv_pic;
    private NotePicAdapter adapter;
    private Double CurLong, CurLat;
    private Boolean isModify;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, null);


        etEditNote = (EditText) view.findViewById(R.id.etEditNote);
        gv_pic = (GridView) view.findViewById(R.id.gv_edit_note_pic);

        note_path = new ArrayList<String>();

        myDB = new MyDB(getActivity());
        dbReader = myDB.getReadableDatabase();
        dbWriter = myDB.getWritableDatabase();

        Bundle bundle = getArguments();
        if (bundle.getDouble("Long", 360) == 360) {
            isModify = true;
            etEditNote.setText(bundle.getString(BUNDLE_NOTE_CONTENT));
            note_id = bundle.getInt(BUNDLE_NOTE_ID, -1);
            for (int i = 0; i < 3; i++) {
                if (!bundle.getString(BUNDLE_NOTE_PIC_PATH[i], "").equals(""))
                    note_path.add(bundle.getString(BUNDLE_NOTE_PIC_PATH[i]));
                else
                    break;
            }
        } else {
            isModify = false;
            CurLong = bundle.getDouble("Long", 0.0);
            CurLat = bundle.getDouble("Lat", 0.0);
        }

        adapter = new NotePicAdapter(getActivity(), note_path);
        gv_pic.setAdapter(adapter);
        gv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (position == adapter.getCount() - 1) {
                    String[] items = {"拍照", "相册"};
                    new AlertDialog.Builder(getActivity())
                            .setTitle("选择照片")
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent;
                                    switch (which) {
                                        case 0:
                                            ImageUtils.imageUriFromCamera = ImageUtils.createImagePathUri();
                                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
                                            startActivityForResult(intent, ImageUtils.GET_IMAGE_BY_CAMERA);
                                            break;
                                        case 1:
                                            intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(intent, ImageUtils.GET_IMAGE_FROM_PHONE);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            }
        });

        new TitleBuilder(view).setTitleText("游记").setLeftText("返回").setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btn_edit_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need long and lat here

                String content = etEditNote.getText().toString();
                if (!content.equals("")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = simpleDateFormat.format(new Date());
                    note_path = adapter.getPic_uri();
                    ContentValues cv = new ContentValues();
                    if (!isModify) {
                        cv.put(MyDB.COLUMN_NOTE_LONG, CurLong);
                        cv.put(MyDB.COLUMN_NOTE_LAI, CurLat);
                    }
                    cv.put(MyDB.COLUMN_NOTE_CONTENT, content);
                    cv.put(MyDB.COLUMN_NOTE_DATE, date);
                    Log.e("note_path", note_path.size() + "");
                    for (int i = 0; i < note_path.size(); i++) {
                        Log.e("mark", i + "");
                        cv.put(MyDB.COLUMN_NOTE_PIC_PATH[i], note_path.get(i));
                    }
                    Log.e("mark", "mark");
                    if (isModify) {
                        dbWriter.update(MyDB.TABLE_NOTE_NAME, cv, MyDB.COLUMN_NOTE_ID + " = ?", new String[]{note_id + ""});
                        Log.e("note", "update success");
                    } else {
                        dbWriter.insert(MyDB.TABLE_NOTE_NAME, null, cv);
                        Log.e("note", "insert success");
                    }
                    getFragmentManager().popBackStack();
                } else {
                    ToastUtils.showToast("内容不能为空");
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final BmobFile bmobFile;
        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (resultCode == getActivity().RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(getActivity(), ImageUtils.imageUriFromCamera);
                    return;
                }
                adapter.add(ImageUtils.imageUriFromCamera.getPath());
                adapter.notifyDataSetChanged();
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (resultCode != getActivity().RESULT_CANCELED) {
                    adapter.add(data.getData().getPath());
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
