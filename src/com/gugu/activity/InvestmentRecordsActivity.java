package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.BuyInfoAppDto;
import com.ares.baggugu.dto.app.FriendAppDto;
import com.ares.baggugu.dto.app.FriendGroupAppDto;
import com.ares.baggugu.dto.app.Paginable;
import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnLoadListener;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout.OnRefreshListener;

/**
 * 投资记录
 *
 * @author sth
 */
public class InvestmentRecordsActivity extends BaseActivity implements OnClickListener, OnLoadListener, OnRefreshListener {

    private ListView listView;

    private SwipeRefreshLayout mSwipeLayout = null;

    private List<BuyInfoAppDto> mList = new ArrayList<BuyInfoAppDto>();
    private Adapter adapter = null;

    private int pageNo = 1;
    private int totalPage = 0;

    private int debtPackageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_investment_records);

        debtPackageId = this.getIntent().getIntExtra("id", 0);

        initView();

        this.requesBuyList("正在请求数据...");
    }

    private void initView() {
        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("投资记录");

        initSwipeRefresh();

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

        this.requesBuyList(null);
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

        this.requesBuyList(null);
    }

    private void requesBuyList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("telphone", ActivityUtil.getSharedPreferences().getString(Constants.UserName, ""));
        tempMap.put("debtPackageId", debtPackageId + "");
        tempMap.put("pageNo", pageNo + "");
        tempMap.put("pageSize", Constants.PAGESIZE + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_DEBTPACKAGE_BUYINFO, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, BuyInfoAppDto.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<BuyInfoAppDto>> dto = null;
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

                        ActivityUtil.setEmptyView(InvestmentRecordsActivity.this, listView).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requesBuyList("正在请求数据...");
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

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView phoneNumTextView;
        private ImageView addRateImageView;
        private TextView amountTextView;
        private TextView timeTextView;
        private TextView rateTextView;
        private TextView addRateTextView;
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

                convertView = mInflater.inflate(R.layout.layout_investment_records, null);

                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.phoneNumTextView = (TextView) convertView.findViewById(R.id.phoneNumTextView);
                holder.addRateImageView = (ImageView) convertView.findViewById(R.id.addRateImageView);
                holder.amountTextView = (TextView) convertView.findViewById(R.id.amountTextView);
                holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
                holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
                holder.addRateTextView = (TextView) convertView.findViewById(R.id.addRateTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0) {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_orange_gray);
            } else {
                holder.contentLayout.setBackgroundResource(R.drawable.bg_white_gray);
            }

            final BuyInfoAppDto infoDto = mList.get(position);

            holder.phoneNumTextView.setText(infoDto.getTelphone());
            holder.amountTextView.setText(infoDto.getMoney() + "元");
            holder.timeTextView.setText(infoDto.getTime());
            holder.rateTextView.setText(infoDto.getRate() + "%");

            try {
                double addRate = Double.parseDouble(infoDto.getRewardRate());
                if (addRate > 0) {
                    holder.addRateImageView.setVisibility(View.VISIBLE);
                    holder.addRateTextView.setVisibility(View.VISIBLE);

                    holder.addRateTextView.setText("+" + infoDto.getRewardRate() + "%");

                } else {
                    holder.addRateImageView.setVisibility(View.GONE);
                    holder.addRateTextView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();

                holder.addRateImageView.setVisibility(View.GONE);
                holder.addRateTextView.setVisibility(View.GONE);
            }

            /*
            if (infoDto.isFriend()) {
                holder.agreeBtn.setEnabled(false);
                holder.agreeBtn.setText("已添加");
                holder.agreeBtn.setVisibility(View.VISIBLE);
            } else {
                if (TextUtils.equals(ActivityUtil.getSharedPreferences().getString(Constants.UserName, ""), infoDto.getTelphone())) {
                    holder.agreeBtn.setVisibility(View.INVISIBLE);
                } else {
                    holder.agreeBtn.setVisibility(View.VISIBLE);
                    holder.agreeBtn.setEnabled(true);
                    holder.agreeBtn.setText("加为好友");
                }
            }

            holder.agreeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFriendAgree(infoDto);
                }
            });
            */

            return convertView;
        }

    }

    private void requestFriendAgree(final BuyInfoAppDto infoDto) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", infoDto.getUserId() + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.AddGuguFriend, map, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, javaType);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        new SweetAlertDialog(InvestmentRecordsActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("好友添加成功").setContentText("请到钱包下“我的好友”进行查看。").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                InvestmentRecordsActivity.this.requesBuyList("正在请求数据...");
                            }
                        }).show();

                    } else {
                        Toast.makeText(InvestmentRecordsActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        addToRequestQueue(request, "正在请求数据...");
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
