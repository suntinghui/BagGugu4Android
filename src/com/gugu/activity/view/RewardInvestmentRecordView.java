package com.gugu.activity.view;

import com.wufriends.gugu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RewardInvestmentRecordView extends LinearLayout {
	
	private TextView numTextView = null;
	private TextView amountTextView = null;
	private TextView timeTextView = null;
	
	public RewardInvestmentRecordView(Context context) {
		super(context);

		this.initView(context);
	}

	public RewardInvestmentRecordView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private RewardInvestmentRecordView initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_reward_investment_record, this);
		
		this.numTextView = (TextView) this.findViewById(R.id.numTextView);
		this.amountTextView = (TextView) this.findViewById(R.id.amountTextView);
		this.timeTextView = (TextView) this.findViewById(R.id.timeTextView);
		
		return this;
	}

	public TextView getNumTextView() {
		return numTextView;
	}

	public TextView getAmountTextView() {
		return amountTextView;
	}

	public TextView getTimeTextView() {
		return timeTextView;
	}
	
}
