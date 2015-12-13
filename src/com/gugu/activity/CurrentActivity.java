package com.gugu.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.gugu.activity.view.CircleProgress;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response;

/**
 * Created by sth on 9/2/15.
 */
public class CurrentActivity extends BaseActivity implements View.OnClickListener {

    private TextView totalEarningsTextView; // 累计收益
    private TextView buyMoneyTextView; // 总投资额
    private TextView yesterdayEarningsTextView; // 昨天收益
    private TextView investmentWhereTextView = null; // 投资去向，好逗逼的翻译
    private CircleProgress circleProgress = null;
    private TextView rateTextView = null; // 利息
    private TextView surplusMoneyTextView = null; // 今日可售
    private TextView minBuyTextView = null; // 起投金额


    private MyHQInfoAppDto infoDto = null;

    private Button investmentBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestHQInfo();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("活期理财");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        totalEarningsTextView = (TextView) this.findViewById(R.id.totalEarningsTextView);
        buyMoneyTextView = (TextView) this.findViewById(R.id.buyMoneyTextView);
        yesterdayEarningsTextView = (TextView) this.findViewById(R.id.yesterdayEarningsTextView);
        rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        surplusMoneyTextView = (TextView) this.findViewById(R.id.surplusMoneyTextView);
        minBuyTextView = (TextView) this.findViewById(R.id.minBuyTextView);

        circleProgress = (CircleProgress) this.findViewById(R.id.circleProgress);
        circleProgress.setType(CircleProgress.ARC);
        circleProgress.setPaintWidth(20);

        investmentWhereTextView = (TextView) this.findViewById(R.id.investmentWhereTextView);
        investmentWhereTextView.setOnClickListener(this);

        investmentBtn = (Button) this.findViewById(R.id.investmentBtn);
        investmentBtn.setOnClickListener(this);
    }

    // 我的活期理财详情
    private void requestHQInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_HQ_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MyHQInfoAppDto.class);
                    AppMessageDto<MyHQInfoAppDto> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        responseHQInfo(infoDto);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseHQInfo(MyHQInfoAppDto dto) {
        totalEarningsTextView.setText(dto.getTotalEarnings() + "");
        buyMoneyTextView.setText(dto.getBuyMoney() + "");
        yesterdayEarningsTextView.setText("昨日收益：" + dto.getYesterdayEarnings() + "元");
        surplusMoneyTextView.setText(dto.getSurplusMoney() + "元");
        minBuyTextView.setText(dto.getMinBuy() + "元");

        // 设置进度
        try {
            int progress = 100 - (int) (100 * Double.parseDouble(dto.getSurplusMoney()) / Double.parseDouble(dto.getTotalMoney()));
            setCircleProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();

            setCircleProgress(0);
        }

        // 设置利率
        try {
            Spannable span = new SpannableString(dto.getRate() + "%");
            int index = dto.getRate().indexOf(".");
            span.setSpan(new RelativeSizeSpan(2.0f), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new RelativeSizeSpan(0.8f), index, dto.getRate().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateTextView.setText(span);
        } catch (Exception e) {
            e.printStackTrace();

            rateTextView.setText(dto.getRate() + "%");
        }
    }

    private void requestBankInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.SECURITY_CENTER_BANK_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, Map.class);
                    AppMessageDto<Map<String, String>> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        HashMap<String, String> map = (HashMap<String, String>) dto.getData();
                        responseBankInfo(map);

                    } else {
                        Toast.makeText(CurrentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在查询请稍候...");
    }

    private void responseBankInfo(HashMap<String, String> map) {
        String bankId = map.get("BANK_ID");
        if (bankId == null || TextUtils.isEmpty(bankId) || TextUtils.equals(bankId, "null")) {
            Toast.makeText(this, "请先绑定银行卡", Toast.LENGTH_SHORT).show();

            // 没有绑定
            Intent intent = new Intent(this, BindingBankActivity.class);
            this.startActivity(intent);

        } else {
            // 已经绑定
            Intent intent = new Intent(this, CurrentTransferInActivity.class);
            intent.putExtra("DTO", infoDto);
            intent.putExtra("MAP", map);
            this.startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.investmentWhereTextView: { // 投资去向
                Intent intent = new Intent(this, CurrentInvestmentSourceActivity.class);
                intent.putExtra("id", infoDto.getDebtId() + "");
                this.startActivity(intent);
            }
            break;

            case R.id.investmentBtn: // 立即投资
                // 不再检查银行卡是否已经绑定
                // requestBankInfo();

                Intent intent = new Intent(this, CurrentTransferInActivity.class);
                intent.putExtra("DTO", infoDto);
                this.startActivityForResult(intent, 0);
                break;
        }
    }

    public void setCircleProgress(final int progress) {

        new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                for (int i = 0; i <= 100; i++) {
                    if (i > progress)
                        break;

                    publishProgress(i);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                circleProgress.setmSubCurProgress(values[0]);
            }
        }.execute(0);
    }
}
