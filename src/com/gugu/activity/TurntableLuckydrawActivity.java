package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.LotteryAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gugu.activity.view.LuckyDrawResultDialog;
import com.gugu.activity.view.LuckyPanView;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.LotteryModel;
import com.gugu.utils.LotteryUtil;
import com.wufriends.gugu.R;

// 转盘抽奖
public class TurntableLuckydrawActivity extends BaseActivity implements OnClickListener {

	private LinearLayout messageLayout;
	private TextView messageTextView;
	private List<String> messageList = new ArrayList<String>();

	private LuckyPanView mLuckyPanView;
	private ImageView mStartBtn;

	private Button scoreButton; // 当前积分
	private TextView tipTextView; // 邀请朋友获得积分，可享朋友理财0.5%返利\n摇身变土豪，躺着把钱赚

	private int integral = -1; // 当前剩余积分
	private int singelIntegral = 0; // 转盘一次需要多少积分

	// 音效播放
	private SoundPool pool;
	private int winSourceid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_turntable_luckydraw);

		initView();
		
		initSound();

		requestHistoryData();
	}

	private void initView() {
		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		((TextView) this.findViewById(R.id.titleTextView)).setText("积分抽奖");

		mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);

		messageLayout = (LinearLayout) this.findViewById(R.id.messageLayout);
		messageTextView = (TextView) this.findViewById(R.id.messageTextView);

		scoreButton = (Button) this.findViewById(R.id.scoreButton);
		scoreButton.setOnClickListener(this);

		tipTextView = (TextView) this.findViewById(R.id.tipTextView);
		tipTextView.setOnClickListener(this);

		mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
		mStartBtn.setEnabled(true);
		mStartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (integral < singelIntegral) {
					Toast.makeText(TurntableLuckydrawActivity.this, "积分不足", Toast.LENGTH_SHORT).show();

					mStartBtn.setEnabled(true);

				} else {
					mStartBtn.setEnabled(false);

					requestPick();
				}
			}
		});

	}
	
	private void initSound() {
		// 有人说如果调用load后直接调用play方法有时候会导致崩溃，所以安全起见，一开始就初始化好声音池。安全第一。
		// 指定声音池的最大音频流数目为10，声音品质为5
		pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// 载入音频流，返回在池中的id
		winSourceid = pool.load(this, R.raw.win, 0);
	}
	
	private void playMusic(int sourceid) {
		// 播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环;第六个参数为速率，速率 最低0.5最高为2，1代表正常速度
		pool.play(sourceid, 1, 1, 0, 0, 1);
	}

	// 转盘抽奖历史数据
	private void requestHistoryData() {
		JSONRequest request = new JSONRequest(this, RequestEnum.USER_TURNTABLE, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, LotteryAppDto.class);
					AppMessageDto<LotteryAppDto> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						responseData(dto.getData());

					} else {
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, null);
	}

	private void responseData(LotteryAppDto dto) {
		integral = dto.getIntegral();
		singelIntegral = dto.getSingelIntegral();

		tipTextView.setText("邀请朋友获得积分，可享朋友理财" + dto.getRate() + "%返利\n摇身变土豪，躺着把钱赚");

		if (integral < singelIntegral) {
			scoreButton.setText("邀请好友得积分");
		} else {
			scoreButton.setText("总积分 " + integral);
		}

		messageList = dto.getResult();
		if (messageList != null && messageList.size() > 0) {
			timer.schedule(task, 0, 2500);
		}
	}

	// 转盘抽奖
	private void requestPick() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", "2");

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_LOTTERY, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						requestHistoryData();

						LotteryModel lottery = LotteryUtil.getLottery(dto.getData());
						startLucky(lottery);

					} else {
						Toast.makeText(TurntableLuckydrawActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

						mStartBtn.setEnabled(true);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在初始化奖金池...");
	}

	private void startLucky(final LotteryModel model) {
		if (!mLuckyPanView.isStart()) {
			// mStartBtn.setImageResource(R.drawable.stop);
			mStartBtn.setImageResource(R.drawable.start);

			mLuckyPanView.luckyStart(model.getIndex());

			new Handler().postDelayed(new Runnable() {
				public void run() {
					if (!mLuckyPanView.isShouldEnd()) {
						mStartBtn.setImageResource(R.drawable.start);
						mLuckyPanView.luckyEnd();

						new ShowResultTask(mLuckyPanView, model).execute();

						mStartBtn.setEnabled(true);
					}
				}
			}, 2000);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.tipTextView:
		case R.id.scoreButton:
			inviteForShare();
			break;
		}

	}

	private void inviteForShare() {
		Intent intent = new Intent(this, InviteShareWebActivity.class);
		intent.putExtra("url", Constants.HOST_IP + "/yq/app");
		this.startActivity(intent);
	}

	// 纯粹就是为了等待停止的过程中不卡界面。
	class ShowResultTask extends AsyncTask {

		private LuckyPanView view;
		private LotteryModel lottery;

		public ShowResultTask(LuckyPanView view, LotteryModel lottery) {
			this.view = view;
			this.lottery = lottery;
		}

		@Override
		protected Object doInBackground(Object... params) {
			while (true) {
				if (view.getSpeed() <= 0) {
					return null;
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			if (this.lottery.getId().equals("QT_BONUS2") || this.lottery.getId().equals("MONEY1")) {
				// 播放音效
				playMusic(winSourceid);
			}
			
			if (this.lottery.getId().equals("NULL0")) {
				Toast.makeText(TurntableLuckydrawActivity.this, "没有中奖 -(-", Toast.LENGTH_SHORT).show();
				
			} else {
				LuckyDrawResultDialog dialog = new LuckyDrawResultDialog(TurntableLuckydrawActivity.this, this.lottery);
				dialog.show();
			}
		}

	}

	// /////////////////////////////////////
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
				messageTextView.setText(messageList.get(i % messageList.size()));

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

	protected void onDestory() {
		super.onDestroy();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
