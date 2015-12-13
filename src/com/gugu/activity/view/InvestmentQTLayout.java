package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DayEarningsInfoAppDto;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.EarningsActivity;
import com.gugu.activity.InvestmentActivity;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.List;

import com.android.volley.Response;

/**
 * Created by sth on 8/24/15.
 */
public class InvestmentQTLayout extends LinearLayout {

    private BaseActivity context;

    public InvestmentQTLayout(BaseActivity context) {
        super(context);

        this.initView(context);
    }

    public InvestmentQTLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_investment_qt, this);
    }

    public void requestData() {
        JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_WAIT_GRAB_ORDERBY, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, MyDebtPackage.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<List<MyDebtPackage>> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responseData(dto.getData());

                    } else {
                        Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, null);
    }

    private void responseData(List<MyDebtPackage> list) {
        LinearLayout contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
        contentLayout.removeAllViews();

        for (int i = 0; i < list.size(); i++) {

            LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, AdapterUtil.dip2px(context, i == 0 ? 20 : 10), 0, 0);

            MyDebtPackage dto = list.get(i);

            if (dto.getGrabTime() == -1) { // 竟买中 等待中
                InvestmentQTWaitLayout layout = new InvestmentQTWaitLayout(context, this);
                layout.setData(dto);
                contentLayout.addView(layout, params);

            } else if (dto.getGrabTime() == -2) { // 我抢完了
                InvestmentQTDoneLayout layout = new InvestmentQTDoneLayout(context, this);
                layout.setData(dto);
                contentLayout.addView(layout, params);

            } else { // 开抢
                InvestmentQTStartLayout layout = new InvestmentQTStartLayout(context, this);
                layout.setData(dto);
                contentLayout.addView(layout, params);
            }
        }
    }

}
