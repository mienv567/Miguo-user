package com.fanwe.search.views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fanwe.o2o.miguo.R;
import com.miguo.ui.view.floatdropdown.view.SearchView;

/**
 * Created by Administrator on 2017/1/5.
 */

public class SearchResultActivity extends FragmentActivity {
    private TextView tvGoods, tvShop;
    private String pageType;
    private SearchView searchView;
    private View left;
    private View right;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        getData();
        preWidget();
        preView();
        setListener();
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_goods_frag_groupon_list:
                clickGoods();
                break;
            case R.id.tv_shop_frag_groupon_list:
                clickShop();
                break;
        }
    }

    public void searchByKeyword(String keyword) {
        if ("shop".equals(pageType)) {
            if (fragmentShop != null) {
                fragmentShop.search(keyword);
            }
        } else {
            if (fragmentGoods != null) {
                fragmentGoods.search(keyword);
            }
        }
    }

    private void clickShop() {
        if ("shop".equals(pageType)) {
            return;
        }
        setTabSelected("shop");
    }

    private void clickGoods() {
        if ("goods".equals(pageType)) {
            return;
        }
        setTabSelected("goods");
    }

    /**
     * 设置选中的标签
     *
     * @param pageType
     */
    private void setTabSelected(String pageType) {
        this.pageType = pageType;
        setTextColor();
        switchFragment();
    }

    /**
     * 设置标题颜色
     */
    private void setTextColor() {
        if ("shop".equals(pageType)) {
            tvShop.setTextColor(getResources().getColor(R.color.black_2e));
            tvGoods.setTextColor(getResources().getColor(R.color.c_CCCCCC));
        } else {
            tvGoods.setTextColor(getResources().getColor(R.color.black_2e));
            tvShop.setTextColor(getResources().getColor(R.color.c_CCCCCC));
        }
    }

    /**
     * 切换fragment
     */
    private void switchFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        if ("shop".equals(pageType)) {
            //商家
            if (fragmentGoods != null && !fragmentGoods.isHidden()) {
                ft.hide(fragmentGoods);
            }
            if (fragmentShop == null) {
                fragmentShop = new FragmentSearchShop();
                ft.add(R.id.content_act_search, fragmentShop);
            } else {
                ft.show(fragmentShop);
            }
            ft.commit();
        } else {
            //商品
            if (fragmentShop != null && !fragmentShop.isHidden()) {
                ft.hide(fragmentShop);
            }
            if (fragmentGoods == null) {
                fragmentGoods = new FragmentSearchGoods();
                ft.add(R.id.content_act_search, fragmentGoods);
            } else {
                ft.show(fragmentGoods);
            }
            ft.commit();
        }
    }

    private FragmentSearchShop fragmentShop;
    private FragmentSearchGoods fragmentGoods;

    android.support.v4.app.FragmentManager fm;

    /**
     * Fragment初始化
     */
    private void initFragment() {
        fm = getSupportFragmentManager();
        setTabSelected("shop");
    }

    private void preView() {
        initFragment();
        if (!TextUtils.isEmpty(keyword)){
            searchView.setText(keyword);
            searchAction();
        }
    }

    private void getData() {
        if (getIntent() != null) {
            keyword = getIntent().getStringExtra("keyword");
        }
    }


    private void setListener() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAction();
            }
        });
    }

    private void searchAction() {
        keyword = searchView.getEditText();
        if (TextUtils.isEmpty(keyword)) {
            //do nothing
        } else {
            searchByKeyword(keyword);
        }
    }


    private void preWidget() {
        tvGoods = (TextView) findViewById(R.id.tv_goods_frag_groupon_list);
        tvShop = (TextView) findViewById(R.id.tv_shop_frag_groupon_list);
        searchView = ((SearchView) findViewById(R.id.searchView));
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
    }
}