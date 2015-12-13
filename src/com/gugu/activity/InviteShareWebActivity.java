package com.gugu.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gugu.activity.view.NetErrorDialog;
import com.gugu.activity.view.NoZoomControllWebView;
import com.gugu.client.Constants;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.NetUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.wufriends.gugu.R;

// 邀请分享
public class InviteShareWebActivity extends BaseActivity implements OnClickListener {

	private Button backBtn = null;
	private TextView titleTextView = null;
	private NoZoomControllWebView webView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_show_webview);

		String url = this.getIntent().getStringExtra("url");

		backBtn = (Button) this.findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);

		titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("邀请好友");

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

		webView.addJavascriptInterface(new JsInteration(), "invite");

		webView.loadUrl(url);
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
		this.finish();
	}

	private String linkUrl = Constants.HOST_IP + "/yq/"+ ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");
	private String shareTitle = Constants.shareTitle;
	private String shareContent = Constants.shareContent;

	public class JsInteration {

		@JavascriptInterface
		public void share() {
			shareYAO();
		}
	}

	private void shareYAO() {
		UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

		// 微信好友
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(shareContent);
		weixinContent.setTitle(shareTitle);
		weixinContent.setTargetUrl(linkUrl);
		weixinContent.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
		mController.setShareMedia(weixinContent);

		// 朋友圈
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(shareContent);
		circleMedia.setTitle(shareTitle);
		circleMedia.setTargetUrl(linkUrl);
		circleMedia.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
		mController.setShareMedia(circleMedia);

		// QQ好友
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(shareContent);
		qqShareContent.setTitle(shareTitle);
		qqShareContent.setTargetUrl(linkUrl);
		qqShareContent.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
		mController.setShareMedia(qqShareContent);

		// 分享到QQ空间
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(shareContent);
		qzone.setTitle(shareTitle);
		qzone.setTargetUrl(linkUrl);
		qzone.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));
		mController.setShareMedia(qzone);

		mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
		mController.openShare(this, false);
	}

}
