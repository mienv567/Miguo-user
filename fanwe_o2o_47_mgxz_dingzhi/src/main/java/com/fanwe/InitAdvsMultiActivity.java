package com.fanwe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.fanwe.app.App;
import com.fanwe.base.CallbackView;
import com.fanwe.cache.CacheUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.SellerConstants;
import com.fanwe.seller.model.getCityList.ModelCityList;
import com.fanwe.seller.presenters.SellerHttpHelper;
import com.fanwe.user.view.WelcomeActivity;
import com.fanwe.work.AppRuntimeWorker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguo.definition.ClassPath;
import com.miguo.factory.ClassNameFactory;
import com.miguo.utils.MGLog;
import com.miguo.utils.MGUIUtil;
import com.miguo.utils.permission.DangerousPermissions;
import com.miguo.utils.permission.PermissionsHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 初始化Activity
 */
public class InitAdvsMultiActivity extends FragmentActivity implements CallbackView {
    private SellerHttpHelper sellerHttpHelper;

    // app所需要的全部危险权限
    static final String[] PERMISSIONS = new String[]{
            DangerousPermissions.CAMERA,
            DangerousPermissions.CONTACTS,
            DangerousPermissions.LOCATION,
            DangerousPermissions.MICROPHONE,
            DangerousPermissions.PHONE,
            DangerousPermissions.STORAGE
    };

    private long mStartTime = 0;

    private final int waitTime = 800;

    private SharedPreferences setting;
    private PermissionsHelper permissionsHelper;
    private long tempT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_init_advs_multi);
        tempT = System.currentTimeMillis();
        mStartTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        } else {
            init();
        }
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    private void checkPermissions() {
        permissionsHelper = new PermissionsHelper(this, PERMISSIONS);
        if (permissionsHelper.checkAllPermissions(PERMISSIONS)) {
            permissionsHelper.onDestroy();
            //do nomarl
            init();

        } else {
            //申请权限
            permissionsHelper.startRequestNeedPermissions();
        }
        permissionsHelper.setonAllNeedPermissionsGrantedListener(new PermissionsHelper.onAllNeedPermissionsGrantedListener() {


            @Override
            public void onAllNeedPermissionsGranted() {
                MGLog.e("权限全部获取了!");
                init();
            }

            @Override
            public void onPermissionsDenied() {
                MGLog.e("权限被拒绝了!");
                InitAdvsMultiActivity.this.finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionsHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        setting = getSharedPreferences("miguo", Context.MODE_PRIVATE);
        sellerHttpHelper = new SellerHttpHelper(this, this);
        startStatistics();
        getDeviceId();
        loadCityFile();
    }

    private void setDefaultCity() {
        if (CacheUtil.getInstance().getCityCurr() == null || TextUtils.isEmpty(CacheUtil.getInstance().getCityCurr().getId())) {
            //默认城市为嵊州
            ModelCityList defaultCity = new ModelCityList();
            defaultCity.setUname("shengzhou");
            defaultCity.setId("fc9ebab9-7aa1-49d5-8c56-2bddc7d92ded");
            defaultCity.setName("嵊州");
            CacheUtil.getInstance().saveCityCurr(defaultCity);
            AppRuntimeWorker.setCityNameByModel(defaultCity);
        }
    }

    /**
     * 读取本地城市列表
     */
    private void loadCityFile() {
        new Thread(new Runnable() {
            public void run() {
                InputStream is = null;
                try {
                    is = getAssets().open("city.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String city = readTextFromIS(is);
                if (!TextUtils.isEmpty(city)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ModelCityList>>() {
                    }.getType();
                    Object object = gson.fromJson(city, type);
                    tempDatasCity = (ArrayList<ModelCityList>) object;
                }
                //保存数据
                saveCityList();
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 按行读取txt
     *
     * @param is
     * @return
     * @throws Exception
     */
    private String readTextFromIS(InputStream is) {
        if (is == null) {
            return "";
        }
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        try {
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 获取设备IMEI
     * 需要权限.6.0申请无效
     */
    public void getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        App.getInstance().setImei(telephonyManager.getDeviceId());
    }

    private void startStatistics() {
        Boolean user_first = setting.getBoolean("FIRST", true);
        String version = setting.getString("version", -1 + "");
        PackageInfo info = SDPackageUtil.getCurrentPackageInfo();
        String versionCode = String.valueOf(info.versionCode);
        if (user_first || (!versionCode.equals(-1 + "") && !version.equals(versionCode))) {// 第一次
            setting.edit().putBoolean("FIRST", false).commit();
            setting.edit().putString("version", versionCode);
        }
    }

    private void requestInitInterface() {
        //请求城市列表
        sellerHttpHelper.getCityList();
    }

    private void startMainActivity() {
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            final Intent intent = new Intent(getApplicationContext(), ClassNameFactory.getClass(ClassPath.HOME_ACTIVITY));
            long currentTime = System.currentTimeMillis();
            long offset = currentTime - mStartTime < 0 ? waitTime : currentTime - mStartTime;
            if (offset > waitTime) {
                startActivity(intent);
                finish();
            } else {
                MGUIUtil.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                }, waitTime - offset);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    public void onSuccess(String responseBody) {

    }

    private List<ModelCityList> citylist = new ArrayList<>();
    private List<ModelCityList> hot_city = new ArrayList<>();
    private ArrayList<ModelCityList> tempDatasCity;
    private ModelCityList defaultCity;

    @Override
    public void onSuccess(String method, List datas) {
        if (SellerConstants.CITY_LIST.equals(method)) {
            tempDatasCity = (ArrayList<ModelCityList>) datas;
            Message message = new Message();
            message.what = 0;
            mHandler.sendMessage(message);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    new Thread(new Runnable() {
                        public void run() {
                            saveCityList();
                        }
                    }).start();
                    break;
                case 1:
                    //请求城市列表
                    requestInitInterface();
                    startMainActivity();
                    Log.e("time", "time:" + (System.currentTimeMillis() - tempT));
                    break;
            }
        }
    };

    @Override
    public void onFailue(String responseBody) {

    }

    /**
     * 保存城市列表
     */
    private void saveCityList() {
        if (!SDCollectionUtil.isEmpty(tempDatasCity)) {
            //获取城市列表、热门城市、默认城市
            generalCityList();
            CacheUtil.getInstance().saveCityList(citylist);
            CacheUtil.getInstance().saveCityListHot(hot_city);
            //缓存当前城市
            if (CacheUtil.getInstance().getCityCurr() == null || TextUtils.isEmpty(CacheUtil.getInstance().getCityCurr().getId())) {
                if (defaultCity != null) {
                    CacheUtil.getInstance().saveCityCurr(defaultCity);
                }
            }
            if (CacheUtil.getInstance().getCityCurr() != null && !TextUtils.isEmpty(CacheUtil.getInstance().getCityCurr().getId())) {
                AppRuntimeWorker.setCityNameByModel(CacheUtil.getInstance().getCityCurr());
            } else {
                AppRuntimeWorker.setCityNameByModel(defaultCity);
            }
        }

    }

    /**
     * 生成有效城市
     */
    private void generalCityList() {
        citylist.clear();
        hot_city.clear();
        for (int i = 0; i < tempDatasCity.size(); i++) {
            ModelCityList bean = tempDatasCity.get(i);
            ModelCityList city = new ModelCityList();
            if (TextUtils.isEmpty(bean.getUname())) {
                //拼音为空，过滤掉
                continue;
            } else {
                city.setId(bean.getId());
                city.setName(bean.getName());
                city.setUname(bean.getUname());
                citylist.add(city);
                if ("1".equals(bean.getIs_hot())) {
                    hot_city.add(city);
                }
                if ("1".equals(bean.getIs_default())) {
                    defaultCity = bean;
                }
            }
        }

    }

}
