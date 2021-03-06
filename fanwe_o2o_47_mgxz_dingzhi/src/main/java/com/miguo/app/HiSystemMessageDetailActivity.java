package com.miguo.app;

import com.fanwe.o2o.miguo.R;
import com.miguo.category.Category;
import com.miguo.category.HiSystemMessageDetailCategory;
import com.miguo.definition.IntentKey;

/**
 * Created by Barry/狗蛋哥/zlh on 2017/3/9.
 * 系统消息列表
 */
public class HiSystemMessageDetailActivity extends HiBaseActivity {

    String url;
    String systemId;
    @Override
    protected Category initCategory() {
        getIntentData();
        return new HiSystemMessageDetailCategory(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_hisystem_message_detail);
    }

    private void getIntentData(){
        if(null != getIntent()){
            this.url = getIntent().getStringExtra(IntentKey.SYSTEM_MESSAGE_URL);
            this.systemId = getIntent().getStringExtra(IntentKey.SYSTEM_MESSAGE_ID);
        }
    }

    @Override
    protected void finishActivity() {
        if(null != getCategory()){
            getCategory().clickBack();
        }
    }

    @Override
    public HiSystemMessageDetailCategory getCategory() {
        return (HiSystemMessageDetailCategory)super.getCategory();
    }

    public String getUrl() {
        return url;
    }

    public String getSystemId() {
        return systemId;
    }
}
