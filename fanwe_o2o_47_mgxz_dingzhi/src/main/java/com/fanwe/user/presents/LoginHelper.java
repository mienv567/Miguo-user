package com.fanwe.user.presents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fanwe.LoginActivity;
import com.fanwe.MainActivity;
import com.fanwe.app.App;
import com.fanwe.base.Presenter;
import com.fanwe.base.Root;
import com.fanwe.fragment.LoginFragment;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.utils.MD5Util;
import com.fanwe.library.utils.SDToast;
import com.fanwe.model.LocalUserModel;
import com.fanwe.model.User_infoModel;
import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtils;
import com.fanwe.user.UserConstants;
import com.fanwe.user.model.UserCurrentInfo;
import com.fanwe.user.model.UserInfoNew;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguo.live.model.generateSign.ModelGenerateSign;
import com.miguo.live.model.generateSign.ResultGenerateSign;
import com.miguo.live.model.generateSign.RootGenerateSign;
import com.miguo.live.presenters.TencentHttpHelper;
import com.tencent.TIMCallBack;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.presenters.ProfileInfoHelper;

import org.apache.http.auth.UsernamePasswordCredentials;

import java.lang.reflect.Type;
import java.util.List;
import java.util.TreeMap;


/**
 * Created by Administrator on 2016/7/22.
 */
public class LoginHelper extends Presenter {
    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginFragment mLoginView;
    private int RoomId = -1;
    private Activity mActivity;
    private boolean notClose =false;

    private TencentHttpHelper mTencentHttpHelper;
    private com.tencent.qcloud.suixinbo.presenters.LoginHelper mTLoginHelper;
    private   IMUserInfoHelper mIMUserInfoHelper;

    public LoginHelper(Context context) {

        mContext = context;
        mTencentHttpHelper = new TencentHttpHelper(mContext);
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(mContext);
        mIMUserInfoHelper = new IMUserInfoHelper();
    }
    public LoginHelper(Activity activity,Context context, LoginFragment loginView) {
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
        mTLoginHelper = new com.tencent.qcloud.suixinbo.presenters.LoginHelper(activity);
    }
    /**
     * 快捷登录。
     * @param mobile
     * @param captcha
     */
    public void doQuickLogin(final String mobile,String captcha){
        TreeMap<String, String> params = new TreeMap<String,String>();
        params.put("mobile", mobile);
        params.put("captcha",captcha);
        params.put("method", UserConstants.USER_QUICK_LOGIN);
        OkHttpUtils.getInstance().get(null,params,new MgCallback(){

            @Override
            public void onSuccessResponse(String responseBody) {
                dealLoginInfo(responseBody,mobile,null);

            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                SDToast.showToast(message);
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
    public void doLogin(final String userName, final String password, int type) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userName);
        params.put("pwd", password);
        params.put("method", UserConstants.USER_lOGIN);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody)  {
                dealLoginInfo(responseBody,userName,password);

            }


            @Override

            public void onErrorResponse(String message, String errorCode) {
                SDToast.showToast(message);
            }
        });

    }
    public void doLogin(final String userName, final String password, int type,boolean notClose) {
        this.notClose = notClose;
        doLogin(userName,password,type);
    }

    /**
     * 注册 。
     */
    public void doRegister(final String userName, String captcha, final String password) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userName);
        params.put("pwd", MD5Util.MD5(password));
        params.put("captcha", captcha);
        params.put("method", UserConstants.USER_REGISTER);
        OkHttpUtils.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                dealLoginInfo(responseBody,userName,password);
            }
            @Override
            public void onErrorResponse(String message, String errorCode) {

                SDToast.showToast(message);
            }
        });
    }
    /**
     * 第三方注册 。
     */
    public void doThirdRegister(final  String userPhone,String openid,String captcha,String icon,String nick,String platform){
        if (TextUtils.isEmpty(platform)||TextUtils.isEmpty(openid)) {
            SDToast.showToast("第三方登录失败");
            if(mActivity!=null){
                mActivity.finish();
            }
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("mobile", userPhone);
        params.put("openid", openid);
        params.put("captcha", captcha);
        if(!TextUtils.isEmpty(icon)) {
            params.put("icon", icon);
        }

        if(!TextUtils.isEmpty(nick)) {
            params.put("nick", nick);
        }
        params.put("platform",platform);
        params.put("method", UserConstants.THIRD_REGISTER_URL);
        OkHttpUtils.getInstance().get(null, params, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody) {
                dealLoginInfo(responseBody,userPhone,null);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {

                SDToast.showToast(message);
            }
        });
    }

    /**
     * 退出
     */
    public void doLogout() {


    }

    public void doImLogin(){
        String userid = MySelfInfo.getInstance().getId();
        if(TextUtils.isEmpty(userid)){
            userid = App.getInstance().getmUserCurrentInfo().getUserInfoNew().getUser_id();
        }
        String userSign = App.getInstance().getUserSign();
        mTLoginHelper.imLogin(userid,userSign,new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                SDToast.showToast("IM 认证失败。");
                App.getInstance().setImLoginSuccess(false);
            }
            @Override
            public void onSuccess() {
                App.getInstance().setImLoginSuccess(true);
            }
        });
    }
    /**
     * 取sign.
     */
    public void getSign(String token){
        MgCallback mgCallback = new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Gson gson=new Gson();
                RootGenerateSign rootGenerateSign = gson.fromJson(responseBody, RootGenerateSign.class);
                List<ResultGenerateSign> resultGenerateSigns = rootGenerateSign.getResult();
                if (resultGenerateSigns==null||resultGenerateSigns.size()<1) {
                    SDToast.showToast("获取用户签名失败。");
                    return;
                }
                ResultGenerateSign resultGenerateSign = resultGenerateSigns.get(0);
                List<ModelGenerateSign> modelGenerateSign = resultGenerateSign.getBody();

                if(modelGenerateSign!=null&& modelGenerateSign.size()>0&&modelGenerateSign.get(0)!=null) {
                    String usersig = modelGenerateSign.get(0).getUsersig();
                    App.getInstance().setUserSign(usersig);
                    MySelfInfo.getInstance().setUserSig(usersig);
                    App.getInstance().setUserSign(usersig);
                    String userId = MySelfInfo.getInstance().getId();

                    if(TextUtils.isEmpty(userId)){
                        UserCurrentInfo currentInfo = App.getInstance().getmUserCurrentInfo();
                        if(currentInfo!=null&&currentInfo.getUserInfoNew()!=null){
                            userId = currentInfo.getUserInfoNew().getUser_id();
                        }else{
                            return;
                        }
                    }
                    mTLoginHelper.imLoginWithoutGetRoom(userId,usersig);
                    loginSuccess();

                };
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                SDToast.showToast("获取用户签名失败。");

            }
        };
        mTencentHttpHelper.getSign(token,mgCallback);
    }

    public void dealLoginInfo(String responseBody,String userName,String password){
        Type type = new TypeToken<Root<UserInfoNew>>() {
        }.getType();
        Gson gson = new Gson();
        Root<UserInfoNew> root = gson.fromJson(responseBody, type);
        UserInfoNew userInfoNew = (UserInfoNew) validateBody(root);
        if (userInfoNew != null) {
            if (userInfoNew != null) {
                App.getInstance().getmUserCurrentInfo().setUserInfoNew(userInfoNew);
                User_infoModel model = new User_infoModel();
                model.setUser_id(userInfoNew.getUser_id());
                MySelfInfo.getInstance().setId(userInfoNew.getUser_id());
                if(!TextUtils.isEmpty(userName)) {
                    model.setMobile(userName);
                }
                if(!TextUtils.isEmpty(password)) {
                    model.setUser_pwd(password);
                }
                if(!TextUtils.isEmpty(userInfoNew.getPwd())){
                    model.setUser_pwd(userInfoNew.getPwd());
                }
                model.setUser_name(userInfoNew.getUser_name());
                dealLoginSuccess(model);
            }
        }
    }
    /**
     * 判断BODY对象是否存在。
     * @param root
     * @return
     */
    public  UserInfoNew validateBody(Root<UserInfoNew> root) {

        if (root.getResult() != null && root.getResult().size() > 0 && root.getResult().get(0) != null && root.getResult().get(0).getBody() != null && root.getResult().get(0).getBody().size() > 0)
        {
            return root.getResult().get(0).getBody().get(0);
        }
        return null;

    }


    public void loginSuccess(){
        Activity lastActivity = SDActivityManager.getInstance().getLastActivity();
        if(notClose){

            return;
        }

        if (lastActivity instanceof MainActivity) {
            mActivity.finish();
        } else {
            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
        }


    }
    protected void dealLoginSuccess(User_infoModel actModel) {
        String token = App.getInstance().getToken();
        if(TextUtils.isEmpty(token)){
            //不成功也跳转。
            loginSuccess();
        }else{
            //baocun
            LocalUserModel.dealLoginSuccess(actModel, true);
            getSign(token);
        }





    }
    @Override
    public void onDestory() {
        mLoginView = null;
        mContext = null;
        mActivity = null;
    }
}
