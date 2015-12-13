package com.gugu.activity.view;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.FriendAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.MyFriendsActivity;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;

/**
 * 好友适配器
 * 
 * FriendsAdapter
 * 
 * @author liying
 * @date 2015-3-28 下午6:00:10
 * @version 1.0.0
 * 
 */
public class FriendsAdapter extends BaseAdapter {

	private Context context;
	private List<FriendAppDto> data;
	private LayoutInflater layoutInflater;

	public FriendsAdapter(Context context, List<FriendAppDto> data) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.data = data;
	}

	public void setData(List<FriendAppDto> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = layoutInflater.inflate(R.layout.friend_item, parent, false);
			
			CustomNetworkImageView userHead = (CustomNetworkImageView) convertView.findViewById(R.id.user_head);
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView desc = (TextView) convertView.findViewById(R.id.desc);
			TextView time = (TextView) convertView.findViewById(R.id.time);
			TextView from = (TextView) convertView.findViewById(R.id.from);
			Button agreeBtn = (Button) convertView.findViewById(R.id.agreeBtn);

			holder.userHead = userHead;
			holder.name = name;
			holder.desc = desc;
			holder.time = time;
			holder.agreeBtn = agreeBtn;
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final FriendAppDto dto = data.get(position);

		holder.userHead.setLocalImageBitmap(R.drawable.user_default_head);
		holder.userHead.setErrorImageResId(R.drawable.user_default_head);
		holder.userHead.setDefaultImageResId(R.drawable.user_default_head);
		if (!StringUtils.isBlank(dto.getUserImg()) && dto.getUserImg().startsWith("http")) {
			holder.userHead.setImageUrl(dto.getUserImg(), ImageCacheManager.getInstance().getImageLoader());
		} else {
			holder.userHead.setImageUrl(Constants.HOST_IP + dto.getUserImg(), ImageCacheManager.getInstance().getImageLoader());
		}

		holder.name.setText(dto.getUserName());
		holder.time.setText(dto.getTime());
		holder.agreeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestFriendAgree(dto);
			}
		});
		holder.friend = dto;
		// a申请中 b对方拒绝 c已是好友
		switch (dto.getStatus()) {
		case 'a':
			if (dto.isMySend()) {
				holder.desc.setText("等待对方同意请求");
				holder.time.setVisibility(View.VISIBLE);
				holder.agreeBtn.setVisibility(View.GONE);
			} else {

				holder.desc.setText("请求添加您为好友");
				holder.time.setVisibility(View.GONE);
				holder.agreeBtn.setVisibility(View.VISIBLE);
			}

			break;

		case 'b':
			if (dto.isMySend()) {
				holder.desc.setText("对方拒绝添加您为好友");
			} else {
				holder.desc.setText("您拒绝了对方的添加好友请求");
			}

			holder.time.setVisibility(View.VISIBLE);
			holder.agreeBtn.setVisibility(View.GONE);
			break;

		case 'c':
			holder.desc.setText("你们已建立好友关系");
			holder.time.setVisibility(View.VISIBLE);
			holder.agreeBtn.setVisibility(View.GONE);
			break;
		}

		return convertView;
	}

	public static final class ViewHolder {// 辅助视图
		public CustomNetworkImageView userHead;
		public TextView name, desc, time;
		public Button agreeBtn;
		public FriendAppDto friend;

	}

	private void requestFriendAgree(final FriendAppDto fdto) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", fdto.getId() + "");

		JSONRequest request = new JSONRequest(context, RequestEnum.FriendAgree, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, javaType);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {

						new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE).setTitleText("提示").setContentText("好友添加成功，请到钱包下“我的好友”进行查看！").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sweetAlertDialog) {
								sweetAlertDialog.cancel();
								((MyFriendsActivity) context).requestFriendList("正在请求数据...");
							}
						}).show();

					} else {
						Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		((MyFriendsActivity) context).addToRequestQueue(request, "正在请求数据...");
	}

}
