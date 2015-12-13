package com.gugu.activity;

import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.EarningsHistorySequenceAppDto;
import com.ares.baggugu.dto.app.MyAppDto;
import com.ares.baggugu.dto.app.TotalMoneyAppDto;
import com.wufriends.gugu.R;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;

// 总资产

public class TotalAssetsActivity extends BaseActivity implements OnClickListener {

    private TextView totalAssetsTextView = null; // 总资产

    private TextView availableBalanceTextView = null; // 账户余额
    private TextView registeGiveMoneyTextView = null; // 特权金
    private TextView receivedPrincipalTextView = null; // 待收本金
    private TextView hqPrincipalTextView = null; // 活期本金
    private TextView receivedInterestTextView = null; // 待收利息
    private TextView withdrawalsTextView = null; // 提现中
    private TextView propertyTextView = null; // 提现中

    private TotalMoneyAppDto infoDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_total_assets);

        initView();

        initValue();

        this.requestTotalMoneyInfo();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("总资产");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        totalAssetsTextView = (TextView) this.findViewById(R.id.totalAssetsTextView);

        availableBalanceTextView = (TextView) this.findViewById(R.id.availableBalanceTextView);
        registeGiveMoneyTextView = (TextView) this.findViewById(R.id.registeGiveMoneyTextView);
        receivedPrincipalTextView = (TextView) this.findViewById(R.id.receivedPrincipalTextView);
        hqPrincipalTextView = (TextView) this.findViewById(R.id.hqPrincipalTextView);
        receivedInterestTextView = (TextView) this.findViewById(R.id.receivedInterestTextView);
        withdrawalsTextView = (TextView) this.findViewById(R.id.withdrawalsTextView);
        propertyTextView = (TextView) this.findViewById(R.id.propertyTextView);
    }

    private void initValue() {
        totalAssetsTextView.setText("0.00");

        availableBalanceTextView.setText("0.00 元");
        registeGiveMoneyTextView.setText("0.00 元");
        receivedPrincipalTextView.setText("0.00 元");
        hqPrincipalTextView.setText("0.00 元");
        receivedInterestTextView.setText("0.00 元");
        withdrawalsTextView.setText("0.00 元");
        propertyTextView.setText("0.00 元");
    }

    private void requestTotalMoneyInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_TOTALMONEY_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, TotalMoneyAppDto.class);
                    AppMessageDto<TotalMoneyAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        response();

                    } else {
                        Toast.makeText(TotalAssetsActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void response() {
        totalAssetsTextView.setText(infoDto.getTotalMoney());

        if (infoDto.getRegistGiveMoneyDay() <= 0) {
            this.findViewById(R.id.registeGiveMoneyLayout).setVisibility(View.GONE);
            this.findViewById(R.id.registeGiveMoneyLineView).setVisibility(View.GONE);
        } else {
            registeGiveMoneyTextView.setText(infoDto.getRegistGiveMoney() + "  元");
        }

        availableBalanceTextView.setText(infoDto.getSurplus() + "  元");
        receivedPrincipalTextView.setText(infoDto.getWaitPrincipal() + "  元");
        hqPrincipalTextView.setText(infoDto.getHqMoney() + "  元");
        receivedInterestTextView.setText(infoDto.getWaitEarnings() + "  元");
        withdrawalsTextView.setText(infoDto.getWithdraw() + "  元");
        propertyTextView.setText(infoDto.getWybMoney() + "  元");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

        }

    }

}
