package com.gugu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import com.ares.baggugu.dto.app.LinkInviteAppDto;
import com.ares.baggugu.dto.app.LinkInviteListAppDto;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.activity.view.RewardShareDialog;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

// 邀请注册统计
public class InviteRegisterActivity extends BaseActivity implements OnClickListener {

    private TextView registedTextView; // 已注册
    private TextView unRegisteTextView; // 未注册

    private LinearLayout registedLayout;
    private LinearLayout unRegistedLayout;

    private ListView registedListView;
    private ListView unRegistedListView;

    private TextView integralTextView = null;

    private RegisteAdapter registedAdapter = null;
    private UnRegisteAdapter unRegistedAdapter = null;

    private List<LinkInviteListAppDto> registedList = new ArrayList<LinkInviteListAppDto>();
    private List<LinkInviteListAppDto> unRegistedList = new ArrayList<LinkInviteListAppDto>();

    private int integral = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_register);

        initView();

        requestRegiste(true);
        requestRegiste(false);
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("邀请统计");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        registedTextView = (TextView) this.findViewById(R.id.registedTextView);
        registedTextView.setOnClickListener(this);

        unRegisteTextView = (TextView) this.findViewById(R.id.unRegisteTextView);
        unRegisteTextView.setOnClickListener(this);

        registedLayout = (LinearLayout) this.findViewById(R.id.registedLayout);
        unRegistedLayout = (LinearLayout) this.findViewById(R.id.unRegistedLayout);

        integralTextView = (TextView) this.findViewById(R.id.integralTextView);

        registedListView = (ListView) this.findViewById(R.id.registedListView);
        registedAdapter = new RegisteAdapter(this);
        registedListView.setAdapter(registedAdapter);

        unRegistedListView = (ListView) this.findViewById(R.id.unRegistedListView);
        unRegistedAdapter = new UnRegisteAdapter(this);
        unRegistedListView.setAdapter(unRegistedAdapter);

        registedTextView.setSelected(true);
        unRegisteTextView.setSelected(false);
        registedLayout.setVisibility(View.VISIBLE);
        unRegistedLayout.setVisibility(View.GONE);

        LinearLayout emptyLayout = (LinearLayout) this.findViewById(R.id.emptyLayout);
        registedListView.setEmptyView(emptyLayout);
        ImageView noDataImageView = (ImageView) emptyLayout.findViewById(R.id.noDataImageView);
        noDataImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRegiste(true);
            }
        });

        LinearLayout unEmptyLayout = (LinearLayout) this.findViewById(R.id.unEmptyLayout);
        unRegistedListView.setEmptyView(unEmptyLayout);
        ImageView unNoDataImageView = (ImageView) unEmptyLayout.findViewById(R.id.noDataImageView);
        unNoDataImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRegiste(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.registedTextView:
                registedTextView.setSelected(true);
                unRegisteTextView.setSelected(false);
                registedLayout.setVisibility(View.VISIBLE);
                unRegistedLayout.setVisibility(View.GONE);
                break;

            case R.id.unRegisteTextView:
                registedTextView.setSelected(false);
                unRegisteTextView.setSelected(true);
                registedLayout.setVisibility(View.GONE);
                unRegistedLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void requestRegiste(final boolean registe) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("isRegist", registe ? "true" : "false");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_STATISTICS, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, LinkInviteAppDto.class);
                    AppMessageDto<LinkInviteAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (registe) {
                        if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                            registedList = dto.getData().getAppDtos();
                            integral = dto.getData().getIntegral();

                        } else {
                            registedList = new ArrayList<LinkInviteListAppDto>();

                            Toast.makeText(InviteRegisterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                        integralTextView.setText(integral + "");
                        registedAdapter.notifyDataSetChanged();

                    } else {
                        if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                            unRegistedList = dto.getData().getAppDtos();
                            integral = dto.getData().getIntegral();
                        } else {
                            unRegistedList = new ArrayList<LinkInviteListAppDto>();

                            Toast.makeText(InviteRegisterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                        integralTextView.setText(integral + "");
                        unRegistedAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    /*
    private void tellHim(String telphone) {
        String shareTitle = Constants.shareTitle;
        String shareContent = Constants.shareContent;
        String smsContent = Constants.shareContent + "【鼓鼓理财】" + Constants.HOST_IP + "/yq/" + ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");

        RewardShareDialog shareDialog = new RewardShareDialog(this, telphone, shareTitle, shareContent, smsContent);
        shareDialog.show();
    }
    */

    private void tellHim(final int id) {
        String hintContent = "系统以短信方式提醒TA登录，TA理财我获利！（每天只能提醒一次）";

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE).setTitleText("\n确认发送？").setContentText(hintContent).setCancelText("取消").setConfirmText("发送").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();

            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();

                requestTellHim(id);

            }
        }).show();
    }

    private void requestTellHim(int id){
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", id+"");

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_REGIST_HINT, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(InviteRegisterActivity.this, "提醒成功", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(InviteRegisterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    // 已注册
    private class ViewHolder {
        private CustomNetworkImageView headImageView; // 头像 末字母
        private TextView nameTextView; // 姓名
        private TextView tipTextView;
        private Button scoreButton; // 100积分
        private TextView pickTextView; // 点击领取
    }

    public class RegisteAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public RegisteAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return registedList.size();
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

                convertView = mInflater.inflate(R.layout.layout_invite_registe, null);

                holder.headImageView = (CustomNetworkImageView) convertView.findViewById(R.id.headImageView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.tipTextView = (TextView) convertView.findViewById(R.id.tipTextView);
                holder.scoreButton = (Button) convertView.findViewById(R.id.scoreButton);
                holder.pickTextView = (TextView) convertView.findViewById(R.id.pickTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LinkInviteListAppDto dto = registedList.get(position);

            holder.headImageView.setDefaultImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setErrorImageResId(R.drawable.fenqi_head_default);
            holder.headImageView.setLocalImageBitmap(R.drawable.fenqi_head_default);
            holder.headImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());

            holder.nameTextView.setText(dto.getName());
            holder.tipTextView.setText(dto.getTelphone());
            holder.scoreButton.setText(dto.getIntegral() + "积分");

            if (dto.isLogin()) {
                if (dto.isReceiveIntegral()) {
                    // 已经登录且已经领取积分
                    holder.tipTextView.setText("积分可用于转盘抽奖");
                    holder.tipTextView.setTextColor(getResources().getColor(R.color.blueme));
                    holder.scoreButton.setBackgroundResource(R.drawable.bg_blue_corner);
                    holder.scoreButton.setTextColor(getResources().getColor(R.color.blueme));
                    holder.pickTextView.setText("已经领取");
                    holder.pickTextView.setTextColor(getResources().getColor(R.color.blueme));

                } else {
                    // 已经登录但未领取积分
                    holder.tipTextView.setText("TA已经登录，积分已激活");
                    holder.tipTextView.setTextColor(getResources().getColor(R.color.blueme));
                    holder.pickTextView.setText("点击领取");
                    holder.pickTextView.setTextColor(getResources().getColor(R.color.blueme));
                    holder.scoreButton.setBackgroundResource(R.drawable.blue_button_selector);
                    holder.scoreButton.setTextColor(getResources().getColor(R.color.white));
                    holder.scoreButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestGetInteral(dto.getTelphone());
                        }
                    });
                }

            } else {
                // 已注册但是未登录
                holder.tipTextView.setText("TA首次登录后即可领取积分");
                holder.tipTextView.setTextColor(getResources().getColor(R.color.orangeme));
                holder.scoreButton.setBackgroundResource(R.drawable.gray_button_selector);
                holder.scoreButton.setTextColor(getResources().getColor(R.color.white));
                holder.pickTextView.setText("点击领取");
                holder.pickTextView.setTextColor(getResources().getColor(R.color.gray_1));
            }

            return convertView;
        }
    }

    // 未注册
    private class UnViewHolder {
        private TextView headTextView; // 头像 末字母
        private TextView nameTextView; // 姓名
        private TextView phoneNumTextView; // 手机号
        private TextView scoreTextView; // TA成功注册，你将获利100积份
        private Button registeBtn; // 注册
    }

    public class UnRegisteAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public UnRegisteAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return unRegistedList.size();
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
            UnViewHolder holder = null;

            if (null == convertView) {
                holder = new UnViewHolder();

                convertView = mInflater.inflate(R.layout.layout_invite_unregiste, null);

                holder.headTextView = (TextView) convertView.findViewById(R.id.headTextView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.phoneNumTextView = (TextView) convertView.findViewById(R.id.phoneNumTextView);
                holder.scoreTextView = (TextView) convertView.findViewById(R.id.scoreTextView);
                holder.registeBtn = (Button) convertView.findViewById(R.id.registeBtn);

                convertView.setTag(holder);
            } else {
                holder = (UnViewHolder) convertView.getTag();
            }

            final LinkInviteListAppDto dto = unRegistedList.get(position);

            String name = dto.getName();
            try {
                holder.headTextView.setText(name.substring(name.length() - 1));
            } catch (Exception e) {
                e.printStackTrace();
                holder.headTextView.setText("鼓");
            }
            holder.nameTextView.setText(name);
            holder.phoneNumTextView.setText(dto.getTelphone());
            holder.scoreTextView.setText("TA成功注册，你将获得 " + dto.getIntegral() + " 积分");
            holder.registeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tellHim(dto.getId());
                }
            });

            return convertView;
        }
    }

    // 领取积分
    private void requestGetInteral(String telphone) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("telphone", telphone);

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_RECEIVE_INTEGRAL, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(InviteRegisterActivity.this, "领取成功", Toast.LENGTH_SHORT).show();

                        // 领取积分后刷新一下
                        requestRegiste(true);

                    } else {
                        Toast.makeText(InviteRegisterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

}
