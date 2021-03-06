package com.fanwe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.fanwe.HomeSearchActivity;
import com.fanwe.MapSearchActivity;
import com.fanwe.YouHuiDetailActivity;
import com.fanwe.adapter.YouHuiListAdapter;
import com.fanwe.baidumap.BaiduMapManager;
import com.fanwe.constant.Constant.SearchTypeMap;
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
import com.fanwe.model.PageModel;
import com.fanwe.model.YouhuiModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.getBusinessCircleList.ModelBusinessCircleList;
import com.fanwe.seller.model.getClassifyList.ModelClassifyList;
import com.fanwe.seller.model.getShopList.ModelShopListNavs;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.live.views.customviews.MGToast;
import com.sunday.eventbus.SDBaseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠列表界面
 *
 * @author js02
 */
public class YouHuiListFragment extends BaseFragment {

    /**
     * 大分类id(int)
     */
    public static final String EXTRA_CATE_ID = "extra_cate_id";

    /**
     * 小分类id(int)
     */
    public static final String EXTRA_TID = "extra_tid";

    /**
     * 商圈id(int)
     */
    public static final String EXTRA_QID = "extra_qid";

    /**
     * 关键字(String)
     */
    public static final String EXTRA_KEY_WORD = "extra_key_word";

    @ViewInject(R.id.lcv_left)
    private SD2LvCategoryView mCvLeft = null;

    @ViewInject(R.id.lcv_middle)
    private SD2LvCategoryView mCvMiddle = null;

    @ViewInject(R.id.lcv_right)
    private SDLvCategoryView mCvRight = null;

    @ViewInject(R.id.ll_empty)
    private LinearLayout mLlEmpty = null;

    @ViewInject(R.id.ll_current_location)
    private LinearLayout mLlCurrentLocation = null;

    @ViewInject(R.id.tv_current_address)
    private TextView mTvAddress = null;

    @ViewInject(R.id.iv_location)
    private ImageView mIvLocation = null;

    @ViewInject(R.id.ll_current_search)
    private LinearLayout mLlCurrentSearch = null;

    @ViewInject(R.id.tv_current_keyword)
    private TextView mTvCurrentKeyword = null;

    @ViewInject(R.id.ptrlv_content)
    private PullToRefreshListView mPtrlvContent = null;

    private YouHuiListAdapter mAdapter = null;
    private List<YouhuiModel> mListModel = new ArrayList<YouhuiModel>();
    private SDViewNavigatorManager mViewManager = new SDViewNavigatorManager();

    private boolean mIsFirstBindCategoryViewData = true;

    // ====================提交服务端参数
    /**
     * 大分类id
     */
    private String cate_id;
    /**
     * 小分类id
     */
    private String tid;
    /**
     * 商圈id
     */
    private String qid;
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 排序类型
     */
    private String order_type;

    private PageModel mPage = new PageModel();

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setmTitleType(TitleType.TITLE);
        return setContentView(R.layout.frag_tuan_list);
    }

    @Override
    protected void init() {
        super.init();
        initTitle();
        getIntentData();
        bindDefaultLvData();
        bindLocationData();
        initCategoryView();
        initCategoryViewNavigatorManager();
        registeClick();
        initPullRefreshLv();

    }

    private void bindLocationData() {
        String addrShort = BaiduMapManager.getInstance().getCurAddressShort();
        if (TextUtils.isEmpty(addrShort)) {
            locationAddress();
        }
    }

    private void initTitle() {

        mTitle.setMiddleTextTop(SDResourcesUtil.getString(R.string.youhui_coupon_list));

        mTitle.initRightItem(2);
        mTitle.getItemRight(0).setImageLeft(R.drawable.ic_location_home_top);
        mTitle.getItemRight(1).setImageLeft(R.drawable.ic_search_home_top);
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {
        Intent intent;
        switch (index) {
            case 0:
                intent = new Intent(getActivity(), MapSearchActivity.class);
                intent.putExtra(MapSearchActivity.EXTRA_SEARCH_TYPE, SearchTypeMap.YOU_HUI);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getActivity(), HomeSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(HomeSearchActivity.EXTRA_SEARCH_TYPE, SearchTypeNormal.YOU_HUI);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**
     * 从intent获取数据
     */
    private void getIntentData() {
        Intent intent = getActivity().getIntent();
        cate_id = intent.getStringExtra(EXTRA_CATE_ID);
        tid = intent.getStringExtra(EXTRA_TID);
        qid = intent.getStringExtra(EXTRA_QID);
        keyword = intent.getStringExtra(EXTRA_KEY_WORD);

        if (TextUtils.isEmpty(keyword)) {
            mLlCurrentLocation.setVisibility(View.VISIBLE);
            mLlCurrentSearch.setVisibility(View.GONE);
            if (BaiduMapManager.getInstance().getCurAddress() != null) {
                mTvAddress.setText(BaiduMapManager.getInstance().getCurAddressShort());
            }
        } else {
            mLlCurrentLocation.setVisibility(View.GONE);
            mLlCurrentSearch.setVisibility(View.VISIBLE);
            mTvCurrentKeyword.setText(keyword);
        }
    }

    /**
     * 初始化下拉刷新控件
     */
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

        mPtrlvContent.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YouhuiModel model = mAdapter.getItem((int) id);
                if (model != null) {
                    Intent intent = new Intent(getActivity(), YouHuiDetailActivity.class);
                    intent.putExtra(YouHuiDetailActivity.EXTRA_YOUHUI_ID, model.getId());
                    startActivity(intent);
                }

            }
        });

        mPtrlvContent.setRefreshing();
    }

    private void bindDefaultLvData() {
        mAdapter = new YouHuiListAdapter(mListModel, getActivity());
        mPtrlvContent.setAdapter(mAdapter);
    }

    private void initCategoryViewNavigatorManager() {
        SDViewBase[] items = new SDViewBase[]{mCvLeft, mCvMiddle, mCvRight};
        mViewManager.setItems(items);
        mViewManager.setmMode(SDViewNavigatorManager.Mode.CAN_NONE_SELECT);
    }

    private void initCategoryView() {
        mCvLeft.getmAttr().setmBackgroundDrawableNormalResId(R.drawable.bg_choosebar_press_down);
        mCvLeft.getmAttr().setmBackgroundDrawableSelectedResId(R.drawable.bg_choosebar_press_up);

        mCvLeft.getmAttr().setmTextColorNormalResId(R.color.text_item_content);
        mCvLeft.getmAttr().setmTextColorSelectedResId(R.color.main_color);
        mCvLeft.setmListener(new SD2LvCategoryView.SD2LvCategoryViewListener() {

            @Override
            public void onRightItemSelect(int leftIndex, int rightIndex, Object leftModel, Object rightModel) {
                ModelClassifyList left = (ModelClassifyList) leftModel;
                ModelClassifyList right = (ModelClassifyList) rightModel;
                cate_id = left.getId();
                tid = right.getId();
                mPtrlvContent.setRefreshing();
            }

            @Override
            public void onLeftItemSelect(int leftIndex, Object leftModel, boolean isNotifyDirect) {
                if (isNotifyDirect) {
                    ModelClassifyList left = (ModelClassifyList) leftModel;
                    ModelClassifyList right = SDCollectionUtil.get(left.getBcate_type(), 0);
                    cate_id = left.getId();
                    if (right != null) {
                        tid = right.getId();
                    } else {
                        tid = "";
                    }
                    mPtrlvContent.setRefreshing();
                }
            }
        });

        mCvMiddle.getmAttr().setmBackgroundDrawableNormalResId(R.drawable.bg_choosebar_press_down_2);
        mCvMiddle.getmAttr().setmBackgroundDrawableSelectedResId(R.drawable.bg_choosebar_press_up_2);

        mCvMiddle.getmAttr().setmTextColorNormalResId(R.color.text_item_content);
        mCvMiddle.getmAttr().setmTextColorSelectedResId(R.color.main_color);
        mCvMiddle.setmListener(new SD2LvCategoryView.SD2LvCategoryViewListener() {

            @Override
            public void onRightItemSelect(int leftIndex, int rightIndex, Object leftModel, Object rightModel) {
                ModelBusinessCircleList right = (ModelBusinessCircleList) rightModel;
                qid = right.getId();
                mPtrlvContent.setRefreshing();
            }

            @Override
            public void onLeftItemSelect(int leftIndex, Object leftModel, boolean isNotifyDirect) {
                if (isNotifyDirect) {
                    ModelBusinessCircleList left = (ModelBusinessCircleList) leftModel;
                    ModelBusinessCircleList right = SDCollectionUtil.get(left.getQuan_sub(), 0);
                    if (right != null) {
                        qid = right.getId();
                    }
                    if (TextUtils.isEmpty(qid)) {
                        qid = left.getId();
                    }
                    mPtrlvContent.setRefreshing();
                }
            }
        });

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

    private void requestData(final boolean isLoadMore) {

    }

    private void registeClick() {
        mIvLocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_location:
                clickTv_locaiton();
                break;

            default:
                break;
        }
    }

    private void clickTv_locaiton() {
        locationAddress();
    }

    /**
     * 定位地址
     */
    private void locationAddress() {
        // 开始定位
        setCurrentLocation("定位中", false);
        BaiduMapManager.getInstance().startLocation(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location != null) {
                    setCurrentLocation(BaiduMapManager.getInstance().getCurAddressShort(), true);
                }
                BaiduMapManager.getInstance().stopLocation();
            }
        });
    }

    private void setCurrentLocation(String string, boolean isLocationSuccess) {
        if (!TextUtils.isEmpty(string)) {
            if (mTvAddress != null) {
                mTvAddress.setText(string);
                if (isLocationSuccess) {
                    mPtrlvContent.setRefreshing();
                }
            }
        }
    }

    @Override
    public void onEventMainThread(SDBaseEvent event) {
        super.onEventMainThread(event);
        switch (EnumEventTag.valueOf(event.getTagInt())) {
            case CITY_CHANGE:
                mPtrlvContent.setRefreshing();
                break;

            default:
                break;
        }
    }

    @Override
    protected String setUmengAnalyticsTag() {
        return this.getClass().getName().toString();
    }
}