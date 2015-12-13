package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.gugu.activity.MyRewardListExActivity;
import com.wufriends.gugu.R;
import com.gugu.activity.MyRewardListActivity;

public class RewardSystemTipDialog extends Dialog implements android.view.View.OnClickListener {

	private ImageView mainImageView;
	private ImageView closeImageView;

	private Activity context = null;

	private Button actionBtn = null;

	// 音效播放
	private SoundPool pool;
	private int dingdongSourceid;

	public RewardSystemTipDialog(Context context) {
		this(context, R.style.ProgressHUD);

	}

	public RewardSystemTipDialog(Context context, int theme) {
		super(context, theme);

		this.initView(context);
	}

	private void initView(Context context) {
		this.context = (Activity) context;

		this.setContentView(R.layout.layout_reward_system_tip);

		this.mainImageView = (ImageView) this.findViewById(R.id.mainImageView);
		this.mainImageView.setOnClickListener(this);

		this.closeImageView = (ImageView) this.findViewById(R.id.closeImageView);
		this.closeImageView.setOnClickListener(this);

		this.actionBtn = (Button) this.findViewById(R.id.actionBtn);
		this.actionBtn.setOnClickListener(this);

		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.dimAmount = 0.6f;
		this.getWindow().setAttributes(lp);

		try {
			// 有人说如果调用load后直接调用play方法有时候会导致崩溃，所以安全起见，一开始就初始化好声音池。安全第一。
			// 指定声音池的最大音频流数目为10，声音品质为5
			pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
			// 载入音频流，返回在池中的id
			dingdongSourceid = pool.load(context, R.raw.dingdong, 0);
			pool.play(dingdongSourceid, 1, 1, 0, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionBtn:
		case R.id.mainImageView:

			Intent intent = new Intent(this.context, MyRewardListExActivity.class);
			this.context.startActivity(intent);

			this.dismiss();
			break;

		case R.id.closeImageView:
			this.dismiss();
			break;
		}
	}
}
