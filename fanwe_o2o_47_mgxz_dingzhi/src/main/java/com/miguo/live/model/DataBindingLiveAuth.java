package com.miguo.live.model;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;


public class DataBindingLiveAuth {

    public final int BEAUTY = 1;
    public final int SMART = 2;
    public final int EXCELLENT = 3;

    public final ObservableInt mode = new ObservableInt();
    //兴趣
    public final ObservableField<String> interest = new ObservableField<String>();
    //常驻城市
    public final ObservableField<String> city = new ObservableField<String>();
    //手机
    public final ObservableField<String> mobile = new ObservableField<String>();

    public DataBindingLiveAuth() {
        mode.set(BEAUTY);
    }
}
