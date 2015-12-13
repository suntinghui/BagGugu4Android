package com.gugu.activity;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.ImSubAccountsAppDto;
import com.ares.baggugu.dto.app.LinkArticle;
import com.ares.baggugu.dto.app.MessageListAppDto;
import com.ares.baggugu.dto.app.StartupImageAppDto;
import com.wufriends.gugu.R;
import com.gugu.client.ActivityManager;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.Util;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    private TabHost tabhost = null;
    private RadioGroup main_radiogroup = null;

    private ImageView redTipBagImageView = null;

    private TabhostReceiver tabhostReceiver = null;
    private RedCircleReceiver redCircleReceiver = null;

    public static String ACTION_CHECK_TABHOST = "com.gugu.check.tabhost";
    public static String ACTION_RED_CIRCLE = "com.gugu.red_circle";

    private long exitTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        clearTopActivity();

        ActivityManager.getInstance().pushActivity(this);

        this.initTabHost();

        this.requestVerifyToken();

        this.aboutUmeng();

        setCheckTab(this.getIntent().getIntExtra("INDEX", 0));

        // 必须在setCheckTab的下面。否则会有刚启动应用选定栏目不起作用的问题。
        this.registerTabhostReceiver();
        this.registerRedCircleReceiver();

        this.requestCheckSplashImage();
    }

    private void clearTopActivity() {
        for (Activity act : ActivityManager.getInstance().getAllActivity()) {
            if (!(act instanceof MainActivity)) {
                act.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.cancelRequest();

        ActivityManager.getInstance().popActivity();

        this.unregisterReceiver(tabhostReceiver);
        this.unregisterReceiver(redCircleReceiver);
    }

    private void initTabHost() {
        // 获取按钮
        main_radiogroup = (RadioGroup) findViewById(R.id.main_radiogroup);

        // 设置监听事件
        CheckListener checkradio = new CheckListener();
        main_radiogroup.setOnCheckedChangeListener(checkradio);

        // 往TabWidget添加Tab
        tabhost = getTabHost();
        tabhost.addTab(tabhost.newTabSpec("tag1").setIndicator("0").setContent(new Intent(this, HomeActivity.class)));
        tabhost.addTab(tabhost.newTabSpec("tag2").setIndicator("1").setContent(new Intent(this, DiscoveryActivity.class)));
        tabhost.addTab(tabhost.newTabSpec("tag3").setIndicator("2").setContent(new Intent(this, BagActivity.class)));

        redTipBagImageView = (ImageView) this.findViewById(R.id.redTipBagImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onResume(this);

        if (redTipBagImageView != null) {
            boolean flag = (ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false) || ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_FRIEND, false));
            redTipBagImageView.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
    }

    // 指定打开的时候显示哪一个界面。通过intent的index（int）来指定
    private void setCheckTab(int index) {
        try {
            tabhost.setCurrentTab(index);

            switch (index) {
                case 0:
                    ((RadioButton) this.findViewById(R.id.tab_main_investment)).setChecked(true);
                    break;

                case 1:
                    ((RadioButton) this.findViewById(R.id.tab_main_discovery)).setChecked(true);
                    break;

                case 2:
                    ((RadioButton) this.findViewById(R.id.tab_main_bag)).setChecked(true);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestVerifyToken() {
        // 程序第一次启动的时候没有BASE-TOKEN则不发送。
        if (!ActivityUtil.getSharedPreferences().contains(Constants.Base_Token))
            return;

        JSONRequest request = new JSONRequest(MainActivity.this, RequestEnum.USER_VERIFY_TOKEN, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, ImSubAccountsAppDto.class);
                    AppMessageDto<ImSubAccountsAppDto> dtos = objectMapper.readValue(response, type);
                    if (dtos.getStatus() == AppResponseStatus.SUCCESS) {
                        if (dtos != null) {
//                            Util.initYTX(dtos.getData(), MainActivity.this);
                        }
                    }
                } catch (Exception e) {
                }

            }
        });

        this.addToRequestQueue(request);
    }

    private void requestCheckSplashImage() {
        JSONRequest request = new JSONRequest(MainActivity.this, RequestEnum.STARTUP_IMAGE, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, StartupImageAppDto.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<ArrayList<StartupImageAppDto>> dto = objectMapper.readValue(response, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Intent intent = new Intent(MainActivity.this, DownloadSplashImageService.class);
                        intent.putExtra("LIST", dto.getData());
                        MainActivity.this.startService(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request);
    }

    // 监听类
    public class CheckListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            try {
                // setCurrentTab 通过标签索引设置当前显示的内容
                // setCurrentTabByTag 通过标签名设置当前显示的内容
                switch (checkedId) {
                    case R.id.tab_main_investment:
                        tabhost.setCurrentTab(0);
                        break;

                    case R.id.tab_main_discovery:
                        tabhost.setCurrentTab(1);
                        break;

                    case R.id.tab_main_bag:
                        tabhost.setCurrentTab(2);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // 定位tabhost
    private void registerTabhostReceiver() {
        tabhostReceiver = new TabhostReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_CHECK_TABHOST);
        this.registerReceiver(tabhostReceiver, filter);
    }

    public class TabhostReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("INDEX", 0);
            setCheckTab(index);
        }
    }

    // 显示红点
    private void registerRedCircleReceiver() {
        redCircleReceiver = new RedCircleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_RED_CIRCLE);
        this.registerReceiver(redCircleReceiver, filter);
    }

    public class RedCircleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (redTipBagImageView != null) {
                boolean flag = (ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false) || ActivityUtil.getSharedPreferences().getBoolean(Constants.RED_CIRCLE_TIP_FRIEND, false));
                redTipBagImageView.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    // 检查更新
    private void checkUpdate() {
        // 因为友盟的更新设置是静态的参数，如果在应用中不止一次调用了检测更新的方法，而每次的设置都不一样，请在每次检测更新的函数之前先恢复默认设置再设置参数，避免在其他地方设置的参数影响到这次更新
        UmengUpdateAgent.setDefault();
        // updateOnlyWifi 布尔值true(默认)只在wifi环境下检测更新，false在所有网络环境中均检测更新。
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        // deltaUpdate 布尔值true(默认)使用增量更新，false使用全量更新。看了FAQ，貌似增量更新会可能有问题，为了保险起见，不使用增量更新
        UmengUpdateAgent.setDeltaUpdate(false);
        UmengUpdateAgent.update(this);
    }

    private void aboutUmeng() {
        // UMeng
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.setAutoLocation(true);

        // 推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        mPushAgent.setDebugMode(false);
        mPushAgent.setPushIntentServiceClass(GuguPushIntentService.class);

        String deviceToken = UmengRegistrar.getRegistrationId(this);
        Log.e("UMENG", "UMENG DEVICE TOKEN : " + deviceToken);
        Editor editor = ActivityUtil.getSharedPreferences().edit();
        editor.putString(Constants.DEVICETOKEN, deviceToken);
        editor.commit();

        // 解决在通知栏里面显示的始终是最新的那一条的问题，谨慎使用，以免用户看到消息过多卸载应用。
        // 合并
        mPushAgent.setMergeNotificaiton(true);

        mPushAgent.onAppStart();

        // UMeng检查更新
        checkUpdate();

        // 在线参数更新
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        String value = OnlineConfigAgent.getInstance().getConfigParams(this, "LuckyDraw");
        Constants.LuckyDraw = Boolean.parseBoolean(value);
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

    // Volley
    private RequestQueue mRequestQueue = null;

    private RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    // Adds the specified request to the global queue using the Default TAG.
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(this);
        getRequestQueue().add(req);
    }

    public void cancelRequest() {
        try {
            this.mRequestQueue.cancelAll(this);
        } catch (Exception e) {

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Intent actionIntent = intent;
        String contactId = actionIntent.getStringExtra("contactId");
        if (!StringUtils.isBlank(contactId)) {
            String userName = actionIntent.getStringExtra("userName");

//            Intent chatIntent = new Intent(this, ChattingActivity.class);
//            chatIntent.putExtra("recipients", contactId);
//            chatIntent.putExtra("userName", userName);
//            startActivity(chatIntent);
        }

        return;

    }

}
