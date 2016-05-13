package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.ConfigAppDto;
import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.UMengPushUtil;
import com.speedtong.sdk.ECDevice;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SystemSettingActivity extends BaseActivity implements OnClickListener {

    private LinearLayout checkUpdateLayout = null;
    private TextView currentVersionTextView = null;
    private LinearLayout feedbackLayout = null;
    private LinearLayout aboutLayout = null;
    private LinearLayout ourHistoryLayout = null;
    private LinearLayout contactLayout = null;

    private TextView telphoneTextView = null;
    private TextView serviceWechatTextView = null;
    private TextView wechatTextView = null;

    private Button logoutBtn = null;

    private ConfigAppDto configDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_setting);

        initView();

        requestInfo();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("系统设置");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        checkUpdateLayout = (LinearLayout) this.findViewById(R.id.checkUpdateLayout);
        checkUpdateLayout.setOnClickListener(this);

        currentVersionTextView = (TextView) this.findViewById(R.id.currentVersionTextView);
        currentVersionTextView.setText(getVersionName());

        feedbackLayout = (LinearLayout) this.findViewById(R.id.feedbackLayout);
        feedbackLayout.setOnClickListener(this);

        ourHistoryLayout = (LinearLayout) this.findViewById(R.id.ourHistoryLayout);
        ourHistoryLayout.setOnClickListener(this);

        aboutLayout = (LinearLayout) this.findViewById(R.id.aboutLayout);
        aboutLayout.setOnClickListener(this);

        contactLayout = (LinearLayout) this.findViewById(R.id.contactLayout);
        contactLayout.setOnClickListener(this);

        telphoneTextView = (TextView) this.findViewById(R.id.telphoneTextView);
        serviceWechatTextView = (TextView) this.findViewById(R.id.serviceWechatTextView);
        wechatTextView = (TextView) this.findViewById(R.id.wechatTextView);

        logoutBtn = (Button) this.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.checkUpdateLayout: {
                checkUpdate(this);
                break;
            }
            case R.id.feedbackLayout: {
                Intent intent = new Intent(this, FeedBackactivity.class);
                this.startActivity(intent);

                break;
            }
            case R.id.ourHistoryLayout: {
                startActivityForResult(new Intent(this, ProductTourActivity.class), 0);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            }
            case R.id.aboutLayout: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "关于");
                intent.putExtra("url", Constants.HOST_IP + "/app/about.html");
                this.startActivity(intent);
                break;
            }

            case R.id.contactLayout: {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + configDto.getTelphone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }

            case R.id.logoutBtn: {
                logout();

                break;
            }

        }

    }

    // 当前版本信息
    private String getVersionName() {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return "当前版本" + version;
        } catch (Exception e) {
            return "";
        }

    }

    // 检查更新
    private void checkUpdate(final BaseActivity mContext) {
        mContext.showProgress("正在检查更新...");

        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setDeltaUpdate(false);
        UmengUpdateAgent.forceUpdate(mContext);

        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                mContext.hideProgress();

                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        showDialog(mContext, SweetAlertDialog.SUCCESS_TYPE, "已经是最新版本！");
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        showDialog(mContext, SweetAlertDialog.WARNING_TYPE, "没有wifi连接， 只在wifi下更新");
                        break;
                    case UpdateStatus.Timeout: // time out
                        showDialog(mContext, SweetAlertDialog.WARNING_TYPE, "连接服务器超时");
                        break;
                }
            }

        });
    }

    private void showDialog(Context context, int type, String msg) {
        new SweetAlertDialog(context, type).setTitleText(null).setContentText(msg).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
            }
        }).show();
    }

    // 检查是否有反馈消息
    private void checkFeedback() {
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
    }

    // 退出应用
    private void logout() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("\n您确定要退出登录吗？").setContentText("").setCancelText("取消").setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();

                requestLogout();
            }
        }).show();
    }

    private void requestInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.CONFIG, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, ConfigAppDto.class);
                    AppMessageDto<ConfigAppDto> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        configDto = dto.getData();

                        telphoneTextView.setText("拨打: " + configDto.getTelphone());
                        serviceWechatTextView.setText("搜索: " + configDto.getGongzhonghao());
                        wechatTextView.setText("添加: " + configDto.getWeixin());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void requestLogout() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_LOGOUT, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        new UMengPushUtil().new RemoveAliasTask(SystemSettingActivity.this).execute();

                        Toast.makeText(SystemSettingActivity.this, "用户已安全退出", Toast.LENGTH_SHORT).show();

                        Editor editor = ActivityUtil.getSharedPreferences().edit();
                        editor.putString(Constants.Base_Token, "");
                        editor.commit();

                        Intent intent = new Intent(SystemSettingActivity.this, LoginActivity.class);
                        intent.putExtra("FROM", LoginActivity.FROM_TOKEN_EXPIRED);
                        SystemSettingActivity.this.startActivity(intent);

                    } else {
                        Toast.makeText(SystemSettingActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在退出...");
    }

}
