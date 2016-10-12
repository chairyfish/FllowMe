package com.example.melvin.fllowme.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.database.MyDB;
import com.example.melvin.fllowme.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melvin on 2016/8/19.
 */
public class ContactAdapter extends BaseAdapter {
    boolean isFirstIn;
    private Context context;
    private Cursor cursor;
    private MyDB myDB;
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;
    private List<Users> contacts = new ArrayList<Users>();
    private ListView listView;
    private ImageLoader imageLoader;
    private int mStart, mEnd;

    public ContactAdapter(Context context, List<Users> contacts, ListView listView) {
        this.context = context;
        this.listView = listView;
        this.contacts = contacts;

        isFirstIn = true;

        //get the data in db
        myDB = new MyDB(context);
        dbReader = myDB.getReadableDatabase();
        dbWriter = myDB.getWritableDatabase();
        cursor = dbReader.query(MyDB.TABLE_CONTACT_NAME, new String[]{MyDB.COLUMN_CONTACT_FRIEND, MyDB.COLUMN_CONTACT_REMARK}, null, null, null, null, null);
        imageLoader = new ImageLoader(this.listView, contacts);
        this.listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    imageLoader.loadImages(mStart, mEnd);
                } else {
                    imageLoader.cancelAllAsyncTask();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                mStart = firstVisibleItem;
                mEnd = firstVisibleItem + visibleItemCount;

                if (isFirstIn && visibleItemCount > 0) {
                    imageLoader.loadImages(mStart, mEnd);
                    isFirstIn = false;
                }
            }
        });
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Users getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_contact, null);
            convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvContact), (ImageView) convertView.findViewById(R.id.iv_friend_photo)));
        }

        ListCell Cell = (ListCell) convertView.getTag();
        TextView tvContact = Cell.getCell_tv();
        ImageView ivContact = Cell.getCell_iv();
        Users friend = contacts.get(position);
        ivContact.setTag(friend.getHeadPic());

        cursor = dbReader.query(MyDB.TABLE_CONTACT_NAME, new String[]{MyDB.COLUMN_CONTACT_REMARK}, MyDB.COLUMN_CONTACT_FRIEND + "= ? ", new String[]{friend.getUsername()}, null, null, null);

        if (cursor.moveToFirst()) {
            tvContact.setText(cursor.getString(cursor.getColumnIndex(MyDB.COLUMN_CONTACT_REMARK)));
        } else
            tvContact.setText(friend.getNickname());

        ImageLoader.showImage(ivContact, friend.getHeadPic());

        return convertView;
    }

    public void addAll(List<Users> Users) {
        contacts = Users;
        notifyDataSetChanged();
    }

    public void clear() {
        contacts.clear();
        notifyDataSetChanged();
    }

    private static class ListCell {
        private TextView Cell_tv;
        private ImageView Cell_iv;

        public ListCell(TextView textView, ImageView imageView) {
            Cell_tv = textView;
            Cell_iv = imageView;
        }

        public TextView getCell_tv() {
            return Cell_tv;
        }

        public ImageView getCell_iv() {
            return Cell_iv;
        }
    }


}
