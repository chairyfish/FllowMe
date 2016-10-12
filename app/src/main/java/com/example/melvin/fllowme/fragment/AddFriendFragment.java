package com.example.melvin.fllowme.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.adapter.AddFriendAdapter;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Melvin on 2016/8/21.
 */
public class AddFriendFragment extends android.app.ListFragment {
    private AddFriendAdapter addFriendAdapter;
    private EditText etSearchId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, null);

        addFriendAdapter = new AddFriendAdapter(getActivity());
        setListAdapter(addFriendAdapter);

        etSearchId = (EditText) view.findViewById(R.id.etSearchId);

        view.findViewById(R.id.btnSearchId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<Users> query = new BmobQuery<Users>();
                query.addWhereEqualTo("username", etSearchId.getText().toString());
                query.findObjects(new FindListener<Users>() {
                    @Override
                    public void done(List<Users> object, BmobException e) {
                        if (e == null) {
                            if (object.size() > 0) {
                                addFriendAdapter.addAll(object);
                            } else
                                ToastUtils.showToast("没有匹配的用户");
                        } else {
                            ToastUtils.showToast("更新用户信息失败:" + e.getMessage());
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Users user = (Users) addFriendAdapter.getItem(position);

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        DetailInfoFragment fragment = new DetailInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", user.getObjectId());
        bundle.putString("username", user.getUsername());
        bundle.putString("nickname", user.getNickname());
        bundle.putString("headpic_url", user.getHeadPic());
        fragment.setArguments(bundle);
        ft.addToBackStack(null).replace(R.id.container, fragment).commit();
    }
}
