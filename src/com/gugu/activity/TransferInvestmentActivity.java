package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DebtPackageInfo2AppDto;
import com.ares.baggugu.dto.app.DebtPackageInfoAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.ConfirmPayDialog;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.model.TransferInfo;

/**
 * 转让 收购债权
 *
 * @author sth
 */
public class TransferInvestmentActivity extends BaseActivity implements OnClickListener {

    private TextView claimsNumTextView; // 债权编码
    private TextView amountTextView; // 收购金额
    private TextView rewardTextView; // 收购奖励
    private TextView balanceTextView; // 余额
    private EditText moneyEditText; // 投资金额

    private Button confirmBtn;
    private TextView protocolTextView;

    private DebtPackageInfoAppDto infoDto = null;

    private double totalMoney = 0.0; // 投资金额
    private double balanceMoney = 0.0; // 账户金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transfer_investment);

        infoDto = (DebtPackageInfoAppDto) this.getIntent().getSerializableExtra("DTO");

        initView();
    }

    public void onResume() {
        super.onResume();

        this.requestBalance();
    }

    private void initView() {
        Button backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("钱包鼓起来");

        TextView bankLimitTextView = (TextView) this.findViewById(R.id.bankLimitTextView);
        bankLimitTextView.setOnClickListener(this);

        claimsNumTextView = (TextView) this.findViewById(R.id.claimsNumTextView);
        amountTextView = (TextView) this.findViewById(R.id.amountTextView);
        rewardTextView = (TextView) this.findViewById(R.id.rewardTextView);
        balanceTextView = (TextView) this.findViewById(R.id.balanceTextView);

        moneyEditText = (EditText) this.findViewById(R.id.moneyEditText);

        protocolTextView = (TextView) this.findViewById(R.id.protocolTextView);
        this.setClickableSpan();

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);

        this.setValue();

    }

    private void setValue() {
        claimsNumTextView.setText(infoDto.getNum());
        amountTextView.setText("￥" + infoDto.getMaxMoney());
        rewardTextView.setText("￥" + infoDto.getReward());
        moneyEditText.setText("￥" + infoDto.getMaxMoney());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.bankLimitTextView:
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "银行限额表");
                intent.putExtra("url", Constants.HOST_IP + "/app/bank.html");
                startActivity(intent);
                break;

            case R.id.confirmBtn:
                totalMoney = Double.parseDouble(infoDto.getMaxMoney());

                ConfirmPayDialog dialog = new ConfirmPayDialog(this);
                dialog.setTransferInfo(new TransferInfo(infoDto.getId(), totalMoney, balanceMoney));
                dialog.show();
                break;
        }

    }

    // 查询余额
    private void requestBalance() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", infoDto.getId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_BUYINFO_2, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, DebtPackageInfo2AppDto.class);
                    AppMessageDto<DebtPackageInfo2AppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        balanceMoney = Double.parseDouble(dto.getData().getMoney());

                        balanceTextView.setText("￥" + dto.getData().getMoney());
                    } else {
                        Toast.makeText(TransferInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

                        balanceTextView.setText("未知");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    // /////////////////////////////
    private void setClickableSpan() {
        String htmlLinkText = "点击确认投资即代表您同意《鼓鼓投资协议》";
        SpannableString spStr = new SpannableString(htmlLinkText);

        TouchableSpan clickSpan = new TouchableSpan();
        spStr.setSpan(clickSpan, 13, 19, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        protocolTextView.setText(spStr);
        protocolTextView.setMovementMethod(new LinkTouchMovementMethod());
    }

    private class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }

    public class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;
        private int mNormalBackgroundColor;
        private int mPressedBackgroundColor;
        private int mNormalTextColor;
        private int mPressedTextColor;

        public TouchableSpan() {
            this(Color.parseColor("#1caff6"), Color.parseColor("#8dd9fd"), Color.parseColor("#ffffff"), Color.parseColor("#999999"));
        }

        public TouchableSpan(int normalTextColor, int pressedTextColor, int mNormalBackgroundColor, int pressedBackgroundColor) {
            mNormalTextColor = normalTextColor;
            mPressedTextColor = pressedTextColor;
            mPressedBackgroundColor = pressedBackgroundColor;
        }

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
            ds.bgColor = mIsPressed ? mPressedBackgroundColor : mNormalBackgroundColor;
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(TransferInvestmentActivity.this, ShowWebViewActivity.class);
            intent.putExtra("title", "鼓鼓投资协议");
            intent.putExtra("url", Constants.PROTOCOL_IP);
            TransferInvestmentActivity.this.startActivity(intent);
        }
    }

}
