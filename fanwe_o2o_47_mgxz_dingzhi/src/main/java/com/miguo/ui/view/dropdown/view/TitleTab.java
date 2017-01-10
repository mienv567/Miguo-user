package com.miguo.ui.view.dropdown.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.o2o.miguo.R;
import com.miguo.ui.view.dropdown.interf.ExpandReverse;

/**
 * Created by didik 
 * Created time 2017/1/5
 * Description: 
 */

public class TitleTab extends LinearLayout implements ExpandReverse {

    private ObjectAnimator expandAnimator;
    private ObjectAnimator reverseAnimator;
    private ImageView arrowImage;
    private boolean isNormal=true;

    public TitleTab(Context context) {
        this(context,null);
    }

    public TitleTab(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        TextView innerText=new TextView(context);
        innerText.setGravity(Gravity.CENTER);
        innerText.setTextSize(14);
        innerText.setTextColor(Color.BLACK);
        innerText.setLines(1);
        innerText.setEllipsize(TextUtils.TruncateAt.END);

        arrowImage = new ImageView(context);
        arrowImage.setImageResource(R.drawable.ic_arrow_down_gray);
        arrowImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LayoutParams arrowParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        arrowParams.setMargins(10,6,0,0);


        //set params
        addView(innerText,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(arrowImage,arrowParams);

        initAnimator();
    }

    private void initAnimator() {
        expandAnimator = ObjectAnimator.ofFloat(arrowImage,"rotation",0,180f);
        reverseAnimator = ObjectAnimator.ofFloat(arrowImage,"rotation",180f,360f);
    }

    public void setText(String text){
        text = TextUtils.isEmpty(text) ? "" : text;
        ((TextView)getChildAt(0)).setText(text);
    }

    public void start(){
        if (isNormal){
            expand();
        }else {
            reverse();
        }
    }

    @Override
    public void expand(){
        expandAnimator.start();
        isNormal=false;
    }

    @Override
    public void reverse() {
        reverseAnimator.start();
        isNormal=true;
    }

}
