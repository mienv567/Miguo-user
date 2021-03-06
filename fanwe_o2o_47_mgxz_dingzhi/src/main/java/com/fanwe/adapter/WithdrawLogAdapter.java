package com.fanwe.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.commission.model.getCommissionLog.ModelCommissionLog;
import com.fanwe.library.adapter.SDBaseAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.ViewHolder;
import com.fanwe.listener.TextMoney;
import com.fanwe.o2o.miguo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WithdrawLogAdapter extends SDBaseAdapter<ModelCommissionLog>
{

	public WithdrawLogAdapter(List<ModelCommissionLog> listModel, Activity activity)
	{
		super(listModel, activity);
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent,
						ModelCommissionLog model) {
		if(convertView==null)
		{
			convertView = mInflater.inflate(R.layout.item_withdraw_log, null);
		}
		ImageView iv_image= ViewHolder.get(R.id.iv_image,convertView);
		TextView tv_time = ViewHolder.get(R.id.tv_time, convertView);
		TextView tv_type = ViewHolder.get(R.id.tv_type, convertView);
		TextView tv_pname = ViewHolder.get(R.id.tv_pname, convertView);
		TextView tv_desc = ViewHolder.get(R.id.tv_desc, convertView);
		TextView tv_money = ViewHolder.get(R.id.tv_money, convertView);
		if(model != null)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String typeTitle="";
			//TODO 说是后台给描述type类型
			//NOTICE: 2016.09.29 后台直接给类型对应的文案,客户端直接显示type字段.
			//设置不同类型显示的标题
			SDViewBinder.setTextView(tv_type, model.getMoney_type(),"");
			
			//设置金额与显示文字颜色,图片
			String money = model.getMoney();
			Float moneyShow = Float.valueOf(money);
			if (moneyShow>0) {
				SDViewBinder.setTextView(tv_money,"+"+TextMoney.textFarmat(model.getMoney())+"元");
				iv_image.setImageResource(R.drawable.bg_int);
				tv_money.setTextColor(Color.parseColor("#2ee17c"));
			}else if (moneyShow<0) {
				SDViewBinder.setTextView(tv_money, TextMoney.textFarmat(model.getMoney())+"元");
				tv_money.setTextColor(Color.parseColor("#FB6F08"));
				iv_image.setImageResource(R.drawable.bg_out);
			}else {
				SDViewBinder.setTextView(tv_money, "0.00元");
				iv_image.setImageResource(R.drawable.bg_int);
				tv_money.setTextColor(Color.parseColor("#2ee17c"));
			}
			
			//设置描述
			SDViewBinder.setTextView(tv_desc, model.getDescription());
			
			//设置时间
			SDViewBinder.setTextView(tv_time, format.format(new Date(Long.valueOf(model.getInsert_time()))));//1472127680687
			
			//设置来自推荐人字段,为空时不设置
			SDViewBinder.setTextView(tv_pname,  model.getMobile(),"");
	}
		return convertView;
	}
	@Override
	public void updateData(List<ModelCommissionLog> listModel)
	{
		super.updateData(listModel);
	}
}
