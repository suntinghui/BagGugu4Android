package com.gugu.activity.view;

import org.codehaus.jackson.map.DeserializationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.std.StdArraySerializers.LongArraySerializer;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.GrabOrderByAppDto;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.gugu.activity.MyInvestmentActivity;
import com.wufriends.gugu.R;
import com.gugu.activity.VoteOfRushActivity;
import com.gugu.activity.VoteOfScheduledActivity;
import com.gugu.activity.VoteOfTransferActivity;
import com.gugu.activity.view.VerifyTransferPWDDialog.OnConfirmListener;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.gugu.utils.DateUtil;
import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;

/**
 * 我的投资 每条
 *
 * @author sth
 */

public class MyInvestmentExAdapter extends AnimatedExpandableListAdapter {

    private MyInvestmentActivity context;

    private WeakHashMap<Integer, GroupHolder> groupHashMap = new WeakHashMap<Integer, GroupHolder>();

    private List<MyDebtPackage> mList = new ArrayList<MyDebtPackage>();
    private List<ChildHolder> hasHandlerList = new ArrayList<ChildHolder>();

    // 转让提示框
    private VerifyTransferPWDDialog verifyTransferPwdDialog = null;

    // 音效播放
    private SoundPool pool;
    private int moneySourceid;
    private int winSourceid;

    // 如果是抢投需要默认打开，此变量是为控制再点击时能关闭。
    private boolean initExpandGroup = true;

    public MyInvestmentExAdapter(MyInvestmentActivity context) {
        this.context = context;

        // 有人说如果调用load后直接调用play方法有时候会导致崩溃，所以安全起见，一开始就初始化好声音池。安全第一。
        // 指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        // 载入音频流，返回在池中的id
        moneySourceid = pool.load(context, R.raw.money_down, 0);
        winSourceid = pool.load(context, R.raw.win, 0);
    }

    private void playMusic(int sourceid) {
        // 播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环;第六个参数为速率，速率 最低0.5最高为2，1代表正常速度
        pool.play(sourceid, 1, 1, 0, 0, 1);
    }

    public void setData(List<MyDebtPackage> tempList) {
        this.mList = tempList;
    }

    // ////////////////////////////////////////////////////////

    private static class GroupHolder {
        private LinearLayout topLayout;
        private ImageView indicatorImageView;
        private TextView investmentMoneyTextView; // 投资额
        private TextView investmentTimeTextView; // 剩余时间
        private TextView rateTextView; // 利率
        private TextView rewardRateTextView; // 奖励利率
        private TextView stateTextView; // 状态
        private View bottomLineView;
    }

    private static class ChildHolder {
        private GroupHolder groupHolder;

        private LinearLayout claimsLayout;
        private TextView claimsNumTextView;
        private TextView claimsTypeTextView;
        private TextView claimsPrincipalTextView; // 本金
        private TextView rateTextView; // 年利率
        private TextView actualAmountTextView; // 实际投资
        private TextView rewardTypeTextView; // 奖励类型 收购or红包
        private TextView rewardAmountTextView; // 奖励
        private TextView periodTextView; // 项目周期
        private TextView principalDateTextView; // 还本日期
        private TextView statusTextView; // 状态
        private TextView earningsAmountTextView; // 本息合计

        private TextView timingTextView;
        private Button rushBtn;

        private ImageView sealCompleteImageView;

        // 计时
        private Handler handler = null;
        private Runnable runnable = null;
        private long countDown = -1;

        // (取消)转让按纽的动作标识
        private boolean isTransfer = true;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public MyDebtPackage getGroup(int groupPosition) {
        return this.mList.get(groupPosition);
    }

    @Override
    public MyDebtPackage getChild(int groupPosition, int childPosition) {
        return this.mList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView == null) {
            holder = new GroupHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_investment_group, null);

            holder.topLayout = (LinearLayout) convertView.findViewById(R.id.topLayout);
            holder.indicatorImageView = (ImageView) convertView.findViewById(R.id.indicatorImageView);
            holder.investmentMoneyTextView = (TextView) convertView.findViewById(R.id.investmentMoneyTextView);
            holder.investmentTimeTextView = (TextView) convertView.findViewById(R.id.investmentTimeTextView);
            holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
            holder.rewardRateTextView = (TextView) convertView.findViewById(R.id.rewardRateTextView);
            holder.stateTextView = (TextView) convertView.findViewById(R.id.stateTextView);
            holder.bottomLineView = convertView.findViewById(R.id.bottomLineView);

            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        MyDebtPackage dto = mList.get(groupPosition);

        groupHashMap.put(dto.getId(), holder);

        this.refreshGroupView(holder, dto);

        if (isExpanded) {
            holder.indicatorImageView.setBackgroundResource(R.drawable.withdrawal_indicator_down);
            holder.bottomLineView.setVisibility(View.GONE);
        } else {
            holder.indicatorImageView.setBackgroundResource(R.drawable.withdrawal_indicator_right);
            holder.bottomLineView.setVisibility(View.VISIBLE);
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
        if (convertView == null) {
            holder = new ChildHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_investment_child, null);

            holder.claimsLayout = (LinearLayout) convertView.findViewById(R.id.claimsLayout);
            holder.claimsNumTextView = (TextView) convertView.findViewById(R.id.claimsNumTextView);
            holder.claimsTypeTextView = (TextView) convertView.findViewById(R.id.claimsTypeTextView);
            holder.claimsPrincipalTextView = (TextView) convertView.findViewById(R.id.claimsPrincipalTextView);
            holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
            holder.actualAmountTextView = (TextView) convertView.findViewById(R.id.actualAmountTextView);
            holder.rewardTypeTextView = (TextView) convertView.findViewById(R.id.rewardTypeTextView);
            holder.rewardAmountTextView = (TextView) convertView.findViewById(R.id.rewardAmountTextView);
            holder.periodTextView = (TextView) convertView.findViewById(R.id.periodTextView);
            holder.principalDateTextView = (TextView) convertView.findViewById(R.id.principalDateTextView);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);
            holder.earningsAmountTextView = (TextView) convertView.findViewById(R.id.earningsAmountTextView);
            holder.timingTextView = (TextView) convertView.findViewById(R.id.timingTextView);
            holder.rushBtn = (Button) convertView.findViewById(R.id.rushBtn);

            holder.sealCompleteImageView = (ImageView) convertView.findViewById(R.id.sealCompleteImageView);

            convertView.setTag(holder);

        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        MyDebtPackage dto = mList.get(groupPosition);

        // 设置子类的GroupHolder
        holder.groupHolder = groupHashMap.get(dto.getId());

        refreshChildView(holder, dto, groupPosition);

        initExpandGroup = false;

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    private void refreshGroupView(final GroupHolder holder, final MyDebtPackage dto) {
        holder.investmentMoneyTextView.setText("投资额：" + dto.getPrincipal() + "元");
        if (dto.getSurplusDay() == 0 || dto.getStatus() == 'c' || dto.getStatus() == 'd') {
            holder.investmentTimeTextView.setText("");
        } else {
            holder.investmentTimeTextView.setText("日返息，" + dto.getSurplusDay() + "天还本");
        }

        holder.rateTextView.setText(dto.getRate() + "%");

        if (dto.getRewardRate() != null) {
            holder.rewardRateTextView.setText("+" + dto.getRewardRate() + "%");
        }

        // 状态
        refreshGroupStatus(holder.stateTextView, dto);
    }

    private void refreshChildView(final ChildHolder tempHolder, final MyDebtPackage dto, int groupPosition) {
        tempHolder.claimsLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                // 债权类型 a定投 b抢投 c转让
                if (dto.getType() == 'a') {
                    intent = new Intent(context, VoteOfScheduledActivity.class);
                } else if (dto.getType() == 'b') {
                    intent = new Intent(context, VoteOfRushActivity.class);
                } else if (dto.getType() == 'c') {
                    intent = new Intent(context, VoteOfTransferActivity.class);
                }

                intent.putExtra("id", dto.getDpid() + "");
                context.startActivity(intent);
            }
        });

        tempHolder.claimsNumTextView.setText("债权编码：  " + dto.getDpnum());
        // 债权类型 a定投 b抢投 c转让
        tempHolder.claimsTypeTextView.setText(dto.getType() == 'a' ? "定投" : (dto.getType() == 'b' ? "抢投" : "转让"));
        tempHolder.claimsPrincipalTextView.setText(dto.getTotalPrincipal() + "元");
        tempHolder.rateTextView.setText(dto.getTotalRate() + "%");
        tempHolder.actualAmountTextView.setText(dto.getPrincipal() + "元");
        tempHolder.rewardTypeTextView.setText(dto.getType() == 'c' ? "收购奖励:\t" : "红包奖励:\t");
        tempHolder.rewardAmountTextView.setText(dto.getReward() + "元");
        tempHolder.periodTextView.setText(dto.getPeriod() == 0 ? "--" : (dto.getPeriod() + "天"));
        tempHolder.principalDateTextView.setText(dto.getEndDate()); // 还本时间
        tempHolder.earningsAmountTextView.setText(dto.getEndTotalMoney());
        tempHolder.earningsAmountTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateCalc(tempHolder, dto);
            }
        });

        // 首先判断是否转让
        // 转让状态 a正常 b转让中 c完成
        // 状态 a等待返息 b返息中 c完成
        setStatus(tempHolder.statusTextView, dto);

        // 如果是已完成，盖印章
        tempHolder.sealCompleteImageView.setVisibility(dto.getStatus() == 'c' || dto.getStatus() == 'd' ? View.VISIBLE : View.GONE);

        // 不能使用是否允许转让来判断状态。
        tempHolder.isTransfer = !dto.isCancelTransfer();

        // 首先判断是否是抢投，如果是抢投，则根据时间值判断抢投的状态
        // 抢排名时间 毫秒值 -1还没开始 0抢投中 Long.MAX_VALUE:9223372036854775807抢投结束 其他表示开抢时间
        tempHolder.rushBtn.setVisibility(View.GONE);
        tempHolder.timingTextView.setVisibility(View.GONE);
        tempHolder.timingTextView.setTextSize(15);

        if (dto.getType() == 'b') {
            if (dto.getGrabTime() == -1) {
                tempHolder.timingTextView.setText("-- : -- : --");
                tempHolder.timingTextView.setVisibility(View.VISIBLE);

            } else if (dto.getGrabTime() == 0 && dto.getOrderby() == 0) {
                tempHolder.rushBtn.setEnabled(true);
                tempHolder.rushBtn.setVisibility(View.VISIBLE);

                // 默认打开
                if (initExpandGroup) {
                    context.expandGroup(groupPosition);
                }

            } else if (dto.getGrabTime() > 0) {
                tempHolder.countDown = (dto.getGrabTime() - dto.getSysTime()) / 1000 + 2;
                // 默认打开
                if (initExpandGroup) {
                    context.expandGroup(groupPosition);
                }

                // 解决刷新后时间混乱
                if (tempHolder.handler != null && tempHolder.runnable != null) {
                    tempHolder.handler.removeCallbacks(tempHolder.runnable);
                }

                tempHolder.handler = new Handler();
                tempHolder.runnable = new Runnable() {
                    @Override
                    public void run() {
                        tempHolder.countDown--;

                        // 时间到，刷新
                        if (tempHolder.countDown == 0) {
                            tempHolder.handler.removeCallbacks(tempHolder.runnable);

                            tempHolder.timingTextView.setText("00 : 00 : 00");

                            tempHolder.timingTextView.setVisibility(View.GONE);
                            tempHolder.rushBtn.setVisibility(View.VISIBLE);
                        }

                        tempHolder.timingTextView.setText(DateUtil.second2Time(tempHolder.countDown));
                        tempHolder.handler.postDelayed(this, 1000);
                    }
                };

                tempHolder.handler.postDelayed(tempHolder.runnable, 1000);

                tempHolder.timingTextView.setText("00 : 00 : 00");

                hasHandlerList.add(tempHolder);
                tempHolder.timingTextView.setVisibility(View.VISIBLE);

            } else if (dto.getGrabTime() == -2 || dto.getOrderby() > 0) {
                tempHolder.timingTextView.setTextSize(13);
                tempHolder.timingTextView.setVisibility(View.VISIBLE);
                tempHolder.timingTextView.setText("已抢到第" + dto.getOrderby() + "名");
            }

        }

        tempHolder.rushBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRushOrderby(dto.getId(), tempHolder);
            }
        });

    }

    private void setStatus(TextView statusTextView, final MyDebtPackage dto) {
        // 先判断支付状态c支付确认中 d支付成功
        if (dto.getPayStatus() == 'c') {
            statusTextView.setText("支付确认中");

        } else {
            // 先判断债权状态 a等待返息 b返息中 c完成 d已赎回 z已删除
            if (dto.getStatus() == 'a') {
                statusTextView.setText("等待返息");
                // 抢排名时间 毫秒值 -1还没开始 0抢投中 Long.MAX_VALUE:9223372036854775807抢投结束 其他表示开抢时间
                if (dto.getType() == 'b') {
                    if (dto.getGrabTime() == -1) {
                        statusTextView.setTextColor(context.getResources().getColor(R.color.redme));
                        statusTextView.setText("竞买中...");
                    } else if (dto.getGrabTime() == 0) {
                        statusTextView.setTextColor(context.getResources().getColor(R.color.redme));
                        statusTextView.setText("抢投中");
                    } else if (dto.getGrabTime() != -2) {
                        statusTextView.setText("等待开抢");
                    }
                }
            } else if (dto.getStatus() == 'b') {
                statusTextView.setText("返息中");
                // 返息中的时候才会出现转让 a正常 b转让中 c完成
                if (dto.getZrStatus() == 'b') {
                    statusTextView.setText("转让中");
                }
            } else if (dto.getStatus() == 'c' || dto.getStatus() == 'd') {
                statusTextView.setText("已完成");
                if (dto.getZrStatus() == 'c') {
                    statusTextView.setText("已转让");
                }
                if (dto.getStatus() == 'd') {
                    statusTextView.setText("已赎回");
                }
            }

        }
    }

    private void refreshGroupStatus(TextView statusTextView, final MyDebtPackage dto) {
        statusTextView.setTextColor(Color.parseColor("#999999"));
        setStatus(statusTextView, dto);
    }

    // 转让债权
    private void requestTransfer(int id, final ChildHolder holder) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", "" + id);
        tempMap.put("password", verifyTransferPwdDialog.getPassword());

        JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_TRANSFER, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(context, "转让成功", Toast.LENGTH_SHORT).show();

                        verifyTransferPwdDialog.dismiss();

                    } else {
                        verifyTransferPwdDialog.setError(dto.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, "正在请求数据...");
    }

    // 取消转让债权
    private void requestCancelTransfer(int id, final ChildHolder holder) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", "" + id);
        tempMap.put("password", verifyTransferPwdDialog.getPassword());

        JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_TRANSFER_CANCEL, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        Toast.makeText(context, "取消转让成功", Toast.LENGTH_SHORT).show();

                        verifyTransferPwdDialog.dismiss();

                    } else {
                        verifyTransferPwdDialog.setError(dto.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, "正在请求数据...");
    }

    // 抢排名
    private void requestRushOrderby(int id, final ChildHolder childHolder) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("id", "" + id);

        JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_GRAB_ORDERBY, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, GrabOrderByAppDto.class);
                    AppMessageDto<GrabOrderByAppDto> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        showRanking(dto.getData());

                        // 替换掉旧数据
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getId() == dto.getData().getId()) {
                                mList.remove(i);
                                mList.add(i, dto.getData());

                                break;
                            }
                        }

                        refreshChildView(childHolder, dto.getData(), Integer.MAX_VALUE);

                        if (childHolder.groupHolder != null) {
                            refreshGroupView(childHolder.groupHolder, dto.getData());
                        }

                    } else {
                        Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context.addToRequestQueue(request, "正在请求数据...");
    }

    private void askTransferTip(final int id, final ChildHolder holder) {
        verifyTransferPwdDialog = new VerifyTransferPWDDialog(context);
        verifyTransferPwdDialog.setTitle("确定转让该债权吗？");
        verifyTransferPwdDialog.setTip("您将被扣除50%的收益作为给收购人的奖励。债权没有被收购前，第二天可取消转让。");
        verifyTransferPwdDialog.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm(String pwdStr) {
                requestTransfer(id, holder);
            }
        });
        verifyTransferPwdDialog.show();
    }

    private void askCancelTransferTip(final int id, final ChildHolder holder) {
        verifyTransferPwdDialog = new VerifyTransferPWDDialog(context);
        verifyTransferPwdDialog.setTitle("确定取消转让债权吗？");
        verifyTransferPwdDialog.setTip("取消转让债权后您将继续获取收益");
        verifyTransferPwdDialog.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm(String pwdStr) {
                requestCancelTransfer(id, holder);
            }
        });
        verifyTransferPwdDialog.show();
    }

    private void showRanking(GrabOrderByAppDto orderDto) {
        RushRankingResultDialog dialog = new RushRankingResultDialog(context);
        dialog.setRank(orderDto.getOrderby());
        dialog.setRate(orderDto.getTotalRate());
        dialog.setRankList(orderDto.getOrderbys());

        dialog.show();

        if (orderDto.getOrderby() <= 1) { // 第一名
            playMusic(winSourceid);
        } else {
            playMusic(moneySourceid);
        }

    }

    public void stopAllHandler() {
        for (ChildHolder holder : hasHandlerList) {
            if (holder.handler != null) {
                try {
                    holder.handler.removeCallbacks(holder.runnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 弹出利息计算方式
    private PopupWindow tipPopup = null;
    private TextView tipTextView = null;

    /**
     * 显示本息计算方式
     */
    private void showRateCalc(ChildHolder holder, MyDebtPackage dto) {
        try {
            if (null == tipPopup) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.layout_popup_tip, null);
                layout.findViewById(R.id.titleTextView).setVisibility(View.GONE);
                tipTextView = (TextView) layout.findViewById(R.id.tipTextView);

                tipPopup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                tipPopup.setFocusable(false);
                tipPopup.setBackgroundDrawable(new BitmapDrawable());
                tipPopup.setOutsideTouchable(false);
            }

            StringBuffer sb = new StringBuffer("");
            sb.append(dto.getEndTotalMoney());
            sb.append("(本息)=");
            sb.append(dto.getPrincipal()).append("+");
            sb.append(dto.getPrincipal()).append("*(");
            sb.append(dto.getPeriod()).append("/365)*");
            sb.append(dto.getTotalRate()).append("%");
            tipTextView.setText(sb.toString());

            int[] location = new int[2];
            holder.earningsAmountTextView.getLocationOnScreen(location);

            int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
            int textViewWidth = tipTextView.getWidth();
            tipPopup.showAtLocation(holder.earningsAmountTextView, Gravity.NO_GRAVITY, (screenWidth - textViewWidth) / 2, location[1] - AdapterUtil.dip2px(context, 45));

            handler.postDelayed(runnable, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        public void run() {
            tipPopup.dismiss();
            handler.removeCallbacks(this);
        }
    };

}
