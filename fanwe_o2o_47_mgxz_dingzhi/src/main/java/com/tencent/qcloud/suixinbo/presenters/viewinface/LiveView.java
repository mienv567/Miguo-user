package com.tencent.qcloud.suixinbo.presenters.viewinface;


import com.tencent.av.TIMAvManager;

import java.util.HashMap;
import java.util.List;

/**
 *  直播界面回调
 */
public interface LiveView extends MvpView {

    /**
     * 显示直播视频
     * @param isHost
     * @param id
     */
    void showVideoView(boolean isHost, String id);

    /**
     * 显示邀请对话框
     */
    void showInviteDialog();

    void refreshText(String text, String name);
    void refreshText(String text, String name,String faceUrl);


    void refreshThumbUp();

    void refreshUI(String id);

    boolean showInviteView(String id);

    void cancelInviteView(String id);

    void cancelMemberView(String id);
    void memberJoin(String id, String name,String faceUrl);

   // void memberJoin(String id, String name);

    void memberQuit(String id, String name,String faceUrl);
   // void memberQuit(String id, String name);

    void readyToQuit();

    void hideInviteDialog();

    void pushStreamSucc(TIMAvManager.StreamRes streamRes);

    void stopStreamSucc();

    void startRecordCallback(boolean isSucc);

    void stopRecordCallback(boolean isSucc, List<String> files);

    void hostLeave(String id, String name,String faceUrl);

    void hostBack(String id, String name,String faceUrl);

    /**
     * 收到红包。
     */
    void getHostRedPacket(HashMap<String,String> params);
    /**
     * IM收到弹幕
     */
    void getDanmu(HashMap<String,String> params);
    /**
     * 用户自己发送弹幕显示
     */
    void showDanmuSelf(HashMap<String,String> params);
    /**
     * 收到礼物
     */
    void getGift(HashMap<String,String> params);

    /**
     * 主播发送红包。
     *
     */
    void sendHostRedPacket(String id,String duration);
    /*
    token 失效 ，主动退出主播页
     */
    void tokenInvalidateAndQuit();

    /**
     * 用户退出 。
     */
    void userExit();
    /**
     * 花钻石钱不够
     */
    void withoutEnoughMoney(String msg);

    /**
     * 主播强制退出直播。如管理台强制关闭直播。
     */
    void hostExitByForce();
}
