package com.gugu.activity;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.FriendAppDto;
import com.ares.baggugu.dto.app.FriendGroupAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.FriendsAdapter;
import com.gugu.activity.view.FriendsAdapter.ViewHolder;
import com.gugu.client.ActivityManager;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnRefreshListener;

/**
 * 我的好友
 * 
 * @author sth
 * 
 */
public class MyFriendsActivity extends BaseActivity implements OnClickListener, OnRefreshListener {
	private Button backBtn = null;
	private RadioGroup tabs;
	private RadioButton tabFriends;

	private View friendsIco;
	private TextView friendsTitle;
	private TextView friendsDesc;
	private View header;

	private ListView listView;

	private SwipeRefreshLayout mSwipeLayout = null;

	private List<FriendAppDto> fenqiFriends;
	private List<FriendAppDto> guguFriends;
	private List<FriendAppDto> friends;

	private FriendsAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_friends);

		initView();

		this.requestFriendList("正在请求数据...");
	}

	private void initView() {
		backBtn = (Button) this.findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		tabs = (RadioGroup) findViewById(R.id.tab_group);
		tabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_friends:
					friendsIco.setBackgroundResource(R.drawable.fenqi_ico);
					friendsTitle.setText("债务好友");
					friendsDesc.setText("来自“你好分期”的债务用户。\n");
					// adapter.changeMode(adapter.RECORD_MODE);
					break;
					
				case R.id.tab_investment:
					friendsIco.setBackgroundResource(R.drawable.friends_ico);
					friendsTitle.setText("鼓鼓好友");
					friendsDesc.setText("你可以在每份购买债权的“投资记录”里面添加投资理财好友，让更多投资人和您成为好朋友。");
					// adapter.changeMode(adapter.PLAN_MODE);
					break;

				}
				updateFriends();

			}

		});

		tabFriends = (RadioButton) findViewById(R.id.tab_friends);
		tabFriends.setChecked(true);

		initSwipeRefresh();

		header = LayoutInflater.from(this).inflate(R.layout.friend_list_head_layout, null);
		fenqiFriends = new ArrayList<FriendAppDto>();
		guguFriends = new ArrayList<FriendAppDto>();
		friends = new ArrayList<FriendAppDto>();
		listView = (ListView) this.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				if (holder != null && holder.friend.getStatus() == 'c') {

//					Intent intent = new Intent(MyFriendsActivity.this, ChattingActivity.class);
//					intent.putExtra("recipients", holder.friend.getVoipAccount());
//					intent.putExtra("userName", holder.friend.getUserName());
//					MyFriendsActivity.this.startActivity(intent);
				}

			}

		});
		listView.addHeaderView(header);
		adapter = new FriendsAdapter(this, friends);
		listView.setAdapter(adapter);
		initViewTopInfo();

	}

	@SuppressLint("ResourceAsColor")
	private void initSwipeRefresh() {
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColor(R.color.redme, R.color.blueme, R.color.orangeme, R.color.greenme);
		mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
	}

	private void updateFriends() {// 更新好友列表

		if (tabFriends.isChecked()) {
			// friends =fenqiFriends;
			adapter.setData(fenqiFriends);
		} else {
			// friends = guguFriends;
			adapter.setData(guguFriends);
		}

		adapter.notifyDataSetChanged();

	}

	private void initViewTopInfo() {
		friendsIco = header.findViewById(R.id.friends_ico);
		friendsTitle = (TextView) header.findViewById(R.id.friends_title);
		friendsDesc = (TextView) header.findViewById(R.id.friends_desc);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.backAction();
			break;

		}
	}

	public void onBackPressed() {
		this.backAction();
	}

	private void backAction() {
		// 为推送准备
		if (ActivityManager.getInstance().getAllActivity().size() == 1) {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			this.finish();

		} else {
			this.finish();
		}
	}

	@Override
	public void onRefresh() {
		this.requestFriendList(null);
	}

	public void requestFriendList(String msg) {

		JSONRequest request = new JSONRequest(this, RequestEnum.FriendList, null, false, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
				JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, FriendGroupAppDto.class);
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

				AppMessageDto<List<FriendGroupAppDto>> dto = null;
				try {
					dto = objectMapper.readValue(jsonObject, javaType);
					if (dto.getStatus() == AppResponseStatus.SUCCESS) {

						for (FriendGroupAppDto group : dto.getData()) {
							if (group.isFQ()) {
								fenqiFriends.clear();
								fenqiFriends.addAll(group.getFriends());
							} else {
								guguFriends.clear();
								guguFriends.addAll(group.getFriends());
							}
						}
						updateFriends();

					}

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					mSwipeLayout.setRefreshing(false);

					mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
				}

			}
		}, new ResponseErrorListener(this) {

			@Override
			public void todo() {
				mSwipeLayout.setRefreshing(false);
			}
		});

		if (!this.addToRequestQueue(request, msg)) {
			mSwipeLayout.setRefreshing(false);
		}
	}

}
