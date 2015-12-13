package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.gugu.activity.BaseActivity;
import com.wufriends.gugu.R;

/**
 * Created by sth on 9/6/15.
 */
public class MyInvestmentCurrentLayout extends LinearLayout {

    private BaseActivity context;

    private TextView typeTextView;
    private TextView moneyTextView;
    private TextView buyTimeTextView;
    private TextView rateTextView;
    private TextView addRateTextView;
    private TextView statusTextView;

    public MyInvestmentCurrentLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public MyInvestmentCurrentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_investment_current_layout, this);

        this.typeTextView = (TextView) this.findViewById(R.id.typeTextView);
        this.moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        this.buyTimeTextView = (TextView) this.findViewById(R.id.buyTimeTextView);
        this.rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        this.addRateTextView = (TextView) this.findViewById(R.id.addRateTextView);
        this.statusTextView = (TextView) this.findViewById(R.id.statusTextView);


        this.typeTextView.setText("活期");
        this.moneyTextView.setText("0.00 元");
        this.buyTimeTextView.setVisibility(View.INVISIBLE);
        this.rateTextView.setText("10%");
        this.addRateTextView.setVisibility(View.INVISIBLE);
        this.statusTextView.setText("活存活取");
        this.statusTextView.setTextColor(context.getResources().getColor(R.color.redme));
    }

    public void refreshData(MyHQInfoAppDto dto) {
        this.moneyTextView.setText(dto.getBuyMoney() + " 元");
        this.rateTextView.setText(dto.getRate() + "%");
    }
}
