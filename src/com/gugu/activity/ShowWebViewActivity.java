package com.gugu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.wufriends.gugu.R;
import com.gugu.activity.view.NetErrorDialog;
import com.gugu.activity.view.NoZoomControllWebView;
import com.gugu.client.ActivityManager;
import com.gugu.utils.NetUtil;

public class ShowWebViewActivity extends BaseActivity implements OnClickListener {

    private Button backBtn = null;
    private Button shareBtn = null;

    private TextView titleTextView = null;
    private NoZoomControllWebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_webview);

        String title = this.getIntent().getStringExtra("title");
        String url = this.getIntent().getStringExtra("url");

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        shareBtn = (Button) this.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(this);

        // 是否显示分享，默认不显示
        boolean showShareBtn = this.getIntent().getBooleanExtra("SHOW_SHARE", false);
        shareBtn.setVisibility(showShareBtn ? View.VISIBLE : View.GONE);

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        webView = (NoZoomControllWebView) this.findViewById(R.id.webview);
        WebSettings setting = webView.getSettings();

        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);

        setting.setSupportZoom(true);
        setting.setLoadsImagesAutomatically(true);

        setting.setBuiltInZoomControls(true);

        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);

        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void onResume() {
        super.onResume();

        if (!NetUtil.isNetworkAvailable(this)) {
            NetErrorDialog.getInstance().show(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backBtn) {
            this.backAction();
        }
    }

    public void onBackPressed() {
        this.backAction();
    }

    private void backAction() {
        // 为推送准备
        if (ActivityManager.getInstance().getAllActivity().size() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();

        } else {
            this.finish();
        }
    }

    // 分享
    private void share(String shareContent, String shareTitle, String linkUrl) {
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

        // 朋友圈
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareContent);
        circleMedia.setTitle(shareTitle);
        circleMedia.setTargetUrl(linkUrl);
        circleMedia.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
        mController.setShareMedia(circleMedia);

        // 分享到QQ空间
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(shareContent);
        qzone.setTitle(shareTitle);
        qzone.setTargetUrl(linkUrl);
        qzone.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
        mController.setShareMedia(qzone);

        mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE);
        mController.openShare(this, false);
    }

}
