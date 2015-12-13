package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.ares.baggugu.dto.app.PropertyTreasureAppDto;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.CurrentActivityEx;
import com.gugu.activity.PropertyActivity;
import com.wufriends.gugu.R;

public class InvestmentPropertyProgressLayout extends LinearLayout {

    private BaseActivity context;

    private FrameLayout rootLayout;
    private TextView amountTextView;
    private TextView earningsTextView;
    private TextView rateTextView;
    private TextView rewardRateTextView;

    public InvestmentPropertyProgressLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public InvestmentPropertyProgressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_investment_property_progress, this);

        rootLayout = (FrameLayout) this.findViewById(R.id.rootLayout);

        amountTextView = (TextView) this.findViewById(R.id.amountTextView);
        earningsTextView = (TextView) this.findViewById(R.id.earningsTextView);
        rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        rewardRateTextView = (TextView) this.findViewById(R.id.rewardRateTextView);
    }

    public void refreshView(final PropertyTreasureAppDto dto) {
        amountTextView.setText(dto.getPaidMoney() + "");
        earningsTextView.setText("当前收益: " + dto.getEarnings() + " 元");
        rateTextView.setText("收益率: " + dto.getRate() + "%");
        rewardRateTextView.setVisibility(View.GONE);

        rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PropertyActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
