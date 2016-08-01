package com.tencent.qcloud.suixinbo.presenters;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.fanwe.app.App;
import com.fanwe.base.CallbackView;
import com.fanwe.user.model.UserInfoNew;
import com.miguo.live.model.LiveConstants;
import com.miguo.live.model.applyRoom.ModelApplyRoom;
import com.miguo.live.presenters.LiveHttpHelper;
import com.miguo.live.views.LiveActivity;
import com.miguo.live.views.customviews.MGToast;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.qcloud.suixinbo.avcontrollers.QavsdkControl;
import com.tencent.qcloud.suixinbo.model.CurLiveInfo;
import com.tencent.qcloud.suixinbo.model.MySelfInfo;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.util.List;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 登录的数据处理类
 */
public class LoginHelper extends com.tencent.qcloud.suixinbo.presenters.Presenter {
    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private int RoomId = -1;

    public LoginHelper(Context context) {
        mContext = context;
    }



    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig  用户签名
     */
    public void imLogin(String identify, String userSig) {
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(Constants.ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(Constants.SDK_APPID));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(
                Constants.SDK_APPID,
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        SxbLog.e(TAG, "IMLogin fail ：" + i + " msg " + s);
//                        IMLogin fail ：6012 msg operation timeout: wait server rsp timeout or no network.
                        Toast.makeText(mContext, "IMLogin fail ：" + i + " msg " + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        SxbLog.i(TAG, "keypath IMLogin succ !");
//                        Toast.makeText(mContext, "IMLogin succ !", Toast.LENGTH_SHORT).show();
                        getMyRoomNum();
                        startAVSDK();
                    }
                });
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
                //清除本地缓存
                MySelfInfo.getInstance().clearCache(mContext);
                //反向初始化avsdk
                stopAVSDK();
            }
        });

    }

    /**
     * 登录TLS账号系统
     *
     * @param id
     * @param password
     */
    public void tlsLogin(String id, String password) {
        int ret = InitBusinessHelper.getmLoginHelper().TLSPwdLogin(id, password.getBytes(), new TLSPwdLoginListener() {
            @Override
            public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {//获取用户信息
//                Toast.makeText(mContext, "TLS login succ ! " + tlsUserInfo.identifier, Toast.LENGTH_SHORT).show();
//                SxbLog.i(TAG, "TLS OnPwdLoginSuccess " + tlsUserInfo.identifier);
                String userSig = InitBusinessHelper.getmLoginHelper().getUserSig(tlsUserInfo.identifier);
                MySelfInfo.getInstance().setId(tlsUserInfo.identifier);
                MySelfInfo.getInstance().setUserSig(userSig);
                imLogin(tlsUserInfo.identifier, userSig);
            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {

            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {

            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                SxbLog.e(TAG, "OnPwdLoginFail " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginFail：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                SxbLog.e(TAG, "OnPwdLoginTimeout " + tlsErrInfo.Msg);
                Toast.makeText(mContext, "OnPwdLoginTimeout：\n" + tlsErrInfo.Msg, Toast.LENGTH_SHORT).show();
            }
        });
        if (ret != -1001) {
            Toast.makeText(mContext, "input invalid !", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 向用户服务器获取自己房间号
     */
    private void getMyRoomNum() {
        if (MySelfInfo.getInstance().getMyRoomNum() == -1) {
            new LiveHttpHelper(mContext, new CallbackView() {
                @Override
                public void onSuccess(String responseBody) {

                }

                @Override
                public void onSuccess(String method, List datas) {
                    switch (method) {
                        case LiveConstants.APPLY_ROOM:
                            ModelApplyRoom room = (ModelApplyRoom) datas.get(0);
                            String room_id = room.getRoom_id();
                            Integer roomId=-1;
                            try {
                                roomId = Integer.valueOf(room_id);
                            }catch (Exception e){
                                MGToast.showToast("获取房间号错误!");
                                return;
                            }
                            MySelfInfo.getInstance().setMyRoomNum(roomId);
                            MySelfInfo.getInstance().writeToCache(mContext.getApplicationContext());
                            //开启直播
                            createAvRoom();
                            break;
                    }
                }

                @Override
                public void onFailue(String responseBody) {

                }
            }).applyRoom("4cb975c9-bf4c-4a23-95b1-9b7f3cc1c4b1");
        }
    }

    /**
     * 进入直播间
     */
    private void createAvRoom() {
//        如果是自己
        UserInfoNew userInfoNew = App.getInstance().getmUserCurrentInfo().getUserInfoNew();
        MySelfInfo.getInstance().setId(userInfoNew.getUser_id());
        Intent intent = new Intent(mContext, LiveActivity.class);
        intent.putExtra(Constants.ID_STATUS, Constants.HOST);
        MySelfInfo.getInstance().setIdStatus(Constants.HOST);
        MySelfInfo.getInstance().setJoinRoomWay(true);
        CurLiveInfo.setTitle("直播");
        CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
        CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
        mContext.startActivity(intent);
    }


    /**
     * 初始化AVSDK
     */
    private void startAVSDK() {
        QavsdkControl.getInstance().setAvConfig(Constants.SDK_APPID, "" + Constants.ACCOUNT_TYPE, MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        QavsdkControl.getInstance().startContext();
    }


    /**
     * 反初始化AVADK
     */
    public void stopAVSDK() {
        QavsdkControl.getInstance().stopContext();
    }


    @Override
    public void onDestory() {
        mContext = null;
    }

}
