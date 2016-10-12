package com.example.melvin.fllowme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.bean.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melvin on 2016/9/4.
 */
public class NoteAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes = new ArrayList<Note>();

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_note, null);
            convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvContent), (TextView) convertView.findViewById(R.id.tvDate)));
        }

        ListCell Cell = (ListCell) convertView.getTag();
        TextView tvContent = Cell.getTvContent();
        TextView tvDate = Cell.getTvDate();
        tvContent.setText(getItem(position).getCotnent());
        tvDate.setText(getItem(position).getDate());
        return convertView;
    }

    public void clear() {
        notes.clear();
        notifyDataSetChanged();
    }

    private static class ListCell {
        private TextView tvContent, tvDate;

        public ListCell(TextView tvContent, TextView tvDate) {
            this.tvContent = tvContent;
            this.tvDate = tvDate;
        }

        public TextView getTvContent() {
            return tvContent;
        }

        public TextView getTvDate() {
            return tvDate;
        }
    }

}
