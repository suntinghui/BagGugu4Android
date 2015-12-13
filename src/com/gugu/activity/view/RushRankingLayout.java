package com.gugu.activity.view;

import com.wufriends.gugu.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RushRankingLayout extends LinearLayout {

	private LinearLayout contentLayout;
	private TextView stagingRankingTextView;
	private TextView stagingTimeTextView;
	private TextView stagingRateTextView;
	private View lineView;

	public RushRankingLayout(Context context) {
		super(context);

		initView(context);
	}

	public RushRankingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_rush_ranking, this);

		contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
		contentLayout.setBackgroundColor(Color.parseColor("#ffffff"));

		stagingRankingTextView = (TextView) this.findViewById(R.id.stagingRankingTextView);
		stagingTimeTextView = (TextView) this.findViewById(R.id.stagingTimeTextView);
		stagingRateTextView = (TextView) this.findViewById(R.id.stagingRateTextView);
		stagingRateTextView.setTextColor(getResources().getColor(R.color.blueme));

		lineView = this.findViewById(R.id.lineView);
		lineView.setVisibility(View.GONE);
	}

	public void setValue(int index, String telphone, String rate) {
		stagingRankingTextView.setText(String.format("%02d", index));
		stagingTimeTextView.setText(telphone);
		stagingRateTextView.setText(rate + "%");
	}

}
