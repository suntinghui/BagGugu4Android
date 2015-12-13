package com.gugu.activity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DebtPackageInfo2AppDto;
import com.ares.baggugu.dto.app.DebtPackageInfoAppDto;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.wufriends.gugu.R;
import com.gugu.activity.view.ConfirmPayDialog;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.model.TransferInfo;
import com.gugu.utils.AdapterUtil;
import com.gugu.utils.MathUtil;
import com.gugu.utils.RateUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * 定投投资
 *
 * @author sth
 */
public class ScheduledInvestmentActivity extends BaseActivity implements OnClickListener, TextWatcher, OnTouchListener {

    private TextView claimsNumTextView; // 债权编码
    private TextView amountTextView; // 可投金额
    private TextView minMaxAmountTextView;
    private TextView maxAmountOfPartTextView; // 限购每份金额
    private TextView maxPartOfPersonTextView; // 限购每人多少份
    private TextView maxRateTextView; // 年化利率
    private TextView maxPeriodTextView; // 投资天数
    private TextView balanceTextView; // 余额
    private TextView rechargeBtn; // 充值
    private EditText moneyEditText; // 投资金额
    private TextView maxValueBtn;

    private TextView amountOfPartTextView; // 每份额度
    private ImageView subPartImageView; // 减份数
    private ImageView plusPartImageView; // 加份数
    private TextView currentPartTextView;// 当前份数

    private Button confirmBtn;
    private TextView protocolTextView;

    private DebtPackageInfoAppDto infoDto = null;

    private Spring subSpring = null;
    private Spring plusSpring = null;

    private View popupLocationView = null;
    private PopupWindow tipPopup = null;
    private TextView tipTextView = null;

    private int maxLimitCount = Integer.MAX_VALUE; // 最大购买份数
    private double maxMoney = Double.MAX_VALUE; // 最大投资金额

    private double totalMoney = 0.0; // 投资金额
    private double balanceMoney = 0.0; // 账户金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scheduled_investment);

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
        minMaxAmountTextView = (TextView) this.findViewById(R.id.minMaxAmountTextView);
        maxAmountOfPartTextView = (TextView) this.findViewById(R.id.maxAmountOfPartTextView);
        maxPartOfPersonTextView = (TextView) this.findViewById(R.id.maxPartOfPersonTextView);
        maxRateTextView = (TextView) this.findViewById(R.id.maxRateTextView);
        maxPeriodTextView = (TextView) this.findViewById(R.id.maxPeriodTextView);
        balanceTextView = (TextView) this.findViewById(R.id.balanceTextView);

        rechargeBtn = (TextView) this.findViewById(R.id.rechargeBtn);
        rechargeBtn.setOnClickListener(this);

        moneyEditText = (EditText) this.findViewById(R.id.moneyEditText);

        popupLocationView = this.findViewById(R.id.popupLocationView);

        maxValueBtn = (TextView) this.findViewById(R.id.maxValueBtn);
        maxValueBtn.setOnClickListener(this);

        amountOfPartTextView = (TextView) this.findViewById(R.id.amountOfPartTextView);

        subPartImageView = (ImageView) this.findViewById(R.id.subPartImageView);
        subPartImageView.setOnTouchListener(this);

        plusPartImageView = (ImageView) this.findViewById(R.id.plusPartImageView);
        plusPartImageView.setOnTouchListener(this);

        currentPartTextView = (TextView) this.findViewById(R.id.currentPartTextView);

        protocolTextView = (TextView) this.findViewById(R.id.protocolTextView);
        this.setClickableSpan();

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);

        reboundAnim();

        initMoney();

        this.setValue();
    }

    // 初始化的投资金额为1份的金额
    private void initMoney() {
        currentPartTextView.setText("01");
        moneyEditText.setText("￥" + infoDto.getLimitMoney());

        // must be here
        moneyEditText.addTextChangedListener(this);
    }

    private void setValue() {
        claimsNumTextView.setText(infoDto.getNum());
        // 类型 a定投 b抢投 c转让
        if (infoDto.getType() == 'b') {
            amountTextView.setText("￥" + this.calcMaxMoney());
        } else {
            amountTextView.setText("￥" + infoDto.getLimitMoney() + " - ￥" + this.calcMaxMoney());
        }

        maxAmountOfPartTextView.setText("￥" + infoDto.getLimitMoney() + "  /份");
        if (infoDto.getLimitCount() == 0) {
            maxPartOfPersonTextView.setText("不限份数");
        } else {
            maxPartOfPersonTextView.setText(this.calcMaxLimitCount() + "份  /人");
        }

        maxRateTextView.setText(infoDto.getMaxRate() + "%");
        maxPeriodTextView.setText(infoDto.getMaxPeriod() + " 天");
        amountOfPartTextView.setText("￥" + infoDto.getLimitMoney());
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

            case R.id.maxValueBtn:
                moneyEditText.setText("￥" + this.calcMaxMoney());
                currentPartTextView.setText(String.format("%02d", this.calcMaxLimitCount()));
                break;

            case R.id.subPartImageView:
                subPartValue();
                break;

            case R.id.plusPartImageView:
                plusPartValue();
                break;

            case R.id.confirmBtn:
                totalMoney = Double.parseDouble(moneyEditText.getText().toString().replace("￥", "").trim());

                ConfirmPayDialog dialog = new ConfirmPayDialog(this);
                dialog.setTransferInfo(new TransferInfo(infoDto.getId(), totalMoney, balanceMoney));
                dialog.show();
                break;
        }

    }

    private void subPartValue() {
        try {
            String temp = currentPartTextView.getText().toString();

            int current = Integer.parseInt(temp);

            if (current <= 1) {
                Toast.makeText(this, "亲，不能再小了", Toast.LENGTH_SHORT).show();
                return;
            }

            String newValue = String.format("%02d", --current);
            currentPartTextView.setText(newValue);
            moneyEditText.setText("￥" + this.calcMoney(current));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void plusPartValue() {
        String temp = currentPartTextView.getText().toString();

        try {
            int current = Integer.parseInt(temp);

            if (current >= this.calcMaxLimitCount()) {
                Toast.makeText(this, "您最多可购买" + this.calcMaxLimitCount() + "份", Toast.LENGTH_SHORT).show();
                moneyEditText.setText("￥" + this.calcMoney(current));
                return;
            }

            String newValue = String.format("%02d", ++current);
            currentPartTextView.setText(newValue);
            moneyEditText.setText("￥" + this.calcMoney(current));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算最大购买份数
     * <p/>
     * 不能直接使用DTO的最大份数，因为有可能最后剩余不足此份数。
     */
    private int calcMaxLimitCount() {
        if (maxLimitCount != Integer.MAX_VALUE)
            return maxLimitCount;

        double maxMoney = Double.parseDouble(infoDto.getMaxMoney());// 最大购买金额
        double limitMoney = Double.parseDouble(infoDto.getLimitMoney()); // 每份金额
        int maxCount = (int) (maxMoney / limitMoney);

        if (infoDto.getLimitCount() == 0) {
            maxLimitCount = maxCount;
            return maxCount;
        }

        maxLimitCount = Math.min(maxCount, infoDto.getLimitCount());
        return maxLimitCount;
    }

    /**
     * 计算最大投资金额
     * <p/>
     * 不能直接使用DTO的最大投资金额，因为那值表示整个项目的剩余投资金额。
     */
    private double calcMaxMoney() {
        if (maxMoney != Double.MAX_VALUE)
            return maxMoney;

        if (infoDto.getLimitCount() == 0) {
            maxMoney = Double.parseDouble(infoDto.getMaxMoney());
            return maxMoney;
        }

        int maxLimitCount = this.calcMaxLimitCount();
        double limitMoney = Double.parseDouble(infoDto.getLimitMoney()); // 每份金额
        maxMoney = maxLimitCount * limitMoney;
        return maxMoney;
    }

    /**
     * 计算当前份数下的投资金额
     *
     * @param currentCount
     * @return
     */
    private double calcMoney(int currentCount) {
        double limitMoney = Double.parseDouble(infoDto.getLimitMoney()); // 每份金额
        return currentCount * limitMoney;
    }

    /**
     * 计算每日增加收益
     *
     * @param principal    本金
     * @param yearRate     年化利率
     * @param monthAddRate 投资天数
     * @return
     */
    private String calcTotalPrincipalAndInterest(BigDecimal principal, BigDecimal yearRate, BigDecimal monthAddRate) {

        return MathUtil.moneyFormatDown(RateUtil.getDayEarnings(MathUtil.add(yearRate, monthAddRate), principal)).toString();
    }

    // 弹出本息合计
    private void showPopTip() {
        try {
            if (null == tipPopup) {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.layout_popup_tip, null);
                tipTextView = (TextView) layout.findViewById(R.id.tipTextView);

                tipPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                tipPopup.setFocusable(false);
                tipPopup.setBackgroundDrawable(new BitmapDrawable());
                tipPopup.setOutsideTouchable(false);
            }

            BigDecimal principal = new BigDecimal(moneyEditText.getText().toString().replace("￥", "").trim());
            BigDecimal yearRate = new BigDecimal(infoDto.getMaxRate());

            BigDecimal monthAddRate = infoDto.getRewardRate() != null ? new BigDecimal(infoDto.getRewardRate()) : BigDecimal.ZERO;


            tipTextView.setText(this.calcTotalPrincipalAndInterest(principal, yearRate, monthAddRate) + " 元");

            int[] location = new int[2];
            popupLocationView.getLocationOnScreen(location);

            tipPopup.showAtLocation(popupLocationView, Gravity.NO_GRAVITY, location[0] + AdapterUtil.dip2px(this, 100), location[1] - AdapterUtil.dip2px(this, 35));

            handler.postDelayed(runnable, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        public void run() {
            tipPopup.dismiss();
            handler.removeCallbacks(this);
        }
    };

    // 监听投资金额改变
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        showPopTip();
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
                        Toast.makeText(ScheduledInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

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

    // ///////////////////////////////

    private void reboundAnim() {
        SpringSystem springSys = SpringSystem.create();

        subSpring = springSys.createSpring();
        subSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.3f);

                ViewHelper.setScaleX(subPartImageView, scale);
                ViewHelper.setScaleY(subPartImageView, scale);
            }
        });

        plusSpring = springSys.createSpring();
        plusSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.3f);

                ViewHelper.setScaleX(plusPartImageView, scale);
                ViewHelper.setScaleY(plusPartImageView, scale);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int key = event.getAction();

        switch (key) {

            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.subPartImageView) {
                    subSpring.setEndValue(1.0);
                } else if (v.getId() == R.id.plusPartImageView) {
                    plusSpring.setEndValue(1.0);
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (v.getId() == R.id.subPartImageView) {

                    subSpring.setEndValue(0.0);

                    // 如果滑出控件则不触发事件
                    float aX = event.getRawX();
                    float aY = event.getRawY();

                    Rect bounds = new Rect();
                    subPartImageView.getGlobalVisibleRect(bounds);

                    if (aX > bounds.left && aX < bounds.right && aY < bounds.bottom && aY > bounds.top) {
                        subPartValue();
                    }

                } else if (v.getId() == R.id.plusPartImageView) {
                    plusSpring.setEndValue(0.0);

                    float aX = event.getRawX();
                    float aY = event.getRawY();

                    Rect bounds = new Rect();
                    plusPartImageView.getGlobalVisibleRect(bounds);

                    if (aX > bounds.left && aX < bounds.right && aY < bounds.bottom && aY > bounds.top) {
                        plusPartValue();
                    }
                }
                break;

            default:
                break;
        }
        return true;
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
            Intent intent = new Intent(ScheduledInvestmentActivity.this, ShowWebViewActivity.class);
            intent.putExtra("title", "鼓鼓投资协议");
            intent.putExtra("url", Constants.PROTOCOL_IP);
            ScheduledInvestmentActivity.this.startActivity(intent);
        }
    }

}
