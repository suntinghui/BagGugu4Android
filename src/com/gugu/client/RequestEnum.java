package com.gugu.client;

import java.util.HashMap;

import com.gugu.client.net.RequestModel;

public class RequestEnum {

    private static HashMap<String, RequestModel> requestMap = null;

    public static RequestModel getRequest(String id) {
        if (null == requestMap) {
            requestMap = new HashMap<String, RequestModel>();

            requestMap.put(USER_REGISTER_CHECK, new RequestModel(USER_REGISTER_CHECK, Constants.HOST_IP_REQ + "/rpc/user/regist/check/telphone.app"));
            requestMap.put(USER_LOGIN, new RequestModel(USER_LOGIN, Constants.HOST_IP_REQ + "/rpc/user/login.app"));
            requestMap.put(USER_VALID_LOGIN_PWD, new RequestModel(USER_VALID_LOGIN_PWD, Constants.HOST_IP_REQ + "/rpc/user/password/valid.app"));
            requestMap.put(USER_REGIST_SEND_SMS, new RequestModel(USER_REGIST_SEND_SMS, Constants.HOST_IP_REQ + "/rpc/user/regist/sms/send.app"));
            requestMap.put(USER_REGIST_SET_PWD, new RequestModel(USER_REGIST_SET_PWD, Constants.HOST_IP_REQ + "/rpc/user/regist/password/set.app"));
            requestMap.put(USER_VERIFY_TOKEN, new RequestModel(USER_VERIFY_TOKEN, Constants.HOST_IP_REQ + "/rpc/user/verify/token.app"));
            requestMap.put(USER_UPDATE_LOGIN_PWD, new RequestModel(USER_UPDATE_LOGIN_PWD, Constants.HOST_IP_REQ + "/rpc/user/password/update.app"));
            requestMap.put(USER_NOLOGIN_PASSWORD_UPDATE, new RequestModel(USER_NOLOGIN_PASSWORD_UPDATE, Constants.HOST_IP_REQ + "/rpc/user/nologin/password/update.app"));
            requestMap.put(USER_LOGOUT, new RequestModel(USER_LOGOUT, Constants.HOST_IP_REQ + "/rpc/user/logout.app"));
            requestMap.put(USER_SET_LOGO, new RequestModel(USER_SET_LOGO, Constants.HOST_IP_REQ + "/rpc/user/set/logo.app"));
            requestMap.put(SECURITY_CENTER_INFO, new RequestModel(SECURITY_CENTER_INFO, Constants.HOST_IP_REQ + "/rpc/security/center/info.app"));
            requestMap.put(SECURITY_CENTER_ITEM_INFO, new RequestModel(SECURITY_CENTER_ITEM_INFO, Constants.HOST_IP_REQ + "/rpc/security/center/item/info.app"));
            requestMap.put(SECURITY_CENTER_ITEM_INFO_REALNAME, new RequestModel(SECURITY_CENTER_ITEM_INFO_REALNAME, Constants.HOST_IP_REQ + "/rpc/security/center/realname/verify/save.app"));
            requestMap.put(SECURITY_CENTER_ITEM_SAVE, new RequestModel(SECURITY_CENTER_ITEM_SAVE, Constants.HOST_IP_REQ + "/rpc/security/center/item/save.app"));
            requestMap.put(SECURITY_CENTER_EMERGENCY_CONTACT_SAVE, new RequestModel(SECURITY_CENTER_EMERGENCY_CONTACT_SAVE, Constants.HOST_IP_REQ + "/rpc/security/center/emergency/contact/save.app"));
            requestMap.put(SECURITY_CENTER_TRANSACTION_PWD_SAVE, new RequestModel(SECURITY_CENTER_TRANSACTION_PWD_SAVE, Constants.HOST_IP_REQ + "/rpc/security/center/transaction/password/save.app"));
            requestMap.put(SEND_VCODE, new RequestModel(SEND_VCODE, Constants.HOST_IP_REQ + "/rpc/send/vcode.app"));
            requestMap.put(NOLOGIN_SEND_VCODE, new RequestModel(NOLOGIN_SEND_VCODE, Constants.HOST_IP_REQ + "/rpc/nologin/send/vcode.app"));
            requestMap.put(DEBTPACKAGE_LIST, new RequestModel(DEBTPACKAGE_LIST, Constants.HOST_IP_REQ + "/rpc/debtpackage/list.app"));
            requestMap.put(DEBTPACKAGE_COUNTDOWN, new RequestModel(DEBTPACKAGE_COUNTDOWN, Constants.HOST_IP_REQ + "/rpc/debtpackage/countdown.app"));
            requestMap.put(DEBTPACKAGE_REMINDME, new RequestModel(DEBTPACKAGE_REMINDME, Constants.HOST_IP_REQ + "/rpc/debtpackage/remindme.app"));
            requestMap.put(TRANSFER_HISTORY, new RequestModel(TRANSFER_HISTORY, Constants.HOST_IP_REQ + "/rpc/user/money/history.app"));
            requestMap.put(MY_BONUS, new RequestModel(MY_BONUS, Constants.HOST_IP_REQ + "/rpc/user/my/bonus.app"));
            requestMap.put(USER_DAYEARNINGS_INFO, new RequestModel(USER_DAYEARNINGS_INFO, Constants.HOST_IP_REQ + "/rpc/user/dayearnings/info.app"));
            requestMap.put(USER_ME, new RequestModel(USER_ME, Constants.HOST_IP_REQ + "/rpc/user/my.app"));
            requestMap.put(USER_TOTALMONEY_INFO, new RequestModel(USER_TOTALMONEY_INFO, Constants.HOST_IP_REQ + "/rpc/user/totalmoney/info.app"));
            requestMap.put(USER_DEBTPACKAGE_LIST, new RequestModel(USER_DEBTPACKAGE_LIST, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/list.app"));
            requestMap.put(USER_DEBTPACKAGE_GRAB_ORDERBY, new RequestModel(USER_DEBTPACKAGE_GRAB_ORDERBY, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/grab/orderby.app"));
            requestMap.put(USER_DEBTPACKAGE_TRANSFER, new RequestModel(USER_DEBTPACKAGE_TRANSFER, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/transfer.app"));
            requestMap.put(USER_DEBTPACKAGE_TRANSFER_CANCEL, new RequestModel(USER_DEBTPACKAGE_TRANSFER_CANCEL, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/transfer/cancel.app"));
            requestMap.put(DEBTPACKAGE_INFO, new RequestModel(DEBTPACKAGE_INFO, Constants.HOST_IP_REQ + "/rpc/debtpackage/info.app"));
            requestMap.put(USER_DEBTPACKAGE_BUYINFO, new RequestModel(USER_DEBTPACKAGE_BUYINFO, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/buyinfo.app"));
            requestMap.put(USER_DEBTPACKAGE_BUYINFO_2, new RequestModel(USER_DEBTPACKAGE_BUYINFO_2, Constants.HOST_IP_REQ + "/rpc/debtpackage/info2.app"));
            requestMap.put(TEST_USER_DEBTPACKAGE_BUY, new RequestModel(TEST_USER_DEBTPACKAGE_BUY, Constants.HOST_IP_REQ + "/rpc/debtpackage/buy.app"));
            requestMap.put(FriendList, new RequestModel(FriendList, Constants.HOST_IP_REQ + "/rpc/friend/list.app"));
            requestMap.put(FriendAgree, new RequestModel(FriendAgree, Constants.HOST_IP_REQ + "/rpc/friend/agree.app"));
            requestMap.put(AddFenqiFriend, new RequestModel(AddFenqiFriend, Constants.HOST_IP_REQ + "/rpc/friend/add/fq.app"));
            requestMap.put(AddGuguFriend, new RequestModel(AddGuguFriend, Constants.HOST_IP_REQ + "/rpc/friend/add2.app"));
            requestMap.put(TOP_IMG, new RequestModel(TOP_IMG, Constants.HOST_IP_REQ + "/rpc/topimg.app"));
            requestMap.put(LINK_ARTICLE, new RequestModel(LINK_ARTICLE, Constants.HOST_IP_REQ + "/rpc/link/article.app"));
            requestMap.put(GetFriendInfo, new RequestModel(GetFriendInfo, Constants.HOST_IP_REQ + "/rpc/friend/info.app"));
            requestMap.put(MESSAGELIST, new RequestModel(MESSAGELIST, Constants.HOST_IP_REQ + "/rpc/message/list.app"));
            requestMap.put(MESSAGEREAD, new RequestModel(MESSAGEREAD, Constants.HOST_IP_REQ + "/rpc/message/read.app"));
            requestMap.put(DEBTPACKAGE_BUY_SENDVCODE, new RequestModel(DEBTPACKAGE_BUY_SENDVCODE, Constants.HOST_IP_REQ + "/rpc/debtpackage/buy/sendvcode.app"));
            requestMap.put(DEBTPACKAGE_BUY, new RequestModel(DEBTPACKAGE_BUY, Constants.HOST_IP_REQ + "/rpc/debtpackage/buy.app"));
            requestMap.put(SECURITY_CENTER_BANK_INFO, new RequestModel(SECURITY_CENTER_BANK_INFO, Constants.HOST_IP_REQ + "/rpc/security/center/bank/info.app"));
            requestMap.put(WITHDRAWAL_APPLY, new RequestModel(WITHDRAWAL_APPLY, Constants.HOST_IP_REQ + "/rpc/withdrawal/apply.app"));
            requestMap.put(WITHDRAWAL_LIST, new RequestModel(WITHDRAWAL_LIST, Constants.HOST_IP_REQ + "/rpc/withdrawal/list.app"));
            requestMap.put(QUERY_BALANCE, new RequestModel(QUERY_BALANCE, Constants.HOST_IP_REQ + "/rpc/user/surplus.app"));
            requestMap.put(SECURITY_CENTER_BANK_BIND_SENDVCODE, new RequestModel(SECURITY_CENTER_BANK_BIND_SENDVCODE, Constants.HOST_IP_REQ + "/rpc/security/center/bank/bind/sendvcode.app"));
            requestMap.put(SECURITY_CENTER_BANK_BIND_PAY, new RequestModel(SECURITY_CENTER_BANK_BIND_PAY, Constants.HOST_IP_REQ + "/rpc/security/center/bank/bind/pay.app"));
            requestMap.put(DEBTPACKAGE_INDEX, new RequestModel(DEBTPACKAGE_INDEX, Constants.HOST_IP_REQ + "/rpc/debtpackage/index2.app"));
            requestMap.put(MESSAGE_COUNT, new RequestModel(MESSAGE_COUNT, Constants.HOST_IP_REQ + "/rpc/message/count.app"));
            requestMap.put(WELFARE_LIST, new RequestModel(WELFARE_LIST, Constants.HOST_IP_REQ + "/rpc/welfare/list.app"));
            requestMap.put(WELFARE_MSG, new RequestModel(WELFARE_MSG, Constants.HOST_IP_REQ + "/rpc/welfare/msg.app"));
            requestMap.put(WELFARE_INFO, new RequestModel(WELFARE_INFO, Constants.HOST_IP_REQ + "/rpc/welfare/info.app"));
            requestMap.put(WELFARE_TELPHONE_MONEY, new RequestModel(WELFARE_TELPHONE_MONEY, Constants.HOST_IP_REQ + "/rpc/welfare/telphone/money.app"));
            requestMap.put(FRIEND_ADD_1, new RequestModel(FRIEND_ADD_1, Constants.HOST_IP_REQ + "/rpc/friend/add1.app"));
            requestMap.put(WELFARE_DELETE_FRIEND, new RequestModel(WELFARE_DELETE_FRIEND, Constants.HOST_IP_REQ + "/rpc/welfare/delete/friend.app"));
            requestMap.put(WITHDRAWAL_INFO, new RequestModel(WITHDRAWAL_INFO, Constants.HOST_IP_REQ + "/rpc/withdrawal/info.app"));
            requestMap.put(USER_DEBTPACKAGE_RANSOM_LIST, new RequestModel(USER_DEBTPACKAGE_RANSOM_LIST, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/ransom/list.app"));
            requestMap.put(USER_DEBTPACKAGE_RANSOM_INFO, new RequestModel(USER_DEBTPACKAGE_RANSOM_INFO, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/ransom/info.app"));
            requestMap.put(USER_DEBTPACKAGE_RANSOM, new RequestModel(USER_DEBTPACKAGE_RANSOM, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/ransom.app"));
            requestMap.put(SUPPORT_BANK_LIST, new RequestModel(SUPPORT_BANK_LIST, Constants.HOST_IP_REQ + "/rpc/bank.app"));
            requestMap.put(USER_LOTTERY, new RequestModel(USER_LOTTERY, Constants.HOST_IP_REQ + "/rpc/user/lottery.app"));
            requestMap.put(USER_TURNTABLE, new RequestModel(USER_TURNTABLE, Constants.HOST_IP_REQ + "/rpc/user/turntable.app"));
            requestMap.put(USER_INVITE_PHONEBOOK_FILTER, new RequestModel(USER_INVITE_PHONEBOOK_FILTER, Constants.HOST_IP_REQ + "/rpc/user/invite/phonebook/filter.app"));
            requestMap.put(USER_INVITE_PHONEBOOK, new RequestModel(USER_INVITE_PHONEBOOK, Constants.HOST_IP_REQ + "/rpc/user/invite/phonebook.app"));
            requestMap.put(USER_INVITE_STATISTICS, new RequestModel(USER_INVITE_STATISTICS, Constants.HOST_IP_REQ + "/rpc/user/invite/statistics.app"));
            requestMap.put(USER_RECEIVE_INTEGRAL, new RequestModel(USER_RECEIVE_INTEGRAL, Constants.HOST_IP_REQ + "/rpc/user/receive/integral.app"));
            requestMap.put(USER_INVITE_INVESTED, new RequestModel(USER_INVITE_INVESTED, Constants.HOST_IP_REQ + "/rpc/user/invite/invested.app"));
            requestMap.put(USER_RECEIVE_EARNINGS, new RequestModel(USER_RECEIVE_EARNINGS, Constants.HOST_IP_REQ + "/rpc/user/receive/earnings.app"));
            requestMap.put(USER_INVITE_INDEX, new RequestModel(USER_INVITE_INDEX, Constants.HOST_IP_REQ + "/rpc/user/invite/index.app"));
            requestMap.put(WELFARE_SPREAD_REWARD, new RequestModel(WELFARE_SPREAD_REWARD, Constants.HOST_IP_REQ + "/rpc/welfare/spread/reward.app"));
            requestMap.put(WELFARE_SPREAD_REWARD_SUBMIT, new RequestModel(WELFARE_SPREAD_REWARD_SUBMIT, Constants.HOST_IP_REQ + "/rpc/welfare/spread/reward/submit.app"));
            requestMap.put(WELFARE_BONUS, new RequestModel(WELFARE_BONUS, Constants.HOST_IP_REQ + "/rpc/welfare/bonus.app"));
            requestMap.put(WELFARE_RECEIVE_BONUS, new RequestModel(WELFARE_RECEIVE_BONUS, Constants.HOST_IP_REQ + "/rpc/welfare/receive/bonus.app"));
            requestMap.put(WELFARE_INDEX, new RequestModel(WELFARE_INDEX, Constants.HOST_IP_REQ + "/rpc/welfare/index.app"));
            requestMap.put(WELFARE_MONTH_ADDRATE, new RequestModel(WELFARE_MONTH_ADDRATE, Constants.HOST_IP_REQ + "/rpc/welfare/month/addrate.app"));
            requestMap.put(WELFARE_MONTH_ADDRATE_USED, new RequestModel(WELFARE_MONTH_ADDRATE_USED, Constants.HOST_IP_REQ + "/rpc/welfare/month/addrate/used.app"));
            requestMap.put(WELFARE_SEND_BONUS, new RequestModel(WELFARE_SEND_BONUS, Constants.HOST_IP_REQ + "/rpc/welfare/month/addrate/used.app"));
            requestMap.put(WELFARE_SEND_BONUS, new RequestModel(WELFARE_SEND_BONUS, Constants.HOST_IP_REQ + "/rpc/welfare/send/bonus.app"));
            requestMap.put(USER_SURPLUS_MONEY, new RequestModel(USER_SURPLUS_MONEY, Constants.HOST_IP_REQ + "/rpc/user/surplus/money.app"));
            requestMap.put(HINT_BUY_DEBT, new RequestModel(HINT_BUY_DEBT, Constants.HOST_IP_REQ + "/rpc/hint/buy/debt.app"));
            requestMap.put(WITHDRAWAL_SURPLUS, new RequestModel(WITHDRAWAL_SURPLUS, Constants.HOST_IP_REQ + "/rpc/withdrawal/surplus.app"));
            requestMap.put(USER_REGIST_GIVE_MONEY, new RequestModel(USER_REGIST_GIVE_MONEY, Constants.HOST_IP_REQ + "/rpc/user/regist/give/money.app"));
            requestMap.put(MESSAGE_SHARE, new RequestModel(MESSAGE_SHARE, Constants.HOST_IP_REQ + "/rpc/message/share.app"));
            requestMap.put(BANK_AREA, new RequestModel(BANK_AREA, Constants.HOST_IP_REQ + "/rpc/bank/area.app"));
            requestMap.put(SECURITY_CENTER_BANK_UPATE_ADDRESS, new RequestModel(SECURITY_CENTER_BANK_UPATE_ADDRESS, Constants.HOST_IP_REQ + "/rpc/security/center/bank/update/address.app"));
            requestMap.put(USER_DEBTPACKAGE_WAIT_GRAB_ORDERBY, new RequestModel(USER_DEBTPACKAGE_WAIT_GRAB_ORDERBY, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/wait/grab/orderby.app"));
            requestMap.put(MESSAGE_LIST_2, new RequestModel(MESSAGE_LIST_2, Constants.HOST_IP_REQ + "/rpc/message/list2.app"));
            requestMap.put(USER_INVITE_REGIST_HINT, new RequestModel(USER_INVITE_REGIST_HINT, Constants.HOST_IP_REQ + "/rpc/user/invite/regist/hint.app"));
            requestMap.put(SECURITY_CENTER_BANK_UNBIND, new RequestModel(SECURITY_CENTER_BANK_UNBIND, Constants.HOST_IP_REQ + "/rpc/security/center/bank/unbind.app"));
            requestMap.put(HQ_INFO, new RequestModel(HQ_INFO, Constants.HOST_IP_REQ + "/rpc/hq/info.app"));
            requestMap.put(USER_HQ_INFO, new RequestModel(USER_HQ_INFO, Constants.HOST_IP_REQ + "/rpc/user/hq/info.app"));
            requestMap.put(BANKS, new RequestModel(BANKS, Constants.HOST_IP_REQ + "/rpc/banks.app"));
            requestMap.put(USER_HQ_MONEY, new RequestModel(USER_HQ_MONEY, Constants.HOST_IP_REQ + "/rpc/user/hq/money.app"));
            requestMap.put(USER_DEBTPACKAGE_HQ_RANSOM, new RequestModel(USER_DEBTPACKAGE_HQ_RANSOM, Constants.HOST_IP_REQ + "/rpc/user/debtpackage/hq/ransom.app"));
            requestMap.put(DEBTPACKAGE_SOURCE_LIST, new RequestModel(DEBTPACKAGE_SOURCE_LIST, Constants.HOST_IP_REQ + "/rpc/debtpackage/source/list.app"));

            requestMap.put(DEBTPACKAGE_ORDER_INFO, new RequestModel(DEBTPACKAGE_ORDER_INFO, Constants.HOST_IP_REQ + "/rpc/debtpackage/order/info.app"));
            requestMap.put(STARTUP_IMAGE, new RequestModel(STARTUP_IMAGE, Constants.HOST_IP_REQ + "/rpc/startup/image.app"));

            requestMap.put(DISCOVERY_TOPIMG, new RequestModel(DISCOVERY_TOPIMG, Constants.HOST_IP_REQ + "/rpc/discovery/topimg.app"));
            requestMap.put(DISCOVERY_LIST, new RequestModel(DISCOVERY_LIST, Constants.HOST_IP_REQ + "/rpc/discovery/list.app"));
            requestMap.put(DISCOVERY_MEDIA_REPORT, new RequestModel(DISCOVERY_MEDIA_REPORT, Constants.HOST_IP_REQ + "/rpc/discovery/media/report.app"));
            requestMap.put(DISCOVERY_PROPERTY_INFO, new RequestModel(DISCOVERY_PROPERTY_INFO, Constants.HOST_IP_REQ + "/rpc/discovery/property/info.app"));
            requestMap.put(DISCOVERY_PROPERTY_DEDUCTION, new RequestModel(DISCOVERY_PROPERTY_DEDUCTION, Constants.HOST_IP_REQ + "/rpc/discovery/property/deduction.app"));
            requestMap.put(DISCOVERY_WAGE_INFO, new RequestModel(DISCOVERY_WAGE_INFO, Constants.HOST_IP_REQ + "/rpc/discovery/wage/info.app"));
            requestMap.put(DISCOVERY_WAGE_LIST, new RequestModel(DISCOVERY_WAGE_LIST, Constants.HOST_IP_REQ + "/rpc/discovery/wage/list.app"));
            requestMap.put(DISCOVERY_PROPERTY_FEES, new RequestModel(DISCOVERY_PROPERTY_FEES, Constants.HOST_IP_REQ + "/rpc/discovery/property/fees.app"));
            requestMap.put(DISCOVERY_PROPERTY_FEES_REFUND, new RequestModel(DISCOVERY_PROPERTY_FEES_REFUND, Constants.HOST_IP_REQ + "/rpc/discovery/property/fees/refund.app"));
            requestMap.put(USER_EARNINGS_LIST, new RequestModel(USER_EARNINGS_LIST, Constants.HOST_IP_REQ + "/rpc/user/earnings/list.app"));

            // 注意保留 rpc
        }

        return requestMap.get(id);
    }

    public static final String USER_REGISTER_CHECK = "USER_REGISTER_CHECK"; // 验证手机号是否已经注册
    public static final String USER_LOGIN = "USER_LOGIN"; // 登录
    public static final String USER_VALID_LOGIN_PWD = "USER_VALID_LOGIN_PWD"; // 验证登录密码
    public static final String USER_NOLOGIN_PASSWORD_UPDATE = "USER_NOLOGIN_PASSWORD_UPDATE"; // 找回密码
    public static final String USER_REGIST_SEND_SMS = "USER_REGIST_SEND_SMS"; // 发送短信验证码
    public static final String USER_REGIST_SET_PWD = "USER_REGIST_SET_PWD"; // 注册
    public static final String USER_VERIFY_TOKEN = "USER_VERIFY_TOKEN"; // 验证TOKEN
    public static final String USER_UPDATE_LOGIN_PWD = "USER_UPDATE_LOGIN_PWD"; // 修改登录密码
    public static final String USER_LOGOUT = "USER_LOGOUT"; // 用户退出
    public static final String USER_SET_LOGO = "USER_SET_LOGO"; // 设置用户头像
    public static final String SECURITY_CENTER_INFO = "SECURITY_CENTER_INFO"; // 安全中⼼心各项状态
    public static final String SECURITY_CENTER_ITEM_INFO = "SECURITY_CENTER_ITEM_INFO"; // 安全中⼼心各项数据
    public static final String SECURITY_CENTER_ITEM_INFO_REALNAME = "SECURITY_CENTER_ITEM_INFO_REALNAME"; // 实名认证
    public static final String SECURITY_CENTER_ITEM_SAVE = "SECURITY_CENTER_ITEM_SAVE"; // 微信 邮箱认证
    public static final String SECURITY_CENTER_EMERGENCY_CONTACT_SAVE = "SECURITY_CENTER_EMERGENCY_CONTACT_SAVE"; // 紧急联系人
    public static final String SECURITY_CENTER_TRANSACTION_PWD_SAVE = "SECURITY_CENTER_TRANSACTION_PWD_SAVE"; // 设置交易密码
    public static final String SEND_VCODE = "SEND_VCODE"; // 当前登录用户发送短信验证码
    public static final String NOLOGIN_SEND_VCODE = "NOLOGIN_SEND_VCODE"; // 指定手机号发送短信验证码
    public static final String DEBTPACKAGE_LIST = "DEBTPACKAGE_LIST"; // 债权包列表
    public static final String DEBTPACKAGE_COUNTDOWN = "DEBTPACKAGE_COUNTDOWN"; // 倒计时
    public static final String DEBTPACKAGE_REMINDME = "DEBTPACKAGE_REMINDME"; // 添加提醒
    public static final String TRANSFER_HISTORY = "TRANSFER_HISTORY"; // 交易记录
    public static final String MY_BONUS = "MY_BONUS"; // 我的红包
    public static final String USER_DAYEARNINGS_INFO = "USER_DAYEARNINGS_INFO"; // 昨日收益
    public static final String USER_ME = "USER_ME"; // 我的钱包
    public static final String USER_TOTALMONEY_INFO = "USER_TOTALMONEY_INFO"; // 总资产
    public static final String USER_DEBTPACKAGE_LIST = "USER_DEBTPACKAGE_LIST"; // 我的资产
    public static final String USER_DEBTPACKAGE_GRAB_ORDERBY = "USER_DEBTPACKAGE_GRAB_ORDERBY"; // 抢排名
    public static final String USER_DEBTPACKAGE_TRANSFER = "USER_DEBTPACKAGE_TRANSFER"; // 转让债权
    public static final String USER_DEBTPACKAGE_TRANSFER_CANCEL = "USER_DEBTPACKAGE_TRANSFER_CANCEL"; // 取消转让债权
    public static final String DEBTPACKAGE_INFO = "DEBTPACKAGE_INFO"; // 债权包详情
    public static final String USER_DEBTPACKAGE_BUYINFO = "USER_DEBTPACKAGE_BUYINFO"; // 债权包购买记录
    public static final String USER_DEBTPACKAGE_BUYINFO_2 = "USER_DEBTPACKAGE_BUYINFO_2"; // 查询余额
    public static final String TEST_USER_DEBTPACKAGE_BUY = "TEST_USER_DEBTPACKAGE_BUY"; // 购买测试
    public static final String FriendList = "FriendList"; // 好友列表
    public static final String FriendAgree = "FriendAgree"; // 同意添加好友
    public static final String AddFenqiFriend = "AddFenqiFriend"; // 添加分期好友
    public static final String AddGuguFriend = "AddGuguFriend"; // 添加鼓鼓好友
    public static final String TOP_IMG = "TOP_IMG"; // 首页头部图片
    public static final String LINK_ARTICLE = "LINK_ARTICLE"; // 媒体报道
    public static final String GetFriendInfo = "GetFriendInfo"; // 获得好友信息
    public static final String MESSAGELIST = "MESSAGELIST"; // 消息列表
    public static final String MESSAGEREAD = "MESSAGEREAD"; // 消息已读
    public static final String DEBTPACKAGE_BUY_SENDVCODE = "DEBTPACKAGE_BUY_SENDVCODE"; // 购买发送验证码
    public static final String DEBTPACKAGE_BUY = "DEBTPACKAGE_BUY"; // 购买银⾏行卡⽀支付
    public static final String SECURITY_CENTER_BANK_INFO = "SECURITY_CENTER_BANK_INFO"; // 绑定的银⾏行卡信息
    public static final String WITHDRAWAL_APPLY = "WITHDRAWAL_APPLY"; // 提现申请
    public static final String WITHDRAWAL_LIST = "WITHDRAWAL_LIST"; // 提现列表
    public static final String QUERY_BALANCE = "QUERY_BALANCE"; // 查询余额
    public static final String SECURITY_CENTER_BANK_BIND_SENDVCODE = "SECURITY_CENTER_BANK_BIND_SENDVCODE"; // 绑定银⾏卡发送验证码
    public static final String SECURITY_CENTER_BANK_BIND_PAY = "SECURITY_CENTER_BANK_BIND_PAY"; // 绑定银⾏卡⽀支付
    public static final String DEBTPACKAGE_INDEX = "DEBTPACKAGE_INDEX"; // 首页数据
    public static final String MESSAGE_COUNT = "MESSAGE_COUNT"; // 统计⽤用户消息数量信息
    public static final String WELFARE_LIST = "WELFARE_LIST";// 用户福利列表
    public static final String WELFARE_MSG = "WELFARE_MSG";// 用户福利消息
    public static final String WELFARE_INFO = "WELFARE_INFO";// 月加息详细、送话费详细
    public static final String WELFARE_TELPHONE_MONEY = "WELFARE_TELPHONE_MONEY";// 申请话费
    public static final String FRIEND_ADD_1 = "FRIEND_ADD_1";// 月加息添加好友
    public static final String WELFARE_DELETE_FRIEND = "WELFARE_DELETE_FRIEND";// 月加息删除好友
    public static final String WITHDRAWAL_INFO = "WITHDRAWAL_INFO";// 提现详细⻚页⾯面
    public static final String USER_DEBTPACKAGE_RANSOM_LIST = "USER_DEBTPACKAGE_RANSOM_LIST";// 定投可赎回或者抢投可转让列表
    public static final String USER_DEBTPACKAGE_RANSOM_INFO = "USER_DEBTPACKAGE_RANSOM_INFO";// 定投可赎回或者抢投可转让详细
    public static final String USER_DEBTPACKAGE_RANSOM = "USER_DEBTPACKAGE_RANSOM";// 定投赎回
    public static final String SUPPORT_BANK_LIST = "SUPPORT_BANK_LIST"; // 取得支持的银行列表
    public static final String USER_LOTTERY = "USER_LOTTERY"; // 抽奖结果
    public static final String USER_TURNTABLE = "USER_TURNTABLE"; // 转盘页面数据
    public static final String USER_INVITE_PHONEBOOK_FILTER = "USER_INVITE_PHONEBOOK_FILTER"; // 通讯录筛选
    public static final String USER_INVITE_PHONEBOOK = "USER_INVITE_PHONEBOOK"; // 邀请通讯录⽤用户
    public static final String USER_INVITE_STATISTICS = "USER_INVITE_STATISTICS"; // 邀请统计
    public static final String USER_RECEIVE_INTEGRAL = "USER_RECEIVE_INTEGRAL"; // 点击领取积分
    public static final String USER_INVITE_INVESTED = "USER_INVITE_INVESTED"; // 好友投资统计
    public static final String USER_RECEIVE_EARNINGS = "USER_RECEIVE_EARNINGS"; // 领取好友投资收益
    public static final String USER_INVITE_INDEX = "USER_INVITE_INDEX"; // 好友获利
    public static final String WELFARE_SPREAD_REWARD = "WELFARE_SPREAD_REWARD"; // 朋友圈总资产奖励
    public static final String WELFARE_SPREAD_REWARD_SUBMIT = "WELFARE_SPREAD_REWARD_SUBMIT"; // 领取好友总资产奖励
    public static final String WELFARE_INDEX = "WELFARE_INDEX"; // 我的奖励
    public static final String WELFARE_MONTH_ADDRATE = "WELFARE_MONTH_ADDRATE"; // 月加息
    public static final String WELFARE_MONTH_ADDRATE_USED = "WELFARE_MONTH_ADDRATE_USED"; // 月加息使用
    public static final String WELFARE_BONUS = "WELFARE_BONUS"; // 红包列表
    public static final String WELFARE_RECEIVE_BONUS = "WELFARE_RECEIVE_BONUS"; // 领取红包
    public static final String WELFARE_SEND_BONUS = "WELFARE_SEND_BONUS"; // 发红包
    public static final String USER_SURPLUS_MONEY = "USER_SURPLUS_MONEY"; // 查询⽤用户余额
    public static final String HINT_BUY_DEBT = "HINT_BUY_DEBT"; // 投资提醒
    public static final String WITHDRAWAL_SURPLUS = "WITHDRAWAL_SURPLUS"; // 查询余额
    public static final String USER_REGIST_GIVE_MONEY = "USER_REGIST_GIVE_MONEY"; // 特权金查询
    public static final String MESSAGE_SHARE = "MESSAGE_SHARE"; // 分享系统消息
    public static final String BANK_AREA = "BANK_AREA"; // 银行城市名称
    public static final String SECURITY_CENTER_BANK_UPATE_ADDRESS = "SECURITY_CENTER_BANK_UPATE_ADDRESS"; // 设置开户⾏
    public static final String USER_DEBTPACKAGE_WAIT_GRAB_ORDERBY = "USER_DEBTPACKAGE_WAIT_GRAB_ORDERBY"; // 投投页面抢投
    public static final String MESSAGE_LIST_2 = "MESSAGE_LIST_2"; // 消息列表
    public static final String USER_INVITE_REGIST_HINT = "USER_INVITE_REGIST_HINT"; // 提醒注册
    public static final String SECURITY_CENTER_BANK_UNBIND = "SECURITY_CENTER_BANK_UNBIND"; // 解绑银行卡
    public static final String HQ_INFO = "HQ_INFO"; // 活期⾸⻚详情
    public static final String USER_HQ_INFO = "USER_HQ_INFO"; // 我的活期理财详情
    public static final String BANKS = "BANKS"; // 银⾏卡列表
    public static final String USER_HQ_MONEY = "USER_HQ_MONEY"; // 活期投资⾦金额
    public static final String USER_DEBTPACKAGE_HQ_RANSOM = "USER_DEBTPACKAGE_HQ_RANSOM"; // 活期转出
    public static final String DEBTPACKAGE_SOURCE_LIST = "DEBTPACKAGE_SOURCE_LIST"; // 投资去向

    public static final String DEBTPACKAGE_ORDER_INFO = "DEBTPACKAGE_ORDER_INFO"; // 获取债权资料详情
    public static final String STARTUP_IMAGE = "STARTUP_IMAGE"; // 获取启动图片

    public static final String DISCOVERY_TOPIMG = "DISCOVERY_TOPIMG"; // 发现顶部图片
    public static final String DISCOVERY_LIST = "DISCOVERY_LIST"; // 发现列表
    public static final String DISCOVERY_MEDIA_REPORT = "DISCOVERY_MEDIA_REPORT"; // 媒体报道
    public static final String DISCOVERY_PROPERTY_INFO = "DISCOVERY_PROPERTY_INFO"; // 物业宝详情
    public static final String DISCOVERY_PROPERTY_DEDUCTION = "DISCOVERY_PROPERTY_DEDUCTION"; // 物业代扣记录
    public static final String DISCOVERY_WAGE_INFO = "DISCOVERY_WAGE_INFO"; // 薪资宝详情
    public static final String DISCOVERY_WAGE_LIST = "DISCOVERY_WAGE_LIST"; // 代发工资记录
    public static final String DISCOVERY_PROPERTY_FEES = "DISCOVERY_PROPERTY_FEES"; // 申请返还保障金详情
    public static final String DISCOVERY_PROPERTY_FEES_REFUND = "DISCOVERY_PROPERTY_FEES_REFUND"; // 申请返还保障金
    public static final String USER_EARNINGS_LIST = "USER_EARNINGS_LIST"; // 收益记录


    // TEMP
    public static final String ME = "ME";
    public static final String GetLocation = "GetLocation";

}
