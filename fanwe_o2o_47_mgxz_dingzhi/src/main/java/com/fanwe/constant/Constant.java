package com.fanwe.constant;

import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.o2o.miguo.R;

public class Constant {

    public static final String RESULT_SUCCESS = "200";

    public static final String EARN_SUB_CHAR = "市";

    /**
     * sdk支付方式
     */
    public static final class PaymentType {
        /**
         * 支付宝（新）
         */
        public static final String MALIPAY = "Malipay";

        public static final String ALIAPP = "Aliapp";
        /**
         * 微信支付
         */
        public static final String WXAPP = "WxApp";
        /**
         * 银联支付
         */
        public static final String UPACPAPP = "Upacpapp";
    }

    public static final class LoadImageType {
        /**
         * 任何网络下都加载图片
         */
        public static final int ALL = 1;
        /**
         * wifi网络下加载图片,移动网络下不允许加载图片
         */
        public static final int ONLY_WIFI = 0;
    }

    public enum TitleType {
        TITLE_NONE, TITLE
    }

    public static final class SearchTypeMap {
        /**
         * 全部
         */
        public static final int ALL = -1;
        /**
         * 优惠券
         */
        public static final int YOU_HUI = 0;
        /**
         * 活动
         */
        public static final int EVENT = 1;
        /**
         * 团购
         */
        public static final int TUAN = 2;
        /**
         * 门店
         */
        public static final int STORE = 3;


    }

    public static final class SearchTypeNormal {
        /**
         * 优惠券
         */
        public static final int YOU_HUI = 0;
        /**
         * 团购
         */
        public static final int TUAN = 2;
        /**
         * 商家
         */
        public static final int MERCHANT = 3;
        /**
         * 活动
         */
        public static final int EVENT = 4;
        /**
         * 商城
         */
        public static final int SHOP = 5;
    }

    public static final class SearchTypeNormalString {
        public static final String YOU_HUI = SDResourcesUtil.getString(R.string.youhui_coupon);
        public static final String TUAN = SDResourcesUtil.getString(R.string.tuan_gou);
        public static final String MERCHANT = SDResourcesUtil.getString(R.string.supplier);
        public static final String EVENT = SDResourcesUtil.getString(R.string.event);
        public static final String SHOP = SDResourcesUtil.getString(R.string.goods);
    }

    public static final class CommentType {
        //团购
        public static final String DEAL = "deal";
        public static final String YOUHUI = "youhui";
        public static final String EVENT = "event";
        //店铺
        public static final String STORE = "store";
    }


    public static final class UserLoginState {
        public static final int UN_LOGIN = 0;
        public static final int LOGIN = 1;
        public static final int TEMP_LOGIN = 2;
    }

    public static final class DealTag {
        /**
         * 0元抽奖
         */
        public static final int FREE_LOTTERY = 1;
        /**
         * 免预约
         */
        public static final int FREE_RESERVATION = 2;
        /**
         * 多套餐
         */
        public static final int MULTI_PACKAGES = 3;
        /**
         * 可订座
         */
        public static final int CAN_RESERVATION = 4;
        /**
         * 折扣券
         */
        public static final int DISCOUNT_TICKETS = 5;
        /**
         * 过期退
         */
        public static final int EXPIRED_REFUND = 6;
        /**
         * 随时退
         */
        public static final int ANY_REFUND = 7;
        /**
         * 7天退
         */
        public static final int SEVEN_REFUND = 8;
        /**
         * 免运费
         */
        public static final int FREE_FREIGHT = 9;
        /**
         * 满立减
         */
        public static final int FULL_MINUS = 10;
    }

    public enum EnumLoginState {
        /**
         * 未登录
         */
        UN_LOGIN,
        /**
         * 临时登录，需要绑定手机号
         */
        LOGIN_NEED_BIND_PHONE,
        /**
         * 已登录，手机号为空
         */
        LOGIN_EMPTY_PHONE,
        /**
         * 已登录
         */
        LOGIN,
        /**
         * 已登录,需要验证
         */
        LOGIN_NEED_VALIDATE
    }

    public static final class IndexType {
        /**
         * 自定义url
         */
        public static final int URL = 0;

        /**
         * 团购列表
         */
        public static final int TUAN_LIST = 11;
        /**
         * 商品列表
         */
        public static final int GOODS_LIST = 12;
        /**
         * 积分商品列表
         */
        public static final int SCORE_LIST = 13;
        /**
         * 活动列表
         */
        public static final int EVENT_LIST = 14;
        /**
         * 优惠券列表
         */
        public static final int YOUHUI_LIST = 15;
        /**
         * 门店列表
         */
        public static final int STORE_LIST = 16;
        /**
         * 文章列表
         */
        public static final int NOTICE_LIST = 17;

        /**
         * 购物明细
         */
        public static final int DEAL_DETAIL = 21;
        /**
         * 活动明细
         */
        public static final int EVENT_DETAIL = 24;
        /**
         * 优惠明细
         */
        public static final int YOUHUI_DETAIL = 25;
        /**
         * 门店明细
         */
        public static final int STORE_DETAIL = 26;
        /**
         * 文章明细
         */
        public static final int NOTICE_DETAIL = 27;

        /**
         * 分销分享管理
         */
        public static final int DISTRIBUTION_MANAGER = 41;

        /**
         * 扫一扫
         */
        public static final int SCAN = 31;
        /**
         * 附近会员
         */
        public static final int NEARUSER = 32;
        /**
         * 分销小店
         */
        public static final int DISTRIBUTION_STORE = 51;
        /**
         * 发现
         */
        public static final int DISCOVERY = 61;

        /**
         * 外卖列表
         */
        public static final int TAKEAWAY_LIST = 100;
        /**
         * 订座列表
         */
        public static final int RESERVATION_LIST = 101;

    }

    public static final class ShareType {
        /**
         * 商品详情
         */
        public static final String GOODS = "1";
        /**
         * 门店详情
         */
        public static final String SHOP = "2";
        /**
         * 专题详情
         */
        public static final String TOPIC = "3";
        /**
         * 直播详情
         */
        public static final String LIVE = "4";
        /**
         * 网站首页
         */
        public static final String WEB_HOME = "10";
        /**
         * 小店首页（名片）
         */
        public static final String SHOP_HOME = "11";
        /**
         * 网红主页
         */
        public static final String USER_HOME = "12";
    }
}
