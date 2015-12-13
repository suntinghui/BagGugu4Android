package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.WelfareAppDto;
import com.ares.baggugu.dto.app.WelfareInfoAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.RewardContactItemView;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.gugu.utils.Util;

/**
 * 月加息 废弃 由MyRewardRateActivity替换
 * 
 * @author sth
 * 
 */

@Deprecated
public class MyRewardRateActivity extends BaseActivity implements OnClickListener {
	
	private TextView valueTextView = null; // +2%
	private TextView tipTextView = null; // 每月邀请好友，每位加+0.2%，好友注册成功再送+0.2%，每月5位好友，共计可以增加利息2%。
	
	private TextView surplusDayTextView; // 可用 0 天
	private TextView addCountTextView; // 已邀请0位好友
	private TextView registCountTextView; // 好友中0位已注册

	private LinearLayout addContactLayout = null;
	private TextView inviteFriendTextView = null; // 邀请好友
	private ImageView addContactImageView = null; // 邀请好友的＋号

	private LinearLayout contactListLayout = null;

	private View topView1 = null;
	private View topView2 = null;
	private View topView3 = null;
	private View topView4 = null;
	private View topView5 = null;
	private View bottomView1 = null;
	private View bottomView2 = null;
	private View bottomView3 = null;
	private View bottomView4 = null;
	private View bottomView5 = null;

	// popup
	private PopupWindow tipPopup = null;
	private LinearLayout popRootLayout = null;
	private TextView popTipTextView = null;

	private Spinner contactSpinner;
	private static final int REQUEST_CONTACT = 0x104;
	// 手机通讯录
	private String phoneBook = "";

	private String userName = ""; // 选定的联系人的姓名
	private String telphone = ""; // 选定的联系人的手机号

	private WelfareInfoAppDto infoDto = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_reward_rate);

		initView();
	}
	
	private void initView() {
		((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
		((TextView) this.findViewById(R.id.titleTextView)).setText("月加息");
		
		valueTextView = (TextView) this.findViewById(R.id.valueTextView);
		tipTextView = (TextView) this.findViewById(R.id.tipTextView);
		
		surplusDayTextView = (TextView) this.findViewById(R.id.surplusDayTextView);
		addCountTextView = (TextView) this.findViewById(R.id.addCountTextView);
		registCountTextView = (TextView) this.findViewById(R.id.registCountTextView);
		contactSpinner = (Spinner) this.findViewById(R.id.contactSpinner);

		topView1 = this.findViewById(R.id.topView1);
		topView2 = this.findViewById(R.id.topView2);
		topView3 = this.findViewById(R.id.topView3);
		topView4 = this.findViewById(R.id.topView4);
		topView5 = this.findViewById(R.id.topView5);

		bottomView1 = this.findViewById(R.id.bottomView1);
		bottomView2 = this.findViewById(R.id.bottomView2);
		bottomView3 = this.findViewById(R.id.bottomView3);
		bottomView4 = this.findViewById(R.id.bottomView4);
		bottomView5 = this.findViewById(R.id.bottomView5);

		addContactLayout = (LinearLayout) this.findViewById(R.id.addContactLayout);
		addContactLayout.setOnClickListener(this);

		inviteFriendTextView = (TextView) this.findViewById(R.id.inviteFriendTextView);
		addContactImageView = (ImageView) this.findViewById(R.id.addContactImageView);

		contactListLayout = (LinearLayout) this.findViewById(R.id.contactListLayout);

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
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
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

		valueTextView.setText("+" + this.infoDto.getValue() + "%");
		// 必须是＋后面一位数变大
		Spannable span = new SpannableString(valueTextView.getText());
		;
		span.setSpan(new RelativeSizeSpan(1.8f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		valueTextView.setText(span);

		tipTextView.setText("每月邀请好友，每位加+" + infoDto.getSingleValue() + "%，好友注册成功再送+" + infoDto.getSingleValue() + "%，每月" + infoDto.getTotalCount() + "位好友，共计可以增加利息" + infoDto.getValue() + "%。");

		surplusDayTextView.setText(infoDto.getSurplusDay() + "");
		addCountTextView.setText(infoDto.getAddCount() + "");
		registCountTextView.setText(infoDto.getRegistCount() + "");

		// 刷新进度条
		this.refreshProgressBar();

		if (this.infoDto.getData().size() == 5) {
			this.inviteFriendTextView.setText("好友列表");
			this.addContactImageView.setVisibility(View.GONE);
			this.addContactLayout.setEnabled(false);

		} else {
			this.inviteFriendTextView.setText("邀请好友");
			this.addContactImageView.setVisibility(View.VISIBLE);
			this.addContactLayout.setEnabled(true);
		}

		// 设置好友列表
		this.refreshContactList();
	}

	private void refreshProgressBar() {
		int halfBlue = Color.parseColor("#9ADEF9");
		int blue = Color.parseColor("#00B0F1");

		int addCount = this.infoDto.getAddCount();
		int registCount = this.infoDto.getRegistCount();
		
		View anchorView = this.topView1;

		switch (addCount) {
		case 0:
			// 已经默认设置为全灰色
			break;

		case 1:
			this.bottomView1.setBackgroundColor(halfBlue);
			break;

		case 2:
			this.bottomView1.setBackgroundColor(halfBlue);
			this.bottomView2.setBackgroundColor(halfBlue);
			break;

		case 3:
			this.bottomView1.setBackgroundColor(halfBlue);
			this.bottomView2.setBackgroundColor(halfBlue);
			this.bottomView3.setBackgroundColor(halfBlue);
			break;

		case 4:
			this.bottomView1.setBackgroundColor(halfBlue);
			this.bottomView2.setBackgroundColor(halfBlue);
			this.bottomView3.setBackgroundColor(halfBlue);
			this.bottomView4.setBackgroundColor(halfBlue);
			break;

		case 5:
			this.bottomView1.setBackgroundColor(halfBlue);
			this.bottomView2.setBackgroundColor(halfBlue);
			this.bottomView3.setBackgroundColor(halfBlue);
			this.bottomView4.setBackgroundColor(halfBlue);
			this.bottomView5.setBackgroundColor(halfBlue);
			break;
		}

		switch (registCount) {
		case 0:
			// 已经默认设置为全灰色
			break;

		case 1:
			this.topView1.setBackgroundColor(blue);
			this.bottomView1.setBackgroundColor(blue);
			
			anchorView = this.topView1;
			break;

		case 2:
			this.topView1.setBackgroundColor(blue);
			this.bottomView1.setBackgroundColor(blue);
			this.topView2.setBackgroundColor(blue);
			this.bottomView2.setBackgroundColor(blue);
			
			anchorView = this.topView2;
			break;

		case 3:
			this.topView1.setBackgroundColor(blue);
			this.bottomView1.setBackgroundColor(blue);
			this.topView2.setBackgroundColor(blue);
			this.bottomView2.setBackgroundColor(blue);
			this.topView3.setBackgroundColor(blue);
			this.bottomView3.setBackgroundColor(blue);
			
			anchorView = this.topView3;
			break;

		case 4:
			this.topView1.setBackgroundColor(blue);
			this.bottomView1.setBackgroundColor(blue);
			this.topView2.setBackgroundColor(blue);
			this.bottomView2.setBackgroundColor(blue);
			this.topView3.setBackgroundColor(blue);
			this.bottomView3.setBackgroundColor(blue);
			this.topView4.setBackgroundColor(blue);
			this.bottomView4.setBackgroundColor(blue);
			
			anchorView = this.topView4;
			break;

		case 5:
			this.topView1.setBackgroundColor(blue);
			this.bottomView1.setBackgroundColor(blue);
			this.topView2.setBackgroundColor(blue);
			this.bottomView2.setBackgroundColor(blue);
			this.topView3.setBackgroundColor(blue);
			this.bottomView3.setBackgroundColor(blue);
			this.topView4.setBackgroundColor(blue);
			this.bottomView4.setBackgroundColor(blue);
			this.topView5.setBackgroundColor(blue);
			this.bottomView5.setBackgroundColor(blue);
			
			anchorView = this.topView5;
			break;
		}
		
		this.showPopTip(anchorView, registCount>0);
	}

	private void refreshContactList() {
		contactListLayout.removeAllViews();

		List<Map<String, String>> list = this.infoDto.getData();

		for (Map<String, String> map : list) {
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			RewardContactItemView itemView = new RewardContactItemView(this);
			contactListLayout.addView(itemView, params);

			map.put("signleValue", this.infoDto.getSingleValue());
			itemView.setData((HashMap<String, String>) map);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.addContactLayout:
			Intent intent_address = new Intent();
			intent_address.setAction(Intent.ACTION_PICK);
			intent_address.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent_address, REQUEST_CONTACT);

			break;
		}
	}

	private void requestAddConstact() {
		// 由于获取电话本的过程比较长，根据联系人多少的不同大约在2－5s之间，所以单独开一线程去处理。
		if (TextUtils.isEmpty(phoneBook)) {
			new processContactBookTask().execute();

		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("telphone", telphone);
			map.put("userName", userName);
			map.put("phoneBook", phoneBook);

			JSONRequest request = new JSONRequest(this, RequestEnum.FRIEND_ADD_1, map, new Response.Listener<String>() {

				@Override
				public void onResponse(String jsonObject) {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
						JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
						AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

						if (dto.getStatus() == AppResponseStatus.SUCCESS) {
							requestRewardInfo();
						} else {
							Toast.makeText(MyRewardRateActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			this.addToRequestQueue(request, "正在添加好友...");
		}

	}

	private void showPopTip(View anchorView, boolean blueFlag) {
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
			}

			this.popTipTextView.setText(this.infoDto.getCompleteTotal()+"%");
			
			// pop 如果没有全蓝，则pop弹出在第一个，否则跟随最后一个全蓝
			
			if (blueFlag) {
				this.popRootLayout.setBackgroundResource(R.drawable.reward_popup_blue);
				this.popTipTextView.setTextColor(getResources().getColor(R.color.white));
			} else {
				this.popRootLayout.setBackgroundResource(R.drawable.reward_popup_gray);
				this.popTipTextView.setTextColor(getResources().getColor(R.color.blueme));
			}
			
			int[] location = new int[2];
			anchorView.getLocationOnScreen(location);
			
			tipPopup.showAsDropDown(anchorView, 0, AdapterUtil.dip2px(this, -35));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onDestory(){
		super.onDestroy();
		
		tipPopup.dismiss();
		tipPopup = null;
	}

	// ///////////////////////////////

	private class processContactBookTask extends AsyncTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			MyRewardRateActivity.this.showProgress("正在处理数据...");
		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
				phoneBook = new ObjectMapper().writeValueAsString(Util.getContactList(MyRewardRateActivity.this));
			} catch (Exception e) {
				e.printStackTrace();
				phoneBook = "";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			MyRewardRateActivity.this.requestAddConstact();
		}

	}

	// 选择手机号
	private void chooseTelphone(String name, final ArrayList<String> list) {
		if (list.size() == 0)
			return;

		this.userName = name;

		if (list.size() == 1) {
			this.telphone = list.get(0);

			requestAddConstact();

			return;
		}

		final SpinnerAdapter adapter = new SpinnerAdapter(MyRewardRateActivity.this, list);
		contactSpinner.setAdapter(adapter);
		contactSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					telphone = list.get(position);
					requestAddConstact();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		contactSpinner.performClick();
	}

	private class ViewHolder {
		private TextView textView;
		private ImageView imageView;
	}

	public class SpinnerAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		private List<String> mList;
		private Context mContext;

		private int selectedIndex = 0;

		public SpinnerAdapter(Context pContext, List<String> pList) {
			this.mContext = pContext;
			this.mList = pList;

			// /////////////////////////
			this.mList.add(0, userName);

			this.mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
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
				convertView = mInflater.inflate(R.layout.spinner_item, null);

				holder.textView = (TextView) convertView.findViewById(R.id.textView);
				holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position != 0) {
				holder.textView.setText(mList.get(position));
				holder.imageView.setSelected(selectedIndex == position);
				holder.imageView.setPressed(selectedIndex == position);
			} else {
				holder.textView.setText(mList.get(position));
				holder.imageView.setVisibility(View.GONE);
			}

			return convertView;
		}

		public void setSelectedIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
		}
	}

	// 电话本返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
			if (data == null) {
				return;
			}

			String phoneNumber = null;
			String name = null;
			Uri contactData = data.getData();
			if (contactData == null) {
				return;
			}

			Cursor cursor = managedQuery(contactData, null, null, null, null);
			if (cursor.moveToFirst()) {
				String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				if (hasPhone.equalsIgnoreCase("1")) {
					hasPhone = "true";
				} else {
					hasPhone = "false";
				}

				if (Boolean.parseBoolean(hasPhone)) {
					ArrayList<String> phoneNumList = new ArrayList<String>();

					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
					while (phones.moveToNext()) {
						phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
						name = phones.getString(phones.getColumnIndex(PhoneLookup.DISPLAY_NAME)).replace(" ", "");

						phoneNumList.add(new String(phoneNumber));
					}

					phones.close();

					chooseTelphone(name, phoneNumList);
				}
			}

		}
	}
}
