package com.gugu.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ecloud.pulltozoomview.PullToZoomBase;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.wufriends.gugu.R;
import com.gugu.activity.view.RewardTipLayout;
import com.gugu.client.ActivityManager;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

import de.hdodenhof.circleimageview.CircleImageView;

public class BagActivity extends BaseActivity implements OnClickListener {

    public static String ACTION_BAG_RED_CIRCLE = "com.gugu.bag.red_circle";

    private PullToZoomScrollViewEx scrollView;

    private RedCircleReceiver redCircleReceiver = null;

    private TextView settingTextView = null; // 设置
    private LinearLayout meLayout = null; // 个人中心
    private TextView telphoneTextView;
    private TextView totalAmountTextView = null; // 总资产
    private TextView messageTextView = null; // 投资本金和待收利息
    private TextView queryTextView = null; // 查看
    private TextView availableAmountTextView = null; // 账户余额

    private TextView totalEarningsTitleTextView = null; // 累计收益 和 特权金切换。特权金时间为0后才显示累计收益
    private TextView totalEarningsTextView = null; // 累计收益
    private TextView giveMessageTextView = null; // 特权金滚动信息

    private CircleImageView headImageView = null; // 用户头像

    private LinearLayout totalEarningsLayout; // 累计收益
    private TextView receiveEarningsTextView = null; // 好友获利金额

    private LinearLayout bagLayout01 = null; // 我的投资
    private LinearLayout bagLayout02 = null; // 交易记录
    private LinearLayout bagLayout03 = null; // 回款明细
    private LinearLayout bagLayout04 = null; // 我的奖励
    private LinearLayout bagLayoutFriends = null; // 我的好友
    private LinearLayout bagInviteFriendLayout = null; // 我的好友
    private LinearLayout bagLayout05 = null; // 我要提现
    private LinearLayout bagLayout06 = null; // 提现记录

    private RewardTipLayout rewardTipLayout = null; // 我的奖励的福利部分

    private ImageView redCircleInvestmentImageView = null; // 我的投资提醒
    private ImageView redCircleFriendImageView = null; // 我的好友提醒

    private long exitTimeMillis = 0;

    private MyAppDto infoDto;

    private ArrayList<String> messageList = new ArrayList<String>();
    private ArrayList<String> giveMessageList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bag);

        loadViewForCode();

        this.registerRedCircleReceiver();
    }

    public void onResume() {
        super.onResume();

        this.requestMe(null);

        if (redCircleInvestmentImageView != null) {
            redCircleInvestmentImageView.setVisibility(ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false) ? View.VISIBLE : View.INVISIBLE);
        }

        if (redCircleFriendImageView != null) {
            redCircleFriendImageView.setVisibility(ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_FRIEND, false) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(redCircleReceiver);
    }

    private void loadViewForCode() {
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        // 背景上的内容，头像，姓名等
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_bag_head, null, false);
        scrollView.setHeaderView(headView);

        // 背景图片
        View zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        scrollView.setZoomView(zoomView);

        // 下面的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_bag_content, null, false);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        initView();
    }

    private void initView() {
        settingTextView = (TextView) scrollView.getHeaderView().findViewById(R.id.settingTextView);
        settingTextView.setOnClickListener(this);

        meLayout = (LinearLayout) scrollView.getHeaderView().findViewById(R.id.meLayout);
        meLayout.setOnClickListener(this);

        telphoneTextView = (TextView) scrollView.getHeaderView().findViewById(R.id.telphoneTextView);

        totalAmountTextView = (TextView) scrollView.getHeaderView().findViewById(R.id.totalAmountTextView);
        totalAmountTextView.setText("0.00");

        messageTextView = (TextView) scrollView.getHeaderView().findViewById(R.id.messageTextView);

        queryTextView = (TextView) scrollView.getHeaderView().findViewById(R.id.queryTextView);
        queryTextView.setOnClickListener(this);


        availableAmountTextView = (TextView) scrollView.getPullRootView().findViewById(R.id.availableAmountTextView);
        availableAmountTextView.setText("0.00");

        totalEarningsLayout = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.totalEarningsLayout);
        totalEarningsLayout.setOnClickListener(this);

        totalEarningsTitleTextView = (TextView) scrollView.getPullRootView().findViewById(R.id.totalEarningsTitleTextView);

        totalEarningsTextView = (TextView) scrollView.getPullRootView().findViewById(R.id.totalEarningsTextView);
        totalEarningsTextView.setText("0.00");

        giveMessageTextView = (TextView) scrollView.getPullRootView().findViewById(R.id.giveMessageTextView);

        headImageView = (CircleImageView) scrollView.getPullRootView().findViewById(R.id.headImageView);

        receiveEarningsTextView = (TextView) scrollView.getPullRootView().findViewById(R.id.receiveEarningsTextView);

        bagLayout01 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout01);
        bagLayout01.setOnClickListener(this);
        bagLayout02 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout02);
        bagLayout02.setOnClickListener(this);
        bagLayout03 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout03);
        bagLayout03.setOnClickListener(this);
        bagLayout04 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout04);
        bagLayout04.setOnClickListener(this);
        bagLayout05 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout05);
        bagLayout05.setOnClickListener(this);
        bagLayout06 = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout06);
        bagLayout06.setOnClickListener(this);
        bagLayoutFriends = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.bagLayout_friends);
        bagLayoutFriends.setOnClickListener(this);
        bagInviteFriendLayout = (LinearLayout) scrollView.getPullRootView().findViewById(R.id.inviteFriendLayout);
        bagInviteFriendLayout.setOnClickListener(this);
        rewardTipLayout = (RewardTipLayout) scrollView.getPullRootView().findViewById(R.id.rewardTipLayout);
        redCircleInvestmentImageView = (ImageView) scrollView.getPullRootView().findViewById(R.id.redCircleInvestmentImageView);
        redCircleFriendImageView = (ImageView) scrollView.getPullRootView().findViewById(R.id.redCircleFriendImageView);

        scrollView.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
            @Override
            public void onPullZooming(int newScrollValue) {
            }

            @Override
            public void onPullZoomEnd() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingTextView: {// 设置
                Intent intent = new Intent(this, SystemSettingActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.queryTextView: {
                Intent intent = new Intent(this, TotalAssetsActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.meLayout: {// 个人中心
                Intent intent = new Intent(this, PersonalCenterActivity.class);
                if (infoDto == null || infoDto.getLogoUrl() == null) {
                    intent.putExtra("logo", "");
                } else {
                    intent.putExtra("logo", infoDto.getLogoUrl());
                }
                this.startActivityForResult(intent, 0);
            }
            break;

            case R.id.totalEarningsLayout: {
                Intent intent = new Intent(this, EarningsActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.bagLayout01: {// 我的投资
                ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false).commit();

                Intent intent = new Intent(this, InvestmentStatisticsActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.bagLayout02: {// 交易记录
                Intent intent = new Intent(this, TransferHistoryActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.bagLayout03: // 回款明细

                break;

            case R.id.bagLayout04: {// 我的奖励
                Intent intent = new Intent(this, MyRewardListExActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.bagLayout05: {// 我要提现
                Intent intent = new Intent(this, WithdrawalListActivity.class);
                this.startActivityForResult(intent, 0);
            }
            break;

            case R.id.bagLayout06: { // 提现记录
                Intent intent = new Intent(this, WithdrawalRecordsActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.bagLayout_friends: { // 我的好友
                ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.RED_CIRCLE_TIP_FRIEND, false).commit();

                Intent intent = new Intent(this, MyFriendsActivity.class);
                this.startActivity(intent);
            }

            break;

            case R.id.inviteFriendLayout: { // 邀请好友
                Intent intent = new Intent(this, InviteContactActivity.class);
                this.startActivity(intent);
            }
            break;
        }

    }

    private void requestMe(String msg) {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_ME, null, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MyAppDto.class);
                    AppMessageDto<MyAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        response();

                    } else {
                        Toast.makeText(BagActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new ResponseErrorListener(this));

        this.addToRequestQueue(request, msg);
    }

    private void response() {
        if (null == infoDto.getLogoUrl() || TextUtils.isEmpty(infoDto.getLogoUrl()) || TextUtils.equals("null", infoDto.getLogoUrl())) {

        } else {
            headImageView.setImageURL(Constants.HOST_IP + infoDto.getLogoUrl() + "?random=" + ActivityUtil.getSharedPreferences().getString(Constants.HEAD_RANDOM, "0"));
        }

        telphoneTextView.setText(infoDto.getEncryptTelphone());

        totalAmountTextView.setText(infoDto.getTotalMoney());
        availableAmountTextView.setText(infoDto.getSurplusMoney());

        if (infoDto.getRegistGiveMoneyDay() == 0) {
            totalEarningsTitleTextView.setText("累计收益");
            totalEarningsTextView.setText(infoDto.getTotalEarnings());
            totalEarningsTextView.setTextSize(30);
            totalEarningsTextView.setTextColor(Color.parseColor("#333333"));

            giveMessageTextView.setVisibility(View.GONE);

        } else {
            totalEarningsTitleTextView.setText("特权金");
            totalEarningsTextView.setText(infoDto.getRegistGiveMoney());
            totalEarningsTextView.setTextSize(25);
            totalEarningsTextView.setTextColor(Color.parseColor("#FF001A"));

            giveMessageTextView.setVisibility(View.VISIBLE);
        }

        rewardTipLayout.setData(infoDto.getPoint());

        receiveEarningsTextView.setText(infoDto.getReceiveEarnings() + " 元");

        giveMessageList.clear();
        giveMessageList.add("投资收益全归您");
        giveMessageList.add("有效期剩" + infoDto.getRegistGiveMoneyDay() + "天");

        messageList.clear();
        messageList.add("投资本金：" + infoDto.getWaitPrincipal() + " 元");
        messageList.add("待收利息：" + infoDto.getWaitEarnings() + " 元");

		/*
        // 当有投资本金的时候才有滚动效果
		if (!infoDto.getWaitPrincipal().equals("0.00")) {
			timer.schedule(task, 0, 3500);
		}
		*/

        timer.schedule(task, 0, 3500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            // 因为返回时有可能导致与未注意相冲突，故采用下拉刷新的方式。
            // this.requestMe(null);
        }
    }

    // 显示红点
    private void registerRedCircleReceiver() {
        redCircleReceiver = new RedCircleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BAG_RED_CIRCLE);
        this.registerReceiver(redCircleReceiver, filter);
    }

    public class RedCircleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (redCircleInvestmentImageView != null) {
                redCircleInvestmentImageView.setVisibility(ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false) ? View.VISIBLE : View.INVISIBLE);
            }

            if (redCircleFriendImageView != null) {
                redCircleFriendImageView.setVisibility(ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_FRIEND, false) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    // ////////////////////

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    int i = 0;
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    try {
                        if (messageList != null && messageList.size() > 0) {
                            messageTextView.setText(messageList.get(i % messageList.size()));
                        }

                        if (giveMessageList != null && giveMessageList.size() > 0) {
                            giveMessageTextView.setText(giveMessageList.get(i % giveMessageList.size()));
                        }

                        in();

                        out();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    i++;
                }

                break;
            }
            super.handleMessage(msg);
        }
    };

    private void in() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                YoYo.with(Techniques.SlideInUp).duration(500).playOn(messageTextView);

                YoYo.with(Techniques.SlideInUp).duration(500).playOn(giveMessageTextView);
            }
        }, 0);
    }

    private void out() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.SlideOutUp).duration(500).playOn(messageTextView);

                YoYo.with(Techniques.SlideOutUp).duration(500).playOn(giveMessageTextView);

            }
        }, 3200);
    }

    public void onDestory() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // /////////////////

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this); // 用来保存统计数据

            for (Activity act : ActivityManager.getInstance().getAllActivity()) {
                act.finish();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
