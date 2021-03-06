package com.miguo.category;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanwe.ModifyPasswordActivity;
import com.fanwe.library.common.SDFragmentManager;
import com.fanwe.library.customview.SDTabItemCorner;
import com.fanwe.library.customview.SDViewBase;
import com.fanwe.library.customview.SDViewNavigatorManager;
import com.fanwe.library.dialog.SDDialogManager;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.model.User_infoModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.user.UserConstants;
import com.fanwe.user.model.ThirdLoginInfo;
import com.fanwe.user.model.UserInfoNew;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.app.HiBaseActivity;
import com.miguo.app.HiLoginActivity;
import com.miguo.dao.GetShareIdByCodeDao;
import com.miguo.dao.LoginByThirdDao;
import com.miguo.dao.ShoppingCartMultiAddDao;
import com.miguo.dao.impl.GetShareIdByCodeDaoImpl;
import com.miguo.dao.impl.LoginByThirdDaoImpl;
import com.miguo.dao.impl.ShoppingCartMultiAddDaoImpl;
import com.miguo.definition.ClassPath;
import com.miguo.definition.IntentKey;
import com.miguo.definition.RequestCode;
import com.miguo.factory.ClassNameFactory;
import com.miguo.fragment.HiLoginByMobileFragment;
import com.miguo.fragment.HiLoginQuickByMobileFragment;
import com.miguo.listener.HiLoginListener;
import com.miguo.live.views.customviews.MGToast;
import com.miguo.presenters.TencentIMBindPresenter;
import com.miguo.presenters.impl.TencentIMBindPresenterImpl;
import com.miguo.third.SinaUsersAPI;
import com.miguo.utils.BaseUtils;
import com.miguo.utils.ClipboardUtils;
import com.miguo.view.GetShareIdByCodeView;
import com.miguo.view.LoginByThirdView;
import com.miguo.view.TencentIMBindPresenterView;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import simbest.com.sharelib.ILoginCallback;
import simbest.com.sharelib.ShareUtils;

/**
 * Created by zlh on 2016/11/30.
 */

public class HiLoginCategory extends Category implements GetShareIdByCodeView, LoginByThirdView, TencentIMBindPresenterView {

    @ViewInject(R.id.title_layout)
    RelativeLayout titleLayout;

    @ViewInject(R.id.register)
    TextView register;

    @ViewInject(R.id.back)
    ImageView back;

    @ViewInject(R.id.tv_find_password)
    private TextView mTv_find_password;

    @ViewInject(R.id.act_login_new_tab_login_normal)
    private SDTabItemCorner mTabLoginNormal;

    @ViewInject(R.id.act_login_new_tab_login_phone)
    private SDTabItemCorner mTabLoginPhone;

    private SDViewNavigatorManager mViewManager;

    private List<Integer> mListSelectIndex;

    /**
     * qqq登录。
     */
    @ViewInject(R.id.qq_login)
    private ImageView qq_login;
    /**
     * 微博登录
     */

    @ViewInject(R.id.weibo_login)
    private ImageView weibo_login;
    /**
     * 微信登录
     */

    @ViewInject(R.id.weixin_login)
    private ImageView weixin_login;

    private String openId;

    //1:qq，2:微信，3：微博
    String platformType = "";
    private ShareUtils su;

    SHARE_MEDIA platform;
    /**
     * 头像。
     */
    String icon = "";
    /**
     * 密码。
     */
    String nick = "";
    /**
     * 分享ID。
     */
    String shareCode;

    long time;

    GetShareIdByCodeDao getShareIdByCodeDao;

    SDFragmentManager fragmentManager;

    LoginByThirdDao loginByThirdDao;

    TencentIMBindPresenter tencentIMBindPresenter;

    /**
     * 添加本地购物车到服务端
     */
    ShoppingCartMultiAddDao shoppingCartMultiAddDao;

    public HiLoginCategory(HiBaseActivity activity) {
        super(activity);
    }

    @Override
    protected void initFirst() {
        mViewManager = new SDViewNavigatorManager();
        mListSelectIndex = new ArrayList<>();
        getShareIdByCodeDao = new GetShareIdByCodeDaoImpl(this);
        loginByThirdDao = new LoginByThirdDaoImpl(this);
        shoppingCartMultiAddDao = new ShoppingCartMultiAddDaoImpl(this);
        tencentIMBindPresenter = new TencentIMBindPresenterImpl(this);
    }

    @Override
    protected void findCategoryViews() {
        ViewUtils.inject(this, getActivity());
    }

    @Override
    protected void initThisListener() {
        listener = new HiLoginListener(this);
    }

    @Override
    protected void setThisListener() {
        back.setOnClickListener(listener);
        register.setOnClickListener(listener);
        mTv_find_password.setOnClickListener(listener);
        qq_login.setOnClickListener(listener);
        weibo_login.setOnClickListener(listener);
        weixin_login.setOnClickListener(listener);
    }

    @Override
    protected void init() {
        getIntentData();
        initFragmentManager();
        initUmeng();
        initShareId();
        changeViewUnLogin();
    }

    @Override
    protected void initViews() {
        setTitlePadding(titleLayout);
    }

    private void getIntentData() {
        mListSelectIndex.add(0);
        mListSelectIndex.add(1);

        if (!mListSelectIndex.contains(getActivity().getmSelectTabIndex())) {
            getActivity().setmSelectTabIndex(0);
        }
    }

    private void initFragmentManager() {
        fragmentManager = new SDFragmentManager(getActivity().getSupportFragmentManager());
    }

    /**
     * 初始化umeng
     */
    private void initUmeng() {
        su = new ShareUtils(getActivity());
    }

    /**
     * 如果是从领取码进来的，要通过分享码，获取分享id
     * 回调View
     * {@link com.miguo.view.GetShareIdByCodeView}
     * {@link }
     */
    private void initShareId() {
        shareCode = "";
        String diamondCode = ClipboardUtils.checkCode(getActivity());
        if (!TextUtils.isEmpty(diamondCode)) {
            getShareIdByCodeDao.getShareIdByCode(diamondCode);
        }
    }

    private void changeViewUnLogin() {
        mTabLoginNormal.setTabName("账号登录");
        mTabLoginNormal.setTabTextSizeSp(18);
        mTabLoginNormal.setmPosition(SDTabItemCorner.EnumTabPosition.FIRST);

        mTabLoginPhone.setTabName("快捷登录");
        mTabLoginPhone.setTabTextSizeSp(18);
        mTabLoginPhone.setmPosition(SDTabItemCorner.EnumTabPosition.LAST);

        mViewManager.setItems(new SDViewBase[]{mTabLoginNormal, mTabLoginPhone});

        mViewManager.setmListener(new SDViewNavigatorManager.SDViewNavigatorManagerListener() {
            @Override
            public void onItemClick(View v, int index) {
                switch (index) {
                    case 0: // 正常登录
                        clickLoginNormal();
                        break;
                    case 1: // 快捷登录
                        clickLoginPhone();
                        break;
                    default:
                        break;
                }
            }
        });

        mViewManager.setSelectIndex(getActivity().getmSelectTabIndex(), null, true);
    }

    /**
     * 找回密码
     */
    public void clickFindPassword() {
        Intent intent = new Intent(getActivity(), ModifyPasswordActivity.class);
        intent.putExtra("pageType", "forget");
        BaseUtils.jumpToNewActivity(getActivity(), intent);
    }

    /**
     * 点击返回
     */
    public void clickBack() {
        BaseUtils.finishActivity(getActivity());
    }

    /**
     * 点击注册
     */
    public void clickRegister() {
        Intent intent = new Intent(getActivity(), ClassNameFactory.getClass(ClassPath.REGISTER_ACTIVITY));
        intent.putExtra(UserConstants.SHARE_ID, getShareCode());
        goRegisterActivity(intent);
    }

    public void clickQQLogin() {
        platform = SHARE_MEDIA.QQ;
        goToAuth(platform);
    }

    public void clickWeiboLogin() {
        platform = SHARE_MEDIA.SINA;
        platformType = "3";
        loginBySina();
    }

    public void clickWechatLogin() {
        platform = SHARE_MEDIA.WEIXIN;
        goToAuth(platform);
    }

    private SsoHandler mSsoHandler;

    /**
     * 新浪微博登录
     */
    private void loginBySina() {
        // 创建授权认证信息
        AuthInfo mAuthInfo = new AuthInfo(getActivity(), UserConstants.APP_KEY, UserConstants.REDIRECT_URL, UserConstants.SCOPE);
        mSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
        //all In one先调用客户端，如果没有客户端就调用web
        mSsoHandler.authorize(new AuthListener());
    }

    /**
     * 新浪微博登录监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                //通过uid访问新浪接口，获取用户信息
                SinaUsersAPI usersAPI = new SinaUsersAPI(getActivity(), UserConstants.APP_KEY, accessToken);
                usersAPI.show(Long.valueOf(accessToken.getUid()), new SinaRequestListener());
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }

        @Override
        public void onCancel() {
        }
    }

    //新浪微博请求接口
    private class SinaRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String id = jsonObject.getString("idstr");// 唯一标识符(uid)
                String name = jsonObject.getString("name");// 名字
                String icon = jsonObject.getString("avatar_hd");// 头像
                loginByThird(id, platformType, icon, name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }

    /**
     * 正常登录的选项卡被选中
     */
    protected void clickLoginNormal() {
        SDViewUtil.show(mTv_find_password);
        getSDFragmentManager().toggle(R.id.act_login_fl_content, null, HiLoginByMobileFragment.class);
    }

    /**
     * 手机号快捷登录的选项卡被选中
     */
    protected void clickLoginPhone() {
        SDViewUtil.hide(mTv_find_password);
        getSDFragmentManager().toggle(R.id.act_login_fl_content, null, HiLoginQuickByMobileFragment.class);
    }

    /**
     * 跳转到相应的授权页。
     *
     * @param platform
     */
    public void goToAuth(final SHARE_MEDIA platform) {
        su.login(platform, new ILoginCallback() {
            @Override
            public void onSuccess(Map<String, String> data) {
                SDDialogManager.showProgressDialog("正在登录,请稍候...");
                if (platform.equals(SHARE_MEDIA.WEIXIN)) {
                    platformType = "2";
                    openId = data.get("unionid");
                    nick = data.get("nickname");
                    icon = data.get("headimgurl");
                } else if (platform.equals(SHARE_MEDIA.QQ)) {
                    platformType = "1";
                    openId = data.get("openid");
                    icon = data.get("profile_image_url");
                    nick = data.get("screen_name");
                }
                if (TextUtils.isEmpty(openId)) {
                    MGToast.showToast("登录失败");
                    return;
                }
                //防止过快请求
                if ((System.currentTimeMillis() - time) < 100) {
                    return;
                } else {
                    time = System.currentTimeMillis();
                }
                loginByThird(openId, platformType, icon, nick);
            }

            @Override
            public void onFaild(String msg) {
                MGToast.showToast("登录失败");
            }

            @Override
            public void onCancel() {
                MGToast.showToast("取消登录");
            }
        });
    }

    private void loginByThird(String openId, String platformType, String icon, String nick) {
        loginByThirdDao.thirdLogin(openId, platformType, icon, nick);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        su.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 手机号 + 密码登录
     * 手机号 + 验证码登录
     * 成功后的回调
     */
    public void handleLoginSuccess(UserInfoNew user) {
        /**
         * 登录成功后要和IM绑定获取腾讯sign签名等
         * {@link com.miguo.presenters.impl.TencentIMBindPresenterImpl}
         * {@link com.miguo.view.TencentIMBindPresenterView}
         * {@link #tencentIMBindFinish()}
         */
        tencentIMBindPresenter.tencentIMBindingWithPushLocalCart();
    }

    @Override
    public void tencentIMBindFinish() {
        handleTencentIMFinish();
    }

    /**
     * 注册绑定IM成功后调用
     * 不管绑定成功失败
     */
    private void handleTencentIMFinish() {
        finishActivity();
    }

    private void finishActivity() {
        if (getActivity() instanceof HiLoginActivity) {
            getActivity().finishActivity2();
            return;
        }
        BaseUtils.finishActivity(getActivity());
    }


    /**
     * 根据领取码获取分享id回调
     */
    @Override
    public void getShareIdError(String message) {
    }

    @Override
    public void getShareIdSucess(String shareId) {
        setShareCode(shareId);
    }

    /**
     * 第三方登录回调
     *
     * @return
     */
    @Override
    public void thirdLoginError(String message) {
        showToast(message);
    }

    @Override
    public void thirdLoginSuccess(User_infoModel userInfoModel, UserInfoNew userInfoNew) {
        handleLoginSuccess(userInfoNew);
    }

    @Override
    public void thirdLoginUnRegister(ThirdLoginInfo thirdLoginInfo) {
        if (null != thirdLoginInfo) {
            handleThirdLoginUnRegister(true, thirdLoginInfo);
        }
    }

    protected void handleThirdLoginUnRegister(boolean third, ThirdLoginInfo thirdLoginInfo) {
        Intent intent = new Intent(getActivity(), ClassNameFactory.getClass(ClassPath.REGISTER_ACTIVITY));
        String openId = thirdLoginInfo.getOpenId();
        String type = thirdLoginInfo.getPlatformType();
        String icon = thirdLoginInfo.getIcon();
        String nick = thirdLoginInfo.getNick();

        if (third && !TextUtils.isEmpty(openId)) {
            intent.putExtra(UserConstants.THIRD_OPENID, openId);
            intent.putExtra(UserConstants.THIRD_PLATFORM, type);
            intent.putExtra(UserConstants.THIRD_ICON, icon);
            intent.putExtra(UserConstants.THIRD_NICK, nick);
        }

        intent.putExtra(UserConstants.SHARE_ID, getShareCode());
        goRegisterActivity(intent);
    }

    private void goRegisterActivity(Intent intent) {
        /**
         * 如果是从领钻码进来的，则跳注册界面的时候不要销毁当前activity，应该注册成功后回调回来再结束，把结果传给HiHomeActivity
         */
        if (getActivity().isFromDiamond()) {
            intent.putExtra(IntentKey.FROM_DIAMOND_TO_LOGIN, getActivity().isFromDiamond());
            BaseUtils.jumpToNewActivityForResult(getActivity(), intent, RequestCode.LOGIN_SUCCESS_FOR_DIAMON);
            return;
        }
        BaseUtils.jumpToNewActivityWithFinish(getActivity(), intent);
    }

    public SDFragmentManager getSDFragmentManager() {
        return fragmentManager;
    }

    public Intent getIntent() {
        return getActivity().getIntent();
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    @Override
    public HiLoginActivity getActivity() {
        return (HiLoginActivity) super.getActivity();
    }
}
