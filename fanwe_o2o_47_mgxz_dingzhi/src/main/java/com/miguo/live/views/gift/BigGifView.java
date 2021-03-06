package com.miguo.live.views.gift;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fanwe.constant.GiftId;
import com.fanwe.o2o.miguo.R;
import com.miguo.live.model.getGiftInfo.GiftListBean;
import com.miguo.live.views.base.BaseRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by 狗蛋哥/zlh on 2016/9/21.
 */
public class BigGifView extends BaseRelativeLayout{

    public static final int GIFT_WIDTH = 750;
    public static final int GIFT_HEIGHT = 680;
    public static final long DURATION = 2000;
    public static final int KISS_WIDTH = 200;
    public static final int KISS_HEIGHT = 200;

    HashMap<Integer, Boolean> showing;
    List<GiftListBean> beans;

    public BigGifView(Context context) {
        super(context);
    }

    public BigGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BigGifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        setBackgroundColor(Color.TRANSPARENT);
        showing = new HashMap<>();
        beans = new ArrayList<>();
    }

    /**
     * 增加么么哒动画
     * @param bean
     */
    public void addKiss(GiftListBean bean){
        if(bean == null || bean.getId() == null || bean.getId().equals("")){
            return;
        }
//        try{
//            beans.remove(bean);
//        }catch (Exception e){
//
//        }
        if(noCurrentAnimation(bean)){
            startKiss(bean);
        }

    }

    private void startKiss(GiftListBean bean){
        addKey(bean.hashCode(), bean);
        int width = dip2px(150);
        int height = width;
        ImageView gift = new ImageView(getContext());
        RelativeLayout.LayoutParams giftParams = getRelativeLayoutParams(width, height);
        giftParams.setMargins(new Random().nextInt(getScreenWidth() - width), new Random().nextInt(geScreenHeight() - height), 0, 0);
        giftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gift.setLayoutParams(giftParams);
        gift.setImageResource(R.drawable.kiss);
        addView(gift);
        startLeaveAnimation(gift, bean);
    }

    /**
     * 增加红包/福气冲天
     * @param bean
     */
    public void addRedPacket(GiftListBean bean){
        if(bean == null || bean.getId() == null || bean.getId().equals("")){
            return;
        }

//        try{
//            beans.remove(bean);
//        }catch (Exception e){
//
//        }
        if(noCurrentAnimation(bean)){
            startPacket(bean);
        }

    }

    private void startPacket(GiftListBean bean){
        addKey(bean.hashCode(), bean);
        int width = dip2px(150);
        int height = width;
        ImageView gift = new ImageView(getContext());
        RelativeLayout.LayoutParams giftParams = getRelativeLayoutParams(width, height);
        giftParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        giftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gift.setLayoutParams(giftParams);
        gift.setImageResource(R.drawable.redpacket);
        addView(gift);
        startLeaveAnimation(gift, bean);
    }

    /**
     * 大礼物
     * @param bean
     */
    public void addBigGift(GiftListBean bean){
        if(bean == null || bean.getId() == null || bean.getId().equals("")){
            return;
        }
//        try{
//            beans.remove(bean);
//        }catch (Exception e){
//
//        }
        if(noCurrentAnimation(bean)){
            startBigGift(bean);
        }
    }

    private void startBigGift(GiftListBean bean){
        addKey(bean.hashCode(), bean);
        ImageView gift = new ImageView(getContext());
        RelativeLayout.LayoutParams giftParams = getRelativeLayoutParams(getGiftWidth(), getGiftHeight());
        giftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gift.setLayoutParams(giftParams);
        addView(gift);
        gift.postDelayed(new BigGiftRunnable(gift, bean), getSpeed(bean.getId()));
    }

    private boolean noCurrentAnimation(GiftListBean bean){
        beans.add(bean);
        return showing.size() == 0;
    }

    private void addKey(int key, GiftListBean bean){
        showing.put(key, true);
    }

    private void removeKey(int key, GiftListBean bean){
        showing.remove(key);
    }

    private void onCurrentAnimationEnd(){
        Log.d(tag, "on current animation end: bean size: " + beans.size());
        if(beans.size() > 0){
            Log.d(tag, "on current animation end: bean id: " + beans.get(0));
            switch (beans.get(0).getId()) {
                /**
                 * 小礼物 随弹幕出现
                 */
                case GiftId.STAR:
                case GiftId.FLOWER:
                case GiftId.SWEET:
                case GiftId.MIGUO_BABY:
                    break;
                /**
                 * 么么哒 屏幕随机出现
                 * 福气临门 屏幕中心出现，慢慢消失
                 */
                case GiftId.KISS:
                    startKiss(beans.get(0));
                    break;
                case GiftId.GOOD_FORTUNE:
                    startPacket(beans.get(0));
                    break;
                /**
                 * 大礼物，动图序列帧
                 */
                case GiftId.BEST_LOVE:
                case GiftId.FIREWORKS:
                case GiftId.BASKETS:
                case GiftId.BOMBARDIER:
                case GiftId.FERRARI:
                case GiftId.SOAR:
                    startBigGift(beans.get(0));
                    break;
            }
        }
    }

    private void startLeaveAnimation(final View view,final GiftListBean bean){
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(300);
        animation.setStartOffset(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BigGifView.this.removeView(view);

                beans.remove(bean);
                removeKey(bean.hashCode(), bean);
                onCurrentAnimationEnd();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    /**
     * 礼物宽度
     * @return
     */
    public int getGiftWidth(){
        return getScreenWidth();
    }

    /**
     * 礼物高度
     * @return
     */
    public int getGiftHeight(){
        return getGiftWidth() * GIFT_HEIGHT / GIFT_WIDTH;
    }

    public class BigGiftRunnable implements Runnable{

        ImageView gift;
        GiftListBean bean;
        int currentIndex;
        long time;

        public BigGiftRunnable(ImageView gift, GiftListBean bean){
            this.gift = gift;
            this.bean = bean;
            setCurrentIndex(0);
            time = System.currentTimeMillis();
        }

        @Override
        public void run() {
            Log.d(tag, "duration: " + (System.currentTimeMillis() - time) + " ,speed: " + getSpeed(bean.getId()));
            time = System.currentTimeMillis();
            if(getCurrentIndex() < getCount()){
                gift.setImageResource(GiftPictures.getItem(bean.getId(), getCurrentIndex()));
                setCurrentIndex(getCurrentIndex() + 1);
                gift.postDelayed(this,getSpeed(bean.getId()));
            }else {
                removeView(gift);
                beans.remove(bean);
                removeKey(bean.hashCode(), bean);
                onCurrentAnimationEnd();
            }
        }

        public int getCount(){
            return GiftPictures.getCount(bean.getId());
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        private void startLeaveAnimation(){
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
            alphaAnimation.setDuration(200);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeView(gift);
                    invalidate();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            gift.startAnimation(alphaAnimation);
        }

    }

    public long getSpeed(String giftId){
        return DURATION / GiftPictures.getCount(giftId);
    }

}
