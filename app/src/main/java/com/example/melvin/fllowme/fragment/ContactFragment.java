package com.example.melvin.fllowme.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.AddFriendActivity;
import com.example.melvin.fllowme.activity.FriendInfoActivity;
import com.example.melvin.fllowme.adapter.ContactAdapter;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.utils.ContextUtils;
import com.example.melvin.fllowme.utils.TitleBuilder;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Melvin on 2016/8/11.
 */
public class ContactFragment extends android.support.v4.app.ListFragment {
    private ContactAdapter contactAdapter;
    private View view;
    private Activity activity;
    private ListView contact_list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        view = View.inflate(activity, R.layout.fragment_contact, null);

        contact_list = (ListView) view.findViewById(android.R.id.list);

        Log.e("mark2", "1");

        Log.e("mark2", "3");

        BmobQuery<Users> query = new BmobQuery<Users>();
//        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        Users friend = BmobUser.getCurrentUser(Users.class);
        query.addWhereRelatedTo("contacts", new BmobPointer(friend));
        Log.e("mark2", "4");

        boolean isCache = query.hasCachedResult(Users.class);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }

        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> objects, BmobException e) {
                Log.e("finish", "here is finish");

                if (objects != null) {
                    Log.e("size", objects.size() + "");
                    Log.e("mark2", "5");
                    contactAdapter = new ContactAdapter(activity, objects, contact_list);
                    setListAdapter(contactAdapter);
                    Log.e("mark2", "6");
                }
            }
        });

        new TitleBuilder(view)
                .setTitleText("friends")
                .setLeftText("refresh")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactAdapter.clear();
                        refresh();
                    }
                }).setRightText("add").setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("Add your friends here");

                startActivity(new Intent(ContextUtils.getInstance(), AddFriendActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Users friend = contactAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", friend.getObjectId());
        bundle.putString("account", friend.getUsername());
        bundle.putString("nickname", friend.getNickname());
//        bundle.putBoolean("sex",friend.getSex());
        bundle.putString("headpic_url", friend.getHeadPic());

        Intent i = new Intent(ContextUtils.getInstance(), FriendInfoActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void refresh() {

        BmobQuery<Users> query = new BmobQuery<Users>();
//        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        Users friend = BmobUser.getCurrentUser(Users.class);
        query.addWhereRelatedTo("contacts", new BmobPointer(friend));

        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE

        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> objects, BmobException e) {
                Log.e("finish", "here is finish");

                if (objects != null) {
                    Log.e("size", objects.size() + "");
                    contactAdapter.addAll(objects);
                }
            }
        });
    }

}
