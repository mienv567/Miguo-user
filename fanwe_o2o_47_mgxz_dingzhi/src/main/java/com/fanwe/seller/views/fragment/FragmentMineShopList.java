package com.fanwe.seller.views.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fanwe.base.CallbackView;
import com.fanwe.fragment.BaseFragment;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.adapters.ShopListAdapter;
import com.fanwe.seller.model.SellerConstants;
import com.fanwe.seller.model.getStoreList.ModelStoreList;
import com.fanwe.seller.presenters.SellerHttpHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 我代言的店铺
 * Created by Administrator on 2016/7/29.
 */
public class FragmentMineShopList extends BaseFragment implements CallbackView {
    private View view;
    private ShopListAdapter mAdapter = null;
    private List<ModelStoreList> mListModel = new ArrayList<>();

    @ViewInject(R.id.ptr_listview_fragment_mine_shop_list)
    private PullToRefreshListView mPtrlvContent = null;

    private SellerHttpHelper sellerHttpHelper;

    int pageSize = 10;
    int pageNum = 1;
    boolean isRefresh = true;
    private int type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setContentView(R.layout.fragment_mine_shop_list);
    }

    @Override
    protected void init() {
        super.init();
        type = getArguments().getInt("type");
        sellerHttpHelper = new SellerHttpHelper(getActivity(), this);
        getData();
        bindDefaultLvData();
        initPullRefreshLv();
        setListener();
    }

    private void setListener() {
        mPtrlvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CurLiveInfo.modelShop = mListModel.get(position - 1);
                //选择当前店铺，进行代言
                getActivity().setResult(8888);
                getActivity().finish();

            }
        });
    }

    private void getData() {
        sellerHttpHelper.getStoreList(pageNum, pageSize, type + "");
    }

    private void initPullRefreshLv() {
        mPtrlvContent.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrlvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                pageNum = 1;
                mListModel.clear();
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = false;
                if (!SDCollectionUtil.isEmpty(mListModel)) {
                    pageNum++;
                }
                getData();
            }
        });
    }

    private void bindDefaultLvData() {
        mAdapter = new ShopListAdapter(mListModel, getActivity());
        mPtrlvContent.setAdapter(mAdapter);
    }

    @Override
    protected String setUmengAnalyticsTag() {
        return this.getClass().getName().toString();
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    private ArrayList<ModelStoreList> temps;

    @Override
    public void onSuccess(String method, List datas) {
        Message message = new Message();
        if (SellerConstants.STORE_LIST.equals(method)) {
            temps = (ArrayList<ModelStoreList>) datas;
            if (!SDCollectionUtil.isEmpty(temps)) {
                mListModel.addAll(temps);
            }
            message.what = 0;
            mHandler.sendMessage(message);
        }

    }

    @Override
    public void onFailue(String responseBody) {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mAdapter.notifyDataSetChanged();
                    mPtrlvContent.onRefreshComplete();
                    break;
            }
        }
    };
}
