package com.gugu.activity;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.gugu.activity.view.LuckyDrawResultDialog;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.LotteryModel;
import com.gugu.utils.LotteryUtil;
import com.wufriends.gugu.R;

// 摇一摇抽奖
public class ShakeshakeActivity extends BaseActivity implements SensorEventListener, OnClickListener {

	private Button backBtn = null;

	private Vibrator vibrator;

	// 触发摇一摇的最小时间间隔
	private final int SHAKE_SHORTEST_TIME_INTERVAL = 5;
	// 传感器值变化的阀值
	private final int SHAKE_SHORTEST_SENSOR_VALUE = 19;
	private long lastShakeTime = 0;
	private SensorManager sensorManager;
	private Sensor sensor;

	// 音效播放
	private SoundPool pool;
	private int winSourceid;

	// 抽奖结果
	// INTEGRAL100 ⼀一百积分 QT_BONUS2 抢投红包两元 MONEY0.5 现⾦金五⽑毛 MONEY1 现⾦金⼀一元 NULL0 恭喜发财 INTEGRAL20 ⼆二⼗十积分 QT_BONUS5 抢投红包五元
	private String result = "NULL0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_shakeshake);

		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("摇一摇中大奖");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		initSound();
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

	@Override
	public void onClick(View v) {
		this.setResult(RESULT_OK);
		this.finish();
	}

	public void onBackPressed() {
		this.setResult(RESULT_OK);
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();

		sensorManager.unregisterListener(this, sensor);
	}

	public void onStop() {
		super.onStop();

		if (vibrator != null) {
			vibrator.cancel();
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		int type = event.sensor.getType();
		if (((currentTime - lastShakeTime) <= SHAKE_SHORTEST_TIME_INTERVAL) || (type != Sensor.TYPE_ACCELEROMETER)) {
			return;
		}

		lastShakeTime = currentTime;
		float[] values = event.values;
		if ((Math.abs(values[0]) > SHAKE_SHORTEST_SENSOR_VALUE || Math.abs(values[1]) > SHAKE_SHORTEST_SENSOR_VALUE || Math.abs(values[2]) > SHAKE_SHORTEST_SENSOR_VALUE)) {
			vibrator.vibrate(1000);

			// 去抽奖
			requestLuckyResult();
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	// 查询抽奖结果
	private void requestLuckyResult() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", 0 + ""); // 0摇⼀一摇 1转盘

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_LOTTERY, null, false, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, javaType);
					if (dto.getStatus() == AppResponseStatus.SUCCESS) {

						LotteryModel lottery = LotteryUtil.getLottery(dto.getData());
						showLuckyResult(lottery);

					} else {
						// Toast.makeText(ShakeshakeActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

						// 如果失败了就按中得20积分计算。呵呵
						LotteryModel lottery = LotteryUtil.getLottery("INTEGRAL20");
						showLuckyResult(lottery);
					}

				} catch (Exception e) {
					e.printStackTrace();

					setResult(RESULT_OK);
					finish();
				}

			}
		});

		this.addToRequestQueue(request, "正在派奖请稍候");
	}

	private void showLuckyResult(LotteryModel lottery) {
		if (lottery.getId().equals("QT_BONUS2") || lottery.getId().equals("QT_BONUS5")|| lottery.getId().equals("MONEY1")) {
			// 播放音效
			playMusic(winSourceid);
		}

		if (lottery.getId().equals("NULL0")) {
			Toast.makeText(ShakeshakeActivity.this, "没有中奖 -(-", Toast.LENGTH_SHORT).show();
			this.setResult(RESULT_OK);
			this.finish();

		} else {
			LuckyDrawResultDialog dialog = new LuckyDrawResultDialog(this, lottery);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			});
			dialog.show();
		}

	}

}
