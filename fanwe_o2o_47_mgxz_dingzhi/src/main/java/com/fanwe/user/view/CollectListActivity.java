package com.fanwe.user.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fanwe.BaseActivity;
import com.fanwe.base.CallbackView2;
import com.fanwe.constant.Constant;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.user.UserConstants;
import com.fanwe.user.adapters.CollectListAdapter;
import com.fanwe.user.model.getShopAndUserCollect.ModelShopAndUserCollect;
import com.fanwe.user.presents.UserHttpHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.miguo.live.views.customviews.MGToast;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏列表
 * Created by Administrator on 2016/9/12.
 */
public class CollectListActivity extends BaseActivity implements CallbackView2 {
    private Context mContext = CollectListActivity.this;
    private UserHttpHelper userHttpHelper;
    private PullToRefreshListView ptrl;
    private List<ModelShopAndUserCollect> datas = new ArrayList<>();
    private CollectListAdapter mCollectListAdapter;
    private boolean isRefresh = true;
    private int pageNum = 1;
    private int pageSize = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setmTitleType(Constant.TitleType.TITLE);
        setContentView(R.layout.act_collect_list);
        mTitle.setMiddleTextTop("我的收藏");
        preWidget();
        setListener();
        userHttpHelper = new UserHttpHelper(this, this);
        preData();
    }

    private void getData() {
        userHttpHelper.getShopAndUserCollect(pageNum, pageSize);
    }

    private void preData() {
        ModelShopAndUserCollect bean;
        for (int i = 1; i < 10; i++) {
            bean = new ModelShopAndUserCollect();
            datas.add(bean);
        }

        mCollectListAdapter = new CollectListAdapter(mContext, getLayoutInflater(), datas);
        ptrl.setAdapter(mCollectListAdapter);

    }

    private void setListener() {
    }

    private void preWidget() {
        ptrl = (PullToRefreshListView) findViewById(R.id.ptrl_act_collect);
        ptrl.setMode(PullToRefreshBase.Mode.BOTH);
        ptrl.setOnRefreshListener(mOnRefresherListener2);
        ptrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long positionL) {
                position--;
                ModelShopAndUserCollect item = datas.get(position);
                MGToast.showToast(item.getNick());
            }
        });
//        ptrl.setRefreshing();
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefresherListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(
                PullToRefreshBase<ListView> refreshView) {
            isRefresh = true;
            pageNum = 1;
            getData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            isRefresh = false;
            if (!SDCollectionUtil.isEmpty(items)) {
                pageNum++;
            }
            getData();
        }
    };

    @Override
    public void onSuccess(String responseBody) {

    }

    List<ModelShopAndUserCollect> items;

    @Override
    public void onSuccess(String method, List datas) {
        Message msg = new Message();
        if (UserConstants.ATTENTION_FOCUS.equals(method)) {
            items = datas;
            msg.what = 0;
        }
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (isRefresh) {
                        datas.clear();
                    }
                    if (!SDCollectionUtil.isEmpty(items)) {
                        datas.addAll(items);
                    }
                    mCollectListAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    ptrl.onRefreshComplete();
                    break;
            }
        }
    };

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {
        Message msg = new Message();
        msg.what = 1;
        mHandler.sendMessage(msg);
    }
}