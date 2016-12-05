package com.fanwe.user.presents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.fanwe.RegisterActivity;
import com.fanwe.app.App;
import com.fanwe.base.CallbackView;
import com.fanwe.base.Presenter;
import com.fanwe.base.Root;
import com.fanwe.fragment.LoginFragment;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.dialog.SDDialogManager;
import com.fanwe.library.utils.MD5Util;
import com.fanwe.model.LocalUserModel;
import com.fanwe.model.User_infoModel;
import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtils;
import com.fanwe.shoppingcart.RefreshCalbackView;
import com.fanwe.shoppingcart.model.LocalShoppingcartDao;
import com.fanwe.shoppingcart.model.ShoppingCartInfo;
import com.fanwe.shoppingcart.presents.OutSideShoppingCartHelper;
import com.fanwe.user.UserConstants;
import com.fanwe.user.model.ThirdLoginInfo;
import com.fanwe.user.model.UserInfoNew;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguo.app.HiHomeActivity;
import com.miguo.definition.ClassPath;
import com.miguo.definition.ResultCode;
import com.miguo.factory.ClassNameFactory;
import com.miguo.live.model.generateSign.ModelGenerateSign;
import com.miguo.live.model.generateSign.ResultGenerateSign;
import com.miguo.live.model.generateSign.RootGenerateSign;
import com.miguo.live.presenters.TencentHttpHelper;
import com.miguo.live.views.customviews.MGToast;
import com.miguo.utils.BaseUtils;
import com.miguo.utils.MGUIUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


/**
 * Created by Administrator on 2016/7/22.
 */
public class LoginHelper extends Presenter {
    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginFragment mLoginView;
    private Activity mActivity;
    private boolean notClose = false;
    private boolean ifShowToast =true;


    private TencentHttpHelper mTencentHttpHelper;
    private com.tencent.qcloud.suixinbo.presenters.LoginHelper mTLoginHelper;


    public LoginHelper(Context context) {

        mContext = context;
        mTencentHttpHelper = new TencentHttpHelper(mContext);
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(mContext);

    }

    public LoginHelper(Activity activity, Context context, LoginFragment loginView) {
        this.mActivity = activity;

        mLoginView = loginView;
        mContext = context;
        mTencentHttpHelper = new TencentHttpHelper(mContext);
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(mContext);
    }

    public LoginHelper(Context context, LoginFragment loginView) {

        mLoginView = loginView;
        mContext = context;
        mTencentHttpHelper = new TencentHttpHelper(mContext);
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(mContext);
    }

    public LoginHelper(Activity activity) {
        this.mActivity = activity;
        mTencentHttpHelper = new TencentHttpHelper(mContext);
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(activity);
    }

    /**
     * 快捷登录。
     *
     * @param mobile
     * @param captcha
     */
    public void doQuickLogin(final String mobile, String captcha , final View view,String share_record_id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", mobile);
        params.put("captcha", captcha);
        params.put("share_record_id",share_record_id);
        params.put("method", UserConstants.USER_QUICK_LOGIN);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<UserInfoNew>>() {
                }.getType();
                Gson gson = new Gson();
                Root<UserInfoNew> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                if (!"200".equals(status)){
                    if (view!=null){
                        MGUIUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.setEnabled(true);
                            }
                        });
                    }
                }
                SDDialogManager.dismissProgressDialog();
                dealLoginInfo(responseBody, mobile, null);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                if (view!=null){
                    MGUIUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    });
                }
                SDDialogManager.dismissProgressDialog();
                MGToast.showToast(message);
            }
        });

    }


    /**
     * @param openId
     * @param platformType
     * @param icon         头像地址
     * @param icon         昵称
     */
    public void thirdLogin(final String openId, final String platformType, final String icon, final String nick, final CallbackView mCallbackView) {
        if (TextUtils.isEmpty(openId)) {
            return;
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("openid", openId);
        params.put("platform", platformType);
        if (!TextUtils.isEmpty(icon)) {
            params.put("icon", icon);
        }
        params.put("nick", nick);

        params.put("method", UserConstants.TRHID_LOGIN_URL);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onErrorResponse(String message, String errorCode) {

            }

            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<UserInfoNew>>() {
                }.getType();
                Gson gson = new Gson();
                Root<UserInfoNew> root = gson.fromJson(responseBody, type);
                String statusCode = root.getStatusCode();
                String message = root.getMessage();
                List datas = new ArrayList();
                if ("210".equals(statusCode)) {
                    UserInfoNew userInfoNew = (UserInfoNew) validateBody(root);
                    if (userInfoNew != null) {
                        if (userInfoNew != null) {
                            App.getInstance().setCurrentUser(userInfoNew);
                            User_infoModel model = new User_infoModel();
                            model.setUser_id(userInfoNew.getUser_id());
                            model.setMobile(userInfoNew.getMobile());
                            model.setUser_name(userInfoNew.getUser_name());
                            model.setUser_pwd(userInfoNew.getPwd());
                            dealLoginInfo(responseBody, userInfoNew.getUser_name(), userInfoNew.getPwd());
                            datas.add(model);
                            mCallbackView.onSuccess(UserConstants.THIRD_LOGIN_SUCCESS, datas);
                        }
                    }
                } else if ("300".equals(statusCode)) {
                    ThirdLoginInfo thirdLoginInfo = new ThirdLoginInfo();
                    thirdLoginInfo.setIcon(icon);
                    thirdLoginInfo.setNick(nick);
                    thirdLoginInfo.setOpenId(openId);
                    thirdLoginInfo.setPlatformType(platformType);
                    datas.add(thirdLoginInfo);
                    mCallbackView.onSuccess(UserConstants.THIRD_LOGIN_UNREGISTER, datas);
                } else {
                    mCallbackView.onFailue(message);
                }

            }

            @Override
            public void onFinish() {
                SDDialogManager.dismissProgressDialog();
            }
        });

    }

    /**
     * 登录
     *
     * @param userName
     * @param password
     * @param type     0为手机登录。1为第三方登录
     */
    public void doLogin(final String userName, final String password, int type, final View view) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userName);
        params.put("pwd", password);
        params.put("method", UserConstants.USER_lOGIN);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<UserInfoNew>>() {
                }.getType();
                Gson gson = new Gson();
                Root<UserInfoNew> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                if (!"200".equals(status)){
                    if (view!=null){
                        MGUIUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.setEnabled(true);
                            }
                        });
                    }
                }
                SDDialogManager.dismissProgressDialog();
                dealLoginInfo(responseBody, userName, password);
            }

            @Override
            public void onFinish() {
                App.isLoging = false;
            }

            @Override

            public void onErrorResponse(String message, String errorCode) {
                if (view!=null){
                    MGUIUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    });
                }
                SDDialogManager.dismissProgressDialog();
                if(ifShowToast) {
                    MGToast.showToast(message);
                }
            }
        });

    }

    public void doLogin(final String userName, final String password, int type, boolean notClose) {
        this.ifShowToast = true;
        this.notClose = notClose;
        doLogin(userName, password, type,null);
    }

    /**
     * 登录时候是否要提示错误信息。
     * @param userName
     * @param password
     * @param type
     * @param notClose
     * @param ifShowToast
     */
    public void doLogin(final String userName, final String password, int type, boolean notClose,boolean ifShowToast) {
        this.ifShowToast = ifShowToast;
        this.notClose = notClose;
        doLogin(userName, password, type,null);
    }

    /**
     * 注册 。
     */
    public void doRegister(final String userName, String captcha, final String password,String share_record_id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userName);
        params.put("pwd", MD5Util.MD5(password));
        params.put("captcha", captcha);
        params.put("share_record_id",share_record_id);
        params.put("method", UserConstants.USER_REGISTER);
        OkHttpUtils.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                dealLoginInfo(responseBody, userName, password);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {

                MGToast.showToast(message);
            }
        });
    }

    /**
     * 第三方注册 。
     */
    public void doThirdRegister(final String userPhone, String openid, String captcha, String icon, String nick, String platform,String share_record_id) {
        if (TextUtils.isEmpty(platform) || TextUtils.isEmpty(openid)) {
            MGToast.showToast("第三方登录失败");
            if (mActivity != null) {
                mActivity.finish();
            }
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userPhone);
        params.put("openid", openid);
        params.put("share_record_id",share_record_id);
        params.put("captcha", captcha);
        if (!TextUtils.isEmpty(icon)) {
            params.put("icon", icon);
        }

        if (!TextUtils.isEmpty(nick)) {
            params.put("nick", nick);
        }
        params.put("platform", platform);
        params.put("method", UserConstants.THIRD_REGISTER_URL);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody) {

                dealLoginInfo(responseBody, userPhone, null);

            }

            @Override
            public void onErrorResponse(String message, String errorCode) {

                MGToast.showToast(message);
            }
        });
    }

    /**
     * 取sign.
     */
    public void getSign(String token) {
        MgCallback mgCallback = new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Gson gson = new Gson();
                RootGenerateSign rootGenerateSign = gson.fromJson(responseBody, RootGenerateSign.class);
                List<ResultGenerateSign> resultGenerateSigns = rootGenerateSign.getResult();
                if (resultGenerateSigns == null || resultGenerateSigns.size() < 1) {
                    MGToast.showToast("获取用户签名失败。");
                    return;
                }
                ResultGenerateSign resultGenerateSign = resultGenerateSigns.get(0);
                List<ModelGenerateSign> modelGenerateSign = resultGenerateSign.getBody();

                if (modelGenerateSign != null && modelGenerateSign.size() > 0 && modelGenerateSign.get(0) != null) {
                    String usersig = modelGenerateSign.get(0).getUsersig();
                    App.getInstance().setUserSign(usersig);
                    MySelfInfo.getInstance().setUserSig(usersig);
                    App.getInstance().setUserSign(usersig);
                    String userId = MySelfInfo.getInstance().getId();

                    if (TextUtils.isEmpty(userId)) {
                        UserInfoNew currentInfo = App.getInstance().getCurrentUser();
                        if (currentInfo != null) {
                            userId = currentInfo.getUser_id();
                        } else {
                            return;
                        }
                    }
                    mTLoginHelper.imLoginWithoutGetRoom(userId, usersig);
                    loginSuccess();
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast("获取用户签名失败。");

            }
        };
        mTencentHttpHelper.getSign(token, mgCallback);
    }

    public void dealLoginInfo(String responseBody, String userName, String password) {
        Type type = new TypeToken<Root<UserInfoNew>>() {
        }.getType();
        Gson gson = new Gson();
        Root<UserInfoNew> root = gson.fromJson(responseBody, type);
        String status = root.getStatusCode();
        String message = root.getMessage();

        UserInfoNew userInfoNew = (UserInfoNew) validateInfoBody(root);
        if (userInfoNew != null) {
            App.getInstance().setCurrentUser(userInfoNew);
            User_infoModel model = new User_infoModel();
            model.setUser_id(userInfoNew.getUser_id());
            MySelfInfo.getInstance().setId(userInfoNew.getUser_id());
            if (!TextUtils.isEmpty(userName)) {
                model.setMobile(userName);
            }
            if (!TextUtils.isEmpty(password)) {
                model.setUser_pwd(password);
            }
            if (!TextUtils.isEmpty(userInfoNew.getPwd())) {
                model.setUser_pwd(userInfoNew.getPwd());
            }
            model.setUser_name(userInfoNew.getUser_name());

            dealLoginSuccess(model);
        } else {
            if(ifShowToast) {
                MGToast.showToast(TextUtils.isEmpty(message) == true ? "登录失败。" : message);
            }
        }
    }
    /**
     * 退出imsdk
     * <p>
     * 退出成功会调用退出AVSDK
     */
    public void imLogout() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                SxbLog.e(TAG, "IMLogout fail ：" + i + " msg " + s);
            }
            @Override
            public void onSuccess() {
                SxbLog.i(TAG, "IMLogout succ !");
                //反向初始化avsdk
                stopAVSDK();
            }
        });

    }

    /**
     * 反初始化AVADK
     */
    public void stopAVSDK() {
        QavsdkControl.getInstance().stopContext();

    }


    public void loginSuccess() {
        Activity lastActivity = SDActivityManager.getInstance().getLastActivity();
        if (notClose) {

            return;
        }

        if(mActivity instanceof RegisterActivity){
            RegisterActivity activity = (RegisterActivity)mActivity;
            if(activity.isFromDiamond()){
                Intent intent = new Intent(activity, ClassNameFactory.getClass(ClassPath.LOGIN_ACTIVITY));
                activity.setResult(ResultCode.RESUTN_OK, intent);
                BaseUtils.finishActivity(activity);
            }else {

                if (lastActivity instanceof HiHomeActivity) {
                    mActivity.finish();
                } else {
                    mActivity.startActivity(new Intent(mActivity, ClassNameFactory.getClass(ClassPath.HOME_ACTIVITY)));
                }

            }
        }


    }

    protected void dealLoginSuccess(User_infoModel actModel) {
        String token = App.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            //不成功也跳转。
            loginSuccess();
        } else {
            //baocun
            LocalUserModel.dealLoginSuccess(actModel, true);
            putLocalShoppingToServer();
            getSign(token);
        }
    }

    /**
     * 把本地购物车提交到线上用户。
     */
    public void putLocalShoppingToServer() {
        List<ShoppingCartInfo> list = App.getInstance().getLocalShoppingCartInfo();
        if (list != null && list.size() > 0) {

            final OutSideShoppingCartHelper helper = new OutSideShoppingCartHelper(new RefreshCalbackView() {
                @Override
                public void onFailue(String method, String responseBody) {

                }

                @Override
                public void onSuccess(String responseBody) {

                }

                @Override
                public void onSuccess(String method, List datas) {
                    LocalShoppingcartDao.deleteAllModel();
                }

                @Override
                public void onFailue(String responseBody) {

                }
            });
            helper.multiAddShopCart(list,false);
        }
    }

    /**
     * 判断BODY对象是否存在。
     *
     * @param root
     * @return
     */

    public UserInfoNew validateInfoBody(Root<UserInfoNew> root) {

        if (root.getResult() != null && root.getResult().size() > 0 && root.getResult().get(0) != null && root.getResult().get(0).getBody() != null && root.getResult().get(0).getBody().size() > 0) {
            return root.getResult().get(0).getBody().get(0);
        }
        return null;
    }

    @Override
    public void onDestory() {
        mLoginView = null;
        mContext = null;
        mActivity = null;
    }
}
