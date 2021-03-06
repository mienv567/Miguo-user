package com.miguo.dao.impl;

import android.util.Log;

import com.fanwe.network.MgCallback;
import com.fanwe.network.OkHttpUtil;
import com.google.gson.Gson;
import com.miguo.dao.HomeGreetingDao;
import com.miguo.entity.HomeGreetingBean;
import com.miguo.view.BaseView;
import com.miguo.view.HomeGreetingView;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.Call;

/**
 * Created by zlh/狗蛋哥/Barry on 2016/10/26.
 * 首页问候语接口实现类
 */
public class HomeGreetingDaoImpl extends BaseDaoImpl implements HomeGreetingDao{

    public HomeGreetingDaoImpl(BaseView baseView) {
        super(baseView);
    }

    @Override
    public HomeGreetingView getListener() {
        return (HomeGreetingView)baseView;
    }

    @Override
    public void getTodayGreeting(final String httpUuid, String token) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("method", "GetCurGreeting");
        if(token != null && !token.equals("")){
            map.put("token", token);
        }
        OkHttpUtil.getInstance().get("", map, new MgCallback() {

            @Override
            public void onSuccessResponse(String responseBody) {
                HomeGreetingBean bean = new Gson().fromJson(responseBody, HomeGreetingBean.class);
                if(bean != null){
                    if(bean.getStatusCode() == 200){
                        getListener().getHomeGreetingSuccess(httpUuid, bean.getResult().get(0).getBody().get(0).getGreetings());
                    }else {
                        getListener().getHomeGreetingError(httpUuid);
                    }
                }
            }

            @Override
            public void onErrorResponse(String message, String errorCode) {
                getListener().getHomeGreetingError(httpUuid);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getListener().getHomeGreetingError(httpUuid);
            }

        });
    }
}
