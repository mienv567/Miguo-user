package com.fanwe.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanwe.library.adapter.SDBaseAdapter;
import com.fanwe.library.customview.SD2LvCategoryViewHelper.SD2LvCategoryViewHelperAdapterInterface;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.ViewHolder;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.model.getClassifyList.ModelClassifyList;

import java.util.List;

public class CategoryCateLeftAdapter extends SDBaseAdapter<ModelClassifyList> implements SD2LvCategoryViewHelperAdapterInterface {

    private int mDefaultIndex;

    public CategoryCateLeftAdapter(List<ModelClassifyList> listModel, Activity activity) {
        super(listModel, activity);
    }

    public void setmDefaultIndex(int rightIndex) {
        this.mDefaultIndex = rightIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_category_left, null);
        }
        TextView tvTitle = ViewHolder.get(convertView, R.id.item_category_left_tv_title);
        TextView tvArrowRight = ViewHolder.get(convertView, R.id.item_category_left_tv_arrow_right);

        ModelClassifyList model = getItem(position);
        if (model != null) {
            SDViewBinder.setTextView(tvTitle, model.getName());
            if (model.isSelect()) {
                convertView.setBackgroundColor(SDResourcesUtil.getColor(R.color.bg_gray_categoryview_item_select));
            } else {
                convertView.setBackgroundColor(SDResourcesUtil.getColor(R.color.white));
            }
            if (model.isHasChild()) {
                tvArrowRight.setVisibility(View.VISIBLE);
            } else {
                tvArrowRight.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public void setPositionSelectState(int position, boolean select, boolean notify) {
        getItem(position).setSelect(select);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public String getTitleNameFromPosition(int position) {
        return getItem(position).getName();
    }

    @Override
    public BaseAdapter getAdapter() {
        return this;
    }

    @Override
    public Object getSelectModelFromPosition(int position) {
        return getItem(position);
    }

    @Override
    public int getTitleIndex() {
        return mDefaultIndex;
    }

    @Override
    public Object getRightListModelFromPosition_left(int position) {
        return getItem(position).getBcate_type();
    }

    @Override
    public void updateRightListModel_right(Object rightListModel) {

    }

    @Override
    public void setPositionSelectState_left(int positionLeft, int positionRight, boolean select) {
        List<ModelClassifyList> listRight = getItem(positionLeft).getBcate_type();
        if (listRight != null && positionRight >= 0 && positionRight < listRight.size()) {
            listRight.get(positionRight).setSelect(select);
        }
    }

}
