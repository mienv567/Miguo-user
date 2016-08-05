package com.miguo.live.views.customviews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.o2o.miguo.R;
import com.miguo.live.views.LiveInputDialogHelper;
import com.miguo.live.views.LiveUserPopHelper;
import com.miguo.live.views.UserRobRedPacketDialogHelper;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.presenters.LiveHelper;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.views.customviews.HeartLayout;


/**
 * Created by didik on 2016/7/22.
 */
public class UserBottomToolView extends LinearLayout implements IViewGroup, View.OnClickListener {
    private Context mContext;
    private View mTvTalk2host;
    private TextView mGoods;
    private TextView mRob;
    private ImageView mGift;
    private ImageView mShare;
    private ImageView mLike;
    private Activity mAct;
    private LiveHelper mLiveHelper;
    private HeartLayout mHeartLayout;
    private long admireTime = 0;//♥的时间
    private View rootView;//父布局,pop定位用
    private LiveUserPopHelper popHelper;
    private UserRobRedPacketDialogHelper redPacketDialogHelper;

    public UserBottomToolView(Context context) {
        this(context, null);
    }

    public UserBottomToolView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserBottomToolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.user_bottom_tool, this);
        mTvTalk2host = this.findViewById(R.id.tv_talk2host);
        mGoods = (TextView) this.findViewById(R.id.goods);
        mRob = (TextView) this.findViewById(R.id.rob);
        mGift = (ImageView) this.findViewById(R.id.iv_gift);
        mShare = (ImageView) this.findViewById(R.id.iv_share);
        mLike = (ImageView) this.findViewById(R.id.iv_like);

        mTvTalk2host.setOnClickListener(this);
        mGoods.setOnClickListener(this);
        mRob.setOnClickListener(this);
        mGift.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mLike.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {

    }

    public void initView(Activity mAct, LiveHelper liveHelper, HeartLayout heartLayout,View rootView) {
        this.mAct = mAct;
        this.mLiveHelper = liveHelper;
        this.mHeartLayout = heartLayout;
        this.rootView=rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == mTvTalk2host) {
            talk2host();
        } else if (v == mGoods) {
            clickGoods();
        } else if (v == mRob) {
            clickRob();
        } else if (v == mGift) {
            clickGift();
        } else if (v == mShare) {
            clickShare();
        } else if (v == mLike) {
            clickLike2ShowHeart();
        }
    }

    /**
     * 点击出星星
     */
    private boolean clickLike2ShowHeart() {
        mHeartLayout.addFavor();
        if (checkInterval()) {
            mLiveHelper.sendC2CMessage(Constants.AVIMCMD_Praise, "", CurLiveInfo.getHostID());
            CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
            return true;
        } else {
            //Toast.makeText(this, getString(R.string.text_live_admire_limit), Toast
            // .LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 分享
     */
    private void clickShare() {
        MGToast.showToast("点击了分享");
    }

    /**
     * 点击礼物
     */
    private void clickGift() {
        MGToast.showToast("点击了礼物");
    }

    /**
     * 点击抢到(红包什么的乱七八糟的)
     */
    private void clickRob() {
//        MGToast.showToast("点击了抢到");
        if (mAct!=null && redPacketDialogHelper==null){
            redPacketDialogHelper = new UserRobRedPacketDialogHelper(mAct);
        }
         redPacketDialogHelper.createDialog();
        redPacketDialogHelper.show();
    }

    /**
     * 点击了商品(宝贝)
     */
    private void clickGoods() {
//        MGToast.showToast("点击了商品(宝贝)");
        if (mAct!=null && rootView!=null && popHelper==null){
            popHelper = new LiveUserPopHelper(mAct,rootView);
        }
        popHelper.show();

    }

    /**
     * 点击了"跟主播聊聊"
     */
    private void talk2host() {
        inputMsgDialog();
    }

    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        if (mAct == null || mLiveHelper == null || mContext == null) {
            return;
        }
        LiveInputDialogHelper inputDialogHelper=new LiveInputDialogHelper(mLiveHelper,mAct);
        inputDialogHelper.show();
    }


    private boolean checkInterval() {
        if (0 == admireTime) {
            admireTime = System.currentTimeMillis();
            return true;
        }
        long newTime = System.currentTimeMillis();
        if (newTime >= admireTime + 1000) {
            admireTime = newTime;
            return true;
        }
        return false;
    }
}
