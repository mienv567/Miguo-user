package com.miguo.category.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.fanwe.TimeLimitActivity;
import com.fanwe.baidumap.BaiduMapManager;
import com.fanwe.dao.barry.GetSpecialListDao;
import com.fanwe.dao.barry.impl.GetSpecialListDaoImpl;
import com.fanwe.dao.barry.view.GetSpecialListView;
import com.fanwe.home.views.FragmentHomeTimeLimit;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.model.GoodsModel;
import com.fanwe.model.PageModel;
import com.fanwe.model.SpecialListModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.view.FixRequestDisallowTouchEventPtrFrameLayout;
import com.fanwe.view.HomeTuanTimeLimitView;
import com.fanwe.view.RecyclerScrollView;
import com.fanwe.work.AppRuntimeWorker;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.adapter.HomeBannerAdapter;
import com.miguo.app.HiShopDetailActivity;
import com.miguo.fake.HomeBannerFakeData;
import com.miguo.fragment.HiBaseFragment;
import com.miguo.fragment.HomeBannerFragmet;
import com.miguo.listener.fragment.HiHomeFragmentListener;
import com.miguo.live.views.utils.BaseUtils;
import com.miguo.ui.view.HomeADView2;
import com.miguo.ui.view.HomeBannerViewPager;
import com.miguo.ui.view.HomeTagsView;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by by zlh/Barry/狗蛋哥 on 2016/10/13.
 */
public class HiHomeFragmentCategory extends FragmentCategory implements
        PtrHandler,
        RecyclerScrollView.OnRecyclerScrollViewListener,
        GetSpecialListView, HomeTuanTimeLimitView.OnTimeLimitClickListener{

    /**
     * 顶部导航栏的东西
     */
    @ViewInject(R.id.title_layout)
    RelativeLayout titleLayout;

    @ViewInject(R.id.frag_home_title_bar_ll_msg)
    LinearLayout messageLayout;


    @ViewInject(R.id.ptr_layout)
    FixRequestDisallowTouchEventPtrFrameLayout ptrFrameLayout;

    @ViewInject(R.id.recycler_scrollview)
    RecyclerScrollView scrollView;

    /**
     * 狗蛋哥早安部分
     */
    @ViewInject(R.id.top_say_hi_layout)
    LinearLayout topSayHi;
    @ViewInject(R.id.scroll_content)
    LinearLayout scrollContent;

    /**
     * 标题栏空间高度
     */
//    @ViewInject(R.id.title_space)
//    Space titleSpace;

    /**
     * 轮播ViewPager
     */
    @ViewInject(R.id.home_view_pager)
    HomeBannerViewPager homeViewPager;

    HomeBannerAdapter homeBannerAdapter;
    @ViewInject(R.id.indicator_circle)
    CircleIndicator circleIndicator;

    /**
     * 限时特惠
     */
    FragmentHomeTimeLimit timeLimit;
    @ViewInject(R.id.home_tuan)
    HomeTuanTimeLimitView homeTuanTimeLimitView;
    GetSpecialListDao getSpecialListDao;

    /**
     * 标签栏
     */
    @ViewInject(R.id.home_tags_view)
    HomeTagsView homeTagsView;

    /**
     * 广告位2
     */
    @ViewInject(R.id.home_ad_view_2)
    HomeADView2 homeADView2;



    boolean hasTop = true;
    int topHeight = dip2px(150);


    public HiHomeFragmentCategory(View view, HiBaseFragment fragment) {
        super(view, fragment);
    }

    @Override
    protected void initFirst() {
        getSpecialListDao = new GetSpecialListDaoImpl(this);

    }

    @Override
    protected void findFragmentViews() {
        ViewUtils.inject(this, view);
    }

    @Override
    protected void initFragmentListener() {
        listener = new HiHomeFragmentListener(this);
    }

    @Override
    protected void setFragmentListener() {
        messageLayout.setOnClickListener(listener);
    }

    @Override
    protected void init() {
        setTopHeight(dip2px(150));
        setTitleAlpha(titleLayout, 0);
        setTitlePadding(titleLayout);
        initPtrLayout(ptrFrameLayout);
        initBanner();
        initHomeTagsView();
        initHomeADView2();
//        addTimeLimitFragment();
        locationCity();
        onRefresh();
    }

    public void onRefresh(){
        onRefreshTimeLimit();
    }

    /**
     * 首页banner数据
     */
    private void initBanner(){
        ArrayList<Fragment> fragmets = new ArrayList<>();
        List<HomeBannerFakeData.Banner> baners = HomeBannerFakeData.getInstance().getBanner();
        for(int i = 0; i< baners.size(); i++){
            HomeBannerFragmet fragmet = new HomeBannerFragmet();
            Bundle bundle = new Bundle();
            bundle.putSerializable("image", baners.get(i));
            fragmet.setArguments(bundle);
            fragmets.add(fragmet);
        }
        homeBannerAdapter = new HomeBannerAdapter(fragment.getChildFragmentManager(), fragmets);
        homeViewPager.setAdapter(homeBannerAdapter);
        circleIndicator.setViewPager(homeViewPager);
        homeBannerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
    }

    /**
     * 添加限时特惠fragment
     */
    private void addTimeLimitFragment() {
        timeLimit = new FragmentHomeTimeLimit();
        timeLimit.setParent(ptrFrameLayout);
//        new SDFragmentManager(fragment.getChildFragmentManager()).replace(R.id.time_limited_layout, timeLimit);
//        fragment.getChildFragmentManager().beginTransaction().commit();
    }


    /**
     * 首页标签、菜单栏
     */
    private void initHomeTagsView(){
        List list = new ArrayList();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        homeTagsView.init(list);
    }

    private void initHomeADView2(){
        List list = new ArrayList();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        homeADView2.init(list);
    }

    protected void initPtrLayout(PtrFrameLayout ptrFrameLayout) {
        ptrFrameLayout.disableWhenHorizontalMove(true);
        ptrFrameLayout.setEnabledNextPtrAtOnce(false);
        MaterialHeader ptrHead = new MaterialHeader(getActivity());
        ptrHead.setPadding(0, 24, 0, 24);
        ptrFrameLayout.setHeaderView(ptrHead);
        ptrFrameLayout.addPtrUIHandler(ptrHead);
        /**
         * 设置下拉刷新回调
         */
        ptrFrameLayout.setPtrHandler(this);
        scrollView.setOnRecyclerScrollViewListener(this);
    }

    public void onRefreshTimeLimit(){
        try{
            getSpecialListDao.getSpecialList(
                    AppRuntimeWorker.getCity_id(),
                    BaiduMapManager.getInstance().getBDLocation().getLongitude() + "",
                    BaiduMapManager.getInstance().getBDLocation().getLatitude() + "",
                    "0");
        }catch (Exception e){
            getSpecialListDao.getSpecialList(
                    "fc9ebab9-7aa1-49d5-8c56-2bddc7d92ded",
                    "",
                    "",
                    "0");
        }
    }

    /**
     * 定位，城市处理
     */
    private List<GoodsModel> mListModel = new ArrayList<GoodsModel>();
    private List<GoodsModel> pageData_2 = new ArrayList<GoodsModel>();
    private List<GoodsModel> pageData_1 = null;

    PageModel pageModel = new PageModel();

    private void locationCity() {
        BaiduMapManager.getInstance().startLocation(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (mListModel != null) {
                    mListModel.clear();
                }
                if (pageModel != null) {
                    pageModel.resetPage();
                }
                pageData_1 = null;
                if (pageData_2 != null) {
                    pageData_2.clear();
                }
                onRefreshBegin(ptrFrameLayout);
                if (location != null) {
                    dealLocationSuccess();
                }
                BaiduMapManager.getInstance().stopLocation();
            }
        });
    }

    private void dealLocationSuccess() {
        String defaultCity = AppRuntimeWorker.getCity_name();
        if (TextUtils.isEmpty(defaultCity)) {
            return;
        }
        if (!BaiduMapManager.getInstance().hasLocationSuccess()) {
            return;
        }
        String dist = BaiduMapManager.getInstance().getDistrictShort();
        String cityId = AppRuntimeWorker.getCityIdByCityName(dist);
        if (!TextUtils.isEmpty(cityId)) // 区域存在于城市列表中
        {
            if (!dist.equals(defaultCity)) // 区域不是默认的
            {
                showChangeLocationDialog(dist);
            }
        } else {
            String city = BaiduMapManager.getInstance().getCityShort();
            cityId = AppRuntimeWorker.getCityIdByCityName(city);
            if (!TextUtils.isEmpty(cityId)) // 城市存在于城市列表中
            {
                if (!city.equals(defaultCity)) // 城市不是默认的
                {
                    showChangeLocationDialog(city);
                }
            }
        }
    }

    private void showChangeLocationDialog(final String location) {
        new SDDialogConfirm()
                .setTextContent(
                        "当前定位位置为：" + location + "\n" + "是否切换到" + location + "?           ")
                .setmListener(new SDDialogCustom.SDDialogCustomListener() {
                    @Override
                    public void onDismiss(SDDialogCustom dialog) {

                    }

                    @Override
                    public void onClickConfirm(View v, SDDialogCustom dialog) {
                        AppRuntimeWorker.setCity_name(location);
                    }

                    @Override
                    public void onClickCancel(View v, SDDialogCustom dialog) {
                    }
                }).show();
    }
    /** 定位，城市处理 */



    /** scroll view 滚动监听 */
    @Override
    public void onScrollToEnd() {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        checkTop(l, t, oldl, oldt);
        checkTitle(l, t , oldl, oldt);
    }

    private void checkTitle(int l, int t, int oldl, int oldt){
        if(!isHasTop() && t < getTopHeight()){
            float radius = (float)t / getTopHeight();
            setTitleAlpha(titleLayout, radius);
        }
    }

    /** scroll view 滚动监听 emd */


    /**
     * 滚出顶部后移除
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    private void checkTop(int l, int t, int oldl, int oldt){
        if(isHasTop()){
            if(t >= getTopHeight()){
                setHasTop(false);
                scrollContent.removeView(topSayHi);
//                titleSpace.setVisibility(View.VISIBLE);
                scrollView.scrollTo(0,0);
            }
        }
    }


    /** ptr framelayout 下拉刷新监听 */
    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return scrollView.canRefresh();
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }


    /**
     * click method
     */
    public void clickMessage(){
        Intent intent = new Intent(getActivity(), HiShopDetailActivity.class);
        BaseUtils.jumpToNewActivity(getActivity(), intent);

    }

    /** click method end ***/


    /** ptr framelayout 下拉刷新监听 end*/


    /** 获取限时特惠数据回调*/
    @Override
    public void getSpecialListSuccess(final SpecialListModel.Result result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result != null){
//                    homeTuanTimeLimitView.setVisibility(View.GONE);
//                    homeTuanTimeLimitView.removeAllViews();
//                            contentLayout.removeAllViews();
                    if(result.getCount_down() != null){

                        if(result.getCount_down().equals("0")){
                            return;
                        }
                        homeTuanTimeLimitView.setVisibility(View.VISIBLE);
                        homeTuanTimeLimitView.init(result);
                        homeTuanTimeLimitView.setParent(ptrFrameLayout);
                        homeTuanTimeLimitView.setOnTimeLimitClickListener(HiHomeFragmentCategory.this);
                    }
                }
            }
        });
    }

    @Override
    public void getSpecialListLoadmoreSuccess(SpecialListModel.Result result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                homeTuanTimeLimitView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void getSpecialListError(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                homeTuanTimeLimitView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void getSpecialListNoData(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                homeTuanTimeLimitView.setVisibility(View.GONE);
            }
        });
    }
    /** 获取限时特惠数据回调 end*/


    /** 限时特惠点击回调*/
    @Override
    public void onTimeLimitClick() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), TimeLimitActivity.class);
        BaseUtils.jumpToNewActivity(getActivity(), intent);
    }
    /** 限时特惠点击回调 end*/




    public boolean isHasTop() {
        return hasTop;
    }

    public void setHasTop(boolean hasTop) {
        this.hasTop = hasTop;
    }

    /**
     * 还要减掉标题栏的高度
     * @return
     */
    public int getTopHeight() {
        return topHeight;
//        return topHeight - dip2px(50);
    }

    public void setTopHeight(int topHeight) {
        this.topHeight = topHeight;
    }
}