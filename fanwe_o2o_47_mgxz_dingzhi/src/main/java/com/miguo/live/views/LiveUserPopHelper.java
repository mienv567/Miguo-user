package com.miguo.live.views;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fanwe.o2o.miguo.R;
import com.miguo.live.adapters.LiveViewPagerItemAdapter;
import com.miguo.live.interf.IHelper;
import com.miguo.utils.DisplayUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItem;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;

/**
 * Created by didik on 2016/8/4.
 * 商品等viewpager界面
 */
public class LiveUserPopHelper implements IHelper {
    private View rootView;
    private ViewPager.OnPageChangeListener listener;
    private Activity mActivity;
    private int prePosition = 0;//之前一个位置
    private PopupWindow popupWindow;

    public LiveUserPopHelper(Activity activity,View rootView) {
        this.mActivity=activity;
        this.rootView=rootView;
        createPopWindow();

    }

    private void createPopWindow() {
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.act_live_pop_viewpager, null);
        initContentView(contentView);
        //设置窗体的宽高属性
        int height = DisplayUtil.dp2px(mActivity, 300);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams
                .MATCH_PARENT, height);
        //设置可以点击
        popupWindow.setTouchable(true);
        //设置背景
//        ColorDrawable background=new ColorDrawable(0x4F000000);
        BitmapDrawable background=new BitmapDrawable();
        //设置背景+
        popupWindow.setBackgroundDrawable(background);
//
        popupWindow.setFocusable(true);

        popupWindow.setOutsideTouchable(true);

    }
    /*显示*/
    public void show(){
        if (popupWindow!=null){
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
        }
    }

    private void initContentView(View contentView) {
        ViewPagerItems pagerItems = new ViewPagerItems(mActivity);

        ViewPagerItem item1 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);
        ViewPagerItem item2 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);
        ViewPagerItem item3 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);
        ViewPagerItem item4 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);
        ViewPagerItem item5 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);
        ViewPagerItem item6 = ViewPagerItem.of("title1", R.layout.item_pager_baobao);

        pagerItems.add(item1);
        pagerItems.add(item2);
        pagerItems.add(item3);
        pagerItems.add(item4);
        pagerItems.add(item5);
        pagerItems.add(item6);


        LiveViewPagerItemAdapter adapter1 = new LiveViewPagerItemAdapter(pagerItems);


        final ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter1);

        final SmartTabLayout viewPagerTab = (SmartTabLayout) contentView.findViewById(R.id.viewpagertab);



        viewPagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View inflate = LayoutInflater.from(mActivity).inflate(R.layout
                        .item_viewpager, container, false);
                TextView textView = (TextView) inflate.findViewById(R.id.tv_show);
                switch (position) {
                    case 0:
                        textView.setText("镇店之宝");

                        break;
                    case 1:
                        textView.setText("主场");
                        break;
                    case 2:
                        textView.setText("红包");
                        break;
                    case 3:
                        textView.setText(4444 + "");
                        break;
                    case 4:
                        textView.setText(5555 + "");
                        break;
                    case 5:
                        textView.setText(6666 + "");
                        break;
                }
                return inflate;
            }
        });
        viewPagerTab.setViewPager(viewPager);
        listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != prePosition) {
                    TextView tv = (TextView) viewPagerTab.getTabAt(prePosition).findViewById(R.id
                            .tv_show);
                    ImageView iv = (ImageView) viewPagerTab.getTabAt(prePosition).findViewById(R
                            .id.iv_show);
                    tv.setTextColor(Color.WHITE);
                    iv.setVisibility(View.GONE);
                }
                TextView tv = (TextView) viewPagerTab.getTabAt(position).findViewById(R.id.tv_show);
                ImageView iv = (ImageView) viewPagerTab.getTabAt(position).findViewById(R.id
                        .iv_show);
                tv.setTextColor(Color.RED);
                iv.setVisibility(View.VISIBLE);
                prePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPagerTab.setOnPageChangeListener(listener);
        listener.onPageSelected(0);



    }


    @Override
    public void onDestroy() {

    }
}