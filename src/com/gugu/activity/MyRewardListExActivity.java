package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyWelfareAppDto;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

public class MyRewardListExActivity extends BaseActivity implements OnClickListener {

    private LinearLayout timelineLayout;
    private LinearLayout scoreLayout;
    private LinearLayout rateLayout;
    private LinearLayout friendLayout;

    private TextView totalMoneyTextView;
    private TextView statusTextView;
    private TextView receiveMoneyValueTextView;
    private TextView integralTextView; // 积分
    private TextView dayTextView; // 加息剩余开数
    private TextView luckyMoneyCountTextView;
    private TextView rateTextView;
    private TextView bonusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reward_list_ex);

        initView();

        requestMyReward();
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("我的奖励");

        timelineLayout = (LinearLayout) this.findViewById(R.id.timelineLayout);
        timelineLayout.setOnClickListener(this);

        scoreLayout = (LinearLayout) this.findViewById(R.id.scoreLayout);
        scoreLayout.setOnClickListener(this);

        rateLayout = (LinearLayout) this.findViewById(R.id.rateLayout);
        rateLayout.setOnClickListener(this);

        friendLayout = (LinearLayout) this.findViewById(R.id.friendLayout);
        friendLayout.setOnClickListener(this);

        totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);
        statusTextView = (TextView) this.findViewById(R.id.statusTextView);
        receiveMoneyValueTextView = (TextView) this.findViewById(R.id.receiveMoneyValueTextView);
        integralTextView = (TextView) this.findViewById(R.id.integralTextView);
        dayTextView = (TextView) this.findViewById(R.id.dayTextView);
        rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        luckyMoneyCountTextView = (TextView) this.findViewById(R.id.luckyMoneyCountTextView);
        bonusTextView = (TextView) this.findViewById(R.id.bonusTextView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.timelineLayout: { // 朋友圈总收益
                Intent intent = new Intent(this, InviteTimelineActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.scoreLayout: {
                Intent intent = new Intent(this, TurntableLuckydrawActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.rateLayout: {
                Intent intent = new Intent(this, MyRewardRateExActivity.class);
                this.startActivity(intent);
            }

            break;

            case R.id.friendLayout: {
                Intent intent = new Intent(this, InviteFriendActivity.class);
                this.startActivity(intent);
            }
            break;

        }
    }

    private void requestMyReward() {
        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_INDEX, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MyWelfareAppDto.class);
                    AppMessageDto<MyWelfareAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responseMyReward(dto.getData());
                    } else {
                        Toast.makeText(MyRewardListExActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseMyReward(MyWelfareAppDto dto) {
        totalMoneyTextView.setText(dto.getTotalMoney());

        // false 即将领取 true可领取
        if (dto.isReceiveMoney()) {
            statusTextView.setText("可领取");
            statusTextView.setTextColor(getResources().getColor(R.color.redme));
        } else {
            statusTextView.setText("即将领取");
            statusTextView.setTextColor(Color.parseColor("#999999"));
        }
        receiveMoneyValueTextView.setText(dto.getReceiveMoneyValue() + "元");
        integralTextView.setText(dto.getIntegral() + "");
        dayTextView.setText(dto.getDay() + "");
        rateTextView.setText(dto.getRate() + "%");
        luckyMoneyCountTextView.setText(dto.getBonusWaitCount() + "");
        bonusTextView.setText(dto.getBonus() + "元");

    }

}
