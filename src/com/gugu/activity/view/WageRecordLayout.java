package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.WageListAppDto;
import com.wufriends.gugu.R;

/**
 * Created by sth on 11/9/15.
 */
public class WageRecordLayout extends LinearLayout {

    private TextView moneyTextView;
    private TextView timeTextView;
    private TextView companyTextView;

    public WageRecordLayout(Context context) {
        super(context);

        initView(context);
    }

    public WageRecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_wage_record, this);

        this.moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        this.timeTextView = (TextView) this.findViewById(R.id.timeTextView);
        this.companyTextView = (TextView) this.findViewById(R.id.companyTextView);
    }

    public void setValue(WageListAppDto dto) {
        this.moneyTextView.setText(dto.getMoney() + " 元");
        this.timeTextView.setText(dto.getTimeStr());
        this.companyTextView.setText(dto.getCompanyName());
    }
}
