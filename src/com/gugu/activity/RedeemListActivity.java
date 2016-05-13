package com.gugu.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DebtTransterDto;
import com.ares.baggugu.dto.app.Paginable;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.ViewUtil;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 5/13/16.
 * <p/>
 * 赎回列表
 */
public class RedeemListActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView countTextView = null;
    private ListView listView;

    private SwipeRefreshLayout mSwipeLayout = null;

    private List<DebtTransterDto> mList = new ArrayList<DebtTransterDto>();
    private Adapter adapter = null;

    private int pageNo = 1;
    private int totalPage = 0;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeem_list);

        initView();

        this.requestRedeemList("正在请求数据...");
    }

    private void initView() {
        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("赎回列表");

        initSwipeRefresh();

        countTextView = (TextView) this.findViewById(R.id.countTextView);
        countTextView.setVisibility(View.INVISIBLE);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new Adapter(this);
        listView.setAdapter(adapter);
    }

    @SuppressLint("ResourceAsColor")
    private void initSwipeRefresh() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnLoadListener(this);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColor(R.color.redme, R.color.blueme, R.color.orangeme, R.color.greenme);
        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeLayout.setLoadNoFull(true);
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        pageNo = 1;
        totalPage = 0;

        this.requestRedeemList(null);
    }

    // 上拉刷新
    @Override
    public void onLoad() {
        pageNo++;

        if (pageNo > totalPage) {
            Toast.makeText(this, "没有更多数据", Toast.LENGTH_SHORT).show();
            mSwipeLayout.setLoading(false);
            mSwipeLayout.setRefreshing(false);
            return;
        }

        this.requestRedeemList(null);
    }

    private void requestRedeemList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", pageNo + "");
        tempMap.put("pageSize", Constants.PAGESIZE + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_TRANSTER_LIST, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, DebtTransterDto.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<DebtTransterDto>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        totalPage = dto.getData().getTotalPage();
                        pageNo = dto.getData().getPageNo();

                        int totalCount = dto.getData().getTotalCount();
                        showCount(totalCount);

                        if (pageNo == 1) {
                            mList.clear();
                        }

                        mList.addAll(dto.getData().getList());
                        adapter.notifyDataSetChanged();

                        ActivityUtil.setEmptyView(RedeemListActivity.this, listView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestRedeemList("正在请求数据...");
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    mSwipeLayout.setLoading(false);
                    mSwipeLayout.setRefreshing(false);

                    if (pageNo == totalPage) {
                        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
                    } else {
                        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
                    }
                }

            }
        }, new ResponseErrorListener(this) {

            @Override
            public void todo() {
                mSwipeLayout.setLoading(false);
                mSwipeLayout.setRefreshing(false);
            }
        });

        if (!this.addToRequestQueue(request, msg)) {
            mSwipeLayout.setRefreshing(false);
            mSwipeLayout.setLoading(false);
        }
    }

    private void showCount(int totalCount) {
        if (totalCount == count)
            return;

        count = totalCount;

        countTextView.setText(count + "项");
        ViewUtil.dropoutView(countTextView, 500);
    }

    private class ViewHolder {
        private RelativeLayout contentLayout;
        private TextView nameTextView;
        private TextView moneyTextView;
        private TextView timeTextView;
        private TextView statusTextView;
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public Adapter(Context context) {
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

                convertView = mInflater.inflate(R.layout.item_redeem, null);

                holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.contentLayout);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
                holder.moneyTextView = (TextView) convertView.findViewById(R.id.moneyTextView);
                holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0) {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_orange_gray);
            } else {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_white_gray);
            }

            final DebtTransterDto dto = mList.get(position);

            holder.moneyTextView.setText(dto.getMoney() + "");
            holder.timeTextView.setText(dto.getTime());
            holder.statusTextView.setText(dto.isComplete() ? "赎回成功" : "正在赎回");
            holder.statusTextView.setTextColor(dto.isComplete() ? Color.parseColor("#999999") : Color.parseColor("#1898FE"));

            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RedeemListActivity.this, RedeemDetailActivity.class);
                    intent.putExtra("id", dto.getId());
                    startActivity(intent);
                }
            });

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
