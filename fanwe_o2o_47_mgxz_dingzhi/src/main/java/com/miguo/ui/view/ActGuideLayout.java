package com.miguo.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.didikee.uilibs.Text.UICharacterCount;
import com.fanwe.app.App;
import com.fanwe.baidumap.BaiduMapManager;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.network.HttpCallback;
import com.fanwe.network.OkHttpUtil;
import com.fanwe.o2o.miguo.R;
import com.fanwe.seller.views.GoodsDetailActivity;
import com.fanwe.utils.DataFormat;
import com.fanwe.utils.SDDistanceUtil;
import com.fanwe.work.AppRuntimeWorker;
import com.miguo.adapter.base.BaseRVAdapter;
import com.miguo.adapter.base.BaseRVViewHolder;
import com.miguo.adapter.base.SimpleRVAdapter;
import com.miguo.live.views.customviews.MGToast;
import com.miguo.model.guidelive.GuideInnerGoods;
import com.miguo.model.guidelive.GuideInnerRoot;
import com.miguo.ui.view.interf.ExpandListener;
import com.miguo.ui.view.interf.ExpandStatus;
import com.miguo.ui.view.interf.Expandable;
import com.miguo.ui.view.interf.IActGuideLayout;
import com.miguo.utils.DisplayUtil;
import com.miguo.utils.MGLog;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by didik on 2016/11/16.
 */

public class ActGuideLayout extends LinearLayout implements Expandable,IActGuideLayout, View
        .OnClickListener {
    private int minHeight=0;
    private int maxHeight=0;
    private final Context context;
    private View ll_tools;
    private ImageView iv_star;
    private ImageView iv_share;
    private LinearLayout ll_root;//root
    private LinearLayout ll_text_container;//文字
    private LinearLayout ll_dot_container;//小点点
    private FrameLayout fl_rv_container;//rv
    private ExpandListener expandListener;
    private boolean expandable=false;
    private ActLayoutReceiveDataListener sendDataListener;
    private RecyclerView parentRV;
    private String mGuideID="";
    private int position=-1;
    private List<GuideInnerGoods> innerGoods;
    private List<String> mTags;
    //    private RecyclerView mRVInner;

    public ActGuideLayout(Context context) {
        this(context,null);
    }

    public ActGuideLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ActGuideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        startFlow();
    }

    private void startFlow() {
        LayoutInflater.from(context).inflate(R.layout.custom_act_guide_layout, this);
        initView();
        registerClickListener();
    }

    private void initView() {
        //分享与
        ll_tools = findViewById(R.id.ll_tools);
        iv_star = (ImageView) findViewById(R.id.iv_star);//点赞
        iv_share = (ImageView) findViewById(R.id.iv_share);//分享

//        mRVInner = (RecyclerView) findViewById(R.id.recyclerview);

        ll_text_container = (LinearLayout) findViewById(R.id.ll_txt_container);
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        ll_dot_container = (LinearLayout) findViewById(R.id.ll_dot_container);
        fl_rv_container = (FrameLayout) findViewById(R.id.fl_recyclerview_container);
    }

    private void registerClickListener() {
        iv_share.setOnClickListener(this);
        iv_star.setOnClickListener(this);
        ll_text_container.setOnClickListener(this);
    }

    public void bindData(int position,String guide_id,int statement,List<GuideInnerGoods> innerGoods,String desc) {
        this.mGuideID=guide_id;
        this.position=position;
        this.innerGoods=innerGoods;
        if (!TextUtils.isEmpty(desc)){
            mTags = getTags(desc);
        }
        //TODO 默认是收缩时的状态
        bindInnerData(statement);
    }

    private List<String> getTags(String desc){
        List<String> tags=new ArrayList<>();
        desc="Display user-ge很好啊 ent into every aspect 我的歌 of your marketing in minutes.Display user-ge很好啊nerated content into every aspect 我的歌 of your marketing efforts in minutes";
        float count = UICharacterCount.getCount(desc);
        if (count*2  <=30){
            tags.add(desc);
            return tags;
        }
        int lastPosition=0;
        int charCount=0;
        int length=desc.length();
        for (int i = 0; i < length; i++) {
            boolean isChinese= UICharacterCount.isChinese(desc.charAt(i));
            if (isChinese) {
                charCount+=2;
            }else {
                charCount++;
            }
            if (charCount==29) {
                if (UICharacterCount.isChinese(desc.charAt(i+1))) {
                    //此时需要截取
                    String str=desc.substring(lastPosition, i+1);
                    tags.add(str);
                    lastPosition = i+1;
                    charCount=0;
                }
            }
            if (charCount ==30) {
                String str=desc.substring(lastPosition, i+1);
                tags.add(str);
                lastPosition=i+1;
                charCount=0;
            }

            if (i==length-1){
                //最后一遍的时候
                if (lastPosition !=length-1){
                    String str = desc.substring(lastPosition, length);
                    tags.add(str);
                }else {
                    //empty
                }

            }
        }
        return tags;
    }

    private void bindInnerData(int statement){
        if (statement== ExpandStatus.NORMAL){
            ll_text_container.setClickable(true);
            ll_tools.setVisibility(GONE);
            addDot2LinearLayout(3,ll_dot_container);
            addTextView(mTags,ll_text_container,statement);
            hideHorizontalRecyclerView(fl_rv_container);
        }else if (statement == ExpandStatus.EXPANDED){
            ll_text_container.setClickable(false);
            ll_tools.setVisibility(VISIBLE);
            hideDotLayout(ll_dot_container);
            addTextView(mTags,ll_text_container,statement);
            addHorizontalRecyclerView(null,fl_rv_container);
        }
    }

    @Override
    public void expand() {
        if (expandListener!=null)expandListener.expandStart();
        minHeight=getHeight();
        expandable=true;
        MGLog.e("test","prepare=="+"height: "+getHeight() +"measuredHeight: "+getMeasuredHeight());
        bindInnerData(ExpandStatus.EXPANDED);
        MGLog.e("test","prepare=="+"height: "+getHeight() +"measuredHeight: "+getMeasuredHeight());
    }

    @Override
    public void shrink() {
        if (expandListener!=null)expandListener.shrinkStart();
    }

    @Override
    public void addDot2LinearLayout(int num, LinearLayout target) {
        if (target==null || num <=0)return;
        if (target.getVisibility()==GONE){
            target.setVisibility(VISIBLE);
        }
        target.removeAllViews();
        int size = DisplayUtil.dp2px(context, 2);
        int margin = DisplayUtil.dp2px(context, 4);
        LinearLayout.LayoutParams params=new LayoutParams(size,size);
        params.setMargins(margin,0,margin,0);
        for (int i = 0; i < num; i++) {
            View dotView = createDotView();
            target.addView(dotView,params);
        }

    }

    @Override
    public void hideDotLayout(ViewGroup dotContainer) {
        if (dotContainer==null)return;
        dotContainer.removeAllViews();
        dotContainer.setVisibility(GONE);
    }

    @Override
    public void removeTextView(int left) {
        int childCount = ll_text_container.getChildCount();
        for (int index = childCount; index > left; index--) {
            ll_text_container.removeViewAt(index);
        }
    }

    @Override
    public TextView createTextView() {
        TextView textView=new TextView(context);
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setTextSize(12);
        textView.setLineSpacing(2,1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public View createDotView() {
        View dot=new View(context);
        dot.setBackgroundColor(Color.DKGRAY);
        return dot;
    }

    @Override
    public void addHorizontalRecyclerView(List data,ViewGroup rvContainer) {
        if (rvContainer==null || innerGoods==null || innerGoods.size()<1)return;
        rvContainer.removeAllViews();
        rvContainer.setVisibility(VISIBLE);
        RecyclerView rvInner=new RecyclerView(context);
        rvInner.setFocusable(false);
        rvInner.setHasFixedSize(true);
        rvInner.setLayoutManager(new LinearLayoutManager(context,HORIZONTAL,false));
        final SimpleRVAdapter<GuideInnerGoods> adapter=new SimpleRVAdapter<GuideInnerGoods>(context,R.layout.item_custom_guide_inner,innerGoods) {

            @Override
            protected void bindView(BaseRVViewHolder helper, GuideInnerGoods item) {
                if (item == null)return;
                SDViewBinder.setImageView(item.getIcon(),helper.getImageView(R.id.iv_img));
                helper.setTextView(R.id.tv_title,item.getName(),"----");
                helper.setTextView(R.id.tv_price_unit,item.getTuan_price_with_unit(),"--");
                helper.setTextView(R.id.tv_location,getLocationInfo(item.getArea_name(),item.getDistance(),item.getCate_name()),"--");
            }

        };
        final int offset = DisplayUtil.dp2px(context, 5);
        rvInner.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                if (parent.getChildAdapterPosition(view)==0){
                    outRect.left=0;
                    outRect.right=offset;
                }else {
                    outRect.left=offset;
                    outRect.right=offset;
                }
            }
        });
        rvInner.setAdapter(adapter);
        rvInner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                float rawX = event.getRawX();
                float rawY = event.getRawY();
//                Log.e("test-inner","x: "+x+"   y:"+y+"   rawX:"+rawX + "   rawY: "+rawY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        break;
                }
                return false;
            }
        });
        adapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String goods_id = adapter.getItem(position).getId();
                if (TextUtils.isEmpty(goods_id))return;
                Intent intent=new Intent(context, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_ID,goods_id);
                context.startActivity(intent);
            }
        });
        rvContainer.addView(rvInner,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private String getLocationInfo(String mapLocation,String distance,String location){
        String tempMapLocation = getLimitedString(mapLocation, 6);
        String tempDistance = SDDistanceUtil.getFormatDistance(DataFormat.toDouble(distance));
        String tempLocation = getLimitedString(location, 4);
        if (!TextUtils.isEmpty(tempMapLocation)){
            tempMapLocation+=" | ";
        }else {
            tempMapLocation=".. | ";
        }
//        if (!TextUtils.isEmpty(tempLocation)&&!TextUtils.isEmpty(tempDistance)){
//            tempDistance+=" | ";
//        }
        if (TextUtils.isEmpty(tempLocation)){
            tempLocation="..";
        }
        if (TextUtils.isEmpty(tempDistance)){
            tempDistance="..";
        }
        return tempMapLocation+ tempDistance +" | "+tempLocation;
    }

    private String getLimitedString(String text,int limitNum){
        if (TextUtils.isEmpty(text)){
            return "";
        }
        int length = Math.round(UICharacterCount.getCount(text));
        if (length<=limitNum){
            return text;
        }else if (length>limitNum){
            return text.substring(0,limitNum-1)+"...";
        }
        return "";
    }

    @Override
    public void hideHorizontalRecyclerView(ViewGroup rvContainer) {
        if (rvContainer == null)return;
        rvContainer.removeAllViews();
        rvContainer.setVisibility(GONE);
    }

    @Override
    public void sendData2Parent(List childData) {

    }

    @Override
    public void addTextView(List tags, LinearLayout target,int status) {
        if (target == null ||tags==null || tags.size()<=0)return;
        target.removeAllViews();
//        int size = num ==-1 ? tags.size() : num;
//        for (int i = 0; i < size; i++) {
//            TextView textView = createTextView();
//            textView.setText(tags.get(i).toString());
//            target.addView(textView);
//        }
        TextView textView = createTextView();
        String finalShow="";
        int maxSize=0;
        Log.e("test","size: "+tags.size());
        if (status == ExpandStatus.EXPANDED){
//            int maxSize=6;//3对
            maxSize = tags.size()>6 ? 6:tags.size();
        }else if (status == ExpandStatus.NORMAL){
            maxSize = tags.size()>2 ? 2:tags.size();
        }
        for (int i = 0; i < maxSize; i++) {
            if (i==maxSize-1){
                finalShow+=tags.get(i);
            }else {
                finalShow+=tags.get(i)+"\n";
            }
        }

        int right = DisplayUtil.dp2px(context, 80);
        int top = DisplayUtil.dp2px(context,9);
        textView.setPadding(right,top,right,0);
        finalShow = status ==ExpandStatus.NORMAL ? finalShow :finalShow+"\n";
        Log.e("test","finalShow: "+finalShow);
        textView.setText(finalShow);
        target.addView(textView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_star:
                doStar();
                break;
            case R.id.iv_share:
                doShare();
                break;
            case R.id.ll_txt_container:
                if (innerGoods == null || innerGoods.size()<1){
                    requestGuideGoods(mGuideID);
                }else {
                    expand();
                }
                break;
            default:
                break;
        }
    }

    private void doStar(){
        MGToast.showToast("star");
    }
    private void doShare(){
        MGToast.showToast("share");
    }

    public void setExpandListener(ExpandListener listener){
        this.expandListener=listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (expandable){
//            expandable=false;
//            maxHeight=getHeight();
//            SimpleExpandAnimation simpleExpandAnimation=new SimpleExpandAnimation(minHeight,maxHeight);
//            Animator[] animators = simpleExpandAnimation.getAnimators(ll_root);
//            Animator animator = animators[0];
//            animator.setDuration(1500);
//            animator.setInterpolator(new AccelerateDecelerateInterpolator());
//            animator.start();
//        }
//        Log.e("test","height-layout: "+getHeight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void requestGuideGoods(String guide_id){
        String city_id = AppRuntimeWorker.getCity_id();
        double latitude = BaiduMapManager.getInstance().getLatitude();
        double longitude = BaiduMapManager.getInstance().getLongitude();
        if (TextUtils.isEmpty(city_id) || TextUtils.isEmpty(guide_id)||latitude==0 || longitude ==0)return;
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("token", App.getInstance().getToken());
        params.put("city_id", city_id);
        params.put("guide_id", guide_id);
        params.put("m_longitude", longitude+"");
        params.put("m_latitude", latitude+"");
        params.put("method", "InterestingGuideGoods");
        OkHttpUtil.getInstance().get(params, new HttpCallback<GuideInnerRoot>() {

            @Override
            public void onSuccessWithBean(GuideInnerRoot guideInnerRoot) {
                List<GuideInnerRoot.ResultBean.BodyBean> body = guideInnerRoot.getResult().get(0)
                        .getBody();
                if (body !=null && body.size()>0){
                    innerGoods = body.get(0).getGoods_list();
                    if (innerGoods!=null && innerGoods.size()>0){
                        if (sendDataListener !=null){
                            sendDataListener.onChildReceiveData(position,innerGoods);
                        }
                        expand();
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    public void setOnChildReceiveDataListener(ActLayoutReceiveDataListener listener){
        this.sendDataListener=listener;
    }

    public void setRecycerView(RecyclerView mRecyclerView) {
        this.parentRV=mRecyclerView;
    }

    public interface ActLayoutReceiveDataListener{
        void onChildReceiveData(int position,List<GuideInnerGoods> childData);
    }
}
