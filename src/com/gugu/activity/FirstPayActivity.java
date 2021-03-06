package com.gugu.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.gugu.model.BankEntityEx;
import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.model.TransferInfo;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.BankUtil;
import com.gugu.utils.IDCardValidate;

/**
 * 首次转账交易
 * <p/>
 * 已经废弃，采用首次绑定付款的方式完成。
 *
 * @author sth
 */

@Deprecated
public class FirstPayActivity extends BaseActivity implements OnClickListener {

    private TextView totalMoneyTextView; // 投资总金额
    private TextView balanceMoneyTextView; // 余额支付金额
    private TextView surplusMoneyTextView; // 银行卡支付金额

    private EditText realnameEditText;
    private EditText idCardEditText;
    private TextView bankTextView;
    private EditText bankNoEditText;
    private Button confirmBtn;
    private Spinner bankSpinner;
    private EditText telphoneEditText;
    private EditText codeEditText;
    private Button timeBtn;

    private List<BankEntityEx> bankList = null;

    private int selectedBankIndex = 0;

    private TransferInfo transferInfo = null;

    // 请求验证码返回的id值作为支付的债权包标识。
    private String reponseId = "";

    // 银行短信验证码时间为60s
    private int currentTime = Constants.SMS_MAX_TIME;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_pay);

        bankList = BankUtil.getBankList(this);
        if (bankList == null || bankList.size() == 0) {
            Toast.makeText(this, "正在初始化银行列表，请重试", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        transferInfo = (TransferInfo) this.getIntent().getSerializableExtra("INFO");

        initView();

        setValue();
    }

    private void initView() {
        try {
            TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
            titleTextView.setText("投资");

            Button backButton = (Button) this.findViewById(R.id.backBtn);
            backButton.setOnClickListener(this);

            confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
            confirmBtn.setOnClickListener(this);

            totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);
            balanceMoneyTextView = (TextView) this.findViewById(R.id.balanceMoneyTextView);
            surplusMoneyTextView = (TextView) this.findViewById(R.id.surplusMoneyTextView);

            realnameEditText = (EditText) this.findViewById(R.id.realnameEditText);
            idCardEditText = (EditText) this.findViewById(R.id.idCardEditText);

            bankTextView = (TextView) this.findViewById(R.id.bankTextView);
            bankTextView.setOnClickListener(this);
            bankTextView.setText(bankList.get(0).getName());
            bankTextView.setCompoundDrawables(getSelectedDrawable(bankList.get(0).getLogoId()), null, null, null);

            bankNoEditText = (EditText) this.findViewById(R.id.bankNoEditText);
            bankSpinner = (Spinner) this.findViewById(R.id.bankSpinner);

            telphoneEditText = (EditText) this.findViewById(R.id.telphoneEditText);

            codeEditText = (EditText) this.findViewById(R.id.codeEditText);

            timeBtn = (Button) this.findViewById(R.id.timeBtn);
            timeBtn.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "努力加载数据中，请稍候", Toast.LENGTH_SHORT).show();

            this.finish();
        }
    }

    private void setValue() {
        totalMoneyTextView.setText(transferInfo.getTransferMoney());
        balanceMoneyTextView.setText(transferInfo.getNeedBalanceStr(transferInfo.isUseBalance()));
        surplusMoneyTextView.setText(transferInfo.getSurplusMoneyStr(transferInfo.isUseBalance()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.setResult(RESULT_CANCELED);
                this.finish();
                break;

            case R.id.bankTextView:
                chooseBank();
                break;

            case R.id.timeBtn: // 发送短信验证码
                if (checkValueForRequestCode()) {
                    requestSendCode();
                }
                break;

            case R.id.confirmBtn: // 支付
                if (checkValueForRequestPay()) {
                    requestPay();
                }
                break;
        }
    }

    private boolean checkValueForRequestCode() {
        String realname = realnameEditText.getText().toString().trim();
        String idCard = idCardEditText.getText().toString().trim();
        String bank = bankTextView.getText().toString().trim();
        String bankNo = bankNoEditText.getText().toString().trim();
        String telphone = telphoneEditText.getText().toString().trim();

        String idValidate = "";
        try {
            idValidate = IDCardValidate.IDCardValidate(idCard);
        } catch (Exception e) {
            e.printStackTrace();
            idValidate = "身份证号输入错误";
        }

        if (TextUtils.isEmpty(realname)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(realnameEditText);
            return false;

        } else if (TextUtils.isEmpty(idCard)) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(idCardEditText);
            return false;

        } else if (!idValidate.equals("")) {
            Toast.makeText(this, idValidate, Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(idCardEditText);
            return false;
        } else if (TextUtils.isEmpty(bank)) {
            Toast.makeText(this, "请选择银行", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(bankTextView);
            return false;
        } else if (TextUtils.isEmpty(bankNo)) {
            Toast.makeText(this, "请输入银行卡号", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(bankNoEditText);
            return false;

        } else if (bankNo.length() < 16) {
            Toast.makeText(this, "银行卡号格式不正确", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(bankNoEditText);
            return false;
        } else if (TextUtils.isEmpty(telphone)) {
            Toast.makeText(this, "请输入预留的手机号", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(telphoneEditText);
            return false;
        } else if (telphone.length() < 11) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            ActivityUtil.shakeView(telphoneEditText);
            return false;
        }

        return true;
    }

    private boolean checkValueForRequestPay() {
        if (checkValueForRequestCode()) {
            String code = codeEditText.getText().toString().trim();

            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "请输入收到的短信验证码", Toast.LENGTH_SHORT).show();
                ActivityUtil.shakeView(codeEditText);
                return false;

            } else if (code.length() < 6) {
                Toast.makeText(this, "请输入6位短信验证码", Toast.LENGTH_SHORT).show();
                ActivityUtil.shakeView(codeEditText);
                return false;
            }

            return true;
        }

        return false;
    }

    private HashMap<String, String> getRequestMap() {
        String realname = realnameEditText.getText().toString().trim();
        String idCard = idCardEditText.getText().toString().trim();
        String bankNo = bankNoEditText.getText().toString().trim();
        String telphone = telphoneEditText.getText().toString().trim();

        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", transferInfo.getId());
        tempMap.put("money", transferInfo.getSurplusMoneyStr(transferInfo.isUseBalance()));
        tempMap.put("surplus", String.valueOf(transferInfo.isUseBalance()));
        tempMap.put("password", "");
        tempMap.put("realName", realname);
        tempMap.put("bankCardNum", bankNo);
        tempMap.put("telphone", telphone);
        tempMap.put("idCard", idCard);
        tempMap.put("bankId", bankList.get(selectedBankIndex).getCode());

        return tempMap;
    }

    private void requestSendCode() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DEBTPACKAGE_BUY_SENDVCODE, getRequestMap(), new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, Integer.class);
                    AppMessageDto<Integer> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(FirstPayActivity.this, "验证码已发送至您的手机", Toast.LENGTH_SHORT).show();

                        reponseId = String.valueOf(dto.getData());

                        // 启动定时器
                        startTimer();
                    } else {
                        Toast.makeText(FirstPayActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void requestPay() {
        final HashMap<String, String> map = this.getRequestMap();
        map.put("id", reponseId);
        map.put("vcode", codeEditText.getText().toString().trim());

        JSONRequest request = new JSONRequest(this, RequestEnum.DEBTPACKAGE_BUY, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        // 交易成功，跳转到成功界面
                        String cardNum = map.get("bankCardNum");
                        String bankName = bankList.get(selectedBankIndex).getName();
                        transferInfo.setTailNum(cardNum.substring(cardNum.length() - 4));
                        transferInfo.setBankName(bankName);

                        Intent intent = new Intent(FirstPayActivity.this, PaySuccessActivity.class);
                        intent.putExtra("INFO", transferInfo);
                        FirstPayActivity.this.startActivityForResult(intent, 0);

                    } else {
                        Toast.makeText(FirstPayActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在支付，请稍候...");
    }

    private void startTimer() {
        currentTime = Constants.SMS_MAX_TIME;
        timeBtn.setText(currentTime + " 秒后重发");
        timeBtn.setVisibility(View.VISIBLE);

        timeBtn.setEnabled(false);
        timeBtn.setTextColor(Color.parseColor("#999999"));

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }

        }, 1000, 1000);
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    if (currentTime > 0) {
                        timeBtn.setText(--currentTime + " 秒后重发");

                    } else {
                        timer.cancel();
                        timer = null;

                        timeBtn.setEnabled(true);
                        timeBtn.setTextColor(Color.parseColor("#ffffff"));
                        timeBtn.setText("重新发送");
                    }

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    // 选择关系
    private void chooseBank() {
        final SpinnerAdapter adapter = new SpinnerAdapter(this);
        // bankSpinner.setPrompt("请选择银行");
        bankSpinner.setAdapter(adapter);
        adapter.setSelectedIndex(0);
        bankSpinner.setSelection(selectedBankIndex);
        bankSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bankTextView.setText(bankList.get(position).getName());
                bankTextView.setCompoundDrawables(getSelectedDrawable(bankList.get(position).getLogoId()), null, null, null);
                selectedBankIndex = position;

                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bankSpinner.performClick();
    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView bankNameTextView;
    }

    public class SpinnerAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private Context mContext;

        public SpinnerAdapter(Context pContext) {
            this.mContext = pContext;

            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return bankList.size();
        }

        @Override
        public Object getItem(int position) {
            return bankList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (null == convertView) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.spinner_bank_item, null);

                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.bankNameTextView = (TextView) convertView.findViewById(R.id.bankNameTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.bankNameTextView.setText(bankList.get(position).getName());
            holder.bankNameTextView.setCompoundDrawables(getSelectedDrawable(bankList.get(position).getLogoId()), null, null, null);
            holder.contentLayout.setSelected(selectedIndex == position);

            return convertView;
        }

        private int selectedIndex = 0;

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

    }

    private Drawable getSelectedDrawable(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

}
