package com.miguo.live.views.listener.dialog;

import android.view.View;

import com.fanwe.o2o.miguo.R;
import com.miguo.live.views.category.dialog.DialogCategory;
import com.miguo.live.views.category.dialog.LiveBackDialogCategory;

/**
 * Created by zlh on 2016/8/26.
 */
public class LiveBackDialogListener extends DialogListener{

    public LiveBackDialogListener(DialogCategory category) {
        super(category);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.sure_action:
                clickSure();
                break;
            case R.id.cancel_action:
                clickCancel();
                break;
        }
    }

    private void clickSure(){
        getCategory().clickSure();
    }

    private void clickCancel(){
        getCategory().clickCancel();
    }

    @Override
    public LiveBackDialogCategory getCategory() {
        return (LiveBackDialogCategory) super.getCategory();
    }
}
