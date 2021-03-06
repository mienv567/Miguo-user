package com.miguo.category;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanwe.StoreLocationActivity;
import com.fanwe.app.App;
import com.fanwe.baidumap.BaiduMapManager;
import com.fanwe.base.CallbackView;
import com.fanwe.common.model.CommonConstants;
import com.fanwe.common.model.createShareRecord.ModelCreateShareRecord;
import com.fanwe.common.presenters.CommonHttpHelper;
import com.fanwe.constant.Constant;
import com.fanwe.constant.TipPopCode;
import com.fanwe.customview.PopTipShare;
import com.fanwe.fragment.ShopFansFragment;
import com.fanwe.fragment.StoreLocationFragment;
import com.fanwe.library.utils.SDActivityUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDIntentUtil;
import com.fanwe.model.Store_infoModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.views.DaiyanSendTypeActivity;
import com.fanwe.user.UserConstants;
import com.fanwe.user.view.UserHomeActivity;
import com.fanwe.utils.DataFormat;
import com.fanwe.utils.ShareUtil;
import com.fanwe.view.LoadMoreRecyclerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.adapter.HiShopDetailLiveAdapter;
import com.miguo.adapter.HiShopDetailRecommendAdapter;
import com.miguo.adapter.ShopDetailPagerAdapter;
import com.miguo.app.HiBaseActivity;
import com.miguo.app.HiShopDetailActivity;
import com.miguo.dao.CollectShopDao;
import com.miguo.dao.HiShopDetailDao;
import com.miguo.dao.RepresentMerchantDao;
import com.miguo.dao.impl.CollectShopDaoImpl;
import com.miguo.dao.impl.HiShopDetailDaoImpl;
import com.miguo.dao.impl.RepresentMerchantDaoImpl;
import com.miguo.definition.ClassPath;
import com.miguo.entity.HiShopDetailBean;
import com.miguo.factory.ClassNameFactory;
import com.miguo.fragment.ShopDetailPagerItemFragmet;
import com.miguo.listener.HiShopDetailListener;
import com.miguo.utils.BaseUtils;
import com.miguo.ui.view.RecyclerBounceNestedScrollView;
import com.miguo.ui.view.ShopDetailTagView;
import com.miguo.ui.view.ShopDetailViewPager;
import com.miguo.view.CollectShopView;
import com.miguo.view.HiShopDetailView;
import com.miguo.view.RepresentMerchantView;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zlh/Barry/狗蛋哥 on 2016/10/19.
 */
public class HiShopDetailCategory extends Category implements HiShopDetailView,
        CollectShopView, RepresentMerchantView,
        RecyclerBounceNestedScrollView.OnRecyclerScrollViewListener,
        ShopFansFragment.ShowListener,
        CallbackView {
    private int resultCode = 0;

    @ViewInject(R.id.recycler_scrollview)
    RecyclerBounceNestedScrollView scrollView;

    @ViewInject(R.id.title_layout)
    RelativeLayout titleLayout;

    @ViewInject(R.id.title_layout_bg)
    RelativeLayout titleLayoutBg;

    @ViewInject(R.id.back)
    ImageView back;
    @ViewInject(R.id.back_bg)
    ImageView backBg;

    @ViewInject(R.id.share)
    ImageView share;
    @ViewInject(R.id.share_bg)
    ImageView shareBg;

    @ViewInject(R.id.title)
    TextView title;

    /**
     * 轮播图
     */
    @ViewInject(R.id.shopdetail_viewpager)
    ShopDetailViewPager viewPager;

    ShopDetailPagerAdapter viewpagerAdapter;

    /**
     * 标题
     */
    @ViewInject(R.id.shop_name)
    TextView shopName;


    /**
     * 标签
     */
    @ViewInject(R.id.tags)
    ShopDetailTagView tags;

    /**
     * 人均消费
     */
    @ViewInject(R.id.ava_price)
    TextView price;

    /**
     * 人气值
     */
    @ViewInject(R.id.hot)
    TextView hots;

    @ViewInject(R.id.collect)
    ImageView collect;

    /**
     * 精选推荐
     */
    @ViewInject(R.id.recmmend)
    LoadMoreRecyclerView recommend;
    HiShopDetailRecommendAdapter recommendAdapter;
    /**
     * 地址
     */
    @ViewInject(R.id.address)
    TextView address;
    /**
     * 营业时间
     */
    @ViewInject(R.id.time)
    TextView time;
    /**
     * 距离
     */
    @ViewInject(R.id.distance)
    TextView distance;

    /**
     * call打电话
     */
    @ViewInject(R.id.call)
    ImageView call;

    /**
     * 地图
     */
    @ViewInject(R.id.location)
    ImageView location;

    /**
     * 适合人群
     */
    @ViewInject(R.id.crowd_people)
    TextView crowdPeople;

    /**
     * 在现场
     */
    @ViewInject(R.id.live)
    LoadMoreRecyclerView live;
    HiShopDetailLiveAdapter liveAdapter;
    /**
     * 是否已代言
     */
    @ViewInject(R.id.tv_represent_message)
    TextView representMessage;
    @ViewInject(R.id.tv_represent)
    TextView tvRepresent;
    @ViewInject(R.id.tv_mine_shop)
    TextView tvMineShop;
    @ViewInject(R.id.tv_share)
    TextView tvShare;
    @ViewInject(R.id.layout_recmmend)
    LinearLayout layoutRecmmend;
    @ViewInject(R.id.layout_people)
    LinearLayout layoutPeople;
    @ViewInject(R.id.layout_live)
    LinearLayout layoutLive;
    @ViewInject(R.id.content_activity_hishop_detail)
    LinearLayout layoutContent;
    @ViewInject(R.id.bottom_activity_hishop_detail)
    RelativeLayout layoutBottom;
    @ViewInject(R.id.mode_top_layout)
    LinearLayout modeTopLayout;
    @ViewInject(R.id.iv_represent)
    ImageView ivRepresent;

    HiShopDetailDao shopDetailDao;
    HiShopDetailBean.Result result;


    CollectShopDao collectShopDao;

    RepresentMerchantDao representMerchantDao;
    private CommonHttpHelper commonHttpHelper;

    public HiShopDetailCategory(HiBaseActivity activity) {
        super(activity);
    }

    @Override
    protected void initFirst() {
        getIntentData();
        getRecordId();
        shopDetailDao = new HiShopDetailDaoImpl(this);
        collectShopDao = new CollectShopDaoImpl(this);
        representMerchantDao = new RepresentMerchantDaoImpl(this);
        List list = new ArrayList();
        recommendAdapter = new HiShopDetailRecommendAdapter(getActivity(), list);

        List list2 = new ArrayList();
        liveAdapter = new HiShopDetailLiveAdapter(getActivity(), list2);
    }

    @Override
    protected void findCategoryViews() {
        ViewUtils.inject(this, getActivity());
    }

    @Override
    protected void initThisListener() {
        listener = new HiShopDetailListener(this);
    }

    @Override
    protected void setThisListener() {
        ivRepresent.setOnClickListener(listener);
        call.setOnClickListener(listener);
        location.setOnClickListener(listener);
        collect.setOnClickListener(listener);
        back.setOnClickListener(listener);
        share.setOnClickListener(listener);
        backBg.setOnClickListener(listener);
        shareBg.setOnClickListener(listener);
        tvRepresent.setOnClickListener(listener);
        offlineLayout.setOnClickListener(listener);
        tvMineShop.setOnClickListener(listener);
        tvShare.setOnClickListener(listener);
        scrollView.setOnRecyclerScrollViewListener(this);
        scrollView.hideLoadingLayout();

    }

    @Override
    protected void init() {
        initShopDetail();
    }

    @Override
    protected void initViews() {
        setTitlePadding(titleLayout);
        setTitlePadding(titleLayoutBg);
        setTitleAlpha(titleLayout, 0);
        initRecommendRecyclerView();
        initLiveRecyclerView();
        /**
         * 大王和小伙伴
         */
//        initShopMember(merchantID);
    }

    private String merchantID;
    /**
     * 上次代言的参数。
     */
    private int lastDataPos1 ;
    private  String is_endorsement ="";

    private void getIntentData() {
        merchantID = getActivity().getIntent().getExtras().getString(HiShopDetailActivity.EXTRA_MERCHANT_ID);
        lastDataPos1 = getActivity().getIntent().getExtras().getInt(HiShopDetailActivity.LAST_DATA_POS,-1);
        Log.d("lastDataPos",lastDataPos1+"");
    }

    private void initShopDetail() {
        try {
            shopDetailDao.getShopDetail(merchantID,
                    BaiduMapManager.getInstance().getBDLocation().getLongitude() + "",
                    BaiduMapManager.getInstance().getBDLocation().getLatitude() + ""
            );
        } catch (Exception e) {
            shopDetailDao.getShopDetail("",
                    "",
                    ""
            );
        }

    }

    /**
     * 回调数据后加载
     */
    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        if (result.getShop_images() == null || result.getShop_images().size() == 0) {
            HiShopDetailBean.Result.ShopImage banner = new HiShopDetailBean().new Result().new ShopImage();
            banner.setImage_url("");
            result.getShop_images().add(banner);
        }
        for (int i = 0; i < result.getShop_images().size(); i++) {
            ShopDetailPagerItemFragmet fragmet = new ShopDetailPagerItemFragmet();
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", result.getShop_images().get(i));
            fragmet.setArguments(bundle);
            fragments.add(fragmet);
        }
        viewpagerAdapter = new ShopDetailPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewpagerAdapter);
    }

    private void initRecommendRecyclerView() {
        recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        recommend.setAdapter(recommendAdapter);
    }

    private void initLiveRecyclerView() {
        live.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        live.setAdapter(liveAdapter);
        updateLiveRecycleViewrHeight();
    }

    private void initShopMember(String shopId) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ShopFansFragment fragment = new ShopFansFragment();
        fragmentTransaction.add(R.id.content_shop_member, fragment);
        fragmentTransaction.commit();
        fragment.setShopId(shopId);
        fragment.setShowListener(this);
    }

    @Override
    public void showSomething() {
        scrollView.smoothScrollTo(0, (modeTopLayout.getMeasuredHeight() - BaseUtils.dip2px(15)));
    }

    boolean scrollAlready;

    /**
     * 点击求代言
     */
    public void clickRepresentBtn() {
        if (!scrollAlready) {
            //1、如果未代言，展开条幅
            ivRepresent.setImageResource(R.drawable.ic_represent_open);
            //2、下滑到屏幕底部(延时1s)
            scrollAlready = true;
            representAnim();
        } else {
            //页面滚动到底部
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    /**
     * 延时1.5s，给条幅展示的时间
     */
    private void representAnim() {
        int time = 0;
        //还未代言，有延时
        if (!isRepresent) {
            time = 1500;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //收起条幅
                if (isRepresent) {
                    ivRepresent.setImageResource(R.drawable.ic_represent_already);
                } else {
                    ivRepresent.setImageResource(R.drawable.ic_represent_do);
                }
                float curTranslationY = ivRepresent.getTranslationY();
                ObjectAnimator animator = ObjectAnimator.ofFloat(ivRepresent, "translationY", curTranslationY,
                        (int) (BaseUtils.getHeight(getActivity()) * 0.5 - BaseUtils.dip2px(85)));
                animator.setDuration(500);
                animator.start();
                //页面滚动到底部
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, time);
    }

    /**
     * 点击代言
     */
    public void clickRepresent() {
        if (TextUtils.isEmpty(App.getInstance().getToken())) {
            clickLogin();
            return;
        }
        if (result == null) {
            return;
        }
        tvRepresent.setClickable(false);
        representMerchantDao.getRepresentMerchant(result.getEnt_id(), result.getId());
        is_endorsement = "1";
    }

    private void clickLogin(){
        Intent intent = new Intent(getActivity(), ClassNameFactory.getClass(ClassPath.LOGIN_ACTIVITY));
        BaseUtils.jumpToNewActivity(getActivity(), intent);
    }

    /**
     * 点击返回
     */
    public void clickBack() {
        Intent mIntent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putInt(HiShopDetailActivity.LAST_DATA_POS, lastDataPos1);
        bundle.putString(HiShopDetailActivity.IS_ENDORSEMENT, is_endorsement);
        mIntent.putExtras(bundle);
        // 设置结果，并进行传送
        getActivity().setResult(resultCode, mIntent);
         BaseUtils.finishActivity(getActivity());
    }

    private String shareRecordId;

    /**
     * 跳转到我的小店
     */
    public void clickMineShopBtn() {
        if(!isEmpty(App.getInstance().getToken())){
            Intent intent = new Intent(getActivity(), ClassNameFactory.getClass(ClassPath.DISTRIBUTION_STORE));
            intent.putExtra("id", App.getInstance().getCurrentUser().getUser_id());
            BaseUtils.jumpToNewActivity(getActivity(), intent);
            return;
        }
        clickLogin();
    }

    /**
     * 点击分享
     */
    public void clickShare() {
        dismissShareTipPop();
        getRecordId();
        if (result != null) {
            ShareUtil.share(getActivity(), result.getShare(), shareRecordId, "Shop");
        }
    }

    private void getRecordId() {
        if (commonHttpHelper == null) {
            commonHttpHelper = new CommonHttpHelper(getActivity(), this);
        }
        commonHttpHelper.createShareRecord(Constant.ShareType.SHOP, merchantID);
    }
    private PopTipShare popTipShare;
    private void showShareTipPop(){
        popTipShare = new PopTipShare(getActivity(),share);
        if (TipPopCode.checkDate(getActivity(),TipPopCode.Shops)){
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!getActivity().isFinishing()){
                        popTipShare.show();
                    }
                }
            },1000);
        }
    }
    private void dismissShareTipPop(){
        if (popTipShare!=null && popTipShare.isShowing()){
            popTipShare.dismiss();
        }
    }
    /**
     * 点击位置
     */
    public void clickLocation() {
        if (result != null) {
            Intent intent = new Intent(getActivity(), StoreLocationActivity.class);
            Store_infoModel store_infoModel = new Store_infoModel();
            store_infoModel.setAddress(result.getAddress());
            store_infoModel.setXpoint(DataFormat.toDouble(result.getGeo_x()));
            store_infoModel.setYpoint(DataFormat.toDouble(result.getGeo_y()));
            store_infoModel.setId(result.getId());
            store_infoModel.setName(result.getShop_name());
            store_infoModel.setTel(result.getTel());

            intent.putExtra(StoreLocationFragment.EXTRA_MODEL_MERCHANTITEMACTMODEL, store_infoModel);
            BaseUtils.jumpToNewActivity(getActivity(), intent);
        }
    }

    /**
     * 点击电话
     */
    public void clickCall() {
        if (result == null || TextUtils.isEmpty(result.getTel())) {
            return;
        }
        Intent intent = SDIntentUtil.getIntentCallPhone(result.getTel());
        SDActivityUtil.startActivity(getActivity(), intent);
    }

    /**
     * 点击收藏
     */
    public void clickCollect() {
        if (TextUtils.isEmpty(App.getInstance().getToken())) {
            Intent intent = new Intent(getActivity(), ClassNameFactory.getClass(ClassPath.LOGIN_ACTIVITY));
            BaseUtils.jumpToNewActivity(getActivity(), intent);
            return;
        }
        if (result == null) {
            return;
        }
        if (!result.isCollect()) {
            collectShopDao.collectShop(result.getId());
        } else {
            collectShopDao.unCollectShop(result.getId());
        }

    }

    @Override
    public void onScrollToEnd() {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        float radio = t / (float) getBannerHeight();
        if (radio <= 1) {
            setTitleAlpha(titleLayout, radio);
        }
    }

    public int getBannerHeight() {
        return dip2px(200);
    }

    /**
     * 更新在现场 直播列表高度
     */
    private void updateLiveRecycleViewrHeight() {
        int height = liveAdapter.getItemHeight();
        LinearLayout.LayoutParams params = getLineaLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(0, dip2px(15), 0, 0);
        live.setLayoutParams(params);
    }

    /**
     * 回调成功
     */
    @Override
    public void getShopDetailSuccess(final HiShopDetailBean.Result result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePage(result);
            }
        });
    }

    /**
     * 更新数据
     *
     * @param result
     */
    private void updatePage(final HiShopDetailBean.Result result) {
        this.result = result;
        if (TextUtils.isEmpty(result.getTel())) {
            call.setImageResource(R.drawable.ic_phone_disable);
        } else {
            call.setImageResource(R.drawable.ic_phone_enable);
        }
        /**
         * 标题
         */
        title.setText(result.getShop_name());
        /**
         * 轮播图
         */
        initViewPager();
        /**
         * 商店名字
         */
        shopName.setText(result.getShop_name());
        /**
         * 门店标签
         */
        if (result.getShop_tags().getTags() != null && result.getShop_tags().getTags().length > 0) {
            this.tags.init(result.getShop_tags().getTags());
        }
        /**
         * 价格
         */
        price.setText(result.getRef_avg_price() + "元/人");
        /**
         * 人气值
         */
        hots.setText("人气值 " + result.getCollect_number());
        /**
         *  收藏
         */
        collect.setImageResource(result.isCollect() ? R.drawable.ic_collect_yes : R.drawable.ic_collect_no);
        /**
         * 地址
         */
        address.setText(result.getAddress());
        /**
         * 营业时间
         */
        time.setText("营业时间：" + result.getTrade_day());
        /**
         * 距离
         */
        if (TextUtils.isEmpty(result.getDistance()) || "-1".equals(result.getDistance())) {
            distance.setText("距离");
        } else {
            distance.setText("距离 " + result.getDistanceFormat());
        }
        /**
         * 线下买单
         */
        updateOffline();

        /**
         * 精选列表
         */
        if (result.getTuan_list() != null && result.getTuan_list().size() > 0) {
            recommendAdapter.notifyDataSetChanged(result.getTuan_list());
        } else {
            layoutRecmmend.setVisibility(View.GONE);
        }
        /**
         * 适合人群
         */
        String tagPeople = getCrowdPeopleText(result);
        if (TextUtils.isEmpty(tagPeople)) {
            layoutPeople.setVisibility(View.GONE);
        } else {
            crowdPeople.setText(tagPeople);
        }
        /**
         * 在现场
         */
        if (result.getLive_list() != null && result.getLive_list().size() > 0) {
            liveAdapter.notifyDataSetChanged(result.getLive_list());
            updateLiveRecycleViewrHeight();
        } else {
            layoutLive.setVisibility(View.GONE);
        }
        //layoutBottom
        if (SDCollectionUtil.isEmpty(result.getTuan_list()) && SDCollectionUtil.isEmpty(result.getLive_list()) && TextUtils.isEmpty(tagPeople)) {
            layoutBottom.post(new Runnable() {
                @Override
                public void run() {
                    int hTotal = BaseUtils.getHeight(getActivity());
                    int hContent = layoutContent.getHeight();
                    int abs = hTotal - hContent;
                    if (abs > 0) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, abs, 0, 0);
                        layoutBottom.setLayoutParams(lp);
                    }
                }
            });
        }
        /**
         * 是否已代言
         */
        updateRepresent();

        showShareTipPop();
    }

    @ViewInject(R.id.offline_layout)
    RelativeLayout offlineLayout;
    @ViewInject(R.id.offline_space)
    View offlineSpace;
    /**
     * 满减
     * 每满100元减8元
     */
    @ViewInject(R.id.decrease)
    TextView decrease;
    /**
     * 时间区域 周一到周四 00:00-24:00
     */
    @ViewInject(R.id.pay_time)
    TextView payTime;
    private void updateOffline(){
        /**
         * 处理是否能线下买单
         */
        handleCanPayFromOffline();
        /**
         * 如果没有开通线下买单，那就不继续走了
         */
        if(!result.canPayFromOffline()){
            return;
        }
        /**
         * 处理不同买单类型的UI变动
         */
        handleOfflinePayType();
    }

    private void handleCanPayFromOffline(){
        offlineLayout.setVisibility(result.canPayFromOffline() ? View.VISIBLE : View.GONE);
        offlineSpace.setVisibility(result.canPayFromOffline() ? View.VISIBLE : View.GONE);
    }

    /**
     * 处理不同买单类型的UI变动
     */
    private void handleOfflinePayType(){
        switch (result.getOfflineInfo().getOnline_pay_type()){
            case HiShopDetailBean.Result.Offline.ORIGINAL:
                updateDecrease("在线买单，有好运");
                updatePayTimeVisibility(View.GONE);
                break;
            case HiShopDetailBean.Result.Offline.DISCOUNT:
                updateDecrease(result.getOfflineInfo().getDiscountText());
                updatePayTimeVisibility(View.VISIBLE);
                updatePayTime();
                break;
            case HiShopDetailBean.Result.Offline.DECREASE:
                updateDecrease(result.getOfflineInfo().getDecreaseText());
                updatePayTimeVisibility(View.VISIBLE);
                updatePayTime();
                break;
        }
    }

    private void updatePayTime(){
        this.payTime.setText(result.getOfflineInfo().getAvailableWeek() + "  " + result.getOfflineInfo().getAvailableTime());
    }

    /**
     * 更新满减文字
     * @param decrease 内容
     */
    private void updateDecrease(String decrease){
        this.decrease.setText(decrease);
    }

    /**
     * 更新时间文本是否可见（原价不可见）
     * @param visibility 是否可见码
     */
    private void updatePayTimeVisibility(int visibility){
        this.payTime.setVisibility(visibility);
    }

    private boolean isRepresent;

    public void updateRepresent() {
        isRepresent = result.isEndorsement();
        /**
         * 是否已代言
         */
        tvRepresent.setVisibility(isRepresent ? View.GONE : View.VISIBLE);
        tvMineShop.setVisibility(isRepresent ? View.VISIBLE : View.GONE);
        tvShare.setVisibility(isRepresent ? View.VISIBLE : View.GONE);
        representMessage.setText(isRepresent ? "你已代言，分享可赚钱" : "代言后发展下级，下级消费时你有收益");
        //求代言按钮
        if (isRepresent) {
            ivRepresent.setImageResource(R.drawable.ic_represent_already);
        } else {
            ivRepresent.setImageResource(R.drawable.ic_represent_do);
        }
    }

    private String getCrowdPeopleText(final HiShopDetailBean.Result result) {
        String tag = "";
        if (result.getCrowd_tags() != null && result.getCrowd_tags().getTags() != null && result.getCrowd_tags().getTags().length > 0) {
            for (int i = 0; i < result.getCrowd_tags().getTags().length; i++) {
                tag = i == 0 ? tag + result.getCrowd_tags().getTags()[i] : tag + " " + result.getCrowd_tags().getTags()[i];
            }
        }
        return tag;
    }

    @Override
    public void getShopDetailError() {

    }

    /**
     * 收藏和取消收藏
     */
    @Override
    public void collectSuccess(final HashMap<String, String> map) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (map != null) {
                    //popularity  人气字段
                    String popularity = map.get(UserConstants.POPULARITY);
                    hots.setText("人气值 " + popularity);
                }
                collect.setImageResource(R.drawable.ic_collect_yes);
                result.setIs_collect("1");
            }
        });
    }

    @Override
    public void collectError() {

    }

    @Override
    public void unCollectSuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collect.setImageResource(R.drawable.ic_collect_no);
                result.setIs_collect("0");
            }
        });
    }

    @Override
    public void unCollectError() {

    }

    /**
     * 点击代言回调
     */
    @Override
    public void getRepresentMerchantSuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.setIs_endorsement("1");
                updateRepresent();
            }
        });


    }

    @Override
    public void getRepresentMerchantError(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(message);
            }
        });
    }

    @Override
    public void onFinish() {
        tvRepresent.setClickable(true);
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    @Override
    public void onSuccess(String method, List datas) {
        if (CommonConstants.CREATE_SHARE_RECORD.equals(method)) {
            if (!SDCollectionUtil.isEmpty(datas)) {
                ModelCreateShareRecord bean = (ModelCreateShareRecord) datas.get(0);
                shareRecordId = bean.getId();
            }
        }
    }

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {

    }

    public void destory(){
        dismissShareTipPop();
    }

    public String getMerchantID() {
        return merchantID;
    }

    public HiShopDetailBean.Result getResult() {
        return result;
    }
}
