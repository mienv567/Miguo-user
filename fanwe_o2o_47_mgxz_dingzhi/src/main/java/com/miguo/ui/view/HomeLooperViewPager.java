package com.miguo.ui.view;

import android.content.Context;
import android.support.v4.view.LoopViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.fanwe.view.RecyclerScrollView;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by zlh on 2016/12/20.
 */

public class HomeLooperViewPager extends AutoBanner {


    RecyclerScrollView recyclerScrollView;
    ViewGroup ptrFrameLayout;
    String tag = HomeBannerViewPager.class.getSimpleName();
    HomeBannerViewPagerOnTouchListener homeBannerViewPagerOnTouchListener;

    public HomeLooperViewPager(Context context) {
        super(context);
    }

    public HomeLooperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                handlerActionDown(ev);

                break;
            case MotionEvent.ACTION_MOVE:
                handlerActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handlerActionCancel(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ptrFrameLayout != null){
            ptrFrameLayout.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void handlerActionLiveList(){
        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionLiveList();
        }
    }

    public void handlerActionShopList(String cate_id,String tid){
        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionShopList(cate_id,tid);
        }
    }

    public void handlerActionDown(MotionEvent ev){
        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionDown(ev);
        }

        if(ptrFrameLayout != null){
            ptrFrameLayout.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void handlerActionMove(MotionEvent ev){

        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionDown(ev);
        }

        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionMove(ev);
        }

    }

    public void handlerActionCancel(MotionEvent ev){
        if(getHomeBannerViewPagerOnTouchListener() != null){
            getHomeBannerViewPagerOnTouchListener().onActionCancel(ev);
        }

        if(ptrFrameLayout != null){
            ptrFrameLayout.requestDisallowInterceptTouchEvent(false);
        }
    }

    public interface HomeBannerViewPagerOnTouchListener{
        void onActionDown(MotionEvent ev);
        void onActionMove(MotionEvent ev);
        void onActionCancel(MotionEvent ev);
        void onActionLiveList();
        void onActionShopList(String cate_id,String tid);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ptrFrameLayout != null){
            ptrFrameLayout.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public HomeBannerViewPagerOnTouchListener getHomeBannerViewPagerOnTouchListener() {
        return homeBannerViewPagerOnTouchListener;
    }

    public void setHomeBannerViewPagerOnTouchListener(HomeBannerViewPagerOnTouchListener homeBannerViewPagerOnTouchListener) {
        this.homeBannerViewPagerOnTouchListener = homeBannerViewPagerOnTouchListener;
    }

    public void setRecyclerScrollView(RecyclerScrollView recyclerScrollView) {
        this.recyclerScrollView = recyclerScrollView;
    }

    public void setPtrFrameLayout(PtrFrameLayout ptrFrameLayout) {
        this.ptrFrameLayout = ptrFrameLayout;
    }

}
