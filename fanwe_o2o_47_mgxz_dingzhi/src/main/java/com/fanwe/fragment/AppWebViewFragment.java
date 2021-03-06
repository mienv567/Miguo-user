package com.fanwe.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.fanwe.app.App;
import com.fanwe.base.CallbackView;
import com.fanwe.common.model.CommonConstants;
import com.fanwe.common.model.createShareRecord.ModelCreateShareRecord;
import com.fanwe.common.presenters.CommonHttpHelper;
import com.fanwe.constant.Constant;
import com.fanwe.constant.ServerUrl;
import com.fanwe.jshandler.AppJsHandler;
import com.fanwe.library.fragment.WebViewFragment;
import com.fanwe.library.title.TitleItemConfig;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDHandlerUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.getGroupDeatilNew.ShareInfoBean;
import com.fanwe.utils.MGDictUtil;
import com.fanwe.utils.ShareUtil;

import java.util.List;

public class AppWebViewFragment extends WebViewFragment implements CallbackView {

    private String mContent;
    private String id;
    private String title = "米果小站";
    private String url;
    private String mSummary;
    private String imageUrl;

    private CommonHttpHelper commonHttpHelper;
    private String shareRecordId;

    @Override
    public void setUrl(String url) {
        this.mStrUrl = url;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public void setUserId(String id) {
        this.id = id;
    }

    public void setShareContent(String location, String title, String summary, String pic) {
        url = location;
        mContent = title;
        mSummary = summary;
        imageUrl = pic;
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void addJavascriptInterface() {
        // 详情回调处理
        AppJsHandler detailHandler = new AppJsHandler(getActivity()) {
            @Override
            @JavascriptInterface
            public void setPageTitle(final String title) {
                SDHandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTitle.setMiddleTextTop(title);
                        setTitle(title);
                    }
                });
            }

            @Override
            @JavascriptInterface
            public void promote(final String location, final String title, final String summary,
                                final String pic) {
                SDHandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setShareContent(location, title, summary, pic);
                    }
                });
            }
        };
        mWeb.addJavascriptInterface(detailHandler, "mgxz");
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getRecordId();
        int resId = getContentViewResId();
        if (resId == 0) {
            resId = R.layout.frag_webview;
        }
        return setContentView(resId);
    }

    @Override
    protected WebView findWebView() {
        return super.findWebView();
    }

    @Override
    protected ProgressBar findProgressBar() {
        return super.findProgressBar();
    }

    @Override
    protected View onCreateTitleView() {
        return super.onCreateTitleView();
    }


    @Override
    protected void init() {
        super.init();
        initTitle(title);

    }

    @Override
    protected void initWebView() {
        initSetting();
        //交汇起冲突
        addJavascriptInterface();
        mWeb.setWebViewClient(getWebViewClient());
        mWeb.setWebChromeClient(getWebChromeClient());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSetting() {
        WebSettings webSettings = mWeb.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWeb.setBackgroundColor(Color.parseColor("#f4f4f4"));
        //添加 https 与 http 混合支持
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    protected void onProgressChanged_WebChromeClient(WebView view, int newProgress) {
        if (mProgressMode != null) {
            switch (mProgressMode) {
                case HORIZONTAL:
                    if (newProgress == 100) {
                        SDViewUtil.hide(mPgbHorizontal);
                    } else {
                        SDViewUtil.show(mPgbHorizontal);
                        mPgbHorizontal.setProgress(newProgress);
                    }
                    break;
                case NONE:

                    break;

                default:
                    break;
            }
        }
    }

    private void initTitle(String title) {
        mTitle.setMiddleTextTop(title);
        if (TextUtils.isEmpty(id)) {
            mTitle.removeAllRightItems();
            mTitle.addItemRight_TEXT("推广");
        } else {
            mTitle.removeAllRightItems();
            if ("lottery".equals(id)) {
                mTitle.setMiddleTextTop("幸运大抽奖");
            } else if ("lotteryList".equals(id)) {
                mTitle.setMiddleTextTop("我的奖品");
            }
        }
    }


    @Override
    public void onRightClick_SDTitleListener(TitleItemConfig config, int index, View view) {
        getRecordId();
        if (TextUtils.isEmpty(mSummary)) {
            mSummary = "欢迎来到我的小店";
        }
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "http://www.mgxz.com/pcApp/Common/images/logo2.png";
            if (!TextUtils.isEmpty(App.getInstance().getCurrentUser().getIcon())) {
                imageUrl = App.getInstance().getCurrentUser().getIcon();
            } else if (!TextUtils.isEmpty(MGDictUtil.getShareIcon())) {
                imageUrl = MGDictUtil.getShareIcon();
            }
        } else if (!imageUrl.startsWith("http")) {
            imageUrl = "http://www.mgxz.com/pcApp/Common/images/logo2.png";
            if (!TextUtils.isEmpty(App.getInstance().getCurrentUser().getIcon())) {
                imageUrl = App.getInstance().getCurrentUser().getIcon();
            } else if (!TextUtils.isEmpty(MGDictUtil.getShareIcon())) {
                imageUrl = MGDictUtil.getShareIcon();
            }
        }
        try {
            if (TextUtils.isEmpty(url)) {
                url = ServerUrl.getAppH5Url() + "user/shop/uid/" + App.getApplication().getCurrentUser().getUser_id() +
                        "/share_record_id/" + shareRecordId;
            } else if (!url.contains("share_record_id")) {
                url = url + "/share_record_id/" + shareRecordId;
            } else if (!url.contains(shareRecordId)) {
                int i = url.indexOf("/share_record_id/");
                String temp = url.substring(0, i);
                url = temp + "/share_record_id/" + shareRecordId;
            }
        } catch (Exception e) {
            url = ServerUrl.getAppH5Url() + "user/shop/uid/" + App.getApplication().getCurrentUser().getUser_id() +
                    "/share_record_id/" + shareRecordId;
        }
        if (TextUtils.isEmpty(mContent)) {
            mContent = "米果小站";
        }

        ShareInfoBean shareInfoBean = new ShareInfoBean();
        shareInfoBean.setClickurl(url);
        shareInfoBean.setTitle(mContent);
        shareInfoBean.setImageurl(imageUrl);
        shareInfoBean.setSummary(mSummary);
        ShareUtil.share(getActivity(), shareInfoBean, "", "");
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    public void setMiddleText(String text) {
        if (mTitle != null) {
            mTitle.removeAllRightItems();
            mTitle.setMiddleTextTop(text);
        }
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    @Override
    public void onSuccess(String method, List datas) {
        if (CommonConstants.CREATE_SHARE_RECORD.equals(method)) {
            if (!SDCollectionUtil.isEmpty(datas)) {
                ModelCreateShareRecord bean = (ModelCreateShareRecord) datas.get(0);
                shareRecordId = bean.getId();
            }
        }
    }

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {

    }

    private void getRecordId() {
        if (commonHttpHelper == null) {
            commonHttpHelper = new CommonHttpHelper(getActivity(), this);
        }
        commonHttpHelper.createShareRecord(Constant.ShareType.SHOP_HOME, id);
    }
}
