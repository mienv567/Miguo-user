package com.fanwe.seller.presenters;

import android.text.TextUtils;

import com.fanwe.app.App;
import com.fanwe.baidumap.BaiduMapManager;
import com.fanwe.base.CallbackView;
import com.fanwe.base.OldCallbackHelper;
import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtil;
import com.fanwe.seller.model.SellerConstants;
import com.fanwe.seller.model.getGroupDeatilNew.ModelGoodsDetailNew;
import com.fanwe.seller.model.getGroupDeatilNew.ResultGoodsDetailNew;
import com.fanwe.seller.model.getGroupDeatilNew.RootGoodsDetailNew;
import com.fanwe.seller.model.getSpecialTopic.ModelSpecialTopic;
import com.fanwe.seller.model.getSpecialTopic.ResultSpecialTopic;
import com.fanwe.seller.model.getSpecialTopic.RootSpecialTopic;
import com.google.gson.Gson;
import com.miguo.live.interf.IHelper;
import com.miguo.utils.MGLog;
import com.miguo.utils.MGUIUtil;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by didik on 2016/10/17.
 */

public class SellerNewHttpHelper extends OldCallbackHelper implements IHelper {
    private CallbackView mView2;
    private Gson gson;


    public SellerNewHttpHelper(CallbackView mView2) {
        this.mView2 = mView2;
        gson = new Gson();
    }

    public void getGroupBuyDetailNew(String id) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("id", id);
        params.put("token", App.getInstance().getToken());
        //TODO 如果坐标获取到了就传给后台
        double x = BaiduMapManager.getInstance().getLongitude();
        double y = BaiduMapManager.getInstance().getLatitude();
        if (x!=0 && y!=0){
            params.put("m_longitude", x+"");
            params.put("m_latitude", y+"");
        }
        params.put("method", SellerConstants.GROUP_BUY_DETAIL_NEW);

        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                List<ResultGoodsDetailNew> result = gson.fromJson(responseBody,
                        RootGoodsDetailNew.class).getResult();
                if (result!=null){
                    ResultGoodsDetailNew resultGoodsDetailNew = result.get(0);
                    if (resultGoodsDetailNew!=null){
                        List<ModelGoodsDetailNew> body = resultGoodsDetailNew.getBody();
                        callback2Success(mView2,SellerConstants.GROUP_BUY_DETAIL_NEW,body);
                    }
                }else {
                    callback2Failure(mView2,SellerConstants.GROUP_BUY_DETAIL_NEW);
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGLog.e(errorCode,message);

            }

            @Override
            public void onFinish() {
                callback2Finish(mView2,SellerConstants.GROUP_BUY_DETAIL_NEW);
            }
        });
    }
    //主题列表
    public void getTopicDetail(String topic_id,String page,String pageSize,String m_longitude,String m_latitude){
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("topic_id", topic_id);
        params.put("page", page);
        params.put("pageSize", pageSize);
        if (!TextUtils.isEmpty(m_latitude)  && !TextUtils.isEmpty(m_longitude)){
            params.put("m_longitude", m_longitude);
            params.put("m_latitude", m_latitude);
        }
        params.put("method", SellerConstants.SPECIAL_TOPIC);
        OkHttpUtil.getInstance().get(null, params, new MgCallback() {
            @Override
            public void onSuccessResponse(String responseBody) {
                List<ResultSpecialTopic> result = gson.fromJson(responseBody, RootSpecialTopic
                        .class).getResult();
                if (result!=null && result.size()>0){
                    ResultSpecialTopic resultSpecialTopic = result.get(0);
                    if (resultSpecialTopic!=null){
                        List<ModelSpecialTopic> body = resultSpecialTopic.getBody();
                        callback2Success(mView2,SellerConstants.SPECIAL_TOPIC,body);
                    }
                }else {
                    callback2Failure(mView2,SellerConstants.SPECIAL_TOPIC);
                }
                callback2Finish(mView2,SellerConstants.SPECIAL_TOPIC);
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                MGLog.e(errorCode,message);
                callback2Failure(mView2,SellerConstants.SPECIAL_TOPIC);
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    private void callback2Success(final CallbackView mView2, final String method, final List data){
        if (mView2!=null){
            MGUIUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onSuccess(mView2,method,data);
                }
            });
        }
    }

    private void callback2Failure(final CallbackView mView2, final String method){
        if (mView2!=null){
            MGUIUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView2.onFailue(method);
                }
            });
        }
    }
    private void callback2Finish(final CallbackView mView2, final String method){
        if (mView2!=null){
            MGUIUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView2.onFinish(method);
                }
            });
        }
    }
}
