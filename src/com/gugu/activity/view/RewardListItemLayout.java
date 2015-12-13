package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wufriends.gugu.R;

public class RewardListItemLayout extends RelativeLayout implements OnTouchListener{
	
	private TextView titleTextView = null; // +2%
	private ImageView completeStatusImageView = null;
	private TextView detailTextView = null; // 月加息，送话费
	private TextView topTipTextView = null; // 进行中
	private ProgressBar progressBar = null;
	private TextView descTextView = null; // 最下面，21天内，均享受福利
	private ImageView rightArrowImageView = null; 

	public RewardListItemLayout(Context context) {
		super(context);

		this.initView(context);
	}
	
	public RewardListItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private RewardListItemLayout initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_reward_list_item, this);
		
		this.setOnTouchListener(this);
		
		this.titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		this.completeStatusImageView = (ImageView) this.findViewById(R.id.completeStatusImageView);
		this.detailTextView = (TextView) this.findViewById(R.id.detailTextView);
		this.topTipTextView = (TextView) this.findViewById(R.id.topTipTextView);
		
		this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		this.progressBar.setIndeterminate(false);
		this.progressBar.incrementProgressBy(1);
		this.progressBar.setMax(100);
		this.progressBar.setProgress(0);
		
		this.descTextView = (TextView) this.findViewById(R.id.descTextView);
		this.rightArrowImageView = (ImageView) this.findViewById(R.id.rightArrowImageView);
		
		return this;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			this.rightArrowImageView.setImageResource(R.drawable.reward_arrow_selected);
			
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			this.rightArrowImageView.setImageResource(R.drawable.reward_arrow_normal);
			
			 v.performClick();
		}
		
		return true;
	}

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public ImageView getCompleteStatusImageView() {
		return completeStatusImageView;
	}

	public TextView getDetailTextView() {
		return detailTextView;
	}

	public TextView getTopTipTextView() {
		return topTipTextView;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public TextView getDescTextView() {
		return descTextView;
	}

}
