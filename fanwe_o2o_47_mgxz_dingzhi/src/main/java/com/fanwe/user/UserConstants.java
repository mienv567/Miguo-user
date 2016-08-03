package com.fanwe.user;
public class UserConstants {
    /**
     * 登录接口。
     */
    public static final String USER_lOGIN="UserLogin";
    /**
     * 注册接口。
     */
    public static final String  USER_REGISTER ="UserRegister";
    /**
     * 验证手机号是否存在.
     */
    public static final  String   USER_CHECK_EXIST  ="UserCheckExist";
    /**
     *
     * 快捷登录接口。
     */
    public static final String  USER_QUICK_LOGIN= "UserQuickLogin";
    /**
     * 第三方登录。
     */
    public static final String TRHID_LOGIN_URL="ThirdpartyLoginA";
    /**
     * 第三方注册 。
     */

    public static final String THIRD_REGISTER_URL="ThirdpartyLoginB";

    /**
     * 第三方OPENID.
     */
    public static final String THIRD_OPENID="openid";
    /**
     * 第三方platform.
     */
    public static final String THIRD_PLATFORM="platform";
    /**
     * 第三方头像地址.
     */
      public static final String THIRD_ICON="icon";
    /**
     * 第三方昵称.
     */
    public static final String THIRD_NICK="nick";


    public static final String BD_EXIT_APP = "bd_sxb_exit";

    public static final String USER_INFO = "user_info";

    public static final String USER_ID = "user_id";

    public static final String USER_SIG = "user_sig";

    public static final String USER_NICK = "user_nick";

    public static final String USER_SIGN = "user_sign";

    public static final String USER_AVATAR = "user_avatar";



    /**
     * 用户 密码。
     */
    public static final String USER_PASSWORD="pwd";

    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "3061230415";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关说于授权回调页对移动客户端应用来对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "sns.whalecloud.com";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    /**
     * qq APPID.
     */
    public static final String QQAPPID="1101169715";
    /**
     * qq getuserinfo url
     */
    public static final  String  QQ_GET_USER_INFO = "https://graph.qq.com/user/get_user_info";

    //返回码
    /**
     * 注册成功。
     */
    public static final  String REGISTER_URL = "211";
    /**
     * 注册 失败。
     */
    public static final String ALL_REGISTERED="311";
    public static final String SUCCESS="200";
    public static final String CODE_ERROR="300";
}