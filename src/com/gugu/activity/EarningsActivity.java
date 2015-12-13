package com.gugu.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DayEarningsInfoAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.activity.view.BezierWaveView;
import com.gugu.activity.view.ProgressPercentLayout;
import com.wufriends.gugu.R;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.yuan.magic.MagicScrollView;
import com.yuan.magic.MagicTextView;

/**
 * 每日收益
 *
 * @author sth
 */
public class EarningsActivity extends BaseActivity implements OnClickListener {

    private TextView refreshTextView = null;
    private BezierWaveView waveView = null;
    private MagicScrollView magicScrollView = null;
    private MagicTextView totalEarningsTextView = null;// 累计收益
    private TextView millionEarningsTextView = null; // 万份收益
    private TextView yesterdayEarningsTextView = null;
    private TextView messageTextView = null; // 余额宝和银行的万份收益信息
    private TextView propertyEarningsTextView; // 物业宝累计收益

    private ProgressPercentLayout hqPrincipalProportionBar = null;
    private ProgressPercentLayout dtPrincipalProportionBar = null;
    private ProgressPercentLayout hqEarningsProportionBar = null;
    private ProgressPercentLayout dtEarningsProportionBar = null;

    private DayEarningsInfoAppDto infoDto = null;

    private ArrayList<String> messageList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_earnings);

        messageList.add("余额宝收益 ≈ 0.88元");
        messageList.add("银行定期 ≈ 0.79元");

        initView();
    }

    public void onResume() {
        super.onResume();

        requestDayearningInfo(null);
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("收益");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        refreshTextView = (TextView) this.findViewById(R.id.refreshTextView);
        refreshTextView.setOnClickListener(this);

        waveView = (BezierWaveView) this.findViewById(R.id.waveView);

        totalEarningsTextView = (MagicTextView) this.findViewById(R.id.totalEarningsTextView);
        totalEarningsTextView.setLargeFontSize(80);
        totalEarningsTextView.setSmallFontSize(80);
        totalEarningsTextView.setValue(0.00);

        magicScrollView = (MagicScrollView) this.findViewById(R.id.magicScrollView);

        millionEarningsTextView = (TextView) this.findViewById(R.id.millionEarningsTextView);
        millionEarningsTextView.setText("0.00");

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);

        yesterdayEarningsTextView = (TextView) this.findViewById(R.id.yesterdayEarningsTextView);
        yesterdayEarningsTextView.setText("0.00");

        propertyEarningsTextView = (TextView) this.findViewById(R.id.propertyEarningsTextView);
        propertyEarningsTextView.setText("0.00");

        hqPrincipalProportionBar = (ProgressPercentLayout) this.findViewById(R.id.hqPrincipalProportionBar);
        hqPrincipalProportionBar.setType(ProgressPercentLayout.TYPE_BLUE);

        dtPrincipalProportionBar = (ProgressPercentLayout) this.findViewById(R.id.dtPrincipalProportionBar);
        dtPrincipalProportionBar.setType(ProgressPercentLayout.TYPE_RED);

        hqEarningsProportionBar = (ProgressPercentLayout) this.findViewById(R.id.hqEarningsProportionBar);
        hqEarningsProportionBar.setType(ProgressPercentLayout.TYPE_BLUE);

        dtEarningsProportionBar = (ProgressPercentLayout) this.findViewById(R.id.dtEarningsProportionBar);
        dtEarningsProportionBar.setType(ProgressPercentLayout.TYPE_RED);

        timer.schedule(task, 0, 2500);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int[] location = new int[2];

            totalEarningsTextView.getLocationInWindow(location);
            totalEarningsTextView.setLocHeight(location[1]);

            magicScrollView.sendScroll(MagicScrollView.UP, 0);
        }

        ;
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.refreshTextView:
                requestDayearningInfo("正在请求数据...");
                break;

        }
    }

    private void requestDayearningInfo(String message) {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DAYEARNINGS_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, DayEarningsInfoAppDto.class);
                    AppMessageDto<DayEarningsInfoAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        infoDto = dto.getData();
                        response();

                    } else {
                        Toast.makeText(EarningsActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, message);
    }

    private void response() {
        try {
            if (infoDto.isShow()) {
                double totalEarnings = Double.parseDouble(infoDto.getTotalEarnings());
                // 只有当数字大于0.10的时候，才会有涨动的动画，而且，如果小于0.10，金额会显示为0.00，且界面卡动。
                if (totalEarnings >= 0.10) {
                    totalEarningsTextView.setValue(totalEarnings);
                    magicScrollView.AddListener(totalEarningsTextView);
                    mHandler.sendEmptyMessageDelayed(0, 100);
                } else {
                    totalEarningsTextView.setText(infoDto.getEarnings());
                }

            } else {
                totalEarningsTextView.setText("正在清算");
            }
        } catch (Exception e) {
            e.printStackTrace();

            totalEarningsTextView.setText("0.00");
        }

        yesterdayEarningsTextView.setText(infoDto.getEarnings());
        millionEarningsTextView.setText(infoDto.getTenthousandEarnings());
        totalEarningsTextView.setText(infoDto.getTotalEarnings());

        hqPrincipalProportionBar.setProgress(infoDto.getHqPrincipalProportion());
        dtPrincipalProportionBar.setProgress(infoDto.getDtPrincipalProportion());
        hqEarningsProportionBar.setProgress(infoDto.getHqEarningsProportion());
        dtEarningsProportionBar.setProgress(infoDto.getDtEarningsProportion());

        messageList.clear();
        messageList.add("余额宝收益 ≈ " + infoDto.getYuebaoEarnings() + "元");
        messageList.add("银行定期 ≈ " + infoDto.getBankEarnings() + "元");

        propertyEarningsTextView.setText(infoDto.getWybEarnings());
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    // ////////////////////

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
                    try {
                        messageTextView.setText(messageList.get(i % messageList.size()));

                        in();
                        out();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    public void onDestory() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // /////////////////

}
