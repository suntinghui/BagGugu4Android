package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.Paginable;
import com.ares.baggugu.dto.app.RansomDebtAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.WithdrawalHeadView;
import com.gugu.activity.view.WithdrawalQDItemAdapter;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnLoadListener;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnRefreshListener;

public class WithdrawalQTActivity extends BaseActivity implements OnRefreshListener, OnClickListener, OnLoadListener {

    public static final int TYPE_DT = 0x001;
    public static final int TYPE_QT = 0x002;

    private int type = TYPE_QT;

    private WithdrawalHeadView headView;

    private ListView listView;

    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = null;
    private SwipeRefreshLayout mSwipeLayout = null;

    private List<RansomDebtAppDto> mList = new ArrayList<RansomDebtAppDto>();
    private WithdrawalQDItemAdapter adapter = null;

    private int pageNo = 1;
    private int totalPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_withdrawal_qd_list);

        this.type = this.getIntent().getIntExtra("TYPE", TYPE_QT);

        initView();

        this.requestWithdrawalList("正在请求数据...");
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        if (this.type == TYPE_QT) {
            titleTextView.setText("抢投转让到余额");
        } else {
            titleTextView.setText("定投赎回到余额");
        }

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        initSwipeRefresh();

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new WithdrawalQDItemAdapter(this, this.type);

        headView = new WithdrawalHeadView(this, this.type);
        listView.addHeaderView(headView);

        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
        swingBottomInAnimationAdapter.setAbsListView(listView);
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(Constants.INITIAL_DELAY_MILLIS);
        listView.setAdapter(swingBottomInAnimationAdapter);
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

        this.requestWithdrawalList(null);
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

        this.requestWithdrawalList(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }

    public void requestWithdrawalList(String msg) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", this.type == TYPE_QT ? "QT" : "DT");
        map.put("pageNo", "1");
        map.put("pageSize", Integer.MAX_VALUE + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_RANSOM_LIST, map, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, RansomDebtAppDto.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<RansomDebtAppDto>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        totalPage = dto.getData().getTotalPage();
                        pageNo = dto.getData().getPageNo();

                        if (pageNo == 1) {
                            mList.clear();
                        }

                        mList.addAll(dto.getData().getList());
                        adapter.setData(mList);

                        // 恶心到了吗？
                        if (WithdrawalQTActivity.this.type == TYPE_DT && !dto.getData().getList().isEmpty()) {
                            headView.setData(dto.getData().getList().get(0).getRansomHint1());
                        }

                        adapter.notifyDataSetChanged();
                        swingBottomInAnimationAdapter.notifyDataSetChanged();

                        ActivityUtil.setEmptyView(WithdrawalQTActivity.this, listView).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestWithdrawalList("正在请求数据...");
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

}
