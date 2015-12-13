package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.LinkInviteDebtAppDto;
import com.ares.baggugu.dto.app.LinkInviteDebtListAppDto;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

// 邀请投资统计
public class InviteInvestmentActivity extends BaseActivity implements OnClickListener {

	private TextView registedTextView; // 已投资
	private TextView unRegisteTextView; // 未投资

	private LinearLayout registedLayout;
	private LinearLayout unRegistedLayout;

	private ListView registedListView;
	private ListView unRegistedListView;

	private TextView integralTextView = null;

	private RegisteAdapter registedAdapter = null;
	private UnRegisteAdapter unRegistedAdapter = null;

	private List<LinkInviteDebtListAppDto> registedList = new ArrayList<LinkInviteDebtListAppDto>();
	private List<LinkInviteDebtListAppDto> unRegistedList = new ArrayList<LinkInviteDebtListAppDto>();

	private String totalMoney = "0.00";
	private String receiveEarnings = "0.00";
	private String rate = "0.00";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_invite_investment);

		initView();

		requestRegiste(true);
		requestRegiste(false);
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("好友投资统计");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		registedTextView = (TextView) this.findViewById(R.id.registedTextView);
		registedTextView.setOnClickListener(this);

		unRegisteTextView = (TextView) this.findViewById(R.id.unRegisteTextView);
		unRegisteTextView.setOnClickListener(this);

		registedLayout = (LinearLayout) this.findViewById(R.id.registedLayout);
		unRegistedLayout = (LinearLayout) this.findViewById(R.id.unRegistedLayout);

		integralTextView = (TextView) this.findViewById(R.id.integralTextView);

		registedListView = (ListView) this.findViewById(R.id.registedListView);
		registedAdapter = new RegisteAdapter(this);
		registedListView.setAdapter(registedAdapter);

		unRegistedListView = (ListView) this.findViewById(R.id.unRegistedListView);
		unRegistedAdapter = new UnRegisteAdapter(this);
		unRegistedListView.setAdapter(unRegistedAdapter);

		registedTextView.setSelected(true);
		unRegisteTextView.setSelected(false);
		registedLayout.setVisibility(View.VISIBLE);
		unRegistedLayout.setVisibility(View.GONE);

		LinearLayout emptyLayout = (LinearLayout) this.findViewById(R.id.emptyLayout);
		registedListView.setEmptyView(emptyLayout);
		ImageView noDataImageView = (ImageView) emptyLayout.findViewById(R.id.noDataImageView);
		noDataImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestRegiste(true);
			}
		});

		LinearLayout unEmptyLayout = (LinearLayout) this.findViewById(R.id.unEmptyLayout);
		unRegistedListView.setEmptyView(unEmptyLayout);
		ImageView unNoDataImageView = (ImageView) unEmptyLayout.findViewById(R.id.noDataImageView);
		unNoDataImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestRegiste(false);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.registedTextView:
			registedTextView.setSelected(true);
			unRegisteTextView.setSelected(false);
			registedLayout.setVisibility(View.VISIBLE);
			unRegistedLayout.setVisibility(View.GONE);
			break;

		case R.id.unRegisteTextView:
			registedTextView.setSelected(false);
			unRegisteTextView.setSelected(true);
			registedLayout.setVisibility(View.GONE);
			unRegistedLayout.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void requestRegiste(final boolean registe) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("isbuy", registe ? "true" : "false");

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_INVESTED, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, LinkInviteDebtAppDto.class);
					AppMessageDto<LinkInviteDebtAppDto> dto = objectMapper.readValue(jsonObject, type);

					if (registe) {
						if (dto.getStatus() == AppResponseStatus.SUCCESS) {
							registedList = dto.getData().getAppDtos();

							totalMoney = dto.getData().getTotalMoney();
							receiveEarnings = dto.getData().getReceiveEarnings();
							rate = dto.getData().getRate();

						} else {
							registedList = new ArrayList<LinkInviteDebtListAppDto>();

							Toast.makeText(InviteInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
						}

						registedAdapter.notifyDataSetChanged();

					} else {
						if (dto.getStatus() == AppResponseStatus.SUCCESS) {
							unRegistedList = dto.getData().getAppDtos();

							totalMoney = dto.getData().getTotalMoney();
							receiveEarnings = dto.getData().getReceiveEarnings();
							rate = dto.getData().getRate();

						} else {
							unRegistedList = new ArrayList<LinkInviteDebtListAppDto>();

							Toast.makeText(InviteInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
						}

						unRegistedAdapter.notifyDataSetChanged();
					}

					refreshTopInfo();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private void refreshTopInfo() {
		// 总投资额0.00元，获利按0.2%计算。为我赚得0.00元

		String text1 = "<font color=#999999>总投资额 </font><font color=#ff5000>" + totalMoney + " 元</font> <font color=#999999>，获利按" + rate + "%计算。</font> <br><font color=#999999>为我赚得</font> <font color=#ff5000>" + receiveEarnings + "元</font>";
		integralTextView.setText(Html.fromHtml(text1));
	}

	private void requestHintBuyDebt(int id) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("id", ""+id);

		JSONRequest request = new JSONRequest(this, RequestEnum.HINT_BUY_DEBT, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					Toast.makeText(InviteInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	// 已投资
	private class ViewHolder {
		private CustomNetworkImageView headImageView; // 头像
		private TextView nameTextView; // 姓名
		private TextView phoneNumTextView; // 手机号
		private TextView hqMoneyTextView; // 活期金额
		private TextView dtMoneyTextView; // 定投金额
		private TextView earningsTextView; // 到期获利
		private Button receiveBtn; // 领取
	}

	public class RegisteAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public RegisteAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return registedList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.layout_invite_haveinvestment, null);

				holder.headImageView = (CustomNetworkImageView) convertView.findViewById(R.id.headImageView);
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.phoneNumTextView = (TextView) convertView.findViewById(R.id.phoneNumTextView);
				holder.hqMoneyTextView = (TextView) convertView.findViewById(R.id.hqMoneyTextView);
				holder.dtMoneyTextView = (TextView) convertView.findViewById(R.id.dtMoneyTextView);
				holder.earningsTextView = (TextView) convertView.findViewById(R.id.earningsTextView);
				holder.receiveBtn = (Button) convertView.findViewById(R.id.receiveBtn);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final LinkInviteDebtListAppDto dto = registedList.get(position);

			holder.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);
			holder.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
			holder.headImageView.setLocalImageBitmap(R.drawable.fenqi_head_default);
			holder.headImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());

			holder.nameTextView.setText(dto.getName());
			holder.phoneNumTextView.setText(dto.getTelphone());
			holder.hqMoneyTextView.setText(dto.getHqMoney() + " 元");
			holder.dtMoneyTextView.setText(dto.getDtMoney() + "元");
			holder.earningsTextView.setText(dto.getEarning() + " 元");

			double earnings = Double.parseDouble(dto.getEarning());
			if (earnings == 0.00) { // 不能领取
				holder.receiveBtn.setBackgroundResource(R.drawable.gray_button_selector);
				holder.receiveBtn.setText(" 已领 ");
                holder.receiveBtn.setVisibility(View.INVISIBLE);

			} else {
				holder.receiveBtn.setBackgroundResource(R.drawable.blue_button_selector);
				holder.receiveBtn.setText(" 领取 ");
                holder.receiveBtn.setVisibility(View.VISIBLE);
				holder.receiveBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						requestReceiveEarnings(dto.getTelphone());
					}
				});
			}

			return convertView;
		}
	}

	// 未投资
	private class UnViewHolder {
		private CustomNetworkImageView headImageView; // 头像
		private TextView nameTextView; // 姓名
		private TextView phoneNumTextView; // 手机号
		private Button sendBoundButton; // 送红包
		private Button tellHimButton; // 送红包
	}

	public class UnRegisteAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public UnRegisteAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return unRegistedList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UnViewHolder holder = null;

			if (null == convertView) {
				holder = new UnViewHolder();

				convertView = mInflater.inflate(R.layout.layout_invite_noinvestment, null);

				holder.headImageView = (CustomNetworkImageView) convertView.findViewById(R.id.headImageView);
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.phoneNumTextView = (TextView) convertView.findViewById(R.id.phoneNumTextView);
				holder.sendBoundButton = (Button) convertView.findViewById(R.id.sendBoundButton);
				holder.tellHimButton = (Button) convertView.findViewById(R.id.tellHimButton);

				convertView.setTag(holder);
			} else {
				holder = (UnViewHolder) convertView.getTag();
			}

			final LinkInviteDebtListAppDto dto = unRegistedList.get(position);

			holder.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);
			holder.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
			holder.headImageView.setLocalImageBitmap(R.drawable.fenqi_head_default);
			holder.headImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());

			holder.nameTextView.setText(dto.getName());
			holder.phoneNumTextView.setText(dto.getTelphone());

			holder.sendBoundButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sendBonus(dto);
				}
			});

			holder.tellHimButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					requestHintBuyDebt(dto.getId());
				}
			});

			return convertView;
		}
	}

	// 领取
	private void requestReceiveEarnings(String telphone) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("telphone", telphone);

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_RECEIVE_EARNINGS, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {

						requestRegiste(true);

						Toast.makeText(InviteInvestmentActivity.this, "成功领取", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(InviteInvestmentActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private void sendBonus(LinkInviteDebtListAppDto dto) {
		Intent intent = new Intent(this, SendBonusActivity.class);
        intent.putExtra("name", dto.getName());
		intent.putExtra("userId", dto.getUserId() + "");
		intent.putExtra("sourceId", dto.getId() + "");
		intent.putExtra("type", "2");
		this.startActivity(intent);
	}
}
