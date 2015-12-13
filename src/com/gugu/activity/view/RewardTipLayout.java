package com.gugu.activity.view;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wufriends.gugu.R;

public class RewardTipLayout extends RelativeLayout {

	private TextView messageTextView = null;

	private ImageView roundImageView01 = null;
	private ImageView roundImageView02 = null;
	private ImageView roundImageView03 = null;

	private String[] messageArray = new String[] { "送福利", "送红包", "月加息", "送现金", "抽大奖" };

	public RewardTipLayout(Context context) {
		super(context);

		this.initView(context);
	}

	public RewardTipLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private RewardTipLayout initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_reward_tip, this);

		messageTextView = (TextView) this.findViewById(R.id.messageTextView);
		timer.schedule(task, 0, 2500);

		roundImageView01 = (ImageView) this.findViewById(R.id.roundImageView01);
		roundImageView02 = (ImageView) this.findViewById(R.id.roundImageView02);
		roundImageView03 = (ImageView) this.findViewById(R.id.roundImageView03);

		return this;
	}

	public RewardTipLayout setData(List<Integer> list) {
		roundImageView01.setImageResource(list.get(0) == 1 ? R.drawable.round_red : R.drawable.round_gray);
		roundImageView02.setImageResource(list.get(1) == 1 ? R.drawable.round_red : R.drawable.round_gray);
		roundImageView03.setImageResource(list.get(2) == 1 ? R.drawable.round_red : R.drawable.round_gray);
		return this;
	}

	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	int i = 0;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				messageTextView.setText(messageArray[i % messageArray.length]);

				in();
				out();

				i++;
			}

				break;
			}
			super.handleMessage(msg);
		}
	};

	private void in() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				messageTextView.setVisibility(View.VISIBLE);
				YoYo.with(Techniques.SlideInUp).duration(500).playOn(messageTextView);
			}
		}, 0);
	}

	private void out() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				YoYo.with(Techniques.SlideOutUp).duration(500).playOn(messageTextView);
			}
		}, 2200);
	}

	public void onDestory() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
