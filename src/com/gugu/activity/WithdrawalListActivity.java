package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.WithdrawalInfoAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.activity.view.WithdrawalListItemLayoutEx;
import com.gugu.model.BankEntityEx;
import com.wufriends.gugu.R;
import com.gugu.activity.view.WithdrawalListItemLayout;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.gugu.utils.BankUtil;

public class WithdrawalListActivity extends BaseActivity implements OnClickListener {

    private TextView messageTextView = null;
    private LinearLayout contentLayout = null;

    private WithdrawalListItemLayoutEx bankLayout = null;
    private WithdrawalListItemLayout hqLayout = null;
    private WithdrawalListItemLayout dtLayout = null;

    private WithdrawalInfoAppDto infoDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_withdrawal_list);

        initView();

        requestWithdrawalInfo();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("我要提现");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);

        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

        LayoutParams params103 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params103.setMargins(0, 0, 0, AdapterUtil.dip2px(this, 20));

        LayoutParams params100 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params100.setMargins(AdapterUtil.dip2px(this, 10), 0, AdapterUtil.dip2px(this, 10), AdapterUtil.dip2px(this, 20));

        LayoutParams params101 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        params101.setMargins(AdapterUtil.dip2px(this, 10), 0, AdapterUtil.dip2px(this, 10), AdapterUtil.dip2px(this, 20));

        bankLayout = new WithdrawalListItemLayoutEx(this);
        bankLayout.setTag(103);
        bankLayout.setOnClickListener(this);
        contentLayout.addView(bankLayout, params103);

        hqLayout = new WithdrawalListItemLayout(this);
        hqLayout.setTag(100);
        hqLayout.setOnClickListener(this);
        contentLayout.addView(hqLayout, params100);

        dtLayout = new WithdrawalListItemLayout(this);
        dtLayout.setTag(101);
        dtLayout.setOnClickListener(this);
        contentLayout.addView(dtLayout, params101);
    }

    private void requestWithdrawalInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.WITHDRAWAL_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, WithdrawalInfoAppDto.class);
                    AppMessageDto<WithdrawalInfoAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();

                        responseWithdrawalInfo();

                    } else {
                        Toast.makeText(WithdrawalListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseWithdrawalInfo() {
        // 新需求删除头部分信息！
        messageTextView.setVisibility(View.GONE);
        /**
         if (infoDto.getMessages().size() > 0) {
         timer.schedule(task, 0, 2500);
         messageTextView.setVisibility(View.VISIBLE);
         } else {
         messageTextView.setVisibility(View.GONE);
         }
         **/

        // 提现到银行卡
        bankLayout.getTypeTextView().setText("账户余额");
        bankLayout.getTypeTextView().setTextColor(getResources().getColor(R.color.blueme));
        bankLayout.getAmountTextView().setText(infoDto.getSurplusMoney());

        String bank_id = infoDto.getBankInfo().get("BANK_ID");
        if (null == bank_id || TextUtils.isEmpty(bank_id) || TextUtils.equals(bank_id, "null")) {
            // 没有绑定银行卡
            bankLayout.getBankImageView().setVisibility(View.GONE);
            bankLayout.getBankNameTextView().setText("没有绑定银行卡");
        } else {
            // 已经绑定银行卡
            bankLayout.getBankImageView().setVisibility(View.VISIBLE);

            BankEntityEx bank = BankUtil.getBankFromCode(bank_id, this);
            bankLayout.getBankImageView().setBackgroundResource(bank.getLogoId());
            bankLayout.getBankNameTextView().setText("尾号：" + infoDto.getBankInfo().get("BANK_CARD").substring(infoDto.getBankInfo().get("BANK_CARD").length() - 4));
        }

        // 活期
        hqLayout.getTypeTextView().setText("活期金额");
        hqLayout.getTypeTextView().setTextColor(getResources().getColor(R.color.orange));

        hqLayout.getTipTextView().setText("转出到");
        hqLayout.getTipTextView().setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.withdrawal_transfer), null);
        hqLayout.getTipTextView().setCompoundDrawablePadding(AdapterUtil.dip2px(this, 5));

        hqLayout.getDescTextView().setText("账户余额");
        hqLayout.getAmountTextView().setText(infoDto.getHqMoney());

        hqLayout.getBankNameTextView().setVisibility(View.GONE);
        hqLayout.getBankImageView().setVisibility(View.GONE);
        hqLayout.getGiveUpInterestTextView().setVisibility(View.VISIBLE);
        hqLayout.getGiveUpInterestTextView().setText("随时存取");

        // 定投
        dtLayout.getTypeTextView().setText("定投金额");
        dtLayout.getTypeTextView().setTextColor(getResources().getColor(R.color.greenme));

        dtLayout.getTipTextView().setText("赎回到");
        dtLayout.getTipTextView().setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.withdrawal_transfer), null);
        dtLayout.getTipTextView().setCompoundDrawablePadding(AdapterUtil.dip2px(this, 5));

        dtLayout.getDescTextView().setText("账户余额");
        dtLayout.getAmountTextView().setText(infoDto.getFixedDebtMoney());

        dtLayout.getBankNameTextView().setVisibility(View.GONE);
        dtLayout.getBankImageView().setVisibility(View.GONE);
        dtLayout.getGiveUpInterestTextView().setVisibility(View.VISIBLE);
        dtLayout.getGiveUpInterestTextView().setText("放弃利息\n共计" + infoDto.getFixedEarnings() + "元");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            default:
                if (v.getTag() == (Integer) 100) {
                    Intent intent = new Intent(this, CurrentTransferOutActivity.class);
                    this.startActivityForResult(intent, 100);

                } else if (v.getTag() == (Integer) 101) {
                    Intent intent = new Intent(this, WithdrawalQTActivity.class);
                    intent.putExtra("TYPE", WithdrawalQTActivity.TYPE_DT);
                    this.startActivityForResult(intent, 101);

                } else if (v.getTag() == (Integer) 102) {
                    Intent intent = new Intent(this, WithdrawalQTActivity.class);
                    intent.putExtra("TYPE", WithdrawalQTActivity.TYPE_QT);
                    this.startActivityForResult(intent, 102);

                } else if (v.getTag() == (Integer) 103) {
                    Intent intent = new Intent(this, WithdrawalsActivity.class);
                    this.startActivityForResult(intent, 103);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestWithdrawalInfo();
    }

    ////////////////////////////////////////////////////
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    int i = 0;
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    messageTextView.setText(infoDto.getMessages().get(i % infoDto.getMessages().size()));

                    in();
                    out();

                    i++;
                }

                break;
            }
            super.handleMessage(msg);
        }
    };

    private void in() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                messageTextView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(messageTextView);
            }
        }, 0);
    }

    private void out() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.SlideOutUp).duration(500).playOn(messageTextView);
            }
        }, 2200);
    }

    protected void onDestory() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
