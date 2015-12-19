package com.gugu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Property;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MediaReportAppDto;
import com.ares.baggugu.dto.app.Paginable;
import com.ares.baggugu.dto.app.PropertyDeductionAppDto;
import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.daimajia.easing.linear.Linear;
import com.gugu.activity.view.PropertyRecordLayout;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;

/**
 * Created by sth on 11/8/15.
 */
public class PropertyActivity extends BaseActivity implements View.OnClickListener {

    private TextView addressTextView = null;
    private TextView tipTextView = null;
    private TextView totalMoneyTextView = null;
    private TextView propertyStatusTextView = null;
    private TextView rateTextView = null;
    private TextView earningsTextView = null;

    private LinearLayout rechargeLayout = null;
    private LinearLayout earningsLayout = null;

    private TextView earningsStatusTextView = null;
    private ImageView earningTipImageView = null;

    private TextView frozenTipTextView = null;
    private LinearLayout contentLayout = null;
    private LinearLayout feeLayout = null;

    private PropertyTreasureAppDto infoDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_property);

        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestPropertyInfo();
    }

    private void initView() {
        ((TextView) this.findViewById(R.id.titleTextView)).setText("物业宝");
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.addressTextView = (TextView) this.findViewById(R.id.addressTextView);
        this.tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        this.totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);
        this.propertyStatusTextView = (TextView) this.findViewById(R.id.propertyStatusTextView);
        this.rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        this.earningsTextView = (TextView) this.findViewById(R.id.earningsTextView);

        this.rechargeLayout = (LinearLayout) this.findViewById(R.id.rechargeLayout);
        this.rechargeLayout.setOnClickListener(this);

        this.earningsLayout = (LinearLayout) this.findViewById(R.id.earningsLayout);
        this.earningsLayout.setOnClickListener(this);

        this.earningsStatusTextView = (TextView) this.findViewById(R.id.earningsStatusTextView);
        this.earningTipImageView = (ImageView) this.findViewById(R.id.earningTipImageView);

        frozenTipTextView = (TextView) this.findViewById(R.id.frozenTipTextView);
        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
        feeLayout = (LinearLayout) this.findViewById(R.id.feeLayout);
    }

    private void requestPropertyInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_PROPERTY_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, PropertyTreasureAppDto.class);
                    AppMessageDto<PropertyTreasureAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        responsePropertyInfo();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responsePropertyInfo() {
        this.addressTextView.setText(infoDto.getAddress());

        String tipStr = "您只需缴纳物业保障金<font color=#FF001A>" + infoDto.getTotalMoney() + "</font>元，即刻永久减免物业费，保障金冻结期间<font color=#FF001A>" + infoDto.getRate() + "%</font>年收益的日返息。";
        this.tipTextView.setText(Html.fromHtml(tipStr));

        this.totalMoneyTextView.setText(infoDto.getTotalMoney());
        this.rateTextView.setText(infoDto.getRate() + "%累计收益");
        this.earningsTextView.setText(infoDto.getEarnings());

        if (Double.parseDouble(infoDto.getPayMoney()) == 0.00) {
            this.propertyStatusTextView.setText("冻结中");
            this.propertyStatusTextView.setTextColor(Color.parseColor("#999999"));

            this.frozenTipTextView.setVisibility(View.GONE);
            this.contentLayout.setVisibility(View.VISIBLE);

            this.earningTipImageView.setVisibility(View.VISIBLE);
            this.earningsStatusTextView.setVisibility(View.VISIBLE);
            this.earningsLayout.setOnClickListener(this);

            this.requestPropertyDeduction();

        } else {
            this.propertyStatusTextView.setText("点击支付");
            this.propertyStatusTextView.setTextColor(getResources().getColor(R.color.greenme));

            this.frozenTipTextView.setText("保障金在冻结期间，可以随时申请退出物业宝活动，" + infoDto.getCompanyName() + "会在确认后3个工作日内将物业保障金退还业主。\n退还业主后，物业费减免活动自动取消。");
            this.frozenTipTextView.setVisibility(View.VISIBLE);
            this.contentLayout.setVisibility(View.GONE);

            this.earningTipImageView.setVisibility(View.INVISIBLE);
            this.earningsStatusTextView.setVisibility(View.INVISIBLE);
            this.earningsLayout.setOnClickListener(null);
        }
    }

    private void requestPropertyDeduction() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", "1");
        tempMap.put("pageSize", "100");

        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_PROPERTY_DEDUCTION, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, PropertyDeductionAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                    AppMessageDto<Paginable<PropertyDeductionAppDto>> dto = objectMapper.readValue(jsonObject, javaType);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responsePropertyDeduction(dto.getData().getList());
                    } else {
                        Toast.makeText(PropertyActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在提交数据...");
    }

    private void responsePropertyDeduction(List<PropertyDeductionAppDto> list) {
        this.feeLayout.removeAllViews();

        for (PropertyDeductionAppDto dto : list) {
            PropertyRecordLayout layout = new PropertyRecordLayout(this);
            layout.setValue(dto);
            this.feeLayout.addView(layout);
        }

        this.findViewById(R.id.noDataTextView).setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.rechargeLayout: {
                if (Double.parseDouble(infoDto.getPayMoney()) == 0.00) {
                    Intent intent = new Intent(this, PropertyFrozenActivity.class);
                    intent.putExtra("DTO", infoDto);
                    this.startActivity(intent);

                } else {
                    if (infoDto == null)
                        return;

                    Intent intent = new Intent(this, PropertyRechargeActivity.class);
                    intent.putExtra("DTO", infoDto);
                    this.startActivity(intent);
                }
            }
            break;

            case R.id.earningsLayout: {
                Intent intent = new Intent(this, CurrentInvestmentSourceActivity.class);
                intent.putExtra("id", infoDto.getDebtId() + "");
                this.startActivity(intent);
            }
            break;
        }
    }
}
