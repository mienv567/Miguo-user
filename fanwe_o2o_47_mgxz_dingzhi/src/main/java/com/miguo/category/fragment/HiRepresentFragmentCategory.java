package com.miguo.category.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.Space;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.getBusinessListings.ModelBusinessListings;
import com.fanwe.seller.model.getBusinessListings.ResultBusinessListings;
import com.fanwe.seller.views.SellerFragment;
import com.fanwe.view.LoadMoreRecyclerView;
import com.fanwe.work.AppRuntimeWorker;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.adapter.HiRepresentBannerFragmentAdapter;
import com.miguo.adapter.HiRepresentCateAdapter;
import com.miguo.adapter.HomePagerAdapter;
import com.miguo.adapter.RepresentShopAdapter;
import com.miguo.dao.GetAdspaceListDao;
import com.miguo.dao.GetSearchCateConditionDao;
import com.miguo.dao.GetShopFromParamsDao;
import com.miguo.dao.impl.GetAdspaceListDaoImpl;
import com.miguo.dao.impl.GetSearchCateConditionDaoImpl;
import com.miguo.dao.impl.GetShopFromParamsDaoImpl;
import com.miguo.definition.AdspaceParams;
import com.miguo.definition.ClassPath;
import com.miguo.definition.FilterIndexParams;
import com.miguo.definition.IntentKey;
import com.miguo.definition.PageSize;
import com.miguo.entity.AdspaceListBean;
import com.miguo.entity.BannerTypeModel;
import com.miguo.entity.RepresentFilterBean;
import com.miguo.entity.SearchCateConditionBean;
import com.miguo.entity.SingleMode;
import com.miguo.factory.AdspaceTypeFactory;
import com.miguo.factory.ClassNameFactory;
import com.miguo.factory.SearchCateConditionFactory;
import com.miguo.fragment.HiBaseFragment;
import com.miguo.fragment.HiRepresentCateFragment;
import com.miguo.listener.fragment.HiRepresentFragmentListener;
import com.miguo.model.TouchToMoveListener;
import com.miguo.ui.view.BarryTab;
import com.miguo.ui.view.HomeViewPager;
import com.miguo.ui.view.PtrFrameLayoutForViewPager;
import com.miguo.ui.view.RecyclerBounceNestedScrollView;
import com.miguo.ui.view.RepresentBannerView;
import com.miguo.ui.view.RepresentViewPager;
import com.miguo.ui.view.floatdropdown.helper.DropDownPopHelper;
import com.miguo.ui.view.floatdropdown.interf.OnDropDownListener;
import com.miguo.ui.view.floatdropdown.view.FakeDropDownMenu;
import com.miguo.utils.BaseUtils;
import com.miguo.utils.HomeCategoryUtils;
import com.miguo.view.GetAdspaceListView;
import com.miguo.view.GetSearchCateConditionView;
import com.miguo.view.GetShopFromParamsView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by zlh on 2017/1/5.
 */

public class HiRepresentFragmentCategory extends FragmentCategory implements  RecyclerBounceNestedScrollView.OnRecyclerScrollViewListener, OnDropDownListener, TouchToMoveListener, RepresentBannerView.OnRepresentBannerClickListener{

    @ViewInject(R.id.ptr_layout)
    PtrFrameLayoutForViewPager ptrFrameLayout;

    @ViewInject(R.id.scroll_layout)
    LinearLayout scrollLayout;

    @ViewInject(R.id.search_bar)
    RelativeLayout searchBar;

    @ViewInject(R.id.coorddinatorlayout)
    RecyclerBounceNestedScrollView scrollview;

    @ViewInject(R.id.app_bar)
    LinearLayout appBarLayout;

    @ViewInject(R.id.title_layout)
    RelativeLayout topLayout;

    @ViewInject(R.id.menu)
    FakeDropDownMenu fakeDropDownMenu;

    @ViewInject(R.id.top_menu)
    FakeDropDownMenu topFakeDropDownMenu;

    @ViewInject(R.id.pager)
    RepresentViewPager pager;
    HiRepresentBannerFragmentAdapter bannerAdapter;
    @ViewInject(R.id.indicator_circle)
    CircleIndicator circleIndicator;

    @ViewInject(R.id.represent_banner)
    RepresentBannerView representBannerView;

    @ViewInject(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;
    RepresentShopAdapter shopAdapter;
    RepresentFilterBean filterBean;

    @ViewInject(R.id.space_layput)
    LinearLayout spaceLayout;
    @ViewInject(R.id.empty_data)
    LinearLayout emptyData;

    GetSearchCateConditionDao getSearchCateConditionDao;
    GetAdspaceListDao getAdspaceListDao;
    GetShopFromParamsDao getShopFromParamsDao;

    DropDownPopHelper dropDownPopHelper;

    boolean touchToMove;

    public HiRepresentFragmentCategory(View view, HiBaseFragment fragment) {
        super(view, fragment);
    }

    @Override
    protected void initFirst() {
        filterBean = new RepresentFilterBean();
        shopAdapter = new RepresentShopAdapter(getActivity(), new ArrayList());
    }

    @Override
    protected void findFragmentViews() {
        ViewUtils.inject(this, view);
    }

    @Override
    protected void initFragmentListener() {
        listener = new HiRepresentFragmentListener(this);
    }

    @Override
    protected void setFragmentListener() {
        searchBar.setOnClickListener(listener);
        scrollview.setOnRecyclerScrollViewListener(this);
        representBannerView.setOnRepresentBannerClickListener(this);
    }

    @Override
    protected void init() {
        initPtrLayout(ptrFrameLayout);
        setTitlePadding(topLayout);
        initRecyclerView();
        /**
         * 接口请求
         */
        initSearchCateCondition();
        initAdspaceList();
        initRepresentShop();
        onRefresh();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(shopAdapter);
    }

    private void initDropDownPopHelper(){
        if(dropDownPopHelper == null){
            dropDownPopHelper = new DropDownPopHelper(getActivity(),topFakeDropDownMenu, fakeDropDownMenu );
            dropDownPopHelper.setOnDropDownListener(this);
            fakeDropDownMenu.setOnFakeClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickMenu();
                }
            });
            return;
        }
        updateDropDownHelper();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return isTouchToMove() && scrollview.canRefresh();
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        onRefresh();
    }

    public void onRefresh(){
        filterBean.setPageNum(PageSize.BASE_NUMBER_ONE);
        setCurrentHttpUuid(UUID.randomUUID().toString());
        setTouchToMove(true);
        getSearchCateConditionDao.getSearchCateCondition();
        getAdspaceListDao.getAdspaceList(getCurrentHttpUuid(), AppRuntimeWorker.getCity_id(), AdspaceParams.TYPE_SHOP, AdspaceParams.TERMINAL_TYPE);
        onRefreshShopList();
    }

    public void onRefreshFromCityChanged(){
        filterBean = new RepresentFilterBean();
        onRefresh();
    }

    private void onRefreshShopList(){
        getShopFromParamsDao.getShop(filterBean);
    }

    public void loadComplete(){
        ptrFrameLayout.refreshComplete();
        scrollview.loadComplite();
    }

    public void loadCompleteWithNoData(){
        ptrFrameLayout.refreshComplete();
        scrollview.loadCompliteWithNoData();
    }

    public void loadCompleteWithNetworkError(){
        ptrFrameLayout.refreshComplete();
        scrollview.loadCompliteWithError();
    }

    /**
     *
     * @param index 1/2/3/4
     * @param pair 附近、分类、排序的左右数据id
     * @param items 筛选的数据只有在index为4的时候
     */
    @Override
    public void onItemSelected(int index, Pair<SingleMode, SingleMode> pair, List<SingleMode> items) {
        switch (index){
            case FilterIndexParams.NEAR_BY:
                handleItemSelectNearBy(pair);
                break;
            case FilterIndexParams.CATEGORY:
                handleItemSelectCategory(pair);
                break;
            case FilterIndexParams.INTEL:
                handleItemSelectsIntel(pair);
                break;
            case FilterIndexParams.FILTER:
                handleItemSelectFilter(items);
                break;
        }
        filterBean.setPageNum(PageSize.BASE_NUMBER_ONE);
        onRefreshShopList();
        dropDownPopHelper.dismiss();
    }

    private void handleItemSelectNearBy(Pair<SingleMode, SingleMode> pair){
        filterBean.setAreaOne(isFirstEmpty(pair) ? "" : pair.first.getSingleId());
        filterBean.setAreaTwo(isSecondEmpty(pair) ? "" : pair.second.getSingleId());
    }

    private void handleItemSelectCategory(Pair<SingleMode, SingleMode> pair){
        filterBean.setCategoryOne(isFirstEmpty(pair) ? "" : pair.first.getSingleId());
        filterBean.setCategoryTwo(isSecondEmpty(pair) ? "" : pair.second.getSingleId());
    }

    private void handleItemSelectsIntel(Pair<SingleMode, SingleMode> pair){
        filterBean.setSortType(isFirstEmpty(pair) ? "" : pair.first.getSingleId());
    }

    private void handleItemSelectFilter(List<SingleMode> items){
        String ids = "";
        if(!SDCollectionUtil.isEmpty(items)){
            for(int i = 0; i < items.size(); i++){
                ids = ids + (i == 0 ? "" : ",") + items.get(i).getSingleId();
            }
        }
        filterBean.setFilter(ids);
    }

    private boolean isSecondEmpty(Pair<SingleMode, SingleMode> pair){
        return null == pair.second;
    }

    private boolean isFirstEmpty(Pair<SingleMode, SingleMode> pair){
        return null == pair.first;
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        handleFilterBar(t);
    }

    @Override
    public void onScrollToEnd() {
        onRefreshShopList();
    }

    @Override
    public void onActionCancel(MotionEvent ev) {
        setTouchToMove(true);
    }

    @Override
    public void onActionDown(MotionEvent ev) {
        setTouchToMove(false);
    }

    @Override
    public void onActionMove(MotionEvent ev) {
        setTouchToMove(false);
    }

    @Override
    public void onBannerClick(AdspaceListBean.Result.Body banner) {
        if (null == banner) {
            return;
        }

        if (null == banner.getType() || null == banner.getType_id()) {
            return;
        }

        String type_id = banner.getType_id();
        if (TextUtils.isEmpty(type_id) || !type_id.startsWith("{")) {
            return;
        }

        BannerTypeModel model = HomeCategoryUtils.parseTypeJson(type_id);

        if (banner.getType().equals(AdspaceParams.BANNER_LIVE_LIST)) {
            onActionLiveList();
            return;
        }
        if (banner.getType().equals(AdspaceParams.BANNER_SHOP_LIST)) {
            onActionShopList(model.getCate_id(), banner.getType_id());
            return;
        }
        String paramValue = model.getId();
        if (TextUtils.isEmpty(paramValue)) {
            paramValue = model.getUrl();
        }

        AdspaceTypeFactory.clickWidthType(banner.getType(), getActivity(), paramValue);
    }

    /**
     * 跳转到直播列表
     */
    public void onActionLiveList() {
        getHomeViewPager().setCurrentItem(2);
        /**
         * 如果当前的低栏隐藏了
         */
        if (getTab().getAlpha() == 0) {
//            startTabShowAnimation();
        }
    }

    /**
     * 跳转到门店列表
     */
    public void onActionShopList(String cate_id, String tid) {
        if(cate_id == null || cate_id.equals("")){
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ClassNameFactory.getClass(ClassPath.SECOND_REPRESENT));
        intent.putExtra(IntentKey.FIRST_TYPE, cate_id);
        intent.putExtra(IntentKey.SECOND_TYPE, tid);
        BaseUtils.jumpToNewActivity(getActivity(), intent);
        /**
         * 如果当前的低栏隐藏了
         */
        if (getTab().getAlpha() == 0) {
//            startTabShowAnimation();
        }
    }

    private void handleFilterBar(int t){
        if(t >= scrollLayout.getMeasuredHeight()){
            if(topFakeDropDownMenu.getVisibility() != View.VISIBLE){
                showTopMenu();
            }
            return;
        }
        if(topFakeDropDownMenu.getVisibility() == View.VISIBLE){
            showFakeMenu();
        }
    }

    private void initRepresentShop(){
        getShopFromParamsDao = new GetShopFromParamsDaoImpl(new GetShopFromParamsView() {
            @Override
            public void getShopFromParamsSuccess(List<ModelBusinessListings> results) {
                filterBean.setPageNum(filterBean.getPageNum() + 1);
                shopAdapter.notifyDataSetChanged(results);
                handleInitShopListHeight(results);
                loadComplete();
            }

            @Override
            public void getShopFromParamsLoadMoreSuccess(List<ModelBusinessListings> results) {
                filterBean.setPageNum(filterBean.getPageNum() + 1);
                if(SDCollectionUtil.isEmpty(results)){
                    loadCompleteWithNoData();
                    return;
                }
                shopAdapter.notifyDataSetChangedLoadmore(results);
                loadComplete();
            }

            @Override
            public void getShopFromParamsError(String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadCompleteWithNetworkError();
                    }
                });
            }
        });
    }

    private void handleInitShopListHeight(List<ModelBusinessListings> results){
        spaceLayout.setVisibility(results.size() > 5 ? View.GONE : View.VISIBLE);
        emptyData.setVisibility(results.size() > 0 ? View.GONE : View.VISIBLE);
        updateSpaceHeight(results);
    }

    private void updateSpaceHeight(List<ModelBusinessListings> results){
        int recyclerviewHeight = results.size() * dip2px(120);
        LinearLayout.LayoutParams params = getLineaLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScreenHeight() - fakeDropDownMenu.getMeasuredHeight() - recyclerviewHeight - dip2px(70));
        spaceLayout.setLayoutParams(params);
    }

    /**
     * 获取分类数据
     */
    private void initSearchCateCondition(){
        getSearchCateConditionDao = new GetSearchCateConditionDaoImpl(new GetSearchCateConditionView(){
            @Override
            public void getSearchCateConditionError(String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadComplete();
                    }
                });
            }

            @Override
            public void getSearchCateConditionSuccess(SearchCateConditionBean.ResultBean.BodyBean body) {
                SearchCateConditionFactory.update(body);
                updateCategories();
                initDropDownPopHelper();
                loadComplete();
            }
        });
    }

    /**
     * 获取banner
     */
    private void initAdspaceList(){
        getAdspaceListDao = new GetAdspaceListDaoImpl(new GetAdspaceListView() {
            @Override
            public void getAdspaceListSuccess(String httpUuid, List<AdspaceListBean.Result.Body> body, String type) {
                initRepresentBanner(body);
                scrollview.smoothScrollTo(0, 2);
                loadComplete();
            }

            @Override
            public void getAdspaceListError(String httpUuid) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.smoothScrollTo(0, 2);
                        loadComplete();
                    }
                });

            }
        });
    }

    private void initRepresentBanner(List<AdspaceListBean.Result.Body> body){
        representBannerView.init(body);
    }

    private void initCategories(List<SearchCateConditionBean.ResultBean.BodyBean.CategoryListBean> categories){
        updateCategoryViewPagerParams(categories);
        ArrayList<Fragment> fragments = new ArrayList<>();
        int count = categories.size() / 8 + (categories.size() % 8 > 0 ? 1 : 0);
        for(int i = 0; i < count; i++){
            List<SearchCateConditionBean.ResultBean.BodyBean.CategoryListBean> current = new ArrayList<>();
            int categoryTypeCount = ((i + 1) * 8 <= categories.size() ? 8 : categories.size() - (i * 8));
            for(int j = 0; j < categoryTypeCount; j++){
                current.add(categories.get(i * 8 + j));
            }
            HiRepresentCateFragment fragment = new HiRepresentCateFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentKey.REPRESENT_CATEGORYS, (Serializable) current);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        bannerAdapter = new HiRepresentBannerFragmentAdapter(fragment.getChildFragmentManager(), fragments);
        pager.setAdapter(bannerAdapter);
        circleIndicator.setViewPager(pager);
        circleIndicator.setVisibility(categories.size() <= 8 ? View.GONE : View.VISIBLE);
        pager.setTouchToMoveListener(this);
        pager.setPtrFrameLayout(ptrFrameLayout);
    }

    private void updateCategoryViewPagerParams(List<SearchCateConditionBean.ResultBean.BodyBean.CategoryListBean> categories){
        LinearLayout.LayoutParams params = getLineaLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getCategoryViewPagerHeight(categories));
        pager.setLayoutParams(params);
    }

    public void onResume(){
        if(SearchCateConditionFactory.userChanged()){
            updateCategories();
            updateDropDownHelper();
        }
    }

    private void  updateDropDownHelper(){
        if(null == dropDownPopHelper){
            return;
        }
        dropDownPopHelper.updateData(SearchCateConditionFactory.getHomeRepresent());
        List<Pair<String, String>> pairs = new ArrayList<>();
        Pair<String, String> area = new Pair<>(filterBean.getAreaOne(), filterBean.getAreaTwo());
        Pair<String, String> category = new Pair<>(filterBean.getCategoryOne(), filterBean.getCategoryTwo());
        Pair<String, String> intel = new Pair<>(filterBean.getSortType(), "");
        pairs.add(area);
        pairs.add(category);
        pairs.add(intel);
        String[] filters = filterBean.getFilter().split("\\,");
        if(null != filters){
            for(int i = 0; i<filters.length; i++){
                Pair<String, String> filter = new Pair<>(filters[i], "");
                pairs.add(filter);
            }
        }
        dropDownPopHelper.performMarkIds(pairs);
    }

    public void updateCategories(){
        SearchCateConditionBean.ResultBean.BodyBean bean = SearchCateConditionFactory.getHomeRepresent();
        if(null != bean){
            initCategories(bean.getCategoryList());
        }
    }

    public void clickMenu(){
        scrollview.smoothScrollTo(0, scrollLayout.getMeasuredHeight() + dip2px(14));
        showTopMenu();
    }

    private void showTopMenu(){
        topFakeDropDownMenu.setVisibility(View.VISIBLE);
        fakeDropDownMenu.setVisibility(View.INVISIBLE);
    }

    private void showFakeMenu(){
        topFakeDropDownMenu.setVisibility(View.INVISIBLE);
        fakeDropDownMenu.setVisibility(View.VISIBLE);
    }

    private int getCategoryViewPagerHeight(List<SearchCateConditionBean.ResultBean.BodyBean.CategoryListBean> categories){
        return categories.size() <= 4 ? HiRepresentCateAdapter.getItemHeight() : HiRepresentCateAdapter.getItemHeight() * 2;
    }

    public boolean isTouchToMove() {
        return touchToMove;
    }

    public void setTouchToMove(boolean touchToMove) {
        this.touchToMove = touchToMove;
    }

    public BarryTab getTab() {
        return (BarryTab) getActivity().findViewById(R.id.tab);
    }

    public HomeViewPager getHomeViewPager() {
        return (HomeViewPager) view.getParent();
    }
}
