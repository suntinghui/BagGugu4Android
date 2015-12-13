package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DayEarningsInfoAppDto;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.ares.baggugu.dto.app.Paginable;
import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.gugu.activity.view.InvestmentCurrentProgressLayout;
import com.gugu.activity.view.InvestmentPropertyProgressLayout;
import com.wufriends.gugu.R;
import com.gugu.activity.view.InvestmentProgressLayout;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnRefreshListener;

/**
 * 每日收益 －－ 投资统计（投资中总金额）
 *
 * @author sth
 */
public class InvestmentStatisticsActivity extends BaseActivity implements OnClickListener {

    private TextView totalInvestmentTextView = null; // 投资总额
    private TextView totalEarningsTextView = null; // 累计收益

    private LinearLayout demoLayout = null; // 指示性文字

    private LinearLayout contentLayout = null;

    private List<MyDebtPackage> debtList = new ArrayList<MyDebtPackage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_investment_statistics);

        initView();

        requestDayearningInfo(null);

        requestHQInfo();

        requestPropertyInfo();

        requesInvestmentList("正在请求数据...");
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("我的投资");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        totalInvestmentTextView = (TextView) this.findViewById(R.id.totalInvestmentTextView);
        totalEarningsTextView = (TextView) this.findViewById(R.id.totalEarningsTextView);

        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

        demoLayout = (LinearLayout) this.findViewById(R.id.demoLayout);

        /////////////

        this.findViewById(R.id.noDataImageView).setVisibility(View.GONE);
        demoLayout.setVisibility(View.VISIBLE);
        contentLayout.setGravity(Gravity.NO_GRAVITY);
    }

    private void requestDayearningInfo(String message) {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DAYEARNINGS_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, DayEarningsInfoAppDto.class);
                    AppMessageDto<DayEarningsInfoAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        totalInvestmentTextView.setText(dto.getData().getUsedPrincipal());
                        totalEarningsTextView.setText(dto.getData().getTotalEarnings());

                    } else {
                        Toast.makeText(InvestmentStatisticsActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, message);
    }

    // 不分页
    private void requesInvestmentList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", "1");
        tempMap.put("pageSize", Integer.MAX_VALUE + "");
        tempMap.put("type", "true"); // true只加载进⾏行中的 false全部的

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_LIST, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, MyDebtPackage.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<MyDebtPackage>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        debtList.clear();

                        debtList.addAll(dto.getData().getList());

                        responseInvestmentList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }, new ResponseErrorListener(this) {

            @Override
            public void todo() {
            }
        });

        if (!this.addToRequestQueue(request, msg)) {
        }
    }

    private void responseInvestmentList() {
        /*
        // 加入活期后，不能再隐藏头部

        if (debtList.size() > 0) {
            this.findViewById(R.id.noDataImageView).setVisibility(View.GONE);
            demoLayout.setVisibility(View.VISIBLE);

            contentLayout.setGravity(Gravity.NO_GRAVITY);

        } else {
            demoLayout.setVisibility(View.GONE);
        }
        */

        // 其他投资信息
        for (MyDebtPackage debt : debtList) {
            InvestmentProgressLayout layout = new InvestmentProgressLayout(this);
            layout.refreshView(debt);

            contentLayout.addView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
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
                        responseHQInfo(dto.getData());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseHQInfo(MyHQInfoAppDto dto) {
        // 活期信息
        InvestmentCurrentProgressLayout currentProgressLayout = new InvestmentCurrentProgressLayout(this);
        currentProgressLayout.refreshView(dto);
        contentLayout.addView(currentProgressLayout, 0, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    // 物业宝详情
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
                        responsePropertyInfo(dto.getData());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responsePropertyInfo(PropertyTreasureAppDto dto) {
        InvestmentPropertyProgressLayout propertyProgressLayout = new InvestmentPropertyProgressLayout(this);
        propertyProgressLayout.refreshView(dto);
        contentLayout.addView(propertyProgressLayout, 1, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backBtn) {
            this.finish();
        }

    }

}
