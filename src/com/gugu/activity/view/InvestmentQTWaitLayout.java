package com.gugu.activity.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.MyDebtPackage;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.InvestmentActivity;
import com.wufriends.gugu.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sth on 8/24/15.
 * <p/>
 * 等待抢投
 */
public class InvestmentQTWaitLayout extends LinearLayout implements View.OnClickListener {

    private BaseActivity context;
    private InvestmentQTLayout parent;

    private TextView messageTextView;
    private TextView currentTextView;
    private TextView totalTextView;
    private Button confirmBtn;

    private ArrayList<String> messageList = new ArrayList<String>();

    public InvestmentQTWaitLayout(BaseActivity context, InvestmentQTLayout parent) {
        super(context);

        this.parent = parent;

        this.initView(context);
    }

    public InvestmentQTWaitLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_investment_qt_wait, this);

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);
        currentTextView = (TextView) this.findViewById(R.id.currentTextView);
        totalTextView = (TextView) this.findViewById(R.id.totalTextView);

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);

        messageList.add("标的售完后进入抢投倒计时");
        messageList.add("抢投时间为第二天12:00");

        timer.schedule(task, 0, 2500);
    }

    public void setData(MyDebtPackage dto) {
        currentTextView.setText(dto.getSoldCount() + "");
        totalTextView.setText(dto.getTotalCount() + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:

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
