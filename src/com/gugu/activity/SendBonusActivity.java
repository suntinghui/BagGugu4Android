package com.gugu.activity;

import java.util.HashMap;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.gugu.activity.view.BonusFlyGoneDialog;
import com.gugu.activity.view.VerifyTransferPWDDialog;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

// 发红包
public class SendBonusActivity extends BaseActivity implements OnClickListener {

    private TextView balanceTextView;
    private EditText moneyEditText;
    private EditText remarkEditText;
    private Button sendBtn;
    private TextView nameTextView;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_bonuds);

        initView();

        requestBalance("正在请求数据...");
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("发红包");

        balanceTextView = (TextView) this.findViewById(R.id.balanceTextView);

        moneyEditText = (EditText) this.findViewById(R.id.moneyEditText);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);

        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        nameTextView.setText(this.getIntent().getStringExtra("name"));
        userName = this.getIntent().getStringExtra("name");
    }

    private void requestBalance(String msg) {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_SURPLUS_MONEY, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        balanceTextView.setText(dto.getData());

                    } else {
                        Toast.makeText(SendBonusActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, msg);
    }

    private void requestSendBonus(String pwdStr) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", this.getIntent().getStringExtra("userId"));
        map.put("money", this.moneyEditText.getText().toString());
        map.put("type", this.getIntent().getStringExtra("type"));
        map.put("sourceId", this.getIntent().getStringExtra("sourceId"));
        map.put("password", pwdStr);
        map.put("remark", remarkEditText.getText().toString());

        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_SEND_BONUS, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        BonusFlyGoneDialog dialog = new BonusFlyGoneDialog(SendBonusActivity.this, userName);
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                setResult(RESULT_OK);
                                SendBonusActivity.this.finish();
                            }
                        });
                        dialog.show();

                    } else {
                        Toast.makeText(SendBonusActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private boolean checkValue() {
        String money = this.moneyEditText.getText().toString();

        double dmoney = 0.00;
        try {
            dmoney = Double.parseDouble(money);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (money.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return false;

        } else if (dmoney <= 0 || dmoney > 200) {
            Toast.makeText(this, "限额0.01－200元", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showTransferPwd() {


        final VerifyTransferPWDDialog verifyTransferPwdDialog = new VerifyTransferPWDDialog(this);
        verifyTransferPwdDialog.setTitle("提示");
        verifyTransferPwdDialog.setTip(null);
        verifyTransferPwdDialog.setOnConfirmListener(new VerifyTransferPWDDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String pwdStr) {
                requestSendBonus(pwdStr);

                verifyTransferPwdDialog.dismiss();
            }
        });
        verifyTransferPwdDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.sendBtn:
                if (checkValue()) {
                    showTransferPwd();
                }
                break;
        }
    }

}
