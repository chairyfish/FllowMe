package com.example.melvin.fllowme.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.melvin.fllowme.R;

import java.util.ArrayList;

/**
 * Created by Melvin on 2016/9/6.
 */
public class NotePicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> pic_uri;

    public NotePicAdapter(Context context, ArrayList<String> pic_uri) {
        this.context = context;
        this.pic_uri = pic_uri;
    }

    @Override
    public int getCount() {
        return pic_uri.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return pic_uri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_edit_note_pic, null);
            convertView.setTag(new ListCell((ImageView) convertView.findViewById(R.id.iv_image), (ImageView) convertView.findViewById(R.id.iv_delete_image)));
        }
        ListCell cell = (ListCell) convertView.getTag();
        ImageView iv_img = cell.getIv_img();
        ImageView iv_delete_img = cell.getIv_delete_img();

        if (position < pic_uri.size()) {
            iv_img.setImageURI(Uri.parse(getItem(position)));
            iv_delete_img.setVisibility(View.VISIBLE);

            iv_delete_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pic_uri.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else if (position < 3) {
            iv_img.setImageResource(R.drawable.compose_pic_add_more);
            iv_delete_img.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void add(String path) {
        pic_uri.add(path);
    }

    public ArrayList<String> getPic_uri() {
        return pic_uri;
    }

    private static class ListCell {
        private ImageView iv_img;
        private ImageView iv_delete_img;

        ListCell(ImageView iv_img, ImageView iv_delete_img) {
            this.iv_img = iv_img;
            this.iv_delete_img = iv_delete_img;
        }

        public ImageView getIv_delete_img() {
            return iv_delete_img;
        }

        public ImageView getIv_img() {
            return iv_img;
        }
    }
}
