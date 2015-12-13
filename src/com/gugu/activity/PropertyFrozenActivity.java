package com.gugu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.PropertyGuaranteeMoneyAppDto;
import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.gugu.activity.view.VerifyTransferPWDDialog;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

import com.android.volley.Response;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sth on 11/9/15.
 */
public class PropertyFrozenActivity extends BaseActivity implements View.OnClickListener {

    private TextView moneyTextView = null;
    private TextView statusTextView = null;
    private TextView frozenTipTextView = null;
    private Button commitBtn = null;

    private VerifyTransferPWDDialog verifyTransferPwdDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_property_frozen);

        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestStatus();
    }

    private void initView() {
        ((TextView) this.findViewById(R.id.titleTextView)).setText("物业宝");
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        this.statusTextView = (TextView) this.findViewById(R.id.statusTextView);
        this.frozenTipTextView = (TextView) this.findViewById(R.id.frozenTipTextView);
        this.commitBtn = (Button) this.findViewById(R.id.commitBtn);
        this.commitBtn.setOnClickListener(this);
        this.commitBtn.setEnabled(false);
    }

    private void requestStatus() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_PROPERTY_FEES, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, PropertyGuaranteeMoneyAppDto.class);
                    AppMessageDto<PropertyGuaranteeMoneyAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        moneyTextView.setText(dto.getData().getMoney() + " 元");
                        statusTextView.setText(dto.getData().isSubmit() ? "申请中" : "冻结中");
                        frozenTipTextView.setText("保障金在冻结期间，可以随时申请退出物业宝活动，" + ((PropertyTreasureAppDto) getIntent().getSerializableExtra("DTO")).getCompanyName() + "会在确认后3个工作日内将物业保障金退还业主。\n退还业主后，物业费减免活动自动取消。");
                        commitBtn.setEnabled(!dto.getData().isSubmit());
                    } else {
                        Toast.makeText(PropertyFrozenActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void commitAction() {
        verifyTransferPwdDialog = new VerifyTransferPWDDialog(this);
        verifyTransferPwdDialog.setTitle("确认解冻保障金吗？");
        verifyTransferPwdDialog.setTip("为保证账户资金安全，需验证交易密码。");
        verifyTransferPwdDialog.setOnConfirmListener(new VerifyTransferPWDDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String pwdStr) {
                requestCommit(pwdStr);
            }
        });
        verifyTransferPwdDialog.show();
    }

    private void requestCommit(String pwd) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("password", pwd);

        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_PROPERTY_FEES_REFUND, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(PropertyFrozenActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                        verifyTransferPwdDialog.dismiss();

                        PropertyFrozenActivity.this.finish();

                    } else {
                        verifyTransferPwdDialog.setError(dto.getMsg());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在提交数据...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.commitBtn: {
                commitAction();
            }
            break;
        }
    }
}
