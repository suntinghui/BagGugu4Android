package com.gugu.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.ares.baggugu.dto.app.Paginable;
import com.gugu.activity.view.MyInvestmentCurrentLayout;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.model.MyDebtPackageEx;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.DateUtil;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;

/**
 * Created by sth on 8/25/15.
 */
public class MyInvestmentExActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private ListView listView;
    private SwipeRefreshLayout mSwipeLayout = null;

    private int pageNo = 1;
    private int totalPage = 0;

    private List<MyDebtPackageEx> mList = new ArrayList<MyDebtPackageEx>();
    private Adapter adapter = null;

    private MyInvestmentCurrentLayout headerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_investment_ex);

        initView();

        requesInvestmentList("正在请求数据...");

        requestHQInfo();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("我的投资");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        initSwipeRefresh();

        listView = (ListView) this.findViewById(R.id.listView);

        headerLayout = new MyInvestmentCurrentLayout(this);
        listView.addHeaderView(headerLayout);

        adapter = new Adapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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

        this.requesInvestmentList(null);
        this.requestHQInfo();
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

        this.requesInvestmentList(null);
    }

    private void requesInvestmentList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", pageNo + "");
        tempMap.put("pageSize", Constants.PAGESIZE + "");
        tempMap.put("type", "false"); // true只加载进⾏行中的 false全部的

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_LIST, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, MyDebtPackageEx.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<MyDebtPackageEx>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        totalPage = dto.getData().getTotalPage();
                        pageNo = dto.getData().getPageNo();

                        if (pageNo == 1) {
                            mList.clear();
                        }

                        mList.addAll(dto.getData().getList());

                        adapter.notifyDataSetChanged();

                        ActivityUtil.setEmptyView(MyInvestmentExActivity.this, listView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requesInvestmentList("正在请求数据...");
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) { // Header View
            Intent intent = new Intent(this, CurrentActivityEx.class);
            this.startActivity(intent);
            return;
        } else {
            Intent intent = new Intent(this, MyInvestmentDetailActivity.class);
            intent.putExtra("DTO", mList.get(position-1));
            this.startActivity(intent);
        }
    }

    private class ViewHolder {
        private TextView typeTextView;
        private TextView moneyTextView;
        private TextView buyTimeTextView;
        private TextView rateTextView;
        private TextView addRateTextView;
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

                convertView = mInflater.inflate(R.layout.item_my_investment_ex, null);

                holder.typeTextView = (TextView) convertView.findViewById(R.id.typeTextView);
                holder.moneyTextView = (TextView) convertView.findViewById(R.id.moneyTextView);
                holder.buyTimeTextView = (TextView) convertView.findViewById(R.id.buyTimeTextView);
                holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
                holder.addRateTextView = (TextView) convertView.findViewById(R.id.addRateTextView);
                holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MyDebtPackageEx infoDto = mList.get(position);

            holder.typeTextView.setText(infoDto.getTypeStr());
            holder.moneyTextView.setText(infoDto.getPrincipal() + " 元");
            holder.buyTimeTextView.setText(DateUtil.getData(infoDto.getBuyTime()));
            holder.rateTextView.setText(infoDto.getRate() + "%");
            if (infoDto.getRewardRate() != null) {
                holder.addRateTextView.setText("+" + infoDto.getRewardRate() + "%");
            } else {
                holder.addRateTextView.setText("");
            }
            holder.statusTextView.setText(infoDto.getStatusStr());
            holder.statusTextView.setTextColor(infoDto.getStatusColor());

            return convertView;
        }
    }

    // 关于活期
    private void requestHQInfo() {
        JSONRequest request = new JSONRequest(this, RequestEnum.USER_HQ_INFO, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MyHQInfoAppDto.class);
                    AppMessageDto<MyHQInfoAppDto> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        responseHQInfo(dto.getData());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseHQInfo(MyHQInfoAppDto dto) {
        headerLayout.refreshData(dto);
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
