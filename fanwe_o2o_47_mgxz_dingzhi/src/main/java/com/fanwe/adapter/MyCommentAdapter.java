package com.fanwe.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fanwe.AlbumActivity;
import com.fanwe.TuanDetailActivity;
import com.fanwe.YouHuiDetailActivity;
import com.fanwe.constant.Constant.CommentType;
import com.fanwe.library.adapter.SDBaseAdapter;
import com.fanwe.library.customview.FlowLayout;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.ViewHolder;
import com.fanwe.model.CommentModel;
import com.fanwe.o2o.miguo.R;
import com.miguo.app.HiShopDetailActivity;
import com.fanwe.seller.views.GoodsDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MyCommentAdapter extends SDBaseAdapter<CommentModel>
{

	private int mImageViewWidth = 150;

	public MyCommentAdapter(List<CommentModel> listModel, Activity activity)
	{
		super(listModel, activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.item_my_comment, null);
		}
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
		LinearLayout ll_replay = ViewHolder.get(convertView, R.id.ll_replay);
		TextView tv_reply_content = ViewHolder.get(convertView, R.id.tv_reply_content);
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		RatingBar rb_star = ViewHolder.get(convertView, R.id.rb_star);
		View viewDiv = ViewHolder.get(convertView, R.id.view_div);

		FlowLayout flow_images = ViewHolder.get(convertView, R.id.flow_images);
		flow_images.removeAllViews();
		if (position == 0)
		{
			viewDiv.setVisibility(View.GONE);
		}

		final CommentModel model = getItem(position);
		if (model != null)
		{
			SDViewBinder.setTextView(tv_name, model.getName());
			SDViewBinder.setTextView(tv_content, model.getContent());
			SDViewBinder.setTextView(tv_reply_content, model.getReply_content());
			SDViewBinder.setTextView(tv_time, model.getCreate_time());
			SDViewBinder.setRatingBar(rb_star, model.getPointFloat());

			String replyContent = model.getReply_content();
			if (TextUtils.isEmpty(replyContent))
			{
				ll_replay.setVisibility(View.GONE);
			} else
			{
				ll_replay.setVisibility(View.VISIBLE);
			}

			bindCommentImages(model.getImages(), model.getOimages(), flow_images);

			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = null;
					String type = model.getType();
					if (CommentType.DEAL.equals(type))
					{
						intent = new Intent(mActivity, GoodsDetailActivity.class);
						intent.putExtra(TuanDetailActivity.EXTRA_GOODS_ID, model.getData_id());
					} else if (CommentType.YOUHUI.equals(type))
					{
						intent = new Intent(mActivity, YouHuiDetailActivity.class);
						intent.putExtra(YouHuiDetailActivity.EXTRA_YOUHUI_ID, model.getData_id());
					} else if (CommentType.EVENT.equals(type))
					{
//						intent = new Intent(mActivity, EventDetailActivity.class);
//						intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, model.getData_id());
					} else if (CommentType.STORE.equals(type))
					{
						intent = new Intent(mActivity, HiShopDetailActivity.class);
						intent.putExtra(HiShopDetailActivity.EXTRA_MERCHANT_ID, model.getData_id());
					}
					if (intent != null)
					{
						mActivity.startActivity(intent);
					}
				}
			});
		}

		return convertView;
	}

	private void bindCommentImages(List<String> listImage, List<String> listOimage, FlowLayout flow_images)
	{
		if (listImage != null && !listImage.isEmpty())
		{
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mImageViewWidth, mImageViewWidth);
			for (int i = 0; i < listImage.size(); i++)
			{
				ImageView iv = new ImageView(mActivity);
				iv.setOnClickListener(new ImageViewClickListener(listOimage, i));
				SDViewBinder.setImageView(iv, listImage.get(i));
				flow_images.addView(iv, params);
			}
		}
	}

	class ImageViewClickListener implements View.OnClickListener
	{

		private List<String> nListOimage;
		private int nIndex;

		public ImageViewClickListener(List<String> nListOimage, int nIndex)
		{
			this.nListOimage = nListOimage;
			this.nIndex = nIndex;
		}

		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(mActivity, AlbumActivity.class);
			intent.putExtra(AlbumActivity.EXTRA_IMAGES_INDEX, nIndex);
			intent.putStringArrayListExtra(AlbumActivity.EXTRA_LIST_IMAGES, (ArrayList<String>) nListOimage);
			mActivity.startActivity(intent);
		}
	}

}
