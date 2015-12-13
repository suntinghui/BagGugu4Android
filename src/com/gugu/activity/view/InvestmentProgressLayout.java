package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ares.baggugu.dto.app.MyDebtPackage;
import com.wufriends.gugu.R;
import com.gugu.activity.BaseActivity;
import com.gugu.activity.VoteOfRushActivity;
import com.gugu.activity.VoteOfScheduledActivity;
import com.gugu.activity.VoteOfTransferActivity;
import com.gugu.utils.DateUtil;

public class InvestmentProgressLayout extends LinearLayout {

	private BaseActivity context;

	private FrameLayout rootLayout;
	private ProgressBar progressBar;
	private TextView dateTextView;
	private TextView waittingTextView;
	private TextView amountTextView;
	private TextView earningsTextView;
	private TextView rateTextView;
	private TextView rewardRateTextView;

	private Runnable progressRunnable = null;
	private int progressInit = 0;

	private Paint mPaint = null;

	public InvestmentProgressLayout(Context context) {
		super(context);

		this.initView(context);
	}

	public InvestmentProgressLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private void initView(Context context) {
		this.context = (BaseActivity) context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.item_investment_progress, this);

		rootLayout = (FrameLayout) this.findViewById(R.id.rootLayout);

		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		progressBar.setIndeterminate(false);
		progressBar.incrementProgressBy(1);
		progressBar.setMax(100);
		progressBar.setProgress(0);

		dateTextView = (TextView) this.findViewById(R.id.dateTextView);
		waittingTextView = (TextView) this.findViewById(R.id.waittingTextView);
		amountTextView = (TextView) this.findViewById(R.id.amountTextView);
		earningsTextView = (TextView) this.findViewById(R.id.earningsTextView);
		rateTextView = (TextView) this.findViewById(R.id.rateTextView);
		rewardRateTextView = (TextView) this.findViewById(R.id.rewardRateTextView);

		// must!!! 否则不执行onDraw()
		// this.setWillNotDraw(false);

		// initPaint();
	}

	private void initPaint() {
		this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPaint.setColor(Color.BLACK);
		this.mPaint.setTextSize(15);
		this.mPaint.setStyle(Style.FILL);
	}

	public void refreshView(final MyDebtPackage dto) {
		setProgressbar(dto.getProgress());
		dateTextView.setText(dto.getBeginDate() == null ? "--" : formatDate(dto.getBeginDate()));
		amountTextView.setText(dto.getPrincipal());
		earningsTextView.setText("当前收益: " + dto.getEarnings() + " 元");
		rateTextView.setText("收益率: " + dto.getRate() + "%");

		if (null == dto.getRewardRate()) {
			rewardRateTextView.setVisibility(View.GONE);
		} else {
			rewardRateTextView.setVisibility(View.VISIBLE);
			rewardRateTextView.setText("+" + dto.getRewardRate() + "%");
		}

		// 如果起息日期与今天日期相等，显示明日返息。否则显示等待返息。如果状为b返息中，则显示项目总周期。
		waittingTextView.setVisibility(View.VISIBLE);

		if (dto.getBeginDate() == null) {
			waittingTextView.setText("还未开始");
		} else if (dto.getBeginDate().equals(DateUtil.getCurrentDate())) {
			waittingTextView.setText("明日返息");
		} else {
			waittingTextView.setText("等待返息");
		}

		// 状态 a等待返息 b返息中 c完成
		if (dto.getStatus() == 'b') {
			waittingTextView.setText(dto.getPeriod() + "天");
		}

		rootLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;

				// 债权类型 a定投 b抢投 c转让
				if (dto.getType() == 'a') {
					intent = new Intent(context, VoteOfScheduledActivity.class);
				} else if (dto.getType() == 'b') {
					intent = new Intent(context, VoteOfRushActivity.class);
				} else if (dto.getType() == 'c') {
					intent = new Intent(context, VoteOfTransferActivity.class);
				}

				intent.putExtra("id", dto.getDpid() + "");
				context.startActivity(intent);
			}
		});
	}

	public String formatDate(String date) {
		try {
			StringBuffer sb = new StringBuffer(date);
			sb.insert(4, "-");
			sb.insert(7, "-");
			return sb.toString();
		} catch (Exception e) {
			return date;
		}
	}

	private void setProgressbar(final int progress) {
		final Handler handler = new Handler();
		progressRunnable = new Runnable() {
			@Override
			public void run() {
				progressBar.setProgress(++progressInit);

				InvestmentProgressLayout.this.invalidate();

				if (progressInit >= progress) {
					handler.removeCallbacks(progressRunnable);
					progressInit = 0;

				} else {
					handler.postDelayed(this, 2);
				}
			}
		};
		handler.postDelayed(progressRunnable, 500);
	}
}
