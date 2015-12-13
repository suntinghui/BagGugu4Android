package com.gugu.activity.view;

import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.GrabOrderByAppDto;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.InvestmentActivity;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.DateUtil;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.Response;

/**
 * Created by sth on 8/24/15.
 * <p/>
 * 抢投开始抢
 */
public class InvestmentQTStartLayout extends RelativeLayout implements View.OnClickListener {

    private BaseActivity context;
    private InvestmentQTLayout parent;

    private TextView timeTextView;
    private TextView messageTextView;
    private Button confirmBtn;

    private Timer countDownTimer = null;
    private long countDown = -1;

    private MyDebtPackage dto;

    // 音效播放
    private SoundPool pool;
    private int moneySourceid;
    private int winSourceid;

    private ArrayList<String> messageList = new ArrayList<String>();

    public InvestmentQTStartLayout(BaseActivity context, InvestmentQTLayout parent) {
        super(context);

        this.parent = parent;

        this.initView(context);
    }

    public InvestmentQTStartLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_investment_qt_start, this);

        timeTextView = (TextView) this.findViewById(R.id.timeTextView);
        // 设置提醒我的字体
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "Helvetica Neue UltraLight.ttf");
        timeTextView.setTypeface(typeFace);

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setEnabled(false);

        initSound();

        messageList.add("开抢前5分钟会有系统提醒");
        messageList.add("手要快哦，祝您能抢到第一");

        timer.schedule(task, 0, 2500);
    }

    public void initSound() {
        // 有人说如果调用load后直接调用play方法有时候会导致崩溃，所以安全起见，一开始就初始化好声音池。安全第一。
        // 指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        // 载入音频流，返回在池中的id
        moneySourceid = pool.load(context, R.raw.money_down, 0);
        winSourceid = pool.load(context, R.raw.win, 0);
    }

    private void playMusic(int sourceid) {
        // 播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环;第六个参数为速率，速率 最低0.5最高为2，1代表正常速度
        pool.play(sourceid, 1, 1, 0, 0, 1);
    }

    public void setData(MyDebtPackage dto) {
        this.dto = dto;

        countDown = (dto.getGrabTime() - dto.getSysTime()) / 1000;

        countDownTimer = new Timer();
        countDownTimer.schedule(timingTask, 0, 1000);
    }

    TimerTask timingTask = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            timingHandler.sendMessage(message);
        }
    };

    Handler timingHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    countDown--;

                    if (countDown <= 0) {
                        timeTextView.setText("开抢啦~");
                        timeTextView.setTextColor(context.getResources().getColor(R.color.redme));

                        countDownTimer.cancel();
                        countDownTimer = null;

                        confirmBtn.setBackgroundResource(R.drawable.red_button_selector);
                        confirmBtn.setEnabled(true);

                        return;
                    }

                    timeTextView.setText(DateUtil.second2Time(countDown));
                    break;
            }
            super.handleMessage(msg);
        }

    };

    // 抢排名
    private void requestRushOrderby() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", "" + dto.getId());

        JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_GRAB_ORDERBY, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, GrabOrderByAppDto.class);
                    AppMessageDto<GrabOrderByAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        showRanking(dto.getData());

                        parent.requestData();

                    } else {
                        Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, "正在请求数据...");
    }

    private void showRanking(GrabOrderByAppDto orderDto) {
        RushRankingResultDialog dialog = new RushRankingResultDialog(context);
        dialog.setRank(orderDto.getOrderby());
        dialog.setRate(orderDto.getTotalRate());
        dialog.setRankList(orderDto.getOrderbys());

        dialog.show();

        if (orderDto.getOrderby() <= 1) { // 第一名
            playMusic(winSourceid);
        } else {
            playMusic(moneySourceid);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                requestRushOrderby();
                break;
        }
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
}
