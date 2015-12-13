package com.gugu.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.ares.baggugu.dto.app.Paginable;
import com.wufriends.gugu.R;
import com.gugu.activity.view.MyInvestmentExAdapter;
import com.gugu.client.ActivityManager;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnLoadListener;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnRefreshListener;

/**
 * 我的投资
 *
 * @author sth
 */

@Deprecated
public class MyInvestmentActivity extends BaseActivity implements OnClickListener, OnLoadListener, OnRefreshListener {

    private TextView typeTextView = null;

    private AnimatedExpandableListView listView = null;
    private MyInvestmentExAdapter adapter = null;
    private SwipeRefreshLayout mSwipeLayout = null;

    private Spinner typeSpinner = null;
    private int typeIndex = 0;

    private List<MyDebtPackage> mList = new ArrayList<MyDebtPackage>();
    // 当前显示的类型
    private List<MyDebtPackage> currentList = new ArrayList<MyDebtPackage>();

    private int pageNo = 1;
    private int totalPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_investment);

        initView();

        this.requesInvestmentList("正在请求数据...");
    }

    public void onDestory() {
        super.onDestroy();

        try {
            adapter.stopAllHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("我的投资");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        initSwipeRefresh();

        typeTextView = (TextView) this.findViewById(R.id.typeTextView);
        typeTextView.setOnClickListener(this);

        this.findViewById(R.id.arrowImageView).setOnClickListener(this);

        typeSpinner = (Spinner) this.findViewById(R.id.typeSpinner);

        listView = (AnimatedExpandableListView) this.findViewById(R.id.listView);
        adapter = new MyInvestmentExAdapter(this);
        listView.setAdapter(adapter);
    }

    public void expandGroup(int groupPosition) {
        if (groupPosition == Integer.MAX_VALUE)
            return;

        try {
            if (!listView.isGroupExpanded(groupPosition)) {
                listView.expandGroupWithAnimation(groupPosition);
            }
        } catch (Exception e) {
        }
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
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, MyDebtPackage.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<MyDebtPackage>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        totalPage = dto.getData().getTotalPage();
                        pageNo = dto.getData().getPageNo();

                        if (pageNo == 1) {
                            mList.clear();
                        }

                        mList.addAll(dto.getData().getList());

                        refreshData();

                        ActivityUtil.setEmptyView(MyInvestmentActivity.this, listView).setOnClickListener(new OnClickListener() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.backAction();
                break;

            case R.id.typeTextView:
            case R.id.arrowImageView:
                chooseType();
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

    private void refreshData() {
        currentList.clear();

        // 债权类型 a定投 b抢投 c转让
        // "全部", "抢投", "定投", "转让"
        switch (typeIndex) {
            case 0: {// 全部
                currentList.addAll(mList);
            }
            break;

            case 1: {// 抢投
                for (MyDebtPackage debt : mList) {
                    if (debt.getType() == 'b') {
                        currentList.add(debt);
                    }
                }
            }
            break;

            case 2: {// 定投
                for (MyDebtPackage debt : mList) {
                    if (debt.getType() == 'a') {
                        currentList.add(debt);
                    }
                }
            }
            break;

            case 3: {// 转让
                for (MyDebtPackage debt : mList) {
                    if (debt.getType() == 'c') {
                        currentList.add(debt);
                    }
                }
            }
            break;
        }

        adapter.setData(currentList);
        adapter.notifyDataSetChanged();
    }

    private List<String> typeList = Arrays.asList(new String[]{"全部", "抢投", "定投", "转让"});

    // 选择类型
    private void chooseType() {
        final SpinnerAdapter adapter = new SpinnerAdapter(this);
        // typeSpinner.setPrompt("请选择银行");
        typeSpinner.setAdapter(adapter);
        typeSpinner.setSelection(typeIndex);
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeTextView.setText(typeList.get(position));
                typeIndex = position;

                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();

                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        typeSpinner.performClick();
    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView textTextView;
    }

    public class SpinnerAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private Context mContext;

        public SpinnerAdapter(Context pContext) {
            this.mContext = pContext;

            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return typeList.size();
        }

        @Override
        public Object getItem(int position) {
            return typeList.get(position);
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
                convertView = mInflater.inflate(R.layout.spinner_string_item, null);

                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.textTextView = (TextView) convertView.findViewById(R.id.textTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textTextView.setText(typeList.get(position));
            holder.contentLayout.setSelected(selectedIndex == position);

            return convertView;
        }

        private int selectedIndex = 0;

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

    }

}
