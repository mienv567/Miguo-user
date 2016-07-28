package com.fanwe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.base.CallbackView;
import com.fanwe.base.CommonHelper;
import com.fanwe.base.Result;
import com.fanwe.common.CommonInterface;
import com.fanwe.constant.Constant.TitleType;
import com.fanwe.fragment.LoginPhoneFragment;
import com.fanwe.http.InterfaceServer;
import com.fanwe.http.listener.SDRequestCallBack;
import com.fanwe.library.customview.ClearEditText;
import com.fanwe.library.customview.SDSendValidateButton;
import com.fanwe.library.customview.SDSendValidateButton.SDSendValidateButtonListener;
import com.fanwe.library.dialog.SDDialogManager;
import com.fanwe.library.utils.SDToast;
import com.fanwe.model.Check_MobActModel;
import com.fanwe.model.RequestModel;
import com.fanwe.model.Sms_send_sms_codeActModel;
import com.fanwe.model.UserInfoModel;
import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtils;
import com.fanwe.o2o.miguo.R;
import com.fanwe.user.UserConstants;
import com.fanwe.utils.Contance;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.TreeMap;

/**
 * 注册界面
 *
 * @author Administrator
 */
public class RegisterActivity extends BaseActivity implements CallbackView {

	private  static String TAG=RegisterActivity.class.getSimpleName();
	@ViewInject(R.id.et_userphone)
	private ClearEditText mEtUserphone;


	/**
	 * 验证码。
	 */
	@ViewInject(R.id.et_pwd)
	private ClearEditText mEtPwd;

	@ViewInject(R.id.btn_send_code)
	private SDSendValidateButton mBt_send_code;

	@ViewInject(R.id.ch_register)
	private CheckBox mCh_register;

	@ViewInject(R.id.ll_register_xieyi)
	private LinearLayout mLl_xieyi;

	@ViewInject(R.id.tv_register)
	private TextView mTvRegister;

	/**
	 * 密码。
	 */

	@ViewInject(R.id.et_pwd_into)
	private ClearEditText mEt_pwd_into;

	/**
	 * 确认手机号。
	 */
	@ViewInject(R.id.pwd)
	private ClearEditText pwd;


	protected static String EXTRAS_Phone = "extras_phone";

	/**
	 * 验证码。
	 */
	private String mStrPwd;

	private String userPhone;
	/**
	 * 密码。
	 */
	private String passwordStr;
	protected Check_MobActModel mActModel;
	CommonHelper mFragmentHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setmTitleType(TitleType.TITLE);
		setContentView(R.layout.act_register);
		init();

	}

	private void init() {
		mFragmentHelper = new CommonHelper(this, this);
		initGetIntent();
		initTitle();
		registeClick();
		initRequest();
		initSDSendValidateButton();
	}


	private void initGetIntent() {

		String phone = getIntent().getStringExtra(EXTRAS_Phone);
		if (!TextUtils.isEmpty(phone)) {
			mEtUserphone.setText(phone);
		}
	}

	private void initTitle() {
		mTitle.setMiddleTextTop("注册");
	}

	private void registeClick() {
		mTvRegister.setOnClickListener(this);
		mLl_xieyi.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_register:
				clickRegister();
				break;
			case R.id.ll_register_xieyi:
				clickXieyi();
				break;
			default:
				break;
		}
	}

	private void clickXieyi() {
		Intent intent = new Intent(this, RegisterAgreementActivity.class);
		startActivity(intent);
	}

	private void clickRegister() {
		if (validateParam()) {
			doRegister();
			//requestShortSet();
		}
	}

	/**
	 * 注册 。
	 */
	public void doRegister() {
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("mobile", userPhone);
		params.put("pwd", passwordStr);
		params.put("captcha", mStrPwd);
		params.put("method", UserConstants.USER_REGISTER);
		OkHttpUtils.getInstance().post(null, params, new MgCallback() {

			@Override
			public void onSuccessListResponse(List<Result> resultList) {
				if (resultList == null || resultList.size()==0) {
					onErrorResponse("注册失败", null);
				}
				Log.d(TAG,resultList.toString());
			}

			@Override
			public void onErrorResponse(String message, String errorCode) {

				SDToast.showToast(message);
			}
		});
	}

	private void requestShortSet() {

		RequestModel model = new RequestModel();
		model.putCtl("user");
		model.putAct("verify_login");
		model.put("mobile", userPhone);
		model.put("sms_verify", mStrPwd);
		SDRequestCallBack<UserInfoModel> handler = new SDRequestCallBack<UserInfoModel>() {

			@Override
			public void onStart() {
				SDDialogManager.showProgressDialog("请稍候...");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (actModel.getVerify_status() == 0) {
					SDToast.showToast("验证码不正确");
				} else {
					if (actModel.getUser() == null) {
						Intent intent = new Intent(RegisterActivity.this, SetPwActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("mobile", userPhone);
						bundle.putString("sms_verify", mStrPwd);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}

			@Override
			public void onFinish() {
				SDDialogManager.dismissProgressDialog();
			}
		};
		InterfaceServer.getInstance().requestInterface(model, handler);
	}

	public void checkMobileExist() {
		userPhone = mEtUserphone.getText().toString();
		if (TextUtils.isEmpty(userPhone)) {
			SDToast.showToast("请输入手机号码");
			return;
		}
		mFragmentHelper.doCheckMobileExist(userPhone, new MgCallback() {
			@Override
			public void onSuccessListResponse(List<Result> resultList) {

			}
			@Override
			public void onSuccessResponse(String responseBody) {
				doGetCaptcha();
			}
           @Override
			public void onErrorResponse(String message, String errorCode) {
				SDToast.showToast(message);
			}
		});


	}

	public void initRequest() {
		RequestModel model = new RequestModel();
		userPhone = mEtUserphone.getText().toString();
		if (TextUtils.isEmpty(userPhone)) {
			SDToast.showToast("请输入手机号码");
			return;
		}
		model.putCtl("user");
		model.putAct("check_mobile");
		model.put("mobile", userPhone);
		SDRequestCallBack<Check_MobActModel> handler = new SDRequestCallBack<Check_MobActModel>() {
			@Override
			public void onStart() {
				SDDialogManager.showProgressDialog("请稍候...");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (actModel.getExists() == 1 || actModel.getIs_tmp() == 1) {
					Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
					intent.putExtra(LoginActivity.EXTRA_SELECT_TAG_INDEX, 1);
					intent.putExtra(LoginPhoneFragment.EXTRA_PHONE_NUMBER, userPhone);
					startActivity(intent);
				} else {
					requestSendCode();
				}
			}

			@Override
			public void onFinish() {
				SDDialogManager.dismissProgressDialog();
			}
		};
		InterfaceServer.getInstance().requestInterface(model, handler);
	}

	private void initSDSendValidateButton() {

		mBt_send_code.setmListener(new SDSendValidateButtonListener() {
			@Override
			public void onTick() {

			}

			@Override
			public void onClickSendValidateButton() {


				//	checkMobileExist();
				doGetCaptcha();
			}
		});
	}

	/**
	 * 用JAVA 接口请求验证码。
	 */
	private void doGetCaptcha() {
		userPhone = mEtUserphone.getText().toString();
		if (TextUtils.isEmpty(userPhone)) {
			SDToast.showToast("请输入手机号码");
			return;
		}
		//开始倒计时。
		mBt_send_code.setmDisableTime(Contance.SEND_CODE_TIME);
		mBt_send_code.startTickWork();
		mFragmentHelper.doGetCaptcha(userPhone, 1, new MgCallback() {

			public void onErrorResponse(String message, String errorCode) {

			}
			@Override
			public void onSuccessListResponse(List<Result> resultList) {
				SDToast.showToast("验证码发送成功");
			}

		});
	}



	/**
	 * 请求验证码
	 */
	protected void requestSendCode() {
		userPhone = mEtUserphone.getText().toString();
		if (TextUtils.isEmpty(userPhone)) {
			SDToast.showToast("请输入手机号码");
			return;
		}
		CommonInterface.requestValidateCode(userPhone, 0, new SDRequestCallBack<Sms_send_sms_codeActModel>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				switch (actModel.getStatus()) {
					case -1:
						break;
					case 1:
						mBt_send_code.setmDisableTime(actModel.getLesstime());
						mBt_send_code.startTickWork();
						break;

					default:
						break;
				}
			}

			@Override
			public void onStart() {
				SDDialogManager.showProgressDialog("请稍候");
			}

			@Override
			public void onFinish() {
				SDDialogManager.dismissProgressDialog();
			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}
		});
	}

	/**
	 * 参数有效性判断。
	 *
	 * @return
	 */
	private boolean validateParam() {
		userPhone = mEtUserphone.getText().toString();
		if (TextUtils.isEmpty(userPhone)) {
			SDToast.showToast("手机号不能为空");
			return false;
		}
		mStrPwd = mEtPwd.getText().toString();
		if (TextUtils.isEmpty(mStrPwd)) {
			SDToast.showToast("验证码不能为空");
			return false;
		}
		if (mCh_register.isChecked()) {
			SDToast.showToast("请先同意用户注册协议");
			return false;
		}
		String pwd2 = pwd.getText().toString();
		String pwd1 = mEt_pwd_into.getText().toString();
		if (!pwd2.equals(pwd1)) {
			SDToast.showToast("两次密码输入不一致。");
		}
		passwordStr = pwd1;
		return true;
	}




	@Override
	public void onSuccess(List<Result> responseBody) {

	}

	public void onSuccess(String responseBody) {

	}

	@Override
	public void onFailue(String responseBody) {

	}



}