package com.gugu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.Paginable;
import com.ares.baggugu.dto.app.WageInfoAppDto;
import com.ares.baggugu.dto.app.WageListAppDto;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.activity.view.WageRecordLayout;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sth on 11/8/15.
 */
public class WageActivity extends BaseActivity implements View.OnClickListener {

    private CustomNetworkImageView logoImageView = null;
    private TextView companyNameTextView = null;
    private TextView rateTextView = null;
    private TextView telphoneTextView = null;
    private TextView moneyTextView = null;
    private CircleImageView headImageView = null;
    private TextView realNameTextView = null;
    private LinearLayout wageLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_wage);

        this.initView();

        this.requestWageInfo();

        this.requestWageList();
    }

    private void initView() {
        ((TextView) this.findViewById(R.id.titleTextView)).setText("薪资宝");
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.logoImageView = (CustomNetworkImageView) this.findViewById(R.id.logoImageView);
        this.companyNameTextView = (TextView) this.findViewById(R.id.companyNameTextView);
        this.rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        this.telphoneTextView = (TextView) this.findViewById(R.id.telphoneTextView);
        this.moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        this.headImageView = (CircleImageView) this.findViewById(R.id.headImageView);
        this.realNameTextView = (TextView) this.findViewById(R.id.realNameTextView);
        this.wageLayout = (LinearLayout) this.findViewById(R.id.wageLayout);
    }

    private void requestWageInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_WAGE_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, WageInfoAppDto.class);
                    AppMessageDto<WageInfoAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        WageInfoAppDto infoDto = dto.getData();

                        logoImageView.setImageUrl(Constants.HOST_IP + infoDto.getCompanyLogo(), ImageCacheManager.getInstance().getImageLoader());
                        companyNameTextView.setText(infoDto.getCompanyName());
                        rateTextView.setText("工资到账直接进入" + infoDto.getRate() + "%活期中，您可以随时提现。");
                        telphoneTextView.setText("绑定账号：" + infoDto.getTelphone());
                        moneyTextView.setText(infoDto.getMoney());
                        headImageView.setImageURL(Constants.HOST_IP + infoDto.getUserLogo());
                        realNameTextView.setText(infoDto.getRealName());

                    } else {
                        Toast.makeText(WageActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void requestWageList() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", "1");
        tempMap.put("pageSize", "100");

        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_WAGE_LIST, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, WageListAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                    AppMessageDto<Paginable<WageListAppDto>> dto = objectMapper.readValue(jsonObject, javaType);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responsePropertyDeduction(dto.getData().getList());
                    } else {
                        Toast.makeText(WageActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在提交数据...");
    }

    private void responsePropertyDeduction(List<WageListAppDto> list) {
        this.wageLayout.removeAllViews();

        for (WageListAppDto dto : list) {
            WageRecordLayout layout = new WageRecordLayout(this);
            layout.setValue(dto);
            this.wageLayout.addView(layout);
        }

        this.findViewById(R.id.noDataTextView).setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
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
