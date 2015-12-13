package com.gugu.activity;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MonthAddRateAppDto;
import com.ares.baggugu.dto.app.MonthAddRateListAppDto;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.wufriends.gugu.R;

/**
 * 月加息
 *
 * @author sth
 */

public class MyRewardRateExActivity extends BaseActivity implements OnClickListener {

    private TextView valueTextView = null; // +2%
    private TextView tipTextView = null; // 每位好友可为我加息0.2%，累计最高2%。
    private Button sendButton = null; // 发起加息

    private NumberProgressBar progressBar = null;
    private TextView surplusDayTextView; // 可用 0 天
    private TextView countTextView; // 5位鼓鼓用户在帮您加息

    private LinearLayout addContactLayout = null;
    private TextView inviteFriendTextView = null; // 邀请好友

    private ListView listView;
    private MyAdapter adapter;

    private MonthAddRateAppDto infoDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_reward_rate_ex);

        initView();
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("月加息");

        valueTextView = (TextView) this.findViewById(R.id.valueTextView);
        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        sendButton = (Button) this.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        progressBar = (NumberProgressBar) this.findViewById(R.id.progressBar);
        progressBar.setReachedBarHeight(15);
        progressBar.setUnreachedBarHeight(15);
        progressBar.setProgressTextSize(40);
        progressBar.setProgressTextColor(getResources().getColor(R.color.redme));

        surplusDayTextView = (TextView) this.findViewById(R.id.surplusDayTextView);
        countTextView = (TextView) this.findViewById(R.id.countTextView);
        addContactLayout = (LinearLayout) this.findViewById(R.id.addContactLayout);
        inviteFriendTextView = (TextView) this.findViewById(R.id.inviteFriendTextView);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        this.requestRewardInfo();
    }

    public void requestRewardInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_MONTH_ADDRATE, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MonthAddRateAppDto.class);
                    AppMessageDto<MonthAddRateAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        responseRewadInfo();

                    } else {
                        Toast.makeText(MyRewardRateExActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseRewadInfo() {
        if (this.infoDto == null)
            return;

        valueTextView.setText("+" + this.infoDto.getTotal() + "%");
        // 必须是＋后面一位数变大
        Spannable span = new SpannableString(valueTextView.getText());
        ;
        span.setSpan(new RelativeSizeSpan(1.8f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        valueTextView.setText(span);
        // 每位好友可为我加息0.2%，累计最高2%。
        tipTextView.setText("每位好友可为我加息+" + infoDto.getSingel() + "%，累计最高+" + infoDto.getTotal() + "%。");

        surplusDayTextView.setText(infoDto.getDay() + "");
        countTextView.setText(infoDto.getUsedCount() + "");

        this.inviteFriendTextView.setText("加息列表");
        this.addContactLayout.setEnabled(false);

        // 设置加息列表
        this.refreshContactList();
    }

    private void refreshContactList() {
        progressBar.setProportion(Float.parseFloat(infoDto.getSingel()) * 0.1f);

        int usedCount = 0;
        List<MonthAddRateListAppDto> list = this.infoDto.getAppDtos();
        for (MonthAddRateListAppDto dto : list) {
            if (dto.isUsed())
                usedCount++;
        }
        progressBar.setProgress(usedCount * 10);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.sendButton:
                share();
                break;

        }
    }

    private String linkUrl = Constants.HOST_IP + "/jx/" + ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");
    private String shareTitle = "帮我加息，多赚2%，让我赚钱让我飞！";
    private String shareContent = "您的朋友发起2%加息，让朋友赚更多，让理财更透明。";

    private void share() {
        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

        // 微信好友
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(shareContent);
        weixinContent.setTitle(shareTitle);
        weixinContent.setTargetUrl(linkUrl);
        weixinContent.setShareImage(new UMImage(this, R.drawable.share_rate_2_wechat));
        mController.setShareMedia(weixinContent);

        // 朋友圈
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(shareContent);
        circleMedia.setTitle(shareTitle);
        circleMedia.setTargetUrl(linkUrl);
        circleMedia.setShareImage(new UMImage(this, R.drawable.share_rate_2_wechat));
        mController.setShareMedia(circleMedia);

        // QQ好友
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(shareContent);
        qqShareContent.setTitle(shareTitle);
        qqShareContent.setTargetUrl(linkUrl);
        qqShareContent.setShareImage(new UMImage(this, R.drawable.share_rate_2_wechat));
        mController.setShareMedia(qqShareContent);

        // 分享到QQ空间
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(shareContent);
        qzone.setTitle(shareTitle);
        qzone.setTargetUrl(linkUrl);
        qzone.setShareImage(new UMImage(this, R.drawable.share_rate_2_wechat));
        mController.setShareMedia(qzone);

        mController.getConfig().removePlatform(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE);
        mController.openShare(this, false);
    }

    // 已注册
    private class ViewHolder {
        private CustomNetworkImageView headImageView; // 头像
        private TextView nameTextView; // 姓名
        private TextView rateTextView;
        private Button sendBoundButton; // 送红包
        private Button usedButton; // 使用
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (infoDto == null || infoDto.getAppDtos() == null)
                return 0;

            return infoDto.getAppDtos().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (null == convertView) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.layout_invite_addrate, null);

                holder.headImageView = (CustomNetworkImageView) convertView.findViewById(R.id.headImageView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
                holder.sendBoundButton = (Button) convertView.findViewById(R.id.sendBoundButton);
                holder.usedButton = (Button) convertView.findViewById(R.id.usedButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MonthAddRateListAppDto dto = infoDto.getAppDtos().get(position);

            holder.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setLocalImageBitmap(R.drawable.fenqi_head_default);
            holder.headImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());

            holder.nameTextView.setText(dto.getName());
            holder.rateTextView.setText("+" + infoDto.getSingel() + "%");

            if (dto.isUsed()) {
                holder.rateTextView.setTextColor(getResources().getColor(R.color.blueme));
                holder.usedButton.setText("使用中..");
                holder.usedButton.setBackgroundResource(R.drawable.gray_button_selector);

            } else {
                holder.rateTextView.setTextColor(Color.parseColor("#999999"));
                holder.usedButton.setText("使用加息");
                holder.usedButton.setBackgroundResource(R.drawable.blue_button_selector);
                holder.usedButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUse(dto.getId());
                    }
                });
            }

            if (dto.isBonus()) {
                holder.sendBoundButton.setText("已发送");
                holder.sendBoundButton.setBackgroundResource(R.drawable.gray_button_selector);
            } else {
                holder.sendBoundButton.setText("发红包");
                holder.sendBoundButton.setBackgroundResource(R.drawable.red_button_selector);
                holder.sendBoundButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendBonus(dto);
                    }
                });
            }

            return convertView;
        }
    }

    public void requestUse(int id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_MONTH_ADDRATE_USED, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(MyRewardRateExActivity.this, "操作成功", Toast.LENGTH_SHORT).show();

                        requestRewardInfo();

                    } else {
                        Toast.makeText(MyRewardRateExActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void sendBonus(MonthAddRateListAppDto dto) {
        Intent intent = new Intent(this, SendBonusActivity.class);
        intent.putExtra("name", dto.getName());
        intent.putExtra("userId", dto.getUserId() + "");
        intent.putExtra("sourceId", dto.getId() + "");
        intent.putExtra("type", "1");
        this.startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.requestRewardInfo();
        }
    }


}
