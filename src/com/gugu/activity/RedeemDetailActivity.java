package com.gugu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DebtTransterInfoDto;
import com.ares.baggugu.dto.app.PropertyGuaranteeMoneyAppDto;
import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;

/**
 * Created by sth on 5/13/16.
 */
public class RedeemDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView topImageView;
    private ImageView bgImageView;
    private TextView moneyTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_redeem_detail);

        this.initView();

        requestRedeemInfo();
    }

    private void initView(){
        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("赎回转让");

        topImageView = (ImageView) this.findViewById(R.id.topImageView);
        bgImageView = (ImageView) this.findViewById(R.id.bgImageView);
        moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        timeTextView = (TextView) this.findViewById(R.id.timeTextView);

        this.findViewById(R.id.doneBtn).setOnClickListener(this);
    }

    private void requestRedeemInfo(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", this.getIntent().getIntExtra("id", 0)+"");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_TRANSTER_INFO, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, DebtTransterInfoDto.class);
                    AppMessageDto<DebtTransterInfoDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        DebtTransterInfoDto infoDto = dto.getData();

                        moneyTextView.setText(infoDto.getMoney()+"");
                        timeTextView.setText(infoDto.isComplete()?infoDto.getCompleteTime():infoDto.getTime());
                        topImageView.setBackgroundResource(infoDto.isComplete() ? R.drawable.redeem_success : R.drawable.redeem_ing);
                        bgImageView.setBackgroundResource(infoDto.isComplete()?R.drawable.redeem_success_2:R.drawable.redeem_ing_2);

                    } else {
                        Toast.makeText(RedeemDetailActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
            case R.id.doneBtn:
                this.finish();
                break;
        }
    }
}
