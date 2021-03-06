package com.fanwe.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fanwe.adapter.OrderDetailGroupGoodsAdapter;
import com.fanwe.o2o.miguo.R;
import com.fanwe.shoppingcart.model.Deals;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 订单详情页面(订单商品fragment)
 * 
 * @author js02
 * 
 */
public class OrderDetailGoodsFragment extends OrderDetailBaseFragment
{

	@ViewInject(R.id.frag_order_detail_goods_ll_all)
	private LinearLayout mLlGoods;

	private List<Deals> mListModel;
	
	@Override
	protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return setContentView(R.layout.frag_order_detail_goods);
	}

	@Override
	protected void init()
	{
		super.init();
		bindData();
	}

	private void bindData()
	{
		if (!toggleFragmentView(mCheckActModel))
		{
			return;
		}

		mListModel = mCheckActModel.getDeals();
		if (!toggleFragmentView(mListModel))
		{
			return;
		}
		mLlGoods.removeAllViews();
		OrderDetailGroupGoodsAdapter adapter = new OrderDetailGroupGoodsAdapter(mListModel, getActivity());
		for (int i = 0; i < mListModel.size(); i++)
		{
			mLlGoods.addView(adapter.getView(i, null, null));
		}
	}

	@Override
	protected void onRefreshData()
	{
		bindData();
		super.onRefreshData();
	}

}