package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MessageListAppDto;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.JsonUtil;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;

@Deprecated
public class MessageActivity extends BaseActivity implements OnClickListener {

    private Button backButton = null;
    private AnimatedExpandableListView listView = null;
    private ExpandableAdapter adapter = null;
    private List<MessageListAppDto> dtoList = null;

    private List<GroupItem> items = null;

    private String[] groupNames = new String[]{"系统消息", "投资消息", "好友消息"};
    private int[] groupImageIds = new int[]{R.drawable.message_system, R.drawable.message_repayment, R.drawable.message_friend};
    // 类型分组 a系统消息 b投资消息 c 好友消息
    private char[] groupType = new char[]{'a', 'b', 'c'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        listView = (AnimatedExpandableListView) this.findViewById(R.id.listView);

        items = new ArrayList<GroupItem>();
        for (int i = 0; i < groupNames.length; i++) {
            GroupItem item = new GroupItem();
            item.groupName = groupNames[i];
            item.groupImageId = groupImageIds[i];
            item.groupType = groupType[i];

            items.add(item);
        }

        adapter = new ExpandableAdapter(this);
        adapter.setData(items);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        requestMessage();
    }

    public void onResume() {
        super.onResume();

        if (Constants.NEED_REFRESH_LOGIN) {
            Constants.NEED_REFRESH_LOGIN = false;

            requestMessage();
        }
    }

    private void refreshView() {
        for (GroupItem groupItem : items) {

            groupItem.items.clear();

            for (int j = 0; j < dtoList.size(); j++) {
                MessageListAppDto dto = dtoList.get(j);
                if (dto.getType() == groupItem.groupType) {
                    ChildItem child = new ChildItem();
                    child.title = dto.getTitle();
                    child.content = dto.getContent();
                    child.time = dto.getTime();
                    child.id = dto.getId();
                    child.read = dto.isRead();
                    child.functionData = dto.getFunctionData();
                    child.functionType = dto.getFunctionType();

                    groupItem.items.add(child);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private static class GroupItem {
        private String groupName = null;
        private int groupImageId = R.drawable.message_system;
        private char groupType = 'a';
        private List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        private int id = 0;
        private String title = null;
        private String content = null;
        private String time = null;
        private boolean read = false;
        private String functionData = null;
        private int functionType = 0;
    }

    private static class ChildHolder {
        private TextView titleTextView = null;
        private TextView contextTextView = null;
        private TextView timeTextView = null;
    }

    private static class GroupHolder {
        private TextView groupNameTextView = null;
        private ImageView groupImageView = null;
        private TextView countTextView = null;
        private ImageView indicatorImageView = null;
    }

    private class ExpandableAdapter extends AnimatedExpandableListAdapter {

        private LayoutInflater inflater;
        private List<GroupItem> items;

        public ExpandableAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.item_msg_group, parent, false);
                holder.groupNameTextView = (TextView) convertView.findViewById(R.id.titleTextView);
                holder.groupImageView = (ImageView) convertView.findViewById(R.id.picImageView);
                holder.indicatorImageView = (ImageView) convertView.findViewById(R.id.indicatorImageView);
                holder.countTextView = (TextView) convertView.findViewById(R.id.countTextView);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.groupNameTextView.setText(item.groupName);
            holder.groupImageView.setImageResource(item.groupImageId);
            holder.countTextView.setText(item.items.size() + "");

            if (isExpanded) {
                holder.indicatorImageView.setImageResource(R.drawable.message_down_arrow);
            } else {
                holder.indicatorImageView.setImageResource(R.drawable.message_right_arrow);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            final ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.item_msg, parent, false);
                holder.titleTextView = (TextView) convertView.findViewById(R.id.tv_title);
                holder.contextTextView = (TextView) convertView.findViewById(R.id.tv_content);
                holder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.titleTextView.setText(item.title);
            holder.contextTextView.setText(item.content);
            holder.timeTextView.setText(item.time);

            if (item.read) {
                holder.titleTextView.setTextColor(Color.parseColor("#999999"));
                holder.contextTextView.setTextColor(Color.parseColor("#aaaaaa"));
                holder.timeTextView.setTextColor(Color.parseColor("#aaaaaa"));
            } else {
                holder.titleTextView.setTextColor(Color.parseColor("#333333"));
                holder.contextTextView.setTextColor(Color.parseColor("#666666"));
                holder.timeTextView.setTextColor(Color.parseColor("#666666"));
            }

            convertView.setPadding(0, 10, 0, 10);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestMessageRead(item);
                }
            });

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
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

    private void requestMessage() {
        JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGELIST, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, MessageListAppDto.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<List<MessageListAppDto>> dto = objectMapper.readValue(jsonObject, type);

                    dtoList = dto.getData();

                    refreshView();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void requestMessageRead(final ChildItem item) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", item.id + "");
        JSONRequest request = new JSONRequest(this, RequestEnum.MESSAGEREAD, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        jumpAction(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    // 如果有修改一定要同时修改推送部分～～
    private void jumpAction(ChildItem item) {
        /**
         * 跳转数据 json格式 a系统消息的时候是http的url地址{"url":"http://www.baidu.com"} b订单 订单标示{"orderId":1} c好友 为空 d还款 还款订单标示{"orderId":1} e审核 为空
         */
        /**
         * 跳转标识  1定投列表 2抢投列表 3转让列表  4打开链接  5好友列表  6我的投资  7 系统提醒（do nothing）  9另一设备登录
         */
        try {
            switch (item.functionType) {

                case 0: // 无任何操作

                    break;

                case 1: {
                    InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_DQ);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("INDEX", 1);
                    this.startActivity(intent);
                }
                break;

                case 2: {
                    InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_HQ);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("INDEX", 1);
                    this.startActivity(intent);
                }
                break;

                case 4: {
                    Intent intent = new Intent(this, ShowShareWebViewActivity.class);
                    intent.putExtra("title", "系统消息");
                    intent.putExtra("url", Constants.HOST_IP + "/rpc/" + JsonUtil.jsonToMap(item.functionData).get("url"));
                    intent.putExtra("SHOW_SHARE", true);

                    intent.putExtra("shareTitle", item.title);
                    intent.putExtra("shareContent", item.content);
                    intent.putExtra("shareURL", Constants.HOST_IP + "/share/message/" + item.id);
                    intent.putExtra("shareData", item.functionData);
                    intent.putExtra("shareId", item.id + "");

                    this.startActivity(intent);
                }
                break;

                case 5: {
                    Intent intent = new Intent(this, MyFriendsActivity.class);
                    this.startActivity(intent);
                }
                break;

                case 6: {
                    Intent intent = new Intent(this, MyInvestmentExActivity.class);
                    this.startActivity(intent);
                }
                break;

                case 9: { // 注销登录
                    Toast.makeText(this, "已注销登录", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                    editor.putString(Constants.Base_Token, "");
                    editor.commit();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("FROM", LoginActivity.FROM_TOKEN_EXPIRED);
                    this.startActivity(intent);

                }

                default:
                    Toast.makeText(this, "没有找到相应的类型", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
