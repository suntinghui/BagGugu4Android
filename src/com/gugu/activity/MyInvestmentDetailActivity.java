package com.gugu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gugu.activity.view.TextTextLayout;
import com.gugu.model.MyDebtPackageEx;
import com.wufriends.gugu.R;

/**
 * Created by sth on 8/25/15.
 */
public class MyInvestmentDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout contentLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_investment_detail);

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("投资详情");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

        MyDebtPackageEx dto = (MyDebtPackageEx) this.getIntent().getSerializableExtra("DTO");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);

        // 债权编码
        TextTextLayout layout01 = new TextTextLayout(this);
        layout01.setData("债权编码", dto.getDpnum());
        layout01.setBgColor(false);
        contentLayout.addView(layout01, params);

        // 投资金额
        TextTextLayout layout02 = new TextTextLayout(this);
        layout02.setData("投资金额", dto.getPrincipal() + "元");
        layout02.setBgColor(true);
        layout02.getValueTextView().setTextColor(getResources().getColor(R.color.redme));
        contentLayout.addView(layout02, params);

        // 年化收益率
        TextTextLayout layout03 = new TextTextLayout(this);
        layout03.setData("年化收益率", dto.getTotalRate() + "%");
        layout03.getValueTextView().setTextColor(getResources().getColor(R.color.orange));
        layout03.setBgColor(false);
        contentLayout.addView(layout03, params);

        // 项目状态
        TextTextLayout layout04 = new TextTextLayout(this);
        layout04.setData("项目状态", dto.getStatusStr());
        layout04.getValueTextView().setTextColor(dto.getStatusColor());
        layout04.setBgColor(true);
        contentLayout.addView(layout04, params);

        // 投资类型
        TextTextLayout layout05 = new TextTextLayout(this);
        layout05.setData("投资类型", dto.getTypeStr());
        layout05.setBgColor(false);
        contentLayout.addView(layout05, params);

        // 还本时间
        TextTextLayout layout06 = new TextTextLayout(this);
        layout06.setData("还本时间", dto.getEndDate());
        layout06.getValueTextView().setTextColor(getResources().getColor(R.color.orangeme));
        layout06.setBgColor(true);
        contentLayout.addView(layout06, params);

        // 债权总额
        TextTextLayout layout07 = new TextTextLayout(this);
        layout07.setData("债权总额", dto.getTotalPrincipal() + "元");
        layout07.setBgColor(false);
        contentLayout.addView(layout07, params);

        // 项目周期
        TextTextLayout layout08 = new TextTextLayout(this);
        layout08.setData("项目周期", dto.getPeriod() + "天");
        layout08.setBgColor(true);
        contentLayout.addView(layout08, params);

        // 剩余还本时间
        TextTextLayout layout09 = new TextTextLayout(this);
        layout09.setData("剩余还本时间", dto.getSurplusDay() + "天");
        layout09.setBgColor(false);
        contentLayout.addView(layout09, params);

        // 红包奖励
        TextTextLayout layout10 = new TextTextLayout(this);
        layout10.setData("红包奖励", dto.getReward() + "元");
        layout10.setBgColor(true);
        contentLayout.addView(layout10, params);

        // 收息时间
        TextTextLayout layout11 = new TextTextLayout(this);
        layout11.setData("收息时间", "每天");
        layout11.setBgColor(false);
        contentLayout.addView(layout11, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
