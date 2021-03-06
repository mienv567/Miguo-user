package com.fanwe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fanwe.adapter.NoticeListAdapter;
import com.fanwe.constant.Constant.TitleType;
import com.fanwe.model.Notice_indexActListModel;
import com.fanwe.model.PageModel;
import com.fanwe.o2o.miguo.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.live.views.customviews.MGToast;

import java.util.ArrayList;
import java.util.List;

/**
 * 公告列表
 * 
 * @author js02
 * 
 */
public class NoticeListActivity extends BaseActivity
{

	@ViewInject(R.id.act_news_list_ptrlv_content)
	private PullToRefreshListView mPtrlvContent = null;

	private List<Notice_indexActListModel> mListModel = new ArrayList<Notice_indexActListModel>();
	private NoticeListAdapter mAdapter;

	private PageModel mPage = new PageModel();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setmTitleType(TitleType.TITLE);
		setContentView(R.layout.act_notice_list);
		init();
	}

	private void init()
	{
		initTitle();
		bindDefaultData();
		initPullToRefreshListView();
	}

	private void initPullToRefreshListView()
	{
		mPtrlvContent.setMode(Mode.BOTH);
		mPtrlvContent.setOnRefreshListener(new OnRefreshListener2<ListView>()
		{

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				mPage.resetPage();
				requestData(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				if (!mPage.increment())
				{
					MGToast.showToast("没有更多数据了");
					mPtrlvContent.onRefreshComplete();
				} else
				{
					requestData(true);
				}
			}
		});

		mPtrlvContent.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Notice_indexActListModel model = mAdapter.getItem((int) id);
				if (model != null)
				{
					Intent intent = new Intent(getApplicationContext(), NoticeDetailActivity.class);
					intent.putExtra(NoticeDetailActivity.EXTRA_NOTICE_ID, model.getId());
					startActivity(intent);
				}

			}
		});

		mPtrlvContent.setRefreshing();

	}

	private void initTitle()
	{
		mTitle.setMiddleTextTop("公告列表");
	}

	private void bindDefaultData()
	{
		mAdapter = new NoticeListAdapter(mListModel, this);
		mPtrlvContent.setAdapter(mAdapter);
	}

	private void requestData(final boolean isLoadMore)
	{
	}

}