package com.gugu.client;

import com.gugu.utils.ActivityUtil;

public class Constants {

    public static final int SMS_MAX_TIME = 60; // 短信平台重发最大时间 秒
    public static final int INITIAL_DELAY_MILLIS = 175;

    public static final int PAGESIZE = 20;

    // 生产
    public static final String HOST_IP = "http://www.baggugu.com";
    public static final String HOST_IP_REQ = HOST_IP + ":8715";

    // 测试
//    public static final String HOST_IP = "http://192.168.1.101:8715";
//    public static final String HOST_IP_REQ = HOST_IP;

    public static final String PROTOCOL_IP = HOST_IP + "/app/agreement.html";

    public static String PHONE_SERVICE = "01053812098";

    public static final String Base_Token = "Base-Token";
    public static final String SESSIONID = "Cookie";
    public static final String Set_Cookie = "Set-Cookie";

    public static final String DEVICETOKEN = "DEVICETOKEN";

    public static final String FIRST_LANUCH = "FIRST_LANUCH_" + ActivityUtil.getVersionCode();

    public static final String HEAD_RANDOM = "HEAD_RANDOM";

    public static final String UserName = "UserName";
    public static final String Password = "Password";
    public static final String USERID = "USERID";

    public static final String UMengPUSHId = "UMengPUSHId";

    public static boolean LuckyDraw = false; // 投资成功后是否有抽奖活动

    public static final String WX_APP_ID = "wx2e4661c7c5c4b28a";
    public static final String WX_AppSecret = "f12ac04cb345abd1bc9d23a699094a0b";

    public static final String QQ_APP_ID = "1104763238";
    public static final String QQ_APP_KEY = "1gkTiZkUig0Fia01";

    public static final String PRE_SHOW_REWARD_TIME = "PRE_SHOW_REWARD_TIME";

    public static final String SHOW_GIVE_MONEY = "SHOW_GIVE_MONEY";

    public static boolean NEED_REFRESH_LOGIN = false;

    // 红点系
    public static final String RED_CIRCLE_TIP_INVESTMENT = "RED_CIRCLE_TIP_INVESTMENT";
    public static final String RED_CIRCLE_TIP_EARNINGS = "RED_CIRCLE_TIP_EARNINGS";
    public static final String RED_CIRCLE_TIP_FRIEND = "RED_CIRCLE_TIP_FRIEND";

    public static final String REG_MOBILE = "^(1[3,5,8,7,4])\\d{9}$";
    public static final String REG_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REG_NUM = "[0-9]{1,}";
    public static final String REG_NICKNAME = "([\u4E00-\u9FA5]+[^@]+$)|(^[^@0-9]+([\u4E00-\u9FA5]+[^@]+|[^@]+[A-Za-z_]+|[^@]+\\d+)[^@]+$){1,64}";
    public static final String REG_PASSWORD = "[a-z0-9A-Z]{4,30}";
    public static final String REG_POST = "^[1-9][0-9]{5}$";
    public static final String REG_DETAIL_ADDRESS = "^\\S{10,100}$";

    public static final String shareTitle = "推荐我心目中的理财应用。【鼓鼓理财-活存活取】";
    public static final String shareContent = "鼓鼓理财 10%活期收益，万人参与超爽体验！注册就送1000元特权金。";

}
