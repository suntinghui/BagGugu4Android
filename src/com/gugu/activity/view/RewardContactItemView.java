package com.gugu.activity.view;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import com.android.volley.Response;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.wufriends.gugu.R;
import com.gugu.activity.MyRewardRateActivity;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class RewardContactItemView extends LinearLayout implements OnClickListener {

    private MyRewardRateActivity context = null;

    private CustomNetworkImageView headImageView = null;
    private TextView nameTextView = null;
    private TextView statusTextView = null;
    private Button tellBtn = null;
    private ImageView deleteImageView = null;

    private HashMap<String, String> map = null;

    public RewardContactItemView(Context context) {
        super(context);

        this.initView(context);
    }

    public RewardContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private RewardContactItemView initView(Context context) {
        this.context = (MyRewardRateActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_reward_contact, this);

        this.headImageView = (CustomNetworkImageView) this.findViewById(R.id.headImageView);
        this.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
        this.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);

        this.nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        this.statusTextView = (TextView) this.findViewById(R.id.statusTextView);
        this.tellBtn = (Button) this.findViewById(R.id.tellBtn);
        this.tellBtn.setOnClickListener(this);

        this.deleteImageView = (ImageView) this.findViewById(R.id.deleteImageView);
        this.deleteImageView.setOnClickListener(this);

        return this;
    }

    public void setData(HashMap<String, String> map) {
        this.map = map;

        this.nameTextView.setText(map.get("realname"));

        this.headImageView.setImageUrl(Constants.HOST_IP + map.get("logo"), ImageCacheManager.getInstance().getImageLoader());

        // 0 表示没有注册，1 表示已注册
        if (Integer.parseInt(map.get("regist")) == 0) {
            this.statusTextView.setText("等待注册");
            this.tellBtn.setText("告诉下");
            this.tellBtn.setEnabled(true);

        } else {
            this.statusTextView.setText("+" + map.get("signleValue") + "%");
            this.tellBtn.setText("已经注册");
            this.tellBtn.setEnabled(false);
            this.deleteImageView.setAlpha(0.2f);
            this.deleteImageView.setEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tellBtn) {
            this.tellHim();

        } else if (v.getId() == R.id.deleteImageView) {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setTitleText("\n您确定要删除" + map.get("realname") + "吗？").setContentText("").setCancelText("取消").setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.cancel();
                }
            }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.cancel();

                    delete();
                }
            }).show();
        }
    }

    private void tellHim() {
        String shareTitle = Constants.shareTitle;
        String shareContent = Constants.shareContent;
        String smsContent = Constants.shareContent + "【鼓鼓理财】 http://www.wufriends.com/ggd/";

        RewardShareDialog shareDialog = new RewardShareDialog(context, this.map.get("telphone"), shareTitle, shareContent, smsContent);
        shareDialog.show();
    }

    private void delete() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", map.get("id"));

        JSONRequest request = new JSONRequest(this.getContext(), RequestEnum.WELFARE_DELETE_FRIEND, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        Toast.makeText(context, "好友已删除", Toast.LENGTH_SHORT).show();

                        context.requestRewardInfo();

                    } else {
                        Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, "正在请求数据...");
    }

}
