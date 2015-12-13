package com.gugu.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.WelfareInfoAppDto;
import com.gugu.activity.view.RewardInvestmentRecordView;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.wufriends.gugu.R;

public class MyRewardRechargeActivity extends BaseActivity implements OnClickListener {

	private TextView valueTextView = null; // 20元
	private TextView tipTextView = null; // 本月投资10次可兑换话费，每次投资不计金额均算一次投资。每次投资送2元话费，满20元兑换一次。
	private TextView telphoneTextView = null; // 要充值的手机号码

	private Button applyBtn = null;

	private ProgressBar progressBar = null;
	private LinearLayout surplusDayLayout = null;
	private TextView surplusDayTextView = null; // 活动天后再次开启
	private TextView addCountTextView = null;// 话费奖励金额
	private LinearLayout recordsLayout = null; // 投资记录内容
	private TextView noRecordTextView = null; // 没有投资记录

	// popup
	private PopupWindow tipPopup = null;
	private LinearLayout popRootLayout = null;
	private TextView popTipTextView = null;

	private WelfareInfoAppDto infoDto = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_reward_recharge);

		initView();
	}

	private void initView() {
		((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
		((TextView) this.findViewById(R.id.titleTextView)).setText("送话费");

		valueTextView = (TextView) this.findViewById(R.id.valueTextView);
		tipTextView = (TextView) this.findViewById(R.id.tipTextView);
		telphoneTextView = (TextView) this.findViewById(R.id.telphoneTextView);

		applyBtn = (Button) this.findViewById(R.id.applyBtn);
		applyBtn.setOnClickListener(this);

		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		progressBar.setIndeterminate(false);
		progressBar.incrementProgressBy(1);
		progressBar.setMax(100);
		progressBar.setProgress(0);

		surplusDayLayout = (LinearLayout) this.findViewById(R.id.surplusDayLayout);
		surplusDayTextView = (TextView) this.findViewById(R.id.surplusDayTextView);
		addCountTextView = (TextView) this.findViewById(R.id.addCountTextView);
		recordsLayout = (LinearLayout) this.findViewById(R.id.recordsLayout);
		noRecordTextView = (TextView) this.findViewById(R.id.noRecordTextView);

		this.requestRewardInfo();
	}

	public void requestRewardInfo() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("welfareId", this.getIntent().getStringExtra("id"));

		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_INFO, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, WelfareInfoAppDto.class);
					AppMessageDto<WelfareInfoAppDto> dto = objectMapper.readValue(jsonObject, type);

					infoDto = dto.getData();

					responseRewadInfo();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private void responseRewadInfo() {
		if (this.infoDto == null)
			return;

		valueTextView.setText(this.infoDto.getValue() + "元");
		// 必须是两位数变大
		Spannable span = new SpannableString(valueTextView.getText());
		span.setSpan(new RelativeSizeSpan(2.0f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		valueTextView.setText(span);

		tipTextView.setText("本月投资" + infoDto.getTotalCount() + "次可兑换话费，每次投资不计金额均算一次投资。每次投资送" + infoDto.getSingleValue() + "元话费，满" + infoDto.getValue() + "元兑换一次。");
		telphoneTextView.setText(infoDto.getTelphone());

		if (infoDto.getStatus() == 'a') {
			surplusDayLayout.setVisibility(View.INVISIBLE);
		} else {
			surplusDayLayout.setVisibility(View.VISIBLE);
		}

		surplusDayTextView.setText(infoDto.getSurplusDay() + "");
		addCountTextView.setText(infoDto.getCompleteTotal() + "");

		float progress = 100.0f * infoDto.getAddCount() / infoDto.getTotalCount();
		this.progressBar.setProgress((int) progress);

		this.refreshButton();

		this.refreshRecordsList();

		this.showPopTip();
	}

	private void refreshButton() {
		if (infoDto.isComplete()) {
			switch (infoDto.getStatus()) {
			case 'a':
				this.applyBtn.setText("申请");
				this.applyBtn.setEnabled(true);
				break;

			case 'b':
				this.applyBtn.setText("已申请");
				this.applyBtn.setEnabled(false);
				break;

			case 'c':
				this.applyBtn.setText("成功");
				this.applyBtn.setEnabled(false);
				break;
			}

		} else {
			this.applyBtn.setText("投资中");
			this.applyBtn.setEnabled(false);
		}
	}

	private void refreshRecordsList() {
		List<Map<String, String>> list = infoDto.getData();

		if (list.size() == 0) {
			this.noRecordTextView.setVisibility(View.VISIBLE);
		} else {
			this.noRecordTextView.setVisibility(View.GONE);

			for (int i = 0; i < list.size(); i++) {
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				RewardInvestmentRecordView recordView = new RewardInvestmentRecordView(this);
				this.recordsLayout.addView(recordView, params);

				HashMap<String, String> tempMap = (HashMap<String, String>) list.get(i);
				recordView.getNumTextView().setText(i + 1 + "");
				recordView.getAmountTextView().setText(tempMap.get("money") + "元");
				recordView.getTimeTextView().setText(tempMap.get("time"));
			}
		}

	}

	// 申请
	private void requestApplay() {
		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_TELPHONE_MONEY, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Toast.makeText(MyRewardRechargeActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

						requestRewardInfo();

					} else {
						Toast.makeText(MyRewardRechargeActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在添加好友...");
	}

	private void showPopTip() {
		try {
			if (null == tipPopup) {
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.layout_reward_popup_tip, null);
				this.popRootLayout = (LinearLayout) layout.findViewById(R.id.rootLayout);
				this.popTipTextView = (TextView) layout.findViewById(R.id.tipTextView);

				tipPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				tipPopup.setFocusable(false);
				tipPopup.setBackgroundDrawable(new BitmapDrawable());
				tipPopup.setOutsideTouchable(false);
				popRootLayout.setBackgroundResource(R.drawable.reward_popup_blue);
				popTipTextView.setTextColor(getResources().getColor(R.color.white));
			}

			this.popTipTextView.setText(this.infoDto.getCompleteTotal() + "元");

			// pop 如果没有全蓝，则pop弹出在第一个，否则跟随最后一个全蓝
			int[] location = new int[2];
			this.progressBar.getLocationOnScreen(location);

			this.progressBar.getWidth();
			this.progressBar.getX();

			float progress = 1.0f * infoDto.getAddCount() / infoDto.getTotalCount();
			float x = this.progressBar.getWidth() * progress;

			tipPopup.showAsDropDown(this.progressBar, (int) x, AdapterUtil.dip2px(this, -45));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.applyBtn:
			this.requestApplay();
			break;

		}

	}

}
