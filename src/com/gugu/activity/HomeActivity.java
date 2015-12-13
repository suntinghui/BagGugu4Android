package com.gugu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import cn.trinea.android.view.autoscrollviewpager.ImagePagerAdapter;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.HQInfoAppDto;
import com.ares.baggugu.dto.app.ImageAppDto;
import com.ares.baggugu.dto.app.IndexAppDto;
import com.ares.baggugu.dto.app.LinkArticle;
import com.ares.baggugu.dto.app.MessageCountAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.GuguApplication;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.activity.view.MediaImagePagerAdapter;
import com.gugu.activity.view.RewardGiveMoneyDialog;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.utils.DateUtil;
import com.gugu.utils.StringUtil;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.wufriends.gugu.R;
import com.gugu.activity.view.HomeItemLayout;
import com.gugu.activity.view.RotationAnnouncementLayout;
import com.gugu.client.ActivityManager;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.UMengShareClient;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.AdapterUtil;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends BaseActivity implements OnClickListener {

    private LinearLayout messageLayout;
    private TextView messageTextView;
    private ImageView messageCloseImageView;
    private boolean hasCloseMessage = false;

    private AutoScrollViewPager viewPager = null;
    private ImagePagerAdapter viewPagerAdapter = null;
    private List<ImageAppDto> imageURLList = new ArrayList<ImageAppDto>();

    private LinearLayout indicatorLayout;
    private ImageView[] indicatorImageViews = null;

    private HomeItemLayout currentLayout = null;
    private HomeItemLayout scheduledLayout = null;

    private CustomNetworkImageView securityImageView = null;
    private CustomNetworkImageView guideImageView = null;

    private IndexAppDto appDto = null;

    private long exitTimeMillis = 0;

    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initView();

        showGiveMoneyTip();

        UMengShareClient.setAPPID(this);
    }

    public void onResume() {
        super.onResume();

        if (null != viewPager) {
            viewPager.startAutoScroll();
        }

        requestMessageCount();
        requestDebtPackageIndex();
    }

    public void onPause() {
        super.onPause();

        if (null != viewPager) {
            viewPager.stopAutoScroll();
        }
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("鼓鼓理财");

        Button helpBtn = (Button) this.findViewById(R.id.help_btn);
        helpBtn.setBackgroundResource(R.drawable.btn_help);
        helpBtn.setOnClickListener(this);

        Button msgButton = (Button) this.findViewById(R.id.btn_msg);
        msgButton.setOnClickListener(this);

        messageLayout = (LinearLayout) this.findViewById(R.id.messageLayout);
        messageLayout.setVisibility(View.GONE);
        messageTextView = (TextView) this.findViewById(R.id.messageTextView);
        messageCloseImageView = (ImageView) this.findViewById(R.id.messageCloseImageView);
        messageCloseImageView.setOnClickListener(this);

        // 活期 定期
        LinearLayout contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, AdapterUtil.dip2px(this, 10), 0, 0);

        contentLayout.addView(initCurrentItem(), params);
        contentLayout.addView(initScheduledItem(), params);

        securityImageView = (CustomNetworkImageView) this.findViewById(R.id.securityImageView);
        securityImageView.setOnClickListener(this);

        guideImageView = (CustomNetworkImageView) this.findViewById(R.id.guideImageView);
        guideImageView.setOnClickListener(this);

//        initSwipeRefresh();
    }

    @SuppressLint("ResourceAsColor")
    private void initSwipeRefresh() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColor(R.color.redme, R.color.blueme, R.color.orangeme, R.color.greenme);
        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
        mSwipeLayout.setLoadNoFull(true);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMessageCount();
                requestDebtPackageIndex();
            }
        });
    }

    private void addAnnouncement(List<Map<String, String>> list) {
        RotationAnnouncementLayout annLayout = (RotationAnnouncementLayout) this.findViewById(R.id.annLayout);
        if (list.size() == 0) {
            annLayout.setVisibility(View.GONE);
        } else {
            annLayout.setVisibility(View.VISIBLE);
            annLayout.setData(list);
        }
    }

    private void initViewPager() {
        // indicator
        indicatorLayout = (LinearLayout) this.findViewById(R.id.indicatorLayout);
        indicatorLayout.removeAllViews();

        indicatorImageViews = new ImageView[imageURLList.size()];
        for (int i = 0; i < imageURLList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(10, 10));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            indicatorImageViews[i] = imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            indicatorLayout.addView(indicatorImageViews[i], layoutParams);
        }

        // http://www.trinea.cn/android/auto-scroll-view-pager/
        // ViewPager
        viewPager = (AutoScrollViewPager) this.findViewById(R.id.viewPager);
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setAutoScrollDurationFactor(7.0);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        viewPager.setStopScrollWhenTouch(true);
        viewPagerAdapter = new ImagePagerAdapter(this, imageURLList);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                int position = index % imageURLList.size();
                for (int i = 0; i < imageURLList.size(); i++) {
                    if (i == position) {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                    } else {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    }

                }
            }

        });
        // viewPagerAdapter.setInfiniteLoop(true);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.startAutoScroll();

        final GestureDetector tapGestureDetector = new GestureDetector(this, new TapGestureListener());
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }


    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            try {
                ImageAppDto imageDto = imageURLList.get(viewPager.getCurrentItem());

                if (!StringUtils.isBlank(imageDto.getLinkUrl())) {

                    Intent intent = new Intent(HomeActivity.this, ShowWebViewActivity.class);
                    intent.putExtra("title", imageDto.getName());
                    intent.putExtra("url", imageDto.getLinkUrl());
                    HomeActivity.this.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.onSingleTapConfirmed(event);
        }
    }

    private HomeItemLayout initCurrentItem() {
        currentLayout = new HomeItemLayout(this);
        currentLayout.setLeftImageView(R.drawable.home_item_01);
        currentLayout.setLineColor(Color.parseColor("#1caff6"));

        currentLayout.setTitle("鼓鼓活期", true);
        currentLayout.getTipTextView().setText("超银行活期36倍");
        currentLayout.getTipTextView().setBackgroundResource(R.drawable.rounded_blue_corner);
        currentLayout.getSbTextView().setText("参与：");
        currentLayout.getUserCountTextView().setText("0");
        currentLayout.getCompleteTextView().setText("完成：");
        currentLayout.getSurplusMoneyTextView().setText("0.00");
        currentLayout.getAddTextView().setVisibility(View.GONE);
        currentLayout.getCircleLayout().setBackgroundResource(R.drawable.home_circle_02);

        currentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_HQ);

                /*
                // 切到投资理财
                Intent intent1 = new Intent(MainActivity.ACTION_CHECK_TABHOST);
                intent1.putExtra("INDEX", 1);
                sendBroadcast(intent1);

                // 再切到投资理财的抢投
                Intent intent2 = new Intent(InvestmentActivity.ACTION_CHANNEL);
                intent2.putExtra("TYPE", InvestmentActivity.TYPE_HQ);
                sendBroadcast(intent2);
                */

                Intent intent = new Intent(HomeActivity.this, InvestmentActivity.class);
                intent.putExtra("type", InvestmentActivity.TYPE_HQ);
                HomeActivity.this.startActivity(intent);
            }
        });
        return currentLayout;
    }

    private HomeItemLayout initScheduledItem() {
        scheduledLayout = new HomeItemLayout(this);
        scheduledLayout.setLeftImageView(R.drawable.home_item_02);
        scheduledLayout.setLineColor(Color.parseColor("#92D94A"));

        scheduledLayout.setTitle("鼓鼓定期", false);
        scheduledLayout.getTipTextView().setText("月加息再加2%");
        scheduledLayout.getTipTextView().setBackgroundResource(R.drawable.rounded_green_corner);
        scheduledLayout.getSbTextView().setText("安全有保障，收益天天见");
        scheduledLayout.getMbTextView().setVisibility(View.GONE);
        scheduledLayout.getUserCountTextView().setVisibility(View.GONE);
        scheduledLayout.getCircleLayout().setBackgroundResource(R.drawable.home_circle_01);
        scheduledLayout.getCompleteTextView().setText("完成：");
        scheduledLayout.getSurplusMoneyTextView().setText("0.00");
        scheduledLayout.getAddTextView().setVisibility(View.VISIBLE);

        scheduledLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // 解决投资理财未初始的问题
                InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_DQ);

                // 切到投资理财
                Intent intent1 = new Intent(MainActivity.ACTION_CHECK_TABHOST);
                intent1.putExtra("INDEX", 1);
                sendBroadcast(intent1);

                // 再切到投资理财的定投
                Intent intent2 = new Intent(InvestmentActivity.ACTION_CHANNEL);
                intent2.putExtra("TYPE", InvestmentActivity.TYPE_DQ);
                sendBroadcast(intent2);
                */

                Intent intent = new Intent(HomeActivity.this, InvestmentActivity.class);
                intent.putExtra("type", InvestmentActivity.TYPE_DQ);
                HomeActivity.this.startActivity(intent);
            }
        });

        return scheduledLayout;
    }

    private void requestDebtPackageIndex() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DEBTPACKAGE_INDEX, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, IndexAppDto.class);
                    AppMessageDto<IndexAppDto> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        appDto = dto.getData();

                        responseDebtPackageIndex();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
//                    mSwipeLayout.setRefreshing(false);
                }
            }
        });

        this.addToRequestQueue(request, null);
    }

    private void responseDebtPackageIndex() {
        if (this.appDto.getMessage().size() > 0 && hasCloseMessage == false) {
            timer.schedule(task, 0, 2500);
            messageLayout.setVisibility(View.VISIBLE);
        } else {
            messageLayout.setVisibility(View.GONE);
        }

        this.addAnnouncement(this.appDto.getTop20());


        imageURLList = this.appDto.getTopImgs();
        initViewPager();
        viewPagerAdapter.notifyDataSetChanged();

//        this.securityImageView.setImageUrl(Constants.HOST_IP + this.appDto.getSafetyImg().getImgUrl(), ImageCacheManager.getInstance().getImageLoader());
//        this.guideImageView.setImageUrl(Constants.HOST_IP + this.appDto.getStrategyImg().getImgUrl(), ImageCacheManager.getInstance().getImageLoader());

        Constants.PHONE_SERVICE = this.appDto.getServiceTelphone();

        // 活期
        HQInfoAppDto hqDto = this.appDto.getHq();
        currentLayout.getTipTextView().setText("超银行活期" + hqDto.getBank() + "倍");
        currentLayout.getUserCountTextView().setText(hqDto.getUserCount() + "");
        currentLayout.getSurplusMoneyTextView().setText(StringUtil.formatAmount(Double.parseDouble(hqDto.getTotalMoney()) - Double.parseDouble(hqDto.getSurplusMoneyStr())));
        currentLayout.getRateTextView().setText(hqDto.getRate());
        if (Double.parseDouble(hqDto.getSurplusMoney()) > 0) {
            currentLayout.getStateTextView().setText("抢购中");

            currentLayout.setColor(Color.parseColor("#FF001A"));
        } else {
            currentLayout.getStateTextView().setText("已售完");

            currentLayout.setColor(Color.parseColor("#999999"));
        }
        try {
            int progress = 100 - (int) (100 * Double.parseDouble(hqDto.getSurplusMoney()) / Double.parseDouble(hqDto.getTotalMoney()));
            currentLayout.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            currentLayout.setProgress(0);
        }

        // 定投
        HQInfoAppDto dtDto = this.appDto.getDq();
        scheduledLayout.getSurplusMoneyTextView().setText(StringUtil.formatAmount(Double.parseDouble(dtDto.getSurplusMoneyStr())));
        scheduledLayout.getRateTextView().setText(dtDto.getRate());
        if (Double.parseDouble(dtDto.getSurplusMoney()) > 0) {
            scheduledLayout.getStateTextView().setText("抢购中");

            scheduledLayout.setColor(Color.parseColor("#FF001A"));

        } else {
            scheduledLayout.getStateTextView().setText("已售完");

            scheduledLayout.setColor(Color.parseColor("#999999"));
        }
        try {
            int progress = 100 - (int) (100 * Double.parseDouble(dtDto.getSurplusMoney()) / Double.parseDouble(dtDto.getTotalMoney()));
            scheduledLayout.setProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
            scheduledLayout.setProgress(0);
        }
    }

    private void requestMessageCount() {
        String token = ActivityUtil.getSharedPreferences().getString(Constants.Base_Token, "");
        if (token == null || token.equals(""))
            return;

        JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGE_COUNT, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MessageCountAppDto.class);
                    AppMessageDto<MessageCountAppDto> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        if (dto.getData().getNoRead() > 0) {
                            findViewById(R.id.newMessageTipImageView).setVisibility(View.GONE);

                            findViewById(R.id.btn_msg).setSelected(true);
                        } else {
                            findViewById(R.id.newMessageTipImageView).setVisibility(View.GONE);
                            findViewById(R.id.btn_msg).setSelected(false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
//                    mSwipeLayout.setRefreshing(false);
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void showGiveMoneyTip() {
        String preDay = ActivityUtil.getSharedPreferences().getString(Constants.PRE_SHOW_REWARD_TIME, "");
        boolean showGiveMoney = ActivityUtil.getSharedPreferences().getBoolean(Constants.SHOW_GIVE_MONEY, true);

        if (showGiveMoney && !DateUtil.getCurrentDate().equals(preDay)) {
            ActivityUtil.getSharedPreferences().edit().putString(Constants.PRE_SHOW_REWARD_TIME, DateUtil.getCurrentDate()).commit();

            requestGiveMoney();
        }
    }

    // 判断是否需要弹出特权金
    private void requestGiveMoney() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_REGIST_GIVE_MONEY, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        double money = Double.parseDouble(dto.getData());
                        if (money > 0) {
                            ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.SHOW_GIVE_MONEY, true).commit();

                            RewardGiveMoneyDialog dialog = new RewardGiveMoneyDialog(GuguApplication.getInstance().getCurrentActivity());
                            dialog.show();

                        } else {
                            ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.SHOW_GIVE_MONEY, false).commit();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_btn: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "帮助中心");
                intent.putExtra("url", Constants.HOST_IP + "/app/help.html");
                startActivity(intent);
            }
            break;

            case R.id.btn_msg: {
                Intent intent_msg = new Intent(this, MessageListActivity.class);
                startActivity(intent_msg);
            }
            break;

            case R.id.messageCloseImageView: {
                messageLayout.setVisibility(View.GONE);
                hasCloseMessage = true;

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
            break;

            case R.id.securityImageView: {
                try {
                    Intent intent = new Intent(this, ShowWebViewActivity.class);
                    intent.putExtra("title", appDto.getSafetyImg().getName());
                    intent.putExtra("url", appDto.getSafetyImg().getLinkUrl());
                    startActivity(intent);

                } catch (Exception e) {
                    Intent intent = new Intent(this, ShowWebViewActivity.class);
                    intent.putExtra("title", "安全有保障");
                    intent.putExtra("url", Constants.HOST_IP + "/app/security.html");
                    startActivity(intent);
                }
            }
            break;

            case R.id.guideImageView: {
                try {
                    Intent intent = new Intent(this, ShowWebViewActivity.class);
                    intent.putExtra("title", appDto.getStrategyImg().getName());
                    intent.putExtra("url", appDto.getStrategyImg().getLinkUrl());
                    startActivity(intent);

                } catch (Exception e) {
                    Intent intent = new Intent(this, ShowWebViewActivity.class);
                    intent.putExtra("title", "新版攻略");
                    intent.putExtra("url", Constants.HOST_IP + "/app/gonglue.html");
                    startActivity(intent);
                }
            }
            break;
        }

    }

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

    ////////////////////////////////////////////////////
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
                    messageTextView.setText(appDto.getMessage().get(i % appDto.getMessage().size()));

                    in();
                    out();

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
                messageTextView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(messageTextView);
            }
        }, 0);
    }

    private void out() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.SlideOutUp).duration(500).playOn(messageTextView);
            }
        }, 2200);
    }

    protected void onDestory() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
