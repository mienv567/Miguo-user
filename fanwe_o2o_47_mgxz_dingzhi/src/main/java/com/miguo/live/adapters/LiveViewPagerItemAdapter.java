package com.miguo.live.adapters;

import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.base.CallbackView;
import com.fanwe.seller.model.SellerDetailInfo;
import com.miguo.live.views.customviews.PagerBaoBaoView;
import com.miguo.live.views.customviews.PagerMainHostView;
import com.miguo.live.views.customviews.PagerRedPacketView;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItem;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;

import java.lang.ref.WeakReference;


/**
 * Created by didik on 2016/7/28.
 */
public class LiveViewPagerItemAdapter extends PagerAdapter {
    private final ViewPagerItems pages;
    private final SparseArrayCompat<WeakReference<View>> holder;
    private final LayoutInflater inflater;
    /**
     * 门店详情。
     */
    private SellerDetailInfo mSellerDetailInfo;
    private View currentView;
    private CallbackView mCallbackview;
    private PagerRedPacketAdapter mRedPacketAdapter;
    private PagerBaoBaoAdapter mBaobaoAdapter;

    public LiveViewPagerItemAdapter(ViewPagerItems pages, CallbackView mCallbackview, PagerRedPacketAdapter mRedPacketAdapter, PagerBaoBaoAdapter mBaobaoAdapter) {
        this.mRedPacketAdapter = mRedPacketAdapter;
        this.mCallbackview = mCallbackview;
        this.pages = pages;
        this.mBaobaoAdapter = mBaobaoAdapter;
        this.holder = new SparseArrayCompat<>(pages.size());
        this.inflater = LayoutInflater.from(pages.getContext());
    }

    public void init(){

    }
    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getPagerItem(position).initiate(inflater, container);
        switch (position){
            case 0:
                view=new PagerBaoBaoView(container.getContext(),mBaobaoAdapter,mCallbackview);
            break;
            case 1:
                view=new PagerMainHostView(container.getContext(),mCallbackview);
                break;
            case 2:
                view=new PagerRedPacketView(container.getContext(),mCallbackview,mRedPacketAdapter);
                break;
            default:
                break;
        }

        container.addView(view);
        holder.put(position, new WeakReference<View>(view));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        holder.remove(position);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getPagerItem(position).getTitle();
    }

    @Override
    public float getPageWidth(int position) {
        return getPagerItem(position).getWidth();
    }

    public View getPage(int position) {
        final WeakReference<View> weakRefItem = holder.get(position);
        return (weakRefItem != null) ? weakRefItem.get() : null;
    }

    protected ViewPagerItem getPagerItem(int position) {
        return  pages.get(position);


    }
    public void setmSellerDetailInfo(SellerDetailInfo mSellerDetailInfo) {
        this.mSellerDetailInfo = mSellerDetailInfo;
    }
    public void refreshSellerDetailInfo(){
        if(currentView!=null&&currentView instanceof PagerMainHostView ) {
            ((PagerMainHostView)currentView).setmSellerDetailInfo(mSellerDetailInfo);
            ((PagerMainHostView)currentView).onEntityChange();
        }
    }


    public PagerBaoBaoAdapter getmBaobaoAdapter() {
        return mBaobaoAdapter;
    }

    public void setmBaobaoAdapter(PagerBaoBaoAdapter mBaobaoAdapter) {
        this.mBaobaoAdapter = mBaobaoAdapter;
    }
}
