package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wufriends.gugu.R;

// 提现专用

public class WithdrawalListItemLayout extends RelativeLayout {

    private TextView typeTextView = null; // 账户余额 抢定投本金
    private TextView tipTextView = null; // 提现到
    private TextView descTextView = null; // 银行卡 账户余额

    private TextView amountTextView = null;
    private TextView bankNameTextView = null;
    private ImageView bankImageView = null;
    private TextView giveUpInterestTextView = null;

    private ImageView rightArrowImageView = null;

    public WithdrawalListItemLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public WithdrawalListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private WithdrawalListItemLayout initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_withdrawal_list_item, this);

        this.typeTextView = (TextView) this.findViewById(R.id.typeTextView);
        this.tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        this.descTextView = (TextView) this.findViewById(R.id.descTextView);
        this.amountTextView = (TextView) this.findViewById(R.id.amountTextView);

        this.bankNameTextView = (TextView) this.findViewById(R.id.bankNameTextView);
        this.bankImageView = (ImageView) this.findViewById(R.id.bankImageView);
        this.giveUpInterestTextView = (TextView) this.findViewById(R.id.giveUpInterestTextView);
        this.rightArrowImageView = (ImageView) this.findViewById(R.id.rightArrowImageView);

        return this;
    }

    public TextView getTypeTextView() {
        return typeTextView;
    }

    public TextView getTipTextView() {
        return tipTextView;
    }

    public TextView getDescTextView() {
        return descTextView;
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

    public TextView getGiveUpInterestTextView() {
        return giveUpInterestTextView;
    }

}
