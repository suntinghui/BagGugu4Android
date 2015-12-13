package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gugu.client.Constants;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class RewardShareDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity context = null;

    private TextView wechatTextView = null;
    private TextView phoneTextView = null;
    private TextView smsTextView = null;

    private String telphone = "";

    private String shareTitle = "";
    private String shareContent = "";
    private String smsContent = "";

    public RewardShareDialog(Context context, String telphone,String shareTitle, String shareContent, String smsContent) {
        this(context, R.style.ProgressHUD);

        this.telphone = telphone;
        this.shareTitle = shareTitle;
        this.shareContent = shareContent;
        this.smsContent = smsContent;
    }

    public RewardShareDialog(Context context, int theme) {
        super(context, theme);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (Activity) context;

        this.setContentView(R.layout.layout_reward_share);

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        lp.width = (int) (this.context.getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        this.getWindow().setAttributes(lp);

        wechatTextView = (TextView) this.findViewById(R.id.wechatTextView);
        wechatTextView.setOnClickListener(this);

        phoneTextView = (TextView) this.findViewById(R.id.phoneTextView);
        phoneTextView.setOnClickListener(this);

        smsTextView = (TextView) this.findViewById(R.id.smsTextView);
        smsTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechatTextView:
                share();
                break;

            case R.id.phoneTextView: {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + this.telphone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.getContext().startActivity(intent);
            }
            break;

            case R.id.smsTextView: {
                Uri smsToUri = Uri.parse("smsto:" + this.telphone);
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", smsContent);
                this.getContext().startActivity(intent);
            }

            break;
        }

        this.dismiss();

    }

    private void share() {
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(shareContent);
        weixinContent.setTitle(shareTitle);
        String linkUrl = Constants.HOST_IP + "/yq/" + ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");
        weixinContent.setTargetUrl(linkUrl);
        weixinContent.setShareImage(new UMImage(context, R.drawable.share_money_1000_wechat));

        mController.setShareMedia(weixinContent);

        mController.postShare(context, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
            @Override
            public void onStart() {
                //Toast.makeText(context, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(context, "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    String eMsg = "";
                    if (eCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(context, "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}
