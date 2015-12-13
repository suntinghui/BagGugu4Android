package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.BonusAppDto;
import com.ares.baggugu.dto.app.BonusListAppDto;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

// 抢投红包
public class InviteQTActivity extends BaseActivity implements OnClickListener {

	private ListView listView = null;
	private ContactAdapter adapter = null;
	private List<BonusListAppDto> mList = new ArrayList<BonusListAppDto>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_invite_qt);

		initView();
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("抢投红包");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		listView = (ListView) this.findViewById(R.id.listView);
		adapter = new ContactAdapter(this);
		listView.setAdapter(adapter);

		ActivityUtil.setEmptyView(this, listView).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestQTList();
			}
		});

		requestQTList();
	}

	private void requestQTList() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isQt", "true");

		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_BONUS, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, BonusAppDto.class);
					AppMessageDto<BonusAppDto> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						mList = dto.getData().getAppDtos();
						
						adapter.notifyDataSetChanged();

					} else {
						Toast.makeText(InviteQTActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	// 领取
	private void requestReceive(int id) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id + "");

		JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_RECEIVE_BONUS, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Toast.makeText(InviteQTActivity.this, "领取成功", Toast.LENGTH_SHORT).show();

						requestQTList();

					} else {
						Toast.makeText(InviteQTActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private class ViewHolder {
		private ImageView leftImageView;
		private TextView nameTextView;
		private Button receiveButton;
	}

	public class ContactAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ContactAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mList.size();
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

				convertView = mInflater.inflate(R.layout.layout_invite_qt, null);

				holder.leftImageView = (ImageView) convertView.findViewById(R.id.leftImageView);
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.receiveButton = (Button) convertView.findViewById(R.id.receiveButton);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final BonusListAppDto dto = mList.get(position);

			double money = Double.parseDouble(dto.getMoney());
			if (money == 2) {
				holder.leftImageView.setBackgroundResource(R.drawable.invite_qt_money_2);
			} else if (money == 5) {
				holder.leftImageView.setBackgroundResource(R.drawable.invite_qt_money_5);
			} else if (money == 10) {
				holder.leftImageView.setBackgroundResource(R.drawable.invite_qt_money_10);
			} else {
				holder.leftImageView.setBackgroundResource(R.drawable.invite_qt_money_20);
			}

			holder.nameTextView.setText("\"" + dto.getName() + "\"");

			if (dto.isActivation()) {
				if (dto.isReceive()) {
					// 已经领取
					holder.receiveButton.setText("  已领  ");
					holder.receiveButton.setBackgroundResource(R.drawable.gray_button_selector);

				} else {
					// 可领
					holder.receiveButton.setText("  可领  ");
					holder.receiveButton.setBackgroundResource(R.drawable.blue_button_selector);
					holder.receiveButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							requestReceive(dto.getId());
						}
					});
				}

			} else {
				// 不可领
				holder.receiveButton.setText(" 抢投后可领 ");
				holder.receiveButton.setBackgroundResource(R.drawable.lightblue_button_selector);
			}

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.tellHimeTextView:

			break;
		}
	}

}
