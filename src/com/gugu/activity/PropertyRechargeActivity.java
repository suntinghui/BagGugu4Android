package com.gugu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.gugu.activity.view.ConfirmPayDialog;
import com.gugu.client.Constants;
import com.gugu.model.TransferInfo;
import com.wufriends.gugu.R;

/**
 * Created by sth on 11/10/15.
 */
public class PropertyRechargeActivity extends BaseActivity implements View.OnClickListener {

    private TextView balanceTextView = null;
    private EditText moneyEditText = null;
    private TextView todayLimitTextView = null; // 今日可购买金额
    private TextView companyNameTextView = null;
    private Button investmentBtn = null;
    private TextView dateTextView = null; // 预计收益时间
    private TextView tipTextView = null;

    private PropertyTreasureAppDto infoDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_property_recharge);

        infoDto = (PropertyTreasureAppDto) this.getIntent().getSerializableExtra("DTO");

        initView();

        refreshData();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("物业宝转入");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        TextView bankLimitTextView = (TextView) this.findViewById(R.id.bankLimitTextView);
        bankLimitTextView.setOnClickListener(this);

        balanceTextView = (TextView) this.findViewById(R.id.balanceTextView);
        moneyEditText = (EditText) this.findViewById(R.id.moneyEditText);
        todayLimitTextView = (TextView) this.findViewById(R.id.todayLimitTextView);
        dateTextView = (TextView) this.findViewById(R.id.dateTextView);
        companyNameTextView = (TextView) this.findViewById(R.id.companyNameTextView);

        investmentBtn = (Button) this.findViewById(R.id.investmentBtn);
        investmentBtn.setOnClickListener(this);

        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
    }

    private void refreshData() {
        double bankPayDouble = Double.parseDouble(infoDto.getBankPayMoney());
        double surplusMoneyDouble = Double.parseDouble(infoDto.getSurplusMoney());
        double payMoneyDouble = Double.parseDouble(infoDto.getPayMoney());
        if (surplusMoneyDouble > payMoneyDouble) {
            moneyEditText.setText(infoDto.getPayMoney());
        } else {
            moneyEditText.setText(String.valueOf(bankPayDouble + surplusMoneyDouble));
        }

        balanceTextView.setText(infoDto.getSurplusMoney());
        todayLimitTextView.setText(infoDto.getPayMoney() + " 元");
        companyNameTextView.setText(infoDto.getCompanyName());
        dateTextView.setText(infoDto.getEarningDayStr());
        tipTextView.setText("提示：足额支付成功后，" + infoDto.getCompanyName() + "会在5个工作日内向您出具“物业保障金”收款回执单。");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.investmentBtn: {// 立即投资
                try {
                    ConfirmPayDialog dialog = new ConfirmPayDialog(this);
                    dialog.setTransferInfo(new TransferInfo(infoDto.getDebtId(), Double.parseDouble(moneyEditText.getText().toString()), Double.parseDouble(infoDto.getSurplusMoney())));
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(this, "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.bankLimitTextView: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "银行限额表");
                intent.putExtra("url", Constants.HOST_IP + "/app/bank.html");
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
