package com.example.melvin.fllowme.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.melvin.fllowme.BaseFragment;
import com.example.melvin.fllowme.R;
import com.example.melvin.fllowme.activity.NoteActivity;
import com.example.melvin.fllowme.activity.RecordMapActivity;
import com.example.melvin.fllowme.bean.Records;
import com.example.melvin.fllowme.bean.RecordsBean;
import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.constants.CommonConstants;
import com.example.melvin.fllowme.utils.ImageLoaderMoments;
import com.example.melvin.fllowme.utils.TitleBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Melvin on 2016/8/11.
 */
public class MomentsFragment extends BaseFragment {
    final List<RecordsBean> RecordsBeanList = new ArrayList();
    RecordsBeanAdapter adapter;
    ListView Youjiquan;
    RecordsBean recordsBean;
    int n = 0;//records条数
    int k = 0;
    int skipnum = 0;
    private String friendId = "";
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
// 在这里可以进行UI操作
                    adapter = new RecordsBeanAdapter(getActivity(), RecordsBeanList, Youjiquan);
                    Youjiquan.setAdapter(adapter);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(activity, R.layout.fragment_moments, null);


        Bundle bundle = getArguments();
        if (bundle == null) {
            new TitleBuilder(view).setTitleText("动态").setLeftText("刷新").setLeftOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipnum = 0;
                    query(CommonConstants.REQUEST_ALL_MOMENTS);
                }
            }).setRightText("发布").setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), NoteActivity.class));
                }
            });
            query(CommonConstants.REQUEST_ALL_MOMENTS);
        } else {
            new TitleBuilder(view).setTitleText(bundle.getString("friendNick"));
            friendId = bundle.getString("friendId");
            query(CommonConstants.REQUEST_SPECIFIC_FRIEND_MOMENTS);
        }
        Youjiquan = (ListView) view.findViewById(R.id.youjiquan);


        Youjiquan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView listView = (ListView) parent;
                RecordsBean recordsBean = (RecordsBean) listView.getItemAtPosition(position);
                String recordid = recordsBean.getId();

                Intent intent = new Intent(getActivity(), RecordMapActivity.class);
                intent.putExtra("recordid", recordid);
                startActivity(intent);
            }
        });


        return view;
    }

    void query(int requsetCode) {
        final BmobQuery<Records> recordsBmobQuery = new BmobQuery<Records>();
        recordsBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        recordsBmobQuery.order("-createdAt");
        //           recordsBmobQuery.addWhereEqualTo("title","上海");
        recordsBmobQuery.setSkip(skipnum);
        recordsBmobQuery.setLimit(4);
        if (requsetCode == CommonConstants.REQUEST_SPECIFIC_FRIEND_MOMENTS) {
            Users friend = new Users();
            friend.setObjectId(friendId);
            recordsBmobQuery.addWhereEqualTo("host", new BmobPointer(friend));
        }
        recordsBmobQuery.findObjects(new FindListener<Records>() {
            @Override
            public void done(final List<Records> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (Records records : list) {
                            Log.i("Records查询", list.size() + " " + records.getAuthor() + records.getTitle());
                            recordsBean = new RecordsBean();
                            recordsBean.author = "作者：" + records.getAuthor();
                            recordsBean.title = records.getTitle();
                            recordsBean.picURL = records.getCoverURL();
                            recordsBean.id = records.getObjectId();
                            RecordsBeanList.add(recordsBean);
                        }
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else {
                    Log.i("Records查询", "失败");
                }
            }
        });
    }


    public class RecordsBeanAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

        private List<RecordsBean> mList;
        private LayoutInflater mInflater;
        private ListView mListView;
        private ImageLoaderMoments imageLoaderMoments;

        private int mStart;
        private int mEnd;
        private boolean isFirstIn, isLastRow;

        public RecordsBeanAdapter(Context context, List<RecordsBean> data, ListView listView) {

            mList = data;
            mInflater = LayoutInflater.from(context);
            mListView = listView;
            isFirstIn = true;
            isLastRow = false;


            imageLoaderMoments = new ImageLoaderMoments(mListView);
            imageLoaderMoments.mUrls = new String[mList.size()];
            for (int i = 0; i < mList.size(); i++) {
                imageLoaderMoments.mUrls[i] = mList.get(i).getPicURL();
                if (imageLoaderMoments.mUrls[0] == null) {
                    Log.i("mUrl", "null");

                }
                //原来是picsmall
            }

            mListView.setOnScrollListener(this);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_moments, null);
                viewHolder.backgroundphoto = (ImageView) convertView.findViewById(R.id.backgroundphoto);

                viewHolder.author = (TextView) convertView.findViewById(R.id.author);
                viewHolder.city = (TextView) convertView.findViewById(R.id.city);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.backgroundphoto.setTag(mList.get(position).getPicURL());


            imageLoaderMoments.showImage(viewHolder.backgroundphoto, mList.get(position).getPicURL());

            viewHolder.author.setText(mList.get(position).getAuthor());
            viewHolder.city.setText(mList.get(position).getTitle());
            return convertView;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            if (scrollState == SCROLL_STATE_IDLE) {
                imageLoaderMoments.loadImages(mStart, mEnd);
            } else {
                imageLoaderMoments.cancelAllAsyncTask();
            }


        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            mStart = firstVisibleItem;
            mEnd = firstVisibleItem + visibleItemCount;

            if (isFirstIn && visibleItemCount > 0) {
                TwoThread tt = new TwoThread();
                tt.start();
                isFirstIn = false;
            }
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                isLastRow = true;
                skipnum = skipnum + 4;

                if (friendId.equals(""))
                    query(CommonConstants.REQUEST_ALL_MOMENTS);
                else
                    query(CommonConstants.REQUEST_SPECIFIC_FRIEND_MOMENTS);
            } else {
                isLastRow = false;
            }

        }

        class ViewHolder {
            ImageView backgroundphoto;
            TextView author;
            TextView city;
        }

        class TwoThread extends Thread {
            public void run() {
                imageLoaderMoments.loadImages(mStart, mEnd);

            }
        }


    }
}