package com.miguo.ui.view.floatdropdown.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanwe.o2o.miguo.R;
import com.miguo.entity.SingleMode;
import com.miguo.ui.view.floatdropdown.interf.OnSingleModeRVItemClickListener;

import java.util.List;

/**
 * Created by didik 
 * Created time 2017/1/6
 * Description: 
 */

public class SingleListAdapter extends RecyclerView.Adapter<SingleListAdapter.ViewHolder> {
    private List<SingleMode> singleModeList;
    private OnSingleModeRVItemClickListener itemClickListener;
    public final int checkedTextColor;
    public final int uncheckedTextColor;
    public final int uncheckedDividerColor;
    private int preClickPosition = -2;

    public SingleListAdapter(List<SingleMode> singleModeList) {
        this.singleModeList = singleModeList;
        checkedTextColor = Color.parseColor("#F5B830");
        uncheckedTextColor=Color.parseColor("#999999");
        uncheckedDividerColor=Color.parseColor("#A1A2A5");
    }

    public void setSingleModeList(List<SingleMode> singleModeList) {
        this.singleModeList = singleModeList;
    }

    public void setItemClickListener(OnSingleModeRVItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    /**
     * 代替点击某个item,代替 {@link View#performClick()}
     * @param handlePosition 位置
     */
    public void performPosition(int handlePosition){
        if (singleModeList==null || singleModeList.size()<=0){
            return;
        }
        SingleMode mode = singleModeList.get(handlePosition);
        boolean checked = mode.isChecked();
        if (checked){
            return;
        }
        mode.setChecked(true);
        notifyItemChanged(handlePosition);
        if (preClickPosition !=-2){
            singleModeList.get(preClickPosition).setChecked(false);
            notifyItemChanged(preClickPosition);
        }
        preClickPosition = handlePosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_rv_single,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SingleMode singleMode = singleModeList.get(position);
        final boolean checked = singleMode.isChecked();
        toggle(checked,holder.tv_name,holder.divider);
        holder.tv_name.setText(singleMode.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked){
                    return;
                }
                singleMode.setChecked(true);
                int adapterCurPosition = holder.getAdapterPosition();
                notifyItemChanged(adapterCurPosition);
                if (preClickPosition !=-2){
                    singleModeList.get(preClickPosition).setChecked(false);
                    notifyItemChanged(preClickPosition);
                }
                preClickPosition = adapterCurPosition;
                if (itemClickListener!=null){
                    itemClickListener.onItemClick(v,holder.getAdapterPosition(),singleMode);
                }
            }
        });
    }
    private void toggle(boolean isCheck,TextView tv_name,View divider){
        if (isCheck){
            tv_name.setTextColor(checkedTextColor);
            divider.setBackgroundColor(checkedTextColor);
        }else {
            tv_name.setTextColor(uncheckedTextColor);
            divider.setBackgroundColor(uncheckedDividerColor);
        }
    }

    @Override
    public int getItemCount() {
        return singleModeList ==null ? 0 : singleModeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public View divider;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_name= (TextView) itemView.findViewById(R.id.tv_name);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
