package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import com.ares.baggugu.dto.app.BonusAppDto;
import com.ares.baggugu.dto.app.BonusListAppDto;
import com.gugu.activity.view.BonusFlyComeDialog;
import com.gugu.activity.view.BonusFlyGoneDialog;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

// 好友红包
public class InviteFriendActivity extends BaseActivity implements OnClickListener {

    private TextView totalMoneyTextView = null;

    private ListView listView = null;
    private ContactAdapter adapter = null;
    private List<BonusListAppDto> mList = new ArrayList<BonusListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friend);

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("好友红包");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);

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
        map.put("isQt", "false");

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

                        totalMoneyTextView.setText(dto.getData().getTotal());

                    } else {
                        Toast.makeText(InviteFriendActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    // 领取
    private void requestReceive(final BonusListAppDto infoDto) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", infoDto.getId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_RECEIVE_BONUS, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        requestQTList();

                        BonusFlyComeDialog dialog = new BonusFlyComeDialog(InviteFriendActivity.this, infoDto.getName(), dto.getData());
                        dialog.show();

                    } else {
                        Toast.makeText(InviteFriendActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private CustomNetworkImageView headImageView;
        private TextView nameTextView;
        private TextView moneyTextView;
        private LinearLayout receiveLayout;
        private ImageView rightImageView;
        private TextView statusTextView;
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

                convertView = mInflater.inflate(R.layout.layout_invite_friend, null);

                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.headImageView = (CustomNetworkImageView) convertView.findViewById(R.id.headImageView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.moneyTextView = (TextView) convertView.findViewById(R.id.moneyTextView);
                holder.receiveLayout = (LinearLayout) convertView.findViewById(R.id.receiveLayout);
                holder.rightImageView = (ImageView) convertView.findViewById(R.id.rightImageView);
                holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BonusListAppDto dto = mList.get(position);

            holder.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setLocalImageBitmap(R.drawable.fenqi_head_default);
            holder.headImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());

            if (position % 2 == 0) {
                holder.contentLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                holder.contentLayout.setBackgroundColor(Color.parseColor("#AAFFFEEF"));
            }

            holder.nameTextView.setText(dto.getName());
            holder.moneyTextView.setText(dto.getMoney());

            if (dto.isReceive()) {
                holder.rightImageView.setVisibility(View.GONE);
                holder.statusTextView.setText("已领取");
                holder.statusTextView.setTextSize(12);
                holder.statusTextView.setTextColor(Color.parseColor("#999999"));

            } else {
                holder.rightImageView.setVisibility(View.VISIBLE);
                holder.statusTextView.setText("点击领取");
                holder.statusTextView.setTextSize(10);
                holder.statusTextView.setTextColor(InviteFriendActivity.this.getResources().getColor(R.color.blueme));
                holder.receiveLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestReceive(dto);
                    }
                });
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
        }
    }

}
