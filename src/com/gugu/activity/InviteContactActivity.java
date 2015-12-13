package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.android.volley.Response;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import cn.trinea.android.view.autoscrollviewpager.ImagePagerAdapter;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.ImageAppDto;
import com.ares.baggugu.dto.app.LinkInviteIndexAppDto;
import com.ares.baggugu.dto.app.PhonebookAppDto;
import com.gugu.activity.MyRewardRateActivity.SpinnerAdapter;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.wufriends.gugu.R;

// 邀请通讯录好友
public class InviteContactActivity extends BaseActivity implements OnClickListener {

    private AutoScrollViewPager viewPager = null;
    private ImagePagerAdapter viewPagerAdapter = null;
    private List<ImageAppDto> imageURLList = new ArrayList<ImageAppDto>();

    private LinearLayout indicatorLayout;
    private ImageView[] indicatorImageViews = null;

    private TextView inviteFriendContactTextView; // 邀请通讯录好友
    private TextView inviteFriendWechatTextView; // 邀请微信好友
    private TextView inviteFriendQQTextView; // 邀请QQ好友
    private LinearLayout countLayout; // 查看注册的好友信息
    private TextView inviteCountTextView; // 共邀请了40位好友
    private TextView registCountTextView; // 已有20位好友注册
    private LinearLayout moneyLayout; // 获利金额信息
    private TextView totalMoneyTextView; // 好友共计投资金额
    private TextView expectedEarningTextView; // 可领取
    private TextView earningTextView; // 已领取

    private Spinner contactSpinner;

    public static final String kACCESS_CONSTANT = "kACCESS_CONSTANT"; // 是否已经显示过访问通讯录的权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_contact);

        initView();

        requestInviteIndex();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != viewPager) {
            viewPager.startAutoScroll();
        }
    }

    public void onPause() {
        super.onPause();

        if (null != viewPager) {
            viewPager.stopAutoScroll();
        }
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("朋友圈获利");

        inviteFriendContactTextView = (TextView) this.findViewById(R.id.inviteFriendContactTextView);
        inviteFriendContactTextView.setOnClickListener(this);

        inviteFriendWechatTextView = (TextView) this.findViewById(R.id.inviteFriendWechatTextView);
        inviteFriendWechatTextView.setOnClickListener(this);

        inviteFriendQQTextView = (TextView) this.findViewById(R.id.inviteFriendQQTextView);
        inviteFriendQQTextView.setOnClickListener(this);

        countLayout = (LinearLayout) this.findViewById(R.id.countLayout);
        countLayout.setOnClickListener(this);

        inviteCountTextView = (TextView) this.findViewById(R.id.inviteCountTextView);

        registCountTextView = (TextView) this.findViewById(R.id.registCountTextView);

        moneyLayout = (LinearLayout) this.findViewById(R.id.moneyLayout);
        moneyLayout.setOnClickListener(this);

        totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);

        expectedEarningTextView = (TextView) this.findViewById(R.id.expectedEarningTextView);

        earningTextView = (TextView) this.findViewById(R.id.earningTextView);

        contactSpinner = (Spinner) this.findViewById(R.id.contactSpinner);
    }

    private void initViewPager() {
        // indicator
        indicatorLayout = (LinearLayout) this.findViewById(R.id.indicatorLayout);
        indicatorLayout.removeAllViews();

        indicatorImageViews = new ImageView[imageURLList.size()];
        for (int i = 0; i < imageURLList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            indicatorImageViews[i] = imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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
        viewPager.setStopScrollWhenTouch(false);
        viewPagerAdapter = new ImagePagerAdapter(this, imageURLList);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

                    Intent intent = new Intent(InviteContactActivity.this, ShowWebViewActivity.class);
                    intent.putExtra("title", imageDto.getName());
                    intent.putExtra("url", imageDto.getLinkUrl());
                    InviteContactActivity.this.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.onSingleTapConfirmed(event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.inviteFriendContactTextView: {
                boolean flag = ActivityUtil.getSharedPreferences().getBoolean(kACCESS_CONSTANT, false);
                if (flag) {
                    Intent intent = new Intent(this, InviteContactListActivity.class);
                    this.startActivity(intent);
                } else {
                    Intent intent = new Intent(this, AccessContactTipActivity.class);
                    this.startActivity(intent);
                }

            }
            break;

            case R.id.inviteFriendWechatTextView: {
                ArrayList<String> list = new ArrayList<String>();
                list.add("微信");
                list.add("微信好友");
                list.add("朋友圈");

                showSpinner("WECHAT", list);
            }
            break;

            case R.id.inviteFriendQQTextView: {
                ArrayList<String> list = new ArrayList<String>();
                list.add("QQ");
                list.add("QQ好友");
                list.add("QQ空间");

                showSpinner("QQ", list);
            }
            break;

            case R.id.countLayout: {
                Intent intent = new Intent(this, InviteRegisterActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.moneyLayout: {
                Intent intent = new Intent(this, InviteInvestmentActivity.class);
                this.startActivity(intent);
            }
            break;
        }
    }

    private void inviteForShare() {
        Intent intent = new Intent(this, InviteShareWebActivity.class);
        intent.putExtra("url", Constants.HOST_IP + "/yq/app");
        this.startActivity(intent);
    }

    private void requestInviteIndex() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_INDEX, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, LinkInviteIndexAppDto.class);
                    AppMessageDto<LinkInviteIndexAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responseInviteIndex(dto.getData());

                    } else {
                        Toast.makeText(InviteContactActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseInviteIndex(LinkInviteIndexAppDto dto) {
        // 共邀请了0位好友
        String text1 = "<font color=#999999>共邀请好友 </font><font color=#1caff6>" + dto.getTotalInvite() + "</font> <font color=#999999> 位</font>";
        inviteCountTextView.setText(Html.fromHtml(text1));

        // 已有0位好友注册
        String text2 = "<font color=#999999>已注册好友 </font><font color=#1caff6 size=18>" + dto.getRegist() + "</font> <font color=#999999> 位</font>";
        registCountTextView.setText(Html.fromHtml(text2));

        // 好友共计投资0.00元
        String text3 = "<font color=#999999>朋友圈投资总额 </font><font color=#1caff6>" + dto.getTotalMoney() + "</font> <font color=#999999> 元</font>";
        totalMoneyTextView.setText(Html.fromHtml(text3));

        expectedEarningTextView.setText(dto.getWaitEarnings());
        earningTextView.setText(dto.getReceiveEarnings());


        imageURLList = dto.getTopImgs();
        initViewPager();
        viewPagerAdapter.notifyDataSetChanged();
    }

    private String linkUrl = Constants.HOST_IP + "/yq/" + ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");
    private String shareTitle = Constants.shareTitle;
    private String shareContent = Constants.shareContent;

    // 分享给微信好友
    private void share2WeiXin() {
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        weixinContent.setShareContent(shareContent);
        // 设置title
        weixinContent.setTitle(shareTitle);
        // 设置分享内容跳转URL
        weixinContent.setTargetUrl(linkUrl);
        // 设置分享图片
        weixinContent.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(weixinContent);

        mController.postShare(this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
            @Override
            public void onStart() {
                // Toast.makeText(context, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(InviteContactActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    String eMsg = "";
                    if (eCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(InviteContactActivity.this, "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    // 分享到朋友圈
    private void share2Timeline() {
        CircleShareContent circleMedia = new CircleShareContent();
        // 设置分享文字
        circleMedia.setShareContent(shareContent);
        // 设置title
        circleMedia.setTitle(shareTitle);
        // 设置分享内容跳转URL
        circleMedia.setTargetUrl(linkUrl);
        // 设置分享图片
        circleMedia.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(circleMedia);

        mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
            @Override
            public void onStart() {
                // Toast.makeText(context, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(InviteContactActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    String eMsg = "";
                    if (eCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(InviteContactActivity.this, "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    // 分享到QQ好友
    private void share2QQFriend() {
        QQShareContent qqShareContent = new QQShareContent();
        // 设置分享文字
        qqShareContent.setShareContent(shareContent);
        // 设置title
        qqShareContent.setTitle(shareTitle);
        // 设置分享内容跳转URL
        qqShareContent.setTargetUrl(linkUrl);
        // 设置分享图片
        qqShareContent.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(qqShareContent);

        mController.postShare(this, SHARE_MEDIA.QQ, new SnsPostListener() {
            @Override
            public void onStart() {
                // Toast.makeText(context, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(InviteContactActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    String eMsg = "";
                    if (eCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(InviteContactActivity.this, "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    // 分享到QQ空间
    private void share2QZone() {
        QZoneShareContent qzone = new QZoneShareContent();
        // 设置分享文字
        qzone.setShareContent(shareContent);
        // 设置title
        qzone.setTitle(shareTitle);
        // 设置分享内容跳转URL
        qzone.setTargetUrl(linkUrl);
        // 设置分享图片
        qzone.setShareImage(new UMImage(this, R.drawable.share_money_1000_wechat));

        UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.setShareMedia(qzone);

        mController.postShare(this, SHARE_MEDIA.QZONE, new SnsPostListener() {
            @Override
            public void onStart() {
                // Toast.makeText(context, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Toast.makeText(InviteContactActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
                } else {
                    String eMsg = "";
                    if (eCode == -101) {
                        eMsg = "没有授权";
                    }
                    Toast.makeText(InviteContactActivity.this, "分享失败[" + eCode + "] " + eMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private void showSpinner(final String type, final List<String> list) {
        final SpinnerAdapter adapter = new SpinnerAdapter(this, list);
        contactSpinner.setAdapter(adapter);
        contactSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;

                if (type.equals("WECHAT")) {
                    if (position == 1) {
                        share2WeiXin();
                    } else {
                        share2Timeline();
                    }

                } else if (type.equals("QQ")) {
                    if (position == 1) {
                        share2QQFriend();
                    } else {
                        share2QZone();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        contactSpinner.performClick();
    }

    private class ViewHolder {
        private TextView textView;
        private ImageView imageView;
    }

    public class SpinnerAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private List<String> mList;
        private Context mContext;

        private int selectedIndex = 0;

        public SpinnerAdapter(Context pContext, List<String> pList) {
            this.mContext = pContext;
            this.mList = pList;

            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
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
                convertView = mInflater.inflate(R.layout.spinner_item, null);

                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position != 0) {
                holder.textView.setText(mList.get(position));
                holder.imageView.setSelected(selectedIndex == position);
                holder.imageView.setPressed(selectedIndex == position);
            } else {
                holder.textView.setText(mList.get(position));
                holder.imageView.setVisibility(View.GONE);
            }

            return convertView;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }
    }

}
