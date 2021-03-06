package com.miguo.live.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.fanwe.base.CallbackView;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.user.view.RedPacketListActivity;
import com.fanwe.work.AppRuntimeWorker;
import com.miguo.live.adapters.UserExitAdapter;
import com.miguo.live.interf.IHelper;
import com.miguo.live.model.LiveConstants;
import com.miguo.live.model.checkFocus.ModelCheckFocus;
import com.miguo.live.model.getAudienceCount.ModelAudienceCount;
import com.miguo.live.model.getLiveListNew.ModelRoom;
import com.miguo.live.model.userFocus.RootUserFocus;
import com.miguo.live.presenters.LiveHttpHelper;
import com.miguo.live.views.customviews.MGToast;
import com.miguo.live.views.customviews.MaxHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by didik on 2016/7/26.
 * 直播退出界面
 */
public class LiveUserExitDialogHelper implements IHelper, View.OnClickListener, CallbackView {

    private Activity mActivity;
    private CircleImageView civ_user_image;
    private TextView tv_username;
    private TextView tv_user_location;
    private TextView tv_count;
    private TextView tv_follow;
    private MaxHeightGridView gridview;
    private Dialog dialog;
    private LiveHttpHelper liveHttpHelper;
    private String count = "";
    private List<ModelRoom> datasList = new ArrayList<>();
    private UserExitAdapter mUserExitAdapter;
    CallbackView callbackHH;

    public LiveUserExitDialogHelper(Activity activity) {
        this.mActivity = activity;
        createDialog();
        setView();
    }

    private void setView() {
        liveHttpHelper = new LiveHttpHelper(mActivity, this, "");
        liveHttpHelper.checkFocus(CurLiveInfo.getHostID());
        liveHttpHelper.getAudienceCount(CurLiveInfo.getRoomNum() + "", "0");
        liveHttpHelper.getLiveListNew(1, 5, "", "", AppRuntimeWorker.getCity_id());
        ImageLoader.getInstance().displayImage(CurLiveInfo.getHostAvator(), civ_user_image);
        tv_username.setText(CurLiveInfo.getHostName());
        tv_user_location.setText(CurLiveInfo.modelShop.getShop_name());
    }


    private void createDialog() {
        dialog = new Dialog(mActivity, R.style.floag_dialog);

        //init view
        dialog.setContentView(R.layout.pop_live_exit);
        dialog.setCancelable(false);
        View iv_close = dialog.findViewById(R.id.iv_close);//右上角关闭
        //主播头像
        civ_user_image = (CircleImageView) dialog.findViewById(R.id.civ_user_image);
        //主播名称
        tv_username = (TextView) dialog.findViewById(R.id.tv_username);
        //位置
        tv_user_location = (TextView) dialog.findViewById(R.id.tv_user_location);
        //观看人数
        tv_count = (TextView) dialog.findViewById(R.id.tv_count);
        View tv_youhuiquan = dialog.findViewById(R.id.tv_youhuiquan);//优惠
        tv_follow = (TextView) dialog.findViewById(R.id.tv_follow);//关注
        //关注
        gridview = (MaxHeightGridView) dialog.findViewById(R.id.mhgridview_show);
        gridview.setEnabled(false);

        //init click
        iv_close.setOnClickListener(this);
        tv_youhuiquan.setOnClickListener(this);
        tv_follow.setOnClickListener(this);

        mUserExitAdapter = new UserExitAdapter(mActivity, mActivity.getLayoutInflater(), datasList);
        gridview.setAdapter(mUserExitAdapter);
    }

    /**
     * 是否在显示
     */

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();

        } else {
            return;
        }
    }

    @Override
    public void onDestroy() {
        //防止内存泄漏,在这里释放资源
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
        gridview = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
            ((LiveActivity) mActivity).userExit();
            mActivity.finish();
        } else if (id == R.id.tv_youhuiquan) {
            clickYouHui();
        }
    }

    /**
     * 优惠
     */
    private void clickYouHui() {
        if(mActivity!=null) {
            Intent intent = new Intent(mActivity, RedPacketListActivity.class);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    ArrayList<RootUserFocus> roots;

    @Override
    public void onSuccess(String method, List datas) {
        if (callbackHH != null) {
            callbackHH.onSuccess(method, datas);
        }
        Message message = new Message();
        if (LiveConstants.CHECK_FOCUS.equals(method)) {
            if (!SDCollectionUtil.isEmpty(datas)) {
                ModelCheckFocus modelCheckFocus = (ModelCheckFocus) datas.get(0);
                if ("1".equals(modelCheckFocus.getFocus())) {
                    //已关注
                    message.what = 1;
                } else {
                    //去关注
                    message.what = 0;
                }
            }
        } else if (LiveConstants.USER_FOCUS.equals(method)) {
            //判断用户关注成功与否
            roots = (ArrayList<RootUserFocus>) datas;
            if (!SDCollectionUtil.isEmpty(roots)) {
                RootUserFocus rootUserFocus = roots.get(0);
                if ("200".equals(rootUserFocus.getStatusCode())) {
                    message.what = 1;
                } else {
                    if (!TextUtils.isEmpty(roots.get(0).getMessage()))
                        MGToast.showToast(roots.get(0).getMessage());
                }
            }
        } else if (LiveConstants.AUDIENCE_COUNT.equals(method)) {
            if (!SDCollectionUtil.isEmpty(datas)) {
                ModelAudienceCount bean = (ModelAudienceCount) datas.get(0);
                count = bean.getCount();
            }
            message.what = 2;
        } else if (LiveConstants.LIVE_LIST_NEW.equals(method)) {
            if (!SDCollectionUtil.isEmpty(datas)) {
                //去除第一个model 目前记录的是分面页信息。
                datas.remove(0);
                datasList.clear();
                //请求了5个room，需要剔除当前观看的房间，并最后保留4个
                for (ModelRoom room : (ArrayList<ModelRoom>) datas) {
                    if (!room.getHost().getHost_user_id().equals(CurLiveInfo.getHostID()) && datasList.size() < 4) {
                        datasList.add(room);
                    }
                }
            }
            message.what = 3;
        }
        mHandler.sendMessage(message);
    }

    @Override
    public void onFailue(String responseBody) {

    }

    @Override
    public void onFinish(String method) {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //关注主播
                    tv_follow.setText("关注主播");
                    tv_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            liveHttpHelper.userFocus(CurLiveInfo.getHostID());
                        }
                    });
                    break;
                case 1:
                    //已关注
                    tv_follow.setText("已关注");
                    tv_follow.setOnClickListener(null);
                    break;
                case 2:
                    tv_count.setText("观看人数：" + count);
                    break;
                case 3:
                    mUserExitAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

}
