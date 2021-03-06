package com.fanwe.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.library.adapter.SDBaseAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.ViewHolder;
import com.fanwe.model.Uc_eventActItemModel;
import com.fanwe.o2o.miguo.R;

public class MyEventAdapter extends SDBaseAdapter<Uc_eventActItemModel>
{

	public MyEventAdapter(List<Uc_eventActItemModel> listModel, Activity activity)
	{
		super(listModel, activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.item_my_event_list, null);
		}

		ImageView iv_qrcode = ViewHolder.get(convertView, R.id.iv_qrcode);
		TextView tv_sn_number = ViewHolder.get(convertView, R.id.tv_sn_number);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_expire_time = ViewHolder.get(convertView, R.id.tv_expire_time);

		final Uc_eventActItemModel model = getItem(position);
		if (model != null)
		{
			SDViewBinder.setImageView(iv_qrcode, model.getQrcode());
			SDViewBinder.setTextView(tv_sn_number, model.getEvent_sn());
			SDViewBinder.setTextView(tv_name, model.getName());
			SDViewBinder.setTextView(tv_expire_time, model.getEvent_end_time());

			convertView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
			{
//					Intent itemintent = new Intent(mActivity, EventDetailActivity.class);
//					itemintent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, model.getId());
//					mActivity.startActivity(itemintent);
				}
			});
		}
		return convertView;
	}

}
