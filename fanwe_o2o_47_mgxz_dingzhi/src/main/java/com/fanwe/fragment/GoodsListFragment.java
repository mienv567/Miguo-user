package com.fanwe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fanwe.HomeSearchActivity;
import com.fanwe.adapter.CategoryBrandListAdapter;
import com.fanwe.adapter.CategoryCateLeftAdapter;
import com.fanwe.adapter.CategoryCateRightAdapter;
import com.fanwe.adapter.CategoryOrderAdapter;
import com.fanwe.adapter.GoodsListAdapter;
import com.fanwe.constant.Constant.SearchTypeNormal;
import com.fanwe.constant.Constant.TitleType;
import com.fanwe.constant.EnumEventTag;
import com.fanwe.library.customview.SD2LvCategoryView;
import com.fanwe.library.customview.SDLvCategoryView;
import com.fanwe.library.customview.SDLvCategoryView.SDLvCategoryViewListener;
import com.fanwe.library.customview.SDViewBase;
import com.fanwe.library.customview.SDViewNavigatorManager;
import com.fanwe.library.title.SDTitleItem;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.model.Brand_listModel;
import com.fanwe.model.GoodsModel;
import com.fanwe.model.PageModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.getClassifyList.ModelClassifyList;
import com.fanwe.seller.model.getShopList.ModelShopListNavs;
import com.fanwe.work.AppRuntimeWorker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.app.HiHomeActivity;
import com.miguo.live.views.customviews.MGToast;
import com.sunday.eventbus.SDBaseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品列表
 *
 * @author Administrator
 */
public class GoodsListFragment extends BaseFragment {

    /**
     * 关键词(String)
     */
    public static String EXTRA_KEY_WORD = "extra_key_word";

    /**
     * 分类id(int)
     */
    public static final String EXTRA_CATE_ID = "extra_cate_id";

    /**
     * 品牌id(int)
     */
    public static final String EXTRA_BID = "extra_bid";

    @ViewInject(R.id.frag_ecshop_list_cv_left)
    private SD2LvCategoryView mCvLeft = null;

    @ViewInject(R.id.frag_ecshop_list_cv_middle)
    private SDLvCategoryView mCvMiddle = null;

    @ViewInject(R.id.frag_ecshop_list_cv_right)
    private SDLvCategoryView mCvRight = null;

    @ViewInject(R.id.frag_ecshop_list_ll_empty)
    private LinearLayout mLlEmpty = null;

    @ViewInject(R.id.frag_ecshop_list_ll_current_search)
    private LinearLayout mLlCurrentSearch = null;

    @ViewInject(R.id.frag_ecshop_list_tv_current_keyword)
    private TextView mTvCurrentKeyword = null;

    @ViewInject(R.id.frag_ecshop_list_ptrlv_content)
    private PullToRefreshListView mPtrlvContent = null;

    private GoodsListAdapter mAdapter = null;
    private List<GoodsModel> mListModel = new ArrayList<GoodsModel>();

    private SDViewNavigatorManager mViewManager = new SDViewNavigatorManager();

    private boolean mIsFirstBindCategoryViewData = true;
    private boolean mIsNeedUpdateBrandList = false;

    // ===============提交服务器参数
    /**
     * 商品分类ID
     */
    private String cate_id;
    /**
     * 品牌ID
     */
    private int bid;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 排序类型
     */
    private String order_type;

    private PageModel mPage = new PageModel();

    private boolean isShow = false;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setmTitleType(TitleType.TITLE);
        return setContentView(R.layout.frag_goods_list);
    }

    @Override
    protected void init() {
        super.init();
        getIntentData();
        initTitle();
        bindDefaultLvData();
        initCategoryView();
        initCategoryViewNavigatorManager();
        registeClick();
        initPullRefreshLv();
    }

    private void initTitle() {
        String title = SDResourcesUtil.getString(R.string.mall);
        String city = AppRuntimeWorker.getCity_name();
        if (!TextUtils.isEmpty(city)) {
            title = title + " - " + city;
        }

        mTitle.setMiddleTextTop(title);

        if (getActivity() instanceof HiHomeActivity) {
            mTitle.setLeftImageLeft(0);
        } else {
            mTitle.setLeftImageLeft(R.drawable.ic_arrow_left_white);
        }

        mTitle.initRightItem(1);
        mTitle.getItemRight(0).setImageLeft(R.drawable.ic_search_home_top);
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {
        Intent intent = new Intent(getActivity(), HomeSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(HomeSearchActivity.EXTRA_SEARCH_TYPE, SearchTypeNormal.SHOP);
        startActivity(intent);
    }

    private void getIntentData() {
        Intent i = getActivity().getIntent();

        keyword = i.getStringExtra(EXTRA_KEY_WORD);
        cate_id = i.getStringExtra(EXTRA_CATE_ID);
        bid = i.getIntExtra(EXTRA_BID, 0);
        if (TextUtils.isEmpty(keyword)) {
            mLlCurrentSearch.setVisibility(View.GONE);
        } else {
            mLlCurrentSearch.setVisibility(View.VISIBLE);
            mTvCurrentKeyword.setText(keyword);
        }

    }

    private void initPullRefreshLv() {
        mPtrlvContent.setMode(Mode.BOTH);
        mPtrlvContent.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage.resetPage();
                requestData(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!mPage.increment()) {
                    MGToast.showToast("没有更多数据了");
                    mPtrlvContent.onRefreshComplete();
                } else {
                    requestData(true);
                }
            }
        });
        mPtrlvContent.setRefreshing();
    }

    private void bindDefaultLvData() {
        mAdapter = new GoodsListAdapter(mListModel, getActivity());
        mPtrlvContent.setAdapter(mAdapter);
    }

    private void initCategoryViewNavigatorManager() {
        SDViewBase[] items = new SDViewBase[]{mCvLeft, mCvMiddle, mCvRight};
        mViewManager.setItems(items);
        mViewManager.setmMode(SDViewNavigatorManager.Mode.CAN_NONE_SELECT);
    }

    private void initCategoryView() {

        // 左边分类view
        mCvLeft.getmAttr().setmBackgroundDrawableNormalResId(R.drawable.bg_choosebar_press_down);
        mCvLeft.getmAttr().setmBackgroundDrawableSelectedResId(R.drawable.bg_choosebar_press_up);

        mCvLeft.getmAttr().setmTextColorNormalResId(R.color.text_item_content);
        mCvLeft.getmAttr().setmTextColorSelectedResId(R.color.main_color);
        mCvLeft.setmListener(new SD2LvCategoryView.SD2LvCategoryViewListener() {

            @Override
            public void onRightItemSelect(int leftIndex, int rightIndex, Object leftModel, Object rightModel) {
                ModelClassifyList right = (ModelClassifyList) rightModel;
                String tempId = right.getId();
                if (!cate_id.equals(tempId)) {
                    mIsNeedUpdateBrandList = true;
                }
                cate_id = tempId;
                mPtrlvContent.setRefreshing();
            }

            @Override
            public void onLeftItemSelect(int leftIndex, Object leftModel, boolean isNotifyDirect) {
                if (isNotifyDirect) {
                    ModelClassifyList left = (ModelClassifyList) leftModel;
                    ModelClassifyList right = SDCollectionUtil.get(left.getBcate_type(), 0);
                    String tempId = "";
                    if (right != null) {
                        tempId = right.getId();
                    }
                    if (TextUtils.isEmpty(tempId)) {
                        tempId = left.getId();
                    }
                    if (cate_id != tempId) {
                        mIsNeedUpdateBrandList = true;
                    }
                    cate_id = tempId;
                    mPtrlvContent.setRefreshing();
                }
            }
        });

        // 中间分类view
        mCvMiddle.getmAttr().setmBackgroundDrawableNormalResId(R.drawable.bg_choosebar_press_down_2);
        mCvMiddle.getmAttr().setmBackgroundDrawableSelectedResId(R.drawable.bg_choosebar_press_up_2);

        mCvMiddle.getmAttr().setmTextColorNormalResId(R.color.text_item_content);
        mCvMiddle.getmAttr().setmTextColorSelectedResId(R.color.main_color);
        mCvMiddle.setmListener(new SDLvCategoryViewListener() {
            @Override
            public void onItemSelect(int index, Object model) {
                Brand_listModel brandListModel = (Brand_listModel) model;
                bid = brandListModel.getId();
                mPtrlvContent.setRefreshing();
            }
        });

        // 右边分类view
        mCvRight.getmAttr().setmBackgroundDrawableNormalResId(R.drawable.bg_choosebar_press_down_3);
        mCvRight.getmAttr().setmBackgroundDrawableSelectedResId(R.drawable.bg_choosebar_press_up_3);

        mCvRight.getmAttr().setmTextColorNormalResId(R.color.text_item_content);
        mCvRight.getmAttr().setmTextColorSelectedResId(R.color.main_color);
        mCvRight.setmListener(new SDLvCategoryViewListener() {
            @Override
            public void onItemSelect(int index, Object model) {
                if (model instanceof ModelShopListNavs) {
                    ModelShopListNavs orderModel = (ModelShopListNavs) model;
                    order_type = orderModel.getCode();
                    mPtrlvContent.setRefreshing();
                }
            }
        });

    }

    /**
     * 请求商品列表接口
     *
     * @param isLoadMore
     */
    private void requestData(final boolean isLoadMore) {

    }

    protected void dealFinishRequest() {
        mPtrlvContent.onRefreshComplete();
        SDViewUtil.toggleEmptyMsgByList(mListModel, mLlEmpty);
    }

    private void bindLeftCategoryViewData(List<ModelClassifyList> listModel) {
        if (!SDCollectionUtil.isEmpty(listModel)) {
            int[] arrIndex = ModelClassifyList.findIndex(cate_id, listModel);
            int leftIndex = arrIndex[0];
            int rightIndex = arrIndex[1];

            ModelClassifyList leftModel = listModel.get(leftIndex);
            List<ModelClassifyList> listRight = leftModel.getBcate_type();

            CategoryCateLeftAdapter adapterLeft = new CategoryCateLeftAdapter(listModel, getActivity());
            adapterLeft.setmDefaultIndex(leftIndex);

            CategoryCateRightAdapter adapterRight = new CategoryCateRightAdapter(listRight, getActivity());
            adapterRight.setmDefaultIndex(rightIndex);

            mCvLeft.setLeftAdapter(adapterLeft);
            mCvLeft.setRightAdapter(adapterRight);
            mCvLeft.setAdapterFinish();
        }
    }

    private void bindMiddleCategoryViewData(List<Brand_listModel> listModel) {
        int index = Brand_listModel.findIndex(bid, listModel);

        CategoryBrandListAdapter adapter = new CategoryBrandListAdapter(listModel, getActivity());
        adapter.setmDefaultIndex(index);

        mCvMiddle.setAdapter(adapter);
        mIsNeedUpdateBrandList = false;
    }

    private void bindRightCategoryViewData(List<ModelShopListNavs> listOrderModel) {
        if (!SDCollectionUtil.isEmpty(listOrderModel)) {
            CategoryOrderAdapter adapter = new CategoryOrderAdapter(listOrderModel, getActivity());
            mCvRight.setAdapter(adapter);
        }
    }

    @Override
    public void onEventMainThread(SDBaseEvent event) {
        super.onEventMainThread(event);
        switch (EnumEventTag.valueOf(event.getTagInt())) {
            case CITY_CHANGE:
                initTitle();
                mPtrlvContent.setRefreshing();
                break;

            default:
                break;
        }
    }

    private void registeClick() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

    @Override
    protected String setUmengAnalyticsTag() {
        return this.getClass().getName().toString();
    }
}