package com.fanwe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanwe.app.App;
import com.fanwe.base.CallbackView2;
import com.fanwe.constant.Constant;
import com.fanwe.constant.Constant.TitleType;
import com.fanwe.fragment.OrderDetailAccountPaymentFragment;
import com.fanwe.fragment.OrderDetailAccountPaymentFragment.OrderDetailAccountPaymentFragmentListener;
import com.fanwe.fragment.OrderDetailFeeFragment;
import com.fanwe.fragment.OrderDetailPaymentsFragment;
import com.fanwe.http.InterfaceServer;
import com.fanwe.http.listener.SDRequestCallBack;
import com.fanwe.library.alipay.easy.PayResult;
import com.fanwe.library.dialog.SDDialogManager;
import com.fanwe.library.utils.SDCollectionUtil;
import com.miguo.live.views.customviews.MGToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.model.MalipayModel;
import com.fanwe.model.PayResultModel;
import com.fanwe.model.Payment_codeModel;
import com.fanwe.model.RequestModel;
import com.fanwe.model.Uc_HomeModel;
import com.fanwe.model.UpacpappModel;
import com.fanwe.model.WxappModel;
import com.fanwe.o2o.miguo.R;
import com.fanwe.shoppingcart.RefreshCalbackView;
import com.fanwe.shoppingcart.ShoppingCartconstants;
import com.fanwe.shoppingcart.model.PaymentTypeInfo;
import com.fanwe.shoppingcart.presents.CommonShoppingHelper;
import com.fanwe.user.UserConstants;
import com.fanwe.user.model.getUserUpgradeOrder.ModelGetUserUpgradeOrder;
import com.fanwe.user.model.postUserUpgradeOrder.ModelPostUserUpgradeOrder;
import com.fanwe.user.presents.UserHttpHelper;
import com.fanwe.wxapp.SDWxappPay;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.miguo.utils.MGUIUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.unionpay.UPPayAssistEx;

import java.util.HashMap;
import java.util.List;

public class ConfirmTopUpActivity extends BaseActivity implements IWXAPIEventHandler, RefreshCalbackView, CallbackView2 {
    @ViewInject(R.id.tv_money)
    private TextView mTv_money;

    @ViewInject(R.id.act_confirm_order_ptrsv_all)
    private PullToRefreshScrollView mPtrsvAll;

    @ViewInject(R.id.act_confirm_order_btn_confirm_order)
    private Button mBtnConfirmOrder;

    /**
     * 00:正式，01:测试
     */
    private static final String UPACPAPP_MODE = "00";

    protected OrderDetailPaymentsFragment mFragPayments;
    protected OrderDetailAccountPaymentFragment mFragAccountPayment;
    protected OrderDetailFeeFragment mFragFees;

    protected PayResultModel mActModel;

    private String mHasPay;
    private CommonShoppingHelper commonShoppingHelper;
    private UserHttpHelper userHttpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setmTitleType(TitleType.TITLE);
        setContentView(R.layout.act_top_up);
        init();
    }

    private void getPayment() {
        if (commonShoppingHelper == null) {
            commonShoppingHelper = new CommonShoppingHelper(this);
        }
        commonShoppingHelper.getPayment();

        if (userHttpHelper == null) {
            userHttpHelper = new UserHttpHelper(this, this);
        }
        userHttpHelper.getUserUpgradeOrder();
    }

    private String orderMoney, orderId;
    private int is_use_account_money = 0;

    private void postUserUpgradeOrder() {
        if (userHttpHelper == null) {
            userHttpHelper = new UserHttpHelper(this, this);
        }
        userHttpHelper.postUserUpgradeOrder(mFragPayments.getPaymentId(), orderId, is_use_account_money);
    }

    private void init() {
        initTitle();
        initClick();
        addFragment();
        initPullToRefreshScrollView();
    }

    public void sonFragemtMethod() {
    }

    private void initClick() {
        mBtnConfirmOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_confirm_order_btn_confirm_order:
                if (TextUtils.isEmpty(mFragPayments.getPaymentId())) {
                    MGToast.showToast("请选择支付方式");
                    return;
                }
                if (TextUtils.isEmpty(orderMoney)) {
                    MGToast.showToast("支付金额为空");
                    return;
                }
                if (v.isClickable()) {
                    v.setBackgroundResource(R.drawable.layer_main_color_corner_press);
                    v.setClickable(false);
                    //60s可点

                } else {
                    v.setBackgroundResource(R.drawable.layer_main_color_corner_normal);
                    v.setClickable(true);
                }
                postUserUpgradeOrder();
                break;

            default:
                break;
        }
    }

    private void initTitle() {
        mTitle.setMiddleTextTop("确认支付");
    }

    private void addFragment() {
        // 支付方式列表
        mFragPayments = new OrderDetailPaymentsFragment();
        getSDFragmentManager().replace(R.id.act_confirm_order_fl_payments,
                mFragPayments);

        // 余额支付
        mFragAccountPayment = new OrderDetailAccountPaymentFragment();
        mFragAccountPayment
                .setmListener(new OrderDetailAccountPaymentFragmentListener() {
                    @Override
                    public void onPaymentChange(boolean isSelected) {
                        // requestCalculate();
                    }
                });
        getSDFragmentManager()
                .replace(R.id.act_confirm_order_fl_account_payments,
                        mFragAccountPayment);

        // 费用信息
        mFragFees = new OrderDetailFeeFragment();
        getSDFragmentManager().replace(R.id.act_confirm_order_fl_fees,
                mFragFees);
    }

    private void initPullToRefreshScrollView() {
        mPtrsvAll.setMode(Mode.PULL_FROM_START);
        mPtrsvAll.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {
                getPayment();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ScrollView> refreshView) {

            }

        });
        mPtrsvAll.setRefreshing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        confirmRankState();
    }

    private void confirmRankState() {
        RequestModel model = new RequestModel();
        model.putCtl("uc_home");
        model.putAct("homepage");
        InterfaceServer.getInstance().requestInterface(model, new SDRequestCallBack<Uc_HomeModel>() {

            @Override
            public void onStart() {
                super.onStart();
                SDDialogManager.showProgressDialog("");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (actModel.getStatus() == 1) {
                    int rank = actModel.getDist().getRank();
                    if (rank >= 2) {
                        MGToast.showToast("升级成功!");
                        finish();
                    } else {
                        //DoNothing
                    }
                }
            }

            @Override
            public void onFinish() {
                SDDialogManager.dismissProgressDialog();
            }
        });
    }

    /**
     * 绑定 支付方式
     *
     * @param datas
     */
    private void bindPayment(List<PaymentTypeInfo> datas) {
        if (!SDCollectionUtil.isEmpty(datas)) {
            if (mFragPayments != null)
                mFragPayments.setListPayment(datas);
        }
    }

    @Override
    public void onFailue(String method, String responseBody) {

    }

    @Override
    public void onSuccess(String responseBody) {

    }

    private Payment_codeModel mPaymentCodeModel;

    @Override
    public void onSuccess(String method, final List datas) {
        switch (method) {
            case ShoppingCartconstants.GET_PAYMENT:
                MGUIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindPayment(datas);
                        mPtrsvAll.onRefreshComplete();
                    }
                });
                break;
            case UserConstants.USER_UPGRADE_ORDER_GET:
                MGUIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!SDCollectionUtil.isEmpty(datas)) {
                            ModelGetUserUpgradeOrder bean = (ModelGetUserUpgradeOrder) datas.get(0);
                            orderMoney = bean.getTotal_price();
                            orderId = bean.getOrder_id();
                            SDViewBinder.setTextView(mTv_money, "￥" + orderMoney);
                        }
                    }
                });
                break;
            case UserConstants.USER_UPGRADE_ORDER_POST:
                if (!SDCollectionUtil.isEmpty(datas)) {
                    ModelPostUserUpgradeOrder bean = (ModelPostUserUpgradeOrder) datas.get(0);
                    mPaymentCodeModel = new Payment_codeModel();
                    String class_name = bean.getClass_name();

                    mHasPay = "pay_wait";
                    HashMap<String, String> config = bean.getConfig();
                    String payMondy = config.get("total_fee");
                    mPaymentCodeModel.setPay_money(payMondy);
                    mPaymentCodeModel.setConfig(config);
                    mPaymentCodeModel.setClass_name(class_name);
                    if (!TextUtils.isEmpty(class_name) && "Aliapp".equals(class_name)) {
                        mPaymentCodeModel.setPayment_name("支付宝支付");
                    } else {
                        mPaymentCodeModel.setPayment_name("微信支付");
                    }
                    clickPay();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        int respType = resp.getType();
        switch (respType) {
            case ConstantsAPI.COMMAND_PAY_BY_WX:
                String content = null;
                switch (resp.errCode) {
                    case 0: // 成功
                        content = "支付成功";
                        break;
                    case -1: // 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                        content = "支付失败";
                        break;
                    case -2: // 无需处理。发生场景：用户不支付了，点击取消，返回APP。
                        content = "取消支付";
                        //showPayment(true);
                        break;

                    default:
                        break;
                }
                if (content != null) {
                    MGToast.showToast(content);
                }
                break;

            default:
                break;
        }
        finish();
    }

    private void clickPay() {
        if (mPaymentCodeModel == null) {
            return;
        }
        String payAction = mPaymentCodeModel.getPay_action();
        String className = mPaymentCodeModel.getClass_name();
        if (!TextUtils.isEmpty(payAction)) // wap
        {
            Intent intent = new Intent(App.getApplication(), AppWebViewActivity.class);
            intent.putExtra(AppWebViewActivity.EXTRA_URL, payAction);
            startActivity(intent);
            return;
        } else {
            if (Constant.PaymentType.MALIPAY.equals(className) || Constant.PaymentType.ALIAPP.equals(className)) // 支付宝sdk新
            {
                payMalipay();
            } else if (Constant.PaymentType.WXAPP.equals(className)) // 微信
            {
                payWxapp();
            } else if (Constant.PaymentType.UPACPAPP.equals(className)) // 银联支付
            {
                payUpacpapp();
            }
        }

    }

    /**
     * 银联支付
     */
    private void payUpacpapp() {
        if (mPaymentCodeModel == null) {
            return;
        }

        UpacpappModel model = mPaymentCodeModel.getUpacpapp();
        if (model == null) {
            MGToast.showToast("获取银联支付参数失败");
            return;
        }

        String tn = model.getTn();
        if (TextUtils.isEmpty(tn)) {
            MGToast.showToast("tn 为空");
            return;
        }
        UPPayAssistEx.startPayByJAR(mActivity, com.unionpay.uppay.PayActivity.class, null, null, tn, UPACPAPP_MODE);
    }

    /**
     * 微信支付
     */
    private void payWxapp() {
        if (mPaymentCodeModel == null) {
            return;
        }

        WxappModel model = mPaymentCodeModel.getWxapp();
        if (model == null) {
            MGToast.showToast("获取微信支付参数失败");
            return;
        }

        String appId = model.getAppid();
        if (TextUtils.isEmpty(appId)) {
            MGToast.showToast("appId为空");
            return;
        }

        String partnerId = model.getMch_id();
        if (TextUtils.isEmpty(partnerId)) {
            MGToast.showToast("partnerId为空");
            return;
        }

        String prepayId = model.getPrepay_id();
        if (TextUtils.isEmpty(prepayId)) {
            MGToast.showToast("prepayId为空");
            return;
        }

        String nonceStr = model.getNonce_str();
        if (TextUtils.isEmpty(nonceStr)) {
            MGToast.showToast("nonceStr为空");
            return;
        }

        String timeStamp = model.getTime_stamp();
        if (TextUtils.isEmpty(timeStamp)) {
            MGToast.showToast("timeStamp为空");
            return;
        }

        String packageValue = model.getPackage_value();
        if (TextUtils.isEmpty(packageValue)) {
            MGToast.showToast("packageValue为空");
            return;
        }

        String sign = model.getSign();
        if (TextUtils.isEmpty(sign)) {
            MGToast.showToast("sign为空");
            return;
        }

        SDWxappPay.getInstance().setAppId(appId);

        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = packageValue;
        req.sign = sign;

        SDWxappPay.getInstance().pay(req);
    }

    /**
     * 支付宝sdk支付(新)
     */
    private void payMalipay() {
        if (mPaymentCodeModel == null) {
            return;
        }
        MalipayModel model = mPaymentCodeModel.getMalipay();
        if (model == null) {
            MGToast.showToast("获取支付宝支付参数失败");
            return;
        }


        String orderSpec = "&partner = " + "\"" + model.getPartner() + "\"" + "&seller_id =" + "\"" + model.getSeller_id() + "\"" + "&out_trade_no=" + "\"" + model.getOut_trade_no() + "\"" + "&subject =" + "\"" + model.getSubject() + "\"" + "&body =" + model.getBody() + "\"" + "&total_fee=" + model.getTotal_fee() + "\"" + "&notify_url=" + model.getNotify_url() + "\"" + "&service=" + "\"" + model.getService() + "\"" + "&payment_type=" + "\"" + model.getPayment_type() + "\"" + "&_input_charset=" + "\"" + model.get_input_charset() + "\"";


        String sign = model.getSign();

        String signType = model.getSign_type();

        if (TextUtils.isEmpty(orderSpec)) {
            MGToast.showToast("order_spec为空");
            return;
        }

        if (TextUtils.isEmpty(sign)) {
            MGToast.showToast("sign为空");
            return;
        }

        if (TextUtils.isEmpty(signType)) {
            MGToast.showToast("signType为空");
            return;
        }

        com.fanwe.library.alipay.easy.SDAlipayer payer = new com.fanwe.library.alipay.easy.SDAlipayer(mActivity);
        payer.setmListener(new com.fanwe.library.alipay.easy.SDAlipayer.SDAlipayerListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(PayResult result) {
                String info = result.getMemo();
                String status = result.getResultStatus();
                if ("9000".equals(status)) // 支付成功
                {
                    MGToast.showToast("支付成功");
                } else if ("8000".equals(status)) // 支付结果确认中
                {
                    MGToast.showToast("支付结果确认中");
                } else {
                    MGToast.showToast(info);
                }
            }

            @Override
            public void onFailure(Exception e, String msg) {
                if (e != null) {
                    MGToast.showToast("错误:" + e.toString());
                } else {
                    if (!TextUtils.isEmpty(msg)) {
                        MGToast.showToast(msg);
                    }
                }
            }
        });
        payer.pay(orderSpec, sign, signType);
    }

}
