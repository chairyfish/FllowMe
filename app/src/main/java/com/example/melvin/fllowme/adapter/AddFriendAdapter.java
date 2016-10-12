package com.example.melvin.fllowme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Users;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by Melvin on 2016/8/21.
 */
public class AddFriendAdapter extends BaseAdapter {
    private Context context;
    private List<Users> userses = new ArrayList<Users>();

    public AddFriendAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return userses.size();
    }

    @Override
    public BmobUser getItem(int position) {
        return userses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_add_friend, null);
            convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvUser)));
        }

        ListCell Cell = (ListCell) convertView.getTag();
        TextView tvUser = Cell.getCell();
        tvUser.setText(userses.get(position).getUsername());
        return convertView;
    }

    public void addAll(List<Users> Users) {
        userses = Users;
        notifyDataSetChanged();
    }

    public void clear() {
        userses.clear();
        notifyDataSetChanged();
    }

    private static class ListCell {
        private TextView Cell;

        public ListCell(TextView textView) {
            Cell = textView;
        }

        public TextView getCell() {
            return Cell;
        }
    }


}
