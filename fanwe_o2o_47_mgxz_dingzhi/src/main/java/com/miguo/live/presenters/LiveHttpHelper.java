package com.miguo.live.presenters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fanwe.app.App;
import com.fanwe.base.CallbackView;
import com.fanwe.base.OldCallbackHelper;
import com.fanwe.base.Root;
import com.fanwe.common.MGDict;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguo.live.interf.IHelper;
import com.miguo.live.model.LiveConstants;
import com.miguo.live.model.PageModel;
import com.miguo.live.model.UserRedPacketInfo;
import com.miguo.live.model.checkFocus.ModelCheckFocus;
import com.miguo.live.model.checkFocus.ResultCheckFocus;
import com.miguo.live.model.checkFocus.RootCheckFocus;
import com.miguo.live.model.generateSign.ModelGenerateSign;
import com.miguo.live.model.generateSign.ResultGenerateSign;
import com.miguo.live.model.generateSign.RootGenerateSign;
import com.miguo.live.model.getAudienceCount.ModelAudienceCount;
import com.miguo.live.model.getAudienceCount.ResultAudienceCount;
import com.miguo.live.model.getAudienceCount.RootAudienceCount;
import com.miguo.live.model.getAudienceList.ModelAudienceInfo;
import com.miguo.live.model.getAudienceList.ResultAudienceList;
import com.miguo.live.model.getAudienceList.RootAudienceList;
import com.miguo.live.model.getBussDictionInfo.ModelBussDictionInfo;
import com.miguo.live.model.getBussDictionInfo.ResultBussDictionInfo;
import com.miguo.live.model.getBussDictionInfo.RootBussDictionInfo;
import com.miguo.live.model.getHandOutRedPacket.ModelHandOutRedPacket;
import com.miguo.live.model.getHandOutRedPacket.ResultHandOutRedPacket;
import com.miguo.live.model.getHandOutRedPacket.RootHandOutRedPacket;
import com.miguo.live.model.getHostAuthTime.RootHostAuthTime;
import com.miguo.live.model.getHostInfo.ModelHostInfo;
import com.miguo.live.model.getHostInfo.ResultHostInfo;
import com.miguo.live.model.getHostInfo.RootHostInfo;
import com.miguo.live.model.getHostTags.ModelHostTags;
import com.miguo.live.model.getHostTags.ResultHostTags;
import com.miguo.live.model.getHostTags.RootHostTags;
import com.miguo.live.model.getLiveListNew.ModelResultLive;
import com.miguo.live.model.getLiveListNew.ModelRoom;
import com.miguo.live.model.getLiveListNew.ModelRootLive;
import com.miguo.live.model.getReceiveCode.ModelReceiveCode;
import com.miguo.live.model.getReceiveCode.ResultReceiveCode;
import com.miguo.live.model.getReceiveCode.RootReceiveCode;
import com.miguo.live.model.getStoresRandomComment.ModelStoresRandomComment;
import com.miguo.live.model.getStoresRandomComment.ResultStoresRandomComment;
import com.miguo.live.model.getStoresRandomComment.RootStoresRandomComment;
import com.miguo.live.model.getUpToken.ModelUpToken;
import com.miguo.live.model.getUpToken.ResultUpToken;
import com.miguo.live.model.getUpToken.RootUpToken;
import com.miguo.live.model.getUseReceiveCode.ResultUseReceiveCode;
import com.miguo.live.model.getUseReceiveCode.RootUseReceiveCode;
import com.miguo.live.model.pagermodel.BaoBaoEntity;
import com.miguo.live.model.payHistory.ModelPayHistory;
import com.miguo.live.model.payHistory.ResultPayHistory;
import com.miguo.live.model.payHistory.RootPayHistory;
import com.miguo.live.model.postHandOutRedPacket.ModelHandOutRedPacketPost;
import com.miguo.live.model.postHandOutRedPacket.ResultHandOutRedPacketPost;
import com.miguo.live.model.postHandOutRedPacket.RootHandOutRedPacketPost;
import com.miguo.live.model.stopLive.ModelStopLive;
import com.miguo.live.model.stopLive.ResultStopLive;
import com.miguo.live.model.stopLive.RootStopLive;
import com.miguo.live.model.userFocus.RootUserFocus;
import com.miguo.live.views.customviews.MGToast;
import com.miguo.live.views.definetion.LogTag;
import com.miguo.utils.MGLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by didik on 2016/7/28.
 * 请求直播列表
 * 申请直播房间ID
 * 请求观众数
 * 获取当前房间的观众列表
 * 结束直播
 * 观众进入房间
 * 观众退出房间
 * 直播登录，返回用户直播签名  GenerateSign
 * get获取主播信息
 * post申请成为主播
 * 获取主播标签
 * 主播退出，结束直播
 * 业务服务器的数据字典接口
 * 获取七牛UpToken
 * 校验用户是否关注该用户(主播)
 * 关注主播
 * 获取主播红包列表
 * 主播发红包
 * 抢红包接口。
 * 取用户所得到的红包列表。
 * 获取门店随机评价
 * 取直播门店的镇店之宝。
 * 获取用户主播认证时间
 */
public class LiveHttpHelper extends OldCallbackHelper implements IHelper {

    private static final String TAG = LiveHttpHelper.class.getSimpleName();
    private Gson gson;
    private CallbackView mView;
    private CallbackView mView2;

    public static final String RESULT_OK = "no_body_but_is_ok";

    public LiveHttpHelper(Context mContext, CallbackView mView) {
        this.mView = mView;
        gson = new Gson();
    }

    public LiveHttpHelper(Activity mActivity, CallbackView mView2, String type) {
        this.mView2 = mView2;
        gson = new Gson();
    }


    /**
     * 请求直播列表新版
     * 首页fragment
     */
    public void getLiveListNew(int pageNum, int pageSize, String tag, String keyword, String city) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("page", String.valueOf(pageNum));
        params.put("page_size", String.valueOf(pageSize));
        params.put("tag", tag);
        params.put("keyword", keyword);
        params.put("city", city);
        params.put("tag", tag);
        params.put("method", LiveConstants.LIVE_LIST_NEW);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                if (mView2 == null) {
                    return;
                }
                ModelRootLive root = gson.fromJson(responseBody, ModelRootLive.class);
                List<ModelResultLive> results = root.getResult();
                if (SDCollectionUtil.isEmpty(results)) {
                    mView2.onSuccess(LiveConstants.LIVE_LIST_NEW, null);
                    return;
                }

                ModelResultLive resultLive = results.get(0);
                 String  page_count= resultLive.getPage_count();
                PageModel page = new PageModel();
                page.setPage_count(resultLive.getPage_count());
                page.setPage_total(resultLive.getPage_total());
                page.setPage(resultLive.getPage());
                List<Object> datas =new ArrayList<Object>();
                datas.add(page);
                List<ModelRoom> rooms = resultLive.getBody();
                datas.addAll(rooms);
                mView2.onSuccess(LiveConstants.LIVE_LIST_NEW, datas);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                if (mView2 == null) {
                    return;
                }
                mView2.onFailue(message);
            }

            @Override
            public void onFinish() {
                if (mView2 == null) {
                    return;
                }
                mView2.onFinish(LiveConstants.LIVE_LIST_NEW);
            }
        });

    }

    /**
     * 申请直播房间ID
     *
     * @param shop_id
     */
    public void applyRoom(String shop_id, MgCallback mgCallback) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("shop_id", shop_id);
        params.put("method", LiveConstants.APPLY_ROOM);

        OkHttpUtil.getInstance().get(null, params, mgCallback);

    }

    /**
     * 请求观众数
     *
     * @param room_id
     * @param is_playback 是否是直播回放
     */
    public void getAudienceCount(String room_id, String is_playback) {
        if (TextUtils.isEmpty(App.getInstance().getToken()) || TextUtils.isEmpty(room_id)) {
            return;
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("is_playback", is_playback);
        params.put("method", LiveConstants.AUDIENCE_COUNT);


        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootAudienceCount rootAudienceCount = gson.fromJson(responseBody, RootAudienceCount.class);
                List<ResultAudienceCount> resultAudienceCounts = rootAudienceCount.getResult();
                if (SDCollectionUtil.isEmpty(resultAudienceCounts)) {
                    if (mView2 != null) {
                        onSuccess(mView2, LiveConstants.AUDIENCE_COUNT, new ArrayList());
                    }
                    if (mView != null) {
                        onSuccess(mView, LiveConstants.AUDIENCE_COUNT, new ArrayList());

                    }
                    return;
                }
                ResultAudienceCount resultAudienceCount = resultAudienceCounts.get(0);
                List<ModelAudienceCount> modelAudienceCounts = resultAudienceCount.getBody();
                if (mView2 != null) {
                    onSuccess(mView2, LiveConstants.AUDIENCE_COUNT, modelAudienceCounts);
                }
                if (mView != null) {
                    onSuccess(mView, LiveConstants.AUDIENCE_COUNT, modelAudienceCounts);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 获取当前房间的观众列表
     *
     * @param room_id
     */
    public void getAudienceList(String room_id, String is_playback) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("is_playback", is_playback);
        params.put("method", LiveConstants.AUDIENCE_LIST);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootAudienceList rootAudienceList = gson.fromJson(responseBody, RootAudienceList
                        .class);
                List<ResultAudienceList> resultAudienceLists = rootAudienceList.getResult();
                if (SDCollectionUtil.isEmpty(resultAudienceLists)) {
                    onSuccess(mView, LiveConstants.AUDIENCE_LIST, null);
                    return;
                }
                ResultAudienceList resultAudienceList = resultAudienceLists.get(0);
                List<ModelAudienceInfo> modelAudienceList = resultAudienceList.getBody();
                onSuccess(mView, LiveConstants.AUDIENCE_LIST, modelAudienceList);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 结束直播
     *
     * @param room_id
     */
    public void endInfo(String room_id) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("method", LiveConstants.END_INFO);

        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                onSuccess(mView, LiveConstants.END_INFO, null);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 观众进入房间
     *
     * @param room_id
     * @param type    1直播 2点播
     */
    public void enterRoom(String room_id, String type, String receive_code) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("type", type);
        params.put("receive_code", receive_code);
        params.put("method", LiveConstants.ENTER_ROOM);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {

                Type type = new TypeToken<Root<HashMap<String, String>>>() {
                }.getType();
                Gson gson = new Gson();
                Root<HashMap<String, String>> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                if ("200".equals(status)) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) validateBody(root);
                    if (hashMap != null) {
                        List<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
                        datas.add(hashMap);
                        onSuccess(mView, LiveConstants.ENTER_ROOM, datas);
                    } else {
                        onFailure2(mView,LiveConstants.ENTER_ROOM);
                    }
                } else {
                    onFailure2(mView,LiveConstants.ENTER_ROOM);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 观众退出房间
     *
     * @param room_id
     * @param type
     */
    public void exitRoom(String room_id, String type) {
        if (TextUtils.isEmpty(type)) {
            type = "1";
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("type", type);
        params.put("method", LiveConstants.EXIT_ROOM);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                onSuccess(mView, LiveConstants.EXIT_ROOM, null);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 直播登录，返回用户直播签名  GenerateSign
     */
    public void generateSign() {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("method", LiveConstants.GENERATE_SIGN);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootGenerateSign rootGenerateSign = gson.fromJson(responseBody, RootGenerateSign
                        .class);
                List<ResultGenerateSign> resultGenerateSigns = rootGenerateSign.getResult();
                if (SDCollectionUtil.isEmpty(resultGenerateSigns)) {
                    onSuccess(mView, LiveConstants.GENERATE_SIGN, null);
                    return;
                }
                ResultGenerateSign resultGenerateSign = resultGenerateSigns.get(0);
                List<ModelGenerateSign> modelGenerateSign = resultGenerateSign.getBody();
                onSuccess(mView, LiveConstants.GENERATE_SIGN, modelGenerateSign);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * get获取主播信息
     *
     * @param host_id
     */
    public void getHostInfo(String host_id) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("host_id", host_id);
        params.put("method", LiveConstants.HOST_INFO);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootHostInfo rootHostInfo = gson.fromJson(responseBody, RootHostInfo.class);
                List<ResultHostInfo> resultHostInfos = rootHostInfo.getResult();
                if (SDCollectionUtil.isEmpty(resultHostInfos)) {
                    onSuccess(mView, LiveConstants.HOST_INFO, null);
                    return;
                }
                ResultHostInfo resultHostInfo = resultHostInfos.get(0);
                List<ModelHostInfo> modelHostInfo = resultHostInfo.getBody();
                onSuccess(mView, LiveConstants.HOST_INFO, modelHostInfo);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * post申请成为主播
     *
     * @param sex
     * @param mobile
     * @param picture
     * @param city
     * @param interest
     */
    public void postHostInfo(String sex, String mobile, String picture, String city, String
            interest) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("sex", sex);
        params.put("mobile", mobile);
        params.put("picture", picture);
        params.put("city", city);
        params.put("interest", interest);
        params.put("method", LiveConstants.HOST_INFO);

        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootHostInfo rootHostInfo = gson.fromJson(responseBody, RootHostInfo.class);
                List<ResultHostInfo> resultHostInfos = rootHostInfo.getResult();
                if (SDCollectionUtil.isEmpty(resultHostInfos)) {
                    onSuccess(mView, LiveConstants.HOST_INFO, null);
                    return;
                }
                ResultHostInfo resultHostInfo = resultHostInfos.get(0);
                List<ModelHostInfo> modelHostInfo = resultHostInfo.getBody();
                onSuccess(mView, LiveConstants.HOST_INFO, modelHostInfo);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }


    /**
     * 获取主播标签
     */
    public void getHostTags(String tag_count) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("tag_count", tag_count);
        params.put("method", LiveConstants.HOST_TAGS);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootHostTags rootHostTags = gson.fromJson(responseBody, RootHostTags.class);
                List<ResultHostTags> resultHostTagss = rootHostTags.getResult();
                if (SDCollectionUtil.isEmpty(resultHostTagss)) {
                    onSuccess(mView, LiveConstants.HOST_TAGS, null);
                    return;
                }
                ResultHostTags resultHostTags = resultHostTagss.get(0);
                List<ModelHostTags> modelHostTags = resultHostTags.getBody();
                onSuccess(mView, LiveConstants.HOST_TAGS, modelHostTags);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 主播退出，结束直播
     */
    public void stopLive(String room_id) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("method", LiveConstants.STOP_LIVE);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootStopLive rootStopLive = gson.fromJson(responseBody, RootStopLive.class);
                List<ResultStopLive> resultStopLives = rootStopLive.getResult();
                if (SDCollectionUtil.isEmpty(resultStopLives)) {
                    if (mView != null) {
                        onSuccess(mView, LiveConstants.STOP_LIVE, null);
                    }
                    return;
                }
                ResultStopLive resultStopLive = resultStopLives.get(0);
                List<ModelStopLive> modelStopLive = resultStopLive.getBody();
                if (mView != null) {
                    onSuccess(mView, LiveConstants.STOP_LIVE, modelStopLive);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast("stopLive on error: " + message);
            }
        });

    }

    /**
     * 业务服务器的数据字典接口
     */
    public void getBussDictionInfo(String dic_type) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("dic_type", dic_type);
        params.put("method", LiveConstants.BUSS_DICTION_INFO);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Log.e("test", responseBody);
                RootBussDictionInfo rootBussDictionInfo = gson.fromJson(responseBody,
                        RootBussDictionInfo.class);
                List<ResultBussDictionInfo> resultBussDictionInfos = rootBussDictionInfo
                        .getResult();
                if (SDCollectionUtil.isEmpty(resultBussDictionInfos)) {
                    if (mView != null) {
                        onSuccess(mView, LiveConstants.BUSS_DICTION_INFO, null);
                    }
                    return;
                }
                ResultBussDictionInfo resultBussDictionInfo = resultBussDictionInfos.get(0);
                List<ModelBussDictionInfo> modelBussDictionInfo = resultBussDictionInfo.getBody();
                String dict = gson.toJson(modelBussDictionInfo);
                MGDict.save2File(dict);
                if (mView != null) {
                    onSuccess(mView, LiveConstants.BUSS_DICTION_INFO, modelBussDictionInfo);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 获取七牛UpToken
     */
    public void getUpToken() {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("method", LiveConstants.UP_TOKEN);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootUpToken rootUpToken = gson.fromJson(responseBody, RootUpToken.class);
                List<ResultUpToken> resultUpTokens = rootUpToken.getResult();
                if (SDCollectionUtil.isEmpty(resultUpTokens)) {
                    onSuccess(mView, LiveConstants.UP_TOKEN, null);
                    return;
                }
                ResultUpToken resultUpToken = resultUpTokens.get(0);
                List<ModelUpToken> modelUpToken = resultUpToken.getBody();
                onSuccess(mView, LiveConstants.UP_TOKEN, modelUpToken);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 校验用户是否关注该用户(主播)
     */
    public void checkFocus(String focus_id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("focus_id", focus_id);
        params.put("method", LiveConstants.CHECK_FOCUS);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootCheckFocus rootCheckFocus = gson.fromJson(responseBody, RootCheckFocus.class);
                List<ResultCheckFocus> resultCheckFocuss = rootCheckFocus.getResult();
                if (SDCollectionUtil.isEmpty(resultCheckFocuss)) {
                    if (mView2 != null) {
                        onSuccess(mView2, LiveConstants.CHECK_FOCUS, new ArrayList());
                    }
                    if (mView != null) {
                        onSuccess(mView, LiveConstants.CHECK_FOCUS, new ArrayList());
                    }
                    return;
                }
                ResultCheckFocus resultCheckFocus = resultCheckFocuss.get(0);
                List<ModelCheckFocus> modelCheckFocus;
                modelCheckFocus = resultCheckFocus.getBody();
                if (mView2 != null) {
                    onSuccess(mView2, LiveConstants.CHECK_FOCUS, modelCheckFocus);
                }
                if (mView != null) {
                    onSuccess(mView, LiveConstants.CHECK_FOCUS, modelCheckFocus);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
//                MGToast.showToast(message);
            }
        });
    }

    /**
     * 关注主播
     */
    public void userFocus(String focus_id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("focus_id", focus_id);
        params.put("method", LiveConstants.USER_FOCUS);
        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootUserFocus root = gson.fromJson(responseBody, RootUserFocus.class);
                ArrayList<RootUserFocus> roots = new ArrayList<>();
                roots.add(root);
                onSuccess(mView2, LiveConstants.USER_FOCUS, roots);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });
    }

    /**
     * 获取主播红包列表
     *
     * @param shop_id
     * @param host_id
     */
    public void getHandOutRedPacket(String shop_id, String host_id) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("id", shop_id);
        params.put("method", LiveConstants.HAND_OUT_RED_PACKET);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Log.d(LogTag.HIJASON, getClass().getSimpleName() + " -> " + "getHandOutRedPacket -> " + "onSuccessResponse:" + "\n" + responseBody);
                RootHandOutRedPacket rootHandOutRedPacket = gson.fromJson(responseBody, RootHandOutRedPacket.class);
                List<ResultHandOutRedPacket> resultHandOutRedPackets = rootHandOutRedPacket.getResult();
                if (SDCollectionUtil.isEmpty(resultHandOutRedPackets)) {
                    onSuccess(mView, LiveConstants.HAND_OUT_RED_PACKET_GET, null);
                    return;
                }
                ResultHandOutRedPacket resultHandOutRedPacket = resultHandOutRedPackets.get(0);
                List<ModelHandOutRedPacket> modelHandOutRedPacket = resultHandOutRedPacket.getBody();
                onSuccess(mView, LiveConstants.HAND_OUT_RED_PACKET_GET, modelHandOutRedPacket);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 主播发红包
     *
     * @param room_id
     * @param shop_id
     * @param host_id
     * @param red_packet_type
     * @param red_packet_count
     * @param red_packet_amount
     */
    public void postHandOutRedPacket(String room_id, String shop_id, String host_id, String red_packet_type, String red_packet_count, String red_packet_amount) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("tencent_room_id", room_id);
        params.put("id", shop_id);
        params.put("spokesman_id", host_id);
        params.put("red_packet_type", red_packet_type);
        params.put("red_packets", red_packet_count);
        params.put("red_packet_amount", red_packet_amount);

        params.put("method", LiveConstants.HAND_OUT_RED_PACKET);

        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootHandOutRedPacketPost rootHandOutRedPacketPost = gson.fromJson(responseBody, RootHandOutRedPacketPost.class);
                List<ResultHandOutRedPacketPost> resultHandOutRedPacketPosts = rootHandOutRedPacketPost.getResult();
                if (SDCollectionUtil.isEmpty(resultHandOutRedPacketPosts)) {
                    onSuccess(mView, LiveConstants.HAND_OUT_RED_PACKET_POST, null);
                    return;
                }
                ResultHandOutRedPacketPost resultHandOutRedPacketPost = resultHandOutRedPacketPosts.get(0);
                List<ModelHandOutRedPacketPost> modelHandOutRedPacketPost = resultHandOutRedPacketPost.getBody();
                onSuccess(mView, LiveConstants.HAND_OUT_RED_PACKET_POST, modelHandOutRedPacketPost);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 抢红包接口。
     *
     * @param user_id
     * @param red_packets_key
     */
    public void getRedPackets(String user_id, String red_packets_key) {

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("user_id", user_id);
        params.put("red_packets_key", red_packets_key);

        params.put("method", LiveConstants.GET_RED_PACKETS);

        OkHttpUtil.getInstance().post(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<UserRedPacketInfo>>() {
                }.getType();
                Gson gson = new Gson();
                Root<UserRedPacketInfo> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                String message = root.getMessage();

                UserRedPacketInfo userRedPacketInfo = (UserRedPacketInfo) validateBody(root);
                if (userRedPacketInfo == null) {
                    mView.onFailue(message);
                } else {
                    List<UserRedPacketInfo> datas = new ArrayList<UserRedPacketInfo>();
                    datas.add(userRedPacketInfo);
                    onSuccess(mView, LiveConstants.GET_RED_PACKETS, datas);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 取用户所得到的红包列表。
     *
     * @param roomID
     */
    public void getUserRedPacketList(String roomID) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        if (!TextUtils.isEmpty(roomID)) {
            params.put("tencent_room_id", roomID);
        }
        params.put("method", LiveConstants.GET_USER_RED_PACKETS);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<UserRedPacketInfo>>() {
                }.getType();
                Gson gson = new Gson();
                Root<UserRedPacketInfo> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                String message = root.getMessage();
                if (LiveConstants.RESULT_SUCCESS.equals(status)) {
                    if (root.getResult() != null && root.getResult().size() > 0 && root.getResult().get(0) != null) {
                        List<UserRedPacketInfo> datas = root.getResult().get(0).getBody();
                        onSuccess(mView, LiveConstants.GET_USER_RED_PACKETS, datas);
                    } else {
                        onSuccess(mView, LiveConstants.GET_USER_RED_PACKETS, null);
                    }
                }

            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
                mView.onFailue(message);
            }
        });
    }

    /**
     * 获取门店随机评价
     *
     * @param shop_id
     * @param comment_count
     */
    public void getStoresRandomComment(String shop_id, String comment_count) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("shop_id", shop_id);
        params.put("comment_count", comment_count);

        params.put("method", LiveConstants.STORES_RANDOM_COMMENT);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootStoresRandomComment rootStoresRandomComment = gson.fromJson(responseBody, RootStoresRandomComment.class);
                List<ResultStoresRandomComment> resultStoresRandomComments = rootStoresRandomComment.getResult();
                if (SDCollectionUtil.isEmpty(resultStoresRandomComments)) {
                    onSuccess(mView, LiveConstants.STORES_RANDOM_COMMENT, null);
                    return;
                }
                ResultStoresRandomComment resultStoresRandomComment = resultStoresRandomComments.get(0);
                List<ModelStoresRandomComment> modelStoresRandomComment = resultStoresRandomComment.getBody();
                onSuccess(mView, LiveConstants.STORES_RANDOM_COMMENT, modelStoresRandomComment);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }


    /**
     * 取直播门店的镇店之宝。
     *
     * @param shop_id 门店 ID。
     */

    public void getGoodsDetailList(String shop_id) {
        if (TextUtils.isEmpty(shop_id)) {
            return;
        }
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("shop_id", shop_id);
        params.put("token", App.getInstance().getToken());
        params.put("method", LiveConstants.LIST_OF_STORES);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                Type type = new TypeToken<Root<BaoBaoEntity>>() {
                }.getType();
                Gson gson = new Gson();
                Root<BaoBaoEntity> root = gson.fromJson(responseBody, type);
                String status = root.getStatusCode();
                String message = root.getMessage();
                if (LiveConstants.RESULT_SUCCESS.equals(status)) {
                    if (root.getResult() != null && root.getResult().size() > 0 && root.getResult().get(0) != null) {
                        List<BaoBaoEntity> datas = root.getResult().get(0).getBody();
                        onSuccess(mView, LiveConstants.LIST_OF_STORES, datas);
                    } else {
                        onSuccess(mView, LiveConstants.LIST_OF_STORES, null);
                    }
                }

            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
                mView.onFailue(message);
            }
        });
    }


    /**
     * 获取用户主播认证时间
     */
    public void getHostAuthTime() {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("method", LiveConstants.HOST_AUTH_TIME);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootHostAuthTime rootHostAuthTime = gson.fromJson(responseBody, RootHostAuthTime.class);
                List<RootHostAuthTime> roots = new ArrayList<>();
                roots.add(rootHostAuthTime);
                onSuccess(mView, LiveConstants.HOST_AUTH_TIME, roots);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGToast.showToast(message);
            }
        });

    }

    /**
     * 获取充值记录
     */
    public void getRechargeDiamondList(int page, int page_size) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(page_size));

        params.put("method", LiveConstants.RECHARGE_DIAMOND_LIST);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootPayHistory root = gson.fromJson(responseBody, RootPayHistory.class);
                List<ResultPayHistory> results = root.getResult();
                if (SDCollectionUtil.isEmpty(results)) {
                    onSuccess(mView2, LiveConstants.RECHARGE_DIAMOND_LIST, null);
                    return;
                }
                List<ModelPayHistory> items = results.get(0).getBody();
                onSuccess(mView2, LiveConstants.RECHARGE_DIAMOND_LIST, items);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                mView2.onFailue(message);
            }

            @Override
            public void onFinish() {
                mView2.onFinish(LiveConstants.RECHARGE_DIAMOND_LIST);
            }
        });

    }

    /**
     * 获取领取码接口
     *
     * @param room_id
     */
    public void getReceiveCode(String room_id, String live_type) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("room_id", room_id);
        params.put("live_type", live_type);
        params.put("user_id", App.getInstance().getCurrentUser().getUser_id());

        params.put("method", LiveConstants.RECEIVE_CODE);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootReceiveCode root = gson.fromJson(responseBody, RootReceiveCode.class);
                List<ResultReceiveCode> results = root.getResult();
                if (SDCollectionUtil.isEmpty(results)) {
                    onSuccess(mView, LiveConstants.RECEIVE_CODE, null);
                    return;
                }
                List<ModelReceiveCode> items = results.get(0).getBody();
                onSuccess(mView, LiveConstants.RECEIVE_CODE, items);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                onSuccess(mView, LiveConstants.RECEIVE_CODE, null);
            }

        });

    }

    /**
     * 支付礼物
     */
    public void postGiftInfo(String live_type, String live_record_id, String gift_num, String gift_id) {
//        live_type	String	必须		类型：1直播 2点播	播放类型
//        live_record_id	String	必须		直播id	直播id(就是房间room_id)
//        gift_num	String	必须		礼物数量
//        token	String	必须		共通参数	用户需登录
//        gift_id	String	必须		礼物id
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("live_type", live_type);
        params.put("live_record_id", live_record_id);
        params.put("gift_num", gift_num);
        params.put("gift_id", gift_id);
        params.put("method", LiveConstants.POST_GIFT_INFO);

        OkHttpUtil.getInstance().put(null, params, new MgCallback() {
            @Override
            public void onErrorResponse(String message, String errorCode) {

            }

            @Override
            public void onSuccessResponse(String responseBody) {
//                200  正常状态
//                302  参数错误，message中会有描述
//                300  处理错误，礼物未赠送成功
//                500  服务器配置错误

                MGLog.e(responseBody);
            }
        });

    }

    /**
     * 使用分享领取码进入房间并领钻
     *
     * @param receive_code
     */
    public void getUseReceiveCode(String receive_code) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        if (!TextUtils.isEmpty(receive_code)) {
            receive_code = receive_code.trim();
        }
        params.put("receive_code", receive_code);

        params.put("method", LiveConstants.USE_RECEIVE_CODE);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                RootUseReceiveCode root = gson.fromJson(responseBody, RootUseReceiveCode.class);
                List<ResultUseReceiveCode> results = root.getResult();
                if (SDCollectionUtil.isEmpty(results)) {
                    onSuccess(mView, LiveConstants.USE_RECEIVE_CODE, null);
                    return;
                }
                List<ModelRoom> items = results.get(0).getBody();
                onSuccess(mView, LiveConstants.USE_RECEIVE_CODE, items);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                onSuccess(mView, LiveConstants.USE_RECEIVE_CODE, null);
            }

        });

    }

    /**
     * 添加旁路直播
     *
     * @param play_url
     * @param channel_id
     * @param room_id
     */
    public void getByPassLive(String play_url, String channel_id, String room_id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("play_url", play_url);
        params.put("channel_id", channel_id);
        params.put("room_id", room_id);
        params.put("token", App.getInstance().getToken());
        params.put("method", LiveConstants.BY_PASS_LIVE);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
            }
        });
    }


    @Override
    public void onDestroy() {
        mView = null;
        mView2 = null;
        gson = null;
    }

    public CallbackView getmView() {
        return mView;
    }

    public void setmView(CallbackView mView) {
        this.mView = mView;
    }
}
