package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gugu.activity.BaseActivity;
import com.gugu.activity.WithdrawalsActivity;
import com.wufriends.gugu.R;

public class WithdrawalListItemLayoutEx extends RelativeLayout implements View.OnClickListener {

    private BaseActivity context = null;

    private TextView typeTextView = null; // 账户余额 抢定投本金
    private TextView amountTextView = null;
    private TextView bankNameTextView = null;
    private ImageView bankImageView = null;
    private Button commitBtn = null;

    public WithdrawalListItemLayoutEx(Context context) {
        super(context);

        this.initView(context);
    }

    public WithdrawalListItemLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private WithdrawalListItemLayoutEx initView(Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_withdrawal_list_item_ex, this);

        this.typeTextView = (TextView) this.findViewById(R.id.typeTextView);
        this.amountTextView = (TextView) this.findViewById(R.id.amountTextView);

        this.bankNameTextView = (TextView) this.findViewById(R.id.bankNameTextView);
        this.bankImageView = (ImageView) this.findViewById(R.id.bankImageView);

        this.commitBtn = (Button) this.findViewById(R.id.commitBtn);
        this.commitBtn.setOnClickListener(this);

        return this;
    }

    public TextView getTypeTextView() {
        return typeTextView;
    }

    public TextView getAmountTextView() {
        return amountTextView;
    }

    public TextView getBankNameTextView() {
        return bankNameTextView;
    }

    public ImageView getBankImageView() {
        return bankImageView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitBtn:
                Intent intent = new Intent(context, WithdrawalsActivity.class);
                context.startActivityForResult(intent, 103);
                break;
        }
    }
}
