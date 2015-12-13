package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
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
import com.gugu.activity.MyInvestmentExActivity;
import com.gugu.client.Constants;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sth on 8/24/15.
 * <p/>
 * 抢完后的状态
 */
public class InvestmentQTDoneLayout extends LinearLayout implements View.OnClickListener {

    private BaseActivity context;
    private InvestmentQTLayout parent;

    private TextView messageTextView;
    private TextView rateTextView;
    private TextView rankingTextView;
    private Button confirmBtn;

    private ArrayList<String> messageList = new ArrayList<String>();

    public InvestmentQTDoneLayout(BaseActivity context, InvestmentQTLayout parent) {
        super(context);

        this.parent = parent;

        this.initView(context);
    }

    public InvestmentQTDoneLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_investment_qt_done, this);

        messageTextView = (TextView) this.findViewById(R.id.messageTextView);

        rateTextView = (TextView) this.findViewById(R.id.rateTextView);

        rankingTextView = (TextView) this.findViewById(R.id.rankingTextView);

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);

        messageList.add("可到“我的投资”查看详情");
        messageList.add("30天后可以转让");
        messageList.add("分享到朋友圈告诉您的朋友");

        timer.schedule(task, 0, 2500);
    }

    public void setData(MyDebtPackage dto) {
        rateTextView.setText(dto.getTotalRate());
        rankingTextView.setText(dto.getOrderby() + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn:
                ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, false).commit();

                Intent intent = new Intent(context, MyInvestmentExActivity.class);
                context.startActivityForResult(intent, 0);
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
