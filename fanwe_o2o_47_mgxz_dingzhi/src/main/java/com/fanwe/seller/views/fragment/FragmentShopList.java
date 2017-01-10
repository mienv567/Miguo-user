package com.fanwe.seller.views.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.base.CallbackView;
import com.fanwe.base.PageBean;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.adapters.SellerListAdapter;
import com.fanwe.seller.model.SellerConstants;
import com.fanwe.seller.model.getBusinessListings.ModelBusinessListings;
import com.fanwe.seller.presenters.SellerHttpHelper;
import com.fanwe.seller.util.CollectionUtils;
import com.fanwe.utils.DataFormat;
import com.miguo.groupon.listener.IDataInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家列表
 * Created by qiang.chen on 2017/1/5.
 */
public class FragmentShopList extends Fragment implements CallbackView {
    private RecyclerView recyclerView;
    private boolean isRefresh = true;
    private int pageNum = 1;
    private int pageSize = 10;
    private boolean isLoadingMore;
    private SellerHttpHelper sellerHttpHelper;
    private List<ModelBusinessListings> datas = new ArrayList<>();
    private SellerListAdapter mSellerListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_sell_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_frag_sell_list);
        setView();
        setListener();
        return view;
    }

    private void setListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                //剩下1个item自动加载，各位自由选择 dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void getData() {
        if (sellerHttpHelper == null) {
            sellerHttpHelper = new SellerHttpHelper(getActivity(), this);
        }
        sellerHttpHelper.getShopSearch("", "", "", "", "", "", "", pageNum, pageSize, "1");
    }

    LinearLayoutManager mLayoutManager;

    private void setView() {
        mSellerListAdapter = new SellerListAdapter(getActivity(), datas);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mSellerListAdapter);
    }

    public void setData(List<ModelBusinessListings> models) {
        datas.clear();
        datas.addAll(models);
        if (mSellerListAdapter != null)
            mSellerListAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        isRefresh = true;
        pageNum = 1;
        getData();
    }

    /**
     * 加载更多数据
     */
    public void loadMore() {
        if (!canLoadMore()) {
            return;
        }
        isRefresh = false;
        if (!SDCollectionUtil.isEmpty(items)) {
            pageNum++;
        }
        getData();
    }

    /**
     * 判断能不能继续加载更多
     *
     * @return
     */
    private boolean canLoadMore() {
        if (pageBean != null && CollectionUtils.isValid(datas) && DataFormat.toInt(pageBean.getData_total()) <= datas.size()) {
            return false;
        }
        //TODO for test
        if (CollectionUtils.isValid(datas) && datas.size() > 20) {
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    private List<ModelBusinessListings> items;
    private PageBean pageBean;

    @Override
    public void onSuccess(String method, List datas) {
        Message message = new Message();
        if (SellerConstants.SHOP_SEARCH.equals(method)) {
            //店铺
            items = datas;
            message.what = 0;
        }
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isLoadingMore = false;
                    if (isRefresh) {
                        datas.clear();
                    }
                    if (!SDCollectionUtil.isEmpty(items)) {
                        datas.addAll(items);
                    }
                    mSellerListAdapter.notifyDataSetChanged();
                    if (mIDataInterface != null) {
                        mIDataInterface.verifyData(CollectionUtils.isValid(datas));
                        //TODO for test
                        if (CollectionUtils.isValid(datas)) {
                            if (datas.size() > 20) {
                                mIDataInterface.verifyData(false);
                            }
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {

    }

    IDataInterface mIDataInterface;

    public void setIDataInterface(IDataInterface iDataInterface) {
        mIDataInterface = iDataInterface;
    }
}
