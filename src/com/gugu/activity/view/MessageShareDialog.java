package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MessageListAppDto;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.InviteContactActivity;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.JsonUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

import com.android.volley.Response;

public class MessageShareDialog extends Dialog implements View.OnClickListener {

    private BaseActivity context = null;

    private TextView timelineTextView = null;
    private TextView QZoneTextView = null;

    private MessageListAppDto dto = null;

    private OnStartShareListener listener = null;

    public MessageShareDialog(Context context, MessageListAppDto dto) {
        this(context, R.style.ProgressHUD);

        this.dto = dto;
    }

    public MessageShareDialog(Context context, int theme) {
        super(context, theme);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (BaseActivity) context;

        this.setContentView(R.layout.layout_message_share);

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        lp.width = (int) (this.context.getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        this.getWindow().setAttributes(lp);

        timelineTextView = (TextView) this.findViewById(R.id.timelineTextView);
        timelineTextView.setOnClickListener(this);

        QZoneTextView = (TextView) this.findViewById(R.id.QZoneTextView);
        QZoneTextView.setOnClickListener(this);
    }

    public void setOnStartShareListener(OnStartShareListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timelineTextView:
                shareTimeline();
                break;

            case R.id.QZoneTextView:
                shareQZone();
                break;

        }

        this.dismiss();

    }

    private void shareTimeline() {
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(dto.getContent());
        circleMedia.setTitle(dto.getTitle());
        circleMedia.setTargetUrl(Constants.HOST_IP + "/share/message/" + dto.getId());
        circleMedia.setShareImage(new UMImage(this.context, R.drawable.share_money_1000_wechat));
        mController.setShareMedia(circleMedia);
        mController.postShare(this.context, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
            @Override
            public void onStart() {
                if (listener != null) {
                    listener.startShare();
                }
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {
            }
        });

    }

    private void shareQZone() {
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(dto.getContent());
        qzone.setTitle(dto.getTitle());
        qzone.setTargetUrl(Constants.HOST_IP + "/share/message/" + dto.getId());
        qzone.setShareImage(new UMImage(this.context, R.drawable.share_money_1000_wechat));

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(qzone);

        mController.postShare(context, SHARE_MEDIA.QZONE, new SnsPostListener() {
            @Override
            public void onStart() {
                if (listener != null) {
                    listener.startShare();
                }
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
            }

        });
    }

    public interface OnStartShareListener {
        public void startShare();
    }
}
