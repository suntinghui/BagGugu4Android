package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
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
import com.ares.baggugu.dto.app.SpreadRewardAppDto;
import com.ares.baggugu.dto.app.SpreadRewardListAppDto;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.StringUtil;
import com.wufriends.gugu.R;

// 朋友圈总资产
public class InviteTimelineActivity extends BaseActivity implements OnClickListener {

    private LinearLayout timelineLayout = null;
    private TextView totalMoneyTextView; // 总收益
    private TextView tellHimeTextView; // 邀请好友一起来

    private ListView listView = null;
    private ContactAdapter adapter = null;
    private List<SpreadRewardListAppDto> mList = new ArrayList<SpreadRewardListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_timline);

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("朋友圈奖励");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        timelineLayout = (LinearLayout) this.findViewById(R.id.timelineLayout);
        timelineLayout.setOnClickListener(this);

        totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);

        tellHimeTextView = (TextView) this.findViewById(R.id.tellHimeTextView);
        tellHimeTextView.setOnClickListener(this);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new ContactAdapter(this);
        listView.setAdapter(adapter);

        requestTimeline();
    }

    private void requestTimeline() {
        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_SPREAD_REWARD, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, SpreadRewardAppDto.class);
                    AppMessageDto<SpreadRewardAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        mList = dto.getData().getAppDtos();
                        adapter.notifyDataSetChanged();

                        totalMoneyTextView.setText(StringUtil.formatAmount(Double.parseDouble(dto.getData().getTotalMoney())) + " 元");

                    } else {
                        Toast.makeText(InviteTimelineActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
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

        JSONRequest request = new JSONRequest(this, RequestEnum.WELFARE_SPREAD_REWARD_SUBMIT, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(InviteTimelineActivity.this, "申请成功", Toast.LENGTH_SHORT).show();

                        requestTimeline();

                    } else {
                        Toast.makeText(InviteTimelineActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
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
        private ImageView leftImageView;
        private TextView totalMoneyTextView;
        private Button moneyButton;
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

                convertView = mInflater.inflate(R.layout.layout_invite_timeline, null);

                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.leftImageView = (ImageView) convertView.findViewById(R.id.leftImageView);
                holder.totalMoneyTextView = (TextView) convertView.findViewById(R.id.totalMoneyTextView);
                holder.moneyButton = (Button) convertView.findViewById(R.id.moneyButton);
                holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final SpreadRewardListAppDto dto = mList.get(position);

            holder.totalMoneyTextView.setText(StringUtil.formatAmount(Double.parseDouble(dto.getRequireMoney())) + " 元");
            holder.moneyButton.setText(dto.getMoney() + "元现金");

            // a未达到要求 b可领取 d申请中 e已领取
            switch (dto.getStatus()) {
                case 'a':
                    holder.leftImageView.setBackgroundResource(R.drawable.invite_timeline_left_disable);
                    holder.statusTextView.setText("加油");
                    holder.statusTextView.setTextColor(Color.parseColor("#AA1caff6"));

                    holder.moneyButton.setBackgroundResource(R.drawable.blue_button_selector);
                    holder.totalMoneyTextView.setTextColor(Color.parseColor("#AA1caff6"));
                    break;

                case 'b':
                    holder.leftImageView.setBackgroundResource(R.drawable.invite_timeline_left_enable);
                    holder.statusTextView.setText("恭喜可领");
                    holder.statusTextView.setTextColor(getResources().getColor(R.color.redme));
                    holder.moneyButton.setBackgroundResource(R.drawable.red_button_selector);
                    holder.moneyButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestReceive(dto.getId());
                        }
                    });
                    break;

                case 'd':
                    holder.leftImageView.setBackgroundResource(R.drawable.invite_timeline_left_enable);
                    holder.statusTextView.setText("申请中");
                    holder.statusTextView.setTextColor(getResources().getColor(R.color.orangeme));

                    holder.moneyButton.setBackgroundResource(R.drawable.gray_button_selector);
                    break;

                case 'e':
                    holder.leftImageView.setBackgroundResource(R.drawable.invite_timeline_left_disable);
                    holder.statusTextView.setText("已领取");
                    holder.statusTextView.setTextColor(getResources().getColor(R.color.greenme));

                    holder.moneyButton.setBackgroundResource(R.drawable.gray_button_selector);
                    break;

                default:
                    break;
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

            case R.id.timelineLayout:
                inviteForShare();
                break;

            case R.id.tellHimeTextView:
                inviteForShare();
                break;
        }
    }

    private void inviteForShare() {
        Intent intent = new Intent(this, InviteShareWebActivity.class);
        intent.putExtra("url", Constants.HOST_IP + "/yq/app");
        this.startActivity(intent);
    }

}
