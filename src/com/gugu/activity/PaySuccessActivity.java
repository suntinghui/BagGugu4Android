package com.gugu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gugu.utils.DateUtil;
import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.model.TransferInfo;
import com.gugu.utils.ActivityUtil;

public class PaySuccessActivity extends BaseActivity implements OnClickListener {

    private TextView timeTextView; // 投资总金额
    private TextView moneyTextView; // 余额支付金额
    private Button doneBtn;

    private TransferInfo transferInfo = null;

    private boolean shouldShake = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_success);

        // -1 不摇奖
        try {
            shouldShake = !("-1".equals(this.getIntent().getStringExtra("SHAKE")));
        } catch (Exception e) {
            e.printStackTrace();

            shouldShake = false;
        }

        transferInfo = (TransferInfo) this.getIntent().getSerializableExtra("INFO");

        initView();

        timeTextView.setText(DateUtil.getCurrentDateTime());
        moneyTextView.setText(transferInfo.getTransferMoney());
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("支付成功");

        timeTextView = (TextView) this.findViewById(R.id.timeTextView);
        moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);

        doneBtn = (Button) this.findViewById(R.id.doneBtn);
        doneBtn.setText((Constants.LuckyDraw && shouldShake) ? "去抽奖！" : "完    成");
        doneBtn.setOnClickListener(this);
    }

    public void onBackPressed() {
        this.setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.setResult(RESULT_OK);
                this.finish();
                break;

            case R.id.doneBtn:
                if (Constants.LuckyDraw && shouldShake) {
                    Intent intent = new Intent(this, ShakeshakeActivity.class);
                    this.startActivityForResult(intent, 0);

                } else {
                    this.setResult(RESULT_OK);
                    this.finish();
                }
                break;
        }

    }

}
