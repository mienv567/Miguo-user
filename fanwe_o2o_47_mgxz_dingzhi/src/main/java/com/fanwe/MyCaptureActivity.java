package com.fanwe;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fanwe.o2o.miguo.R;
import com.fanwe.zxing.CaptureActivity;
import com.google.zxing.Result;
import com.miguo.app.HiHomeActivity;
import com.miguo.definition.ResultCode;
import com.miguo.utils.BaseUtils;

public class MyCaptureActivity extends CaptureActivity
{
	/** 是否扫描成功后结束二维码扫描activity，0：否，1:是,值为字符串 */
	public static final String EXTRA_IS_FINISH_ACTIVITY = "extra_is_finish_activity";
	/** 扫描成功返回码 */
	public static final int RESULT_CODE_SCAN_SUCCESS = ResultCode.RESULT_CODE_SCAN_SUCCESS;
	/** 扫描成功，扫描activity结束后Intent中取扫描结果的key */
	public static final String EXTRA_RESULT_SUCCESS_STRING = "extra_result_success_string";

	private ImageView iv_back;

	private boolean mIsStartByAdvs = false;
	private int mFinishActivityWhenScanFinish = 1;

	RelativeLayout titleLayout;

	@Override
	protected void init()
	{
		initIntentData();
		setLayoutId(R.layout.include_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		titleLayout = (RelativeLayout)findViewById(R.id.title_layout);
		registeClick();
		initTitlePadding();
	}

	private void initTitlePadding(){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		if (titleLayout != null) {
//			titleLayout.setPadding(0, BaseUtils.getStatusBarHeight(this), 0, 0);
		}
	}

	private void initIntentData()
	{
		mFinishActivityWhenScanFinish = getIntent().getIntExtra(EXTRA_IS_FINISH_ACTIVITY, 1);
		mIsStartByAdvs = getIntent().getBooleanExtra(BaseActivity.EXTRA_IS_ADVS, false);
	}

	private void registeClick()
	{
		iv_back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});
	}

	@Override
	protected void onSuccessScanning(Result result)
	{
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RESULT_SUCCESS_STRING, result.getText());
		setResult(RESULT_CODE_SCAN_SUCCESS, intent);
		if (mFinishActivityWhenScanFinish == 1)
		{
			finish();
		}
	}

	@Override
	public void finish()
	{
		if (mIsStartByAdvs)
		{
			startActivity(new Intent(this, HiHomeActivity.class));
		}
		super.finish();
	}
}
