package com.gugu.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.WelfareAppDto;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wufriends.gugu.R;
import com.gugu.activity.view.RewardListItemLayout;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;

/**
 * 已经废弃，由 MyRewardListExActivity 代替
 * 
 * @author sth
 * 
 */

@Deprecated
public class MyRewardListActivity extends BaseActivity implements OnClickListener {

	private TextView messageTextView = null;
	private LinearLayout contentLayout = null;

	private RewardListItemLayout rateLayout = null; // 月加息
	private RewardListItemLayout rechargeLayout = null; // 送话费

	private List<String> messageList = null;

	private int rateDtoId = 0; // 月加息的ID
	private int rechargeDtoId = 0; // 送话费的ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_reward);

		initView();

		requestMyRewardList();

		requestMyRewardMessage();
	}

	private void initView() {
		((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
		((TextView) this.findViewById(R.id.titleTextView)).setText("我的奖励");

		messageTextView = (TextView) this.findViewById(R.id.messageTextView);
		contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

		LinearLayout contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(AdapterUtil.dip2px(this, 10), AdapterUtil.dip2px(this, 20), AdapterUtil.dip2px(this, 10), 0);

		rateLayout = new RewardListItemLayout(this);
		rateLayout.setTag(100);
		rateLayout.setOnClickListener(this);
		contentLayout.addView(rateLayout, params);

		rechargeLayout = new RewardListItemLayout(this);
		rechargeLayout.setTag(101);
		rechargeLayout.setOnClickListener(this);
		contentLayout.addView(rechargeLayout, params);
	}

	private void requestMyRewardList() {
		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_LIST, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, WelfareAppDto.class);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
					AppMessageDto<List<WelfareAppDto>> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						responseRewardList(dto.getData());

					} else {
						Toast.makeText(MyRewardListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private void responseRewardList(List<WelfareAppDto> list) {
		for (WelfareAppDto dto : list) {
			// 1月加息 2送话费
			if (dto.getType() == 1) {
				rateDtoId = dto.getId();

				rateLayout.getTitleTextView().setText("+" + dto.getValue() + "%");
				rateLayout.getTitleTextView().setTextSize(25);

				// 必须是＋后面一位数变大
				Spannable span = new SpannableString(rateLayout.getTitleTextView().getText());
				;
				span.setSpan(new RelativeSizeSpan(1.8f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				rateLayout.getTitleTextView().setText(span);

				rateLayout.getDetailTextView().setText("月加息");
				rateLayout.getDescTextView().setText(dto.getSurplusDay() + "天内，均享受福利");

				float progress = 100.0f * dto.getSurplusDay() / dto.getTotalDay();
				rateLayout.getProgressBar().setProgress((int) progress);

				if (dto.isComplete()) {
					rateLayout.getCompleteStatusImageView().setImageResource(R.drawable.complete_status_selected);
				} else {
					rateLayout.getCompleteStatusImageView().setImageResource(R.drawable.complete_status_normal);
				}

				if (dto.getCompleteTotal().equalsIgnoreCase("0")) {
					rateLayout.getTopTipTextView().setText("尚未开始");
					rateLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.gray_1));
				} else if (dto.isComplete()) {
					rateLayout.getTopTipTextView().setText("已完成");
					rateLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.greenme));
				} else {
					rateLayout.getTopTipTextView().setText(dto.getCompleteTotal() + "%\n进行中...");
					rateLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.redme));
				}

			} else {
				rechargeDtoId = dto.getId();

				rechargeLayout.getTitleTextView().setText(dto.getValue() + "元");

				// 必须是两位数变大
				Spannable span = new SpannableString(rechargeLayout.getTitleTextView().getText());
				;
				span.setSpan(new RelativeSizeSpan(2.2f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				rechargeLayout.getTitleTextView().setText(span);

				rechargeLayout.getDetailTextView().setText("送话费");
				rechargeLayout.getDescTextView().setText("每月最多送一次，不足累计到下月");

				float progress = 0.0f;
				try {
					progress = 100.0f * Integer.parseInt(dto.getCompleteTotal()) / Integer.parseInt(dto.getValue());
				} catch (Exception e) {
					e.printStackTrace();
					progress = 0.0f;
				}
				rechargeLayout.getProgressBar().setProgress((int) progress);

				if (dto.isComplete()) {
					rechargeLayout.getCompleteStatusImageView().setImageResource(R.drawable.complete_status_selected);
				} else {
					rechargeLayout.getCompleteStatusImageView().setImageResource(R.drawable.complete_status_normal);
				}

				if (dto.isComplete()) {
					switch (dto.getStatus()) {
					case 'a':
						rechargeLayout.getTopTipTextView().setText("可领取");
						rechargeLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.redme));
						break;

					case 'b':
						rechargeLayout.getTopTipTextView().setText("已领取");
						rechargeLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.orange));
						break;

					case 'c':
						rechargeLayout.getTopTipTextView().setText("已完成");
						rechargeLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.orangeme));
						break;
					}
				} else {
					rechargeLayout.getTopTipTextView().setText(dto.getCompleteTotal() + "元\n进行中...");
					rechargeLayout.getTopTipTextView().setTextColor(getResources().getColor(R.color.redme));
				}
			}
		}
	}

	private void requestMyRewardMessage() {
		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_MSG, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, String.class);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
					AppMessageDto<List<String>> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						messageList = dto.getData();

						if (messageList != null) {
							timer.schedule(task, 0, 2500);
						}

					} else {
						Toast.makeText(MyRewardListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.backBtn) {
			this.finish();

		} else if (v.getTag() == (Integer) 100) {
			Intent intent = new Intent(this, MyRewardRateActivity.class);
			intent.putExtra("id", "" + rateDtoId);
			this.startActivity(intent);

		} else if (v.getTag() == (Integer) 101) {
			Intent intent = new Intent(this, MyRewardRechargeActivity.class);
			intent.putExtra("id", "" + rechargeDtoId);
			this.startActivity(intent);

		}

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
