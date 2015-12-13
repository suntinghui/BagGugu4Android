package com.gugu.activity.view;

import org.codehaus.jackson.map.DeserializationConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.RansomDebtAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.WithdrawalQTActivity;
import com.gugu.activity.view.VerifyTransferPWDDialog.OnConfirmListener;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;

public class WithdrawalQDItemAdapter extends BaseAdapter {

	private static final int ACTION_NOTHING = 0x10000;
	private static final int ACTION_TRANSFER = 0x10001;
	private static final int ACTION_CANCEL_TRANSFER = 0x10002;
	private static final int ACTION_REDEMPTION = 0x10003;

	private int action = ACTION_NOTHING;

	private WithdrawalQTActivity context;

	private List<RansomDebtAppDto> deptList = new ArrayList<RansomDebtAppDto>();

	private ViewHolder holder = null;

	// 转让提示框
	private VerifyTransferPWDDialog verifyTransferPwdDialog = null;

	private int type = WithdrawalQTActivity.TYPE_QT;

	public WithdrawalQDItemAdapter(WithdrawalQTActivity context, int type) {
		this.context = context;

		this.type = type;
	}

	public void setData(List<RansomDebtAppDto> tempList) {
		if (tempList == null)
			return;

		this.deptList = tempList;
	}

	@Override
	public int getCount() {
		return deptList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (null == convertView) {
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.layout_withdrawal_qd_item, null);

			holder.progressLayout = (RelativeLayout) convertView.findViewById(R.id.progressLayout);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
			holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
			holder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
			holder.amountTextView = (TextView) convertView.findViewById(R.id.amountTextView);
			holder.earningTextView = (TextView) convertView.findViewById(R.id.earningTextView);
			holder.deductionTextView = (TextView) convertView.findViewById(R.id.deductionTextView);
			holder.actionBtn = (Button) convertView.findViewById(R.id.actionBtn);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final RansomDebtAppDto debtDto = deptList.get(position);

		holder.actionBtn.setTag(position);
		holder.actionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (action == ACTION_REDEMPTION) { // 赎回
					redemption(debtDto);
				} else if (action == ACTION_TRANSFER) { // 转让
					transfer(debtDto.getId());
				} else if (action == ACTION_CANCEL_TRANSFER) { // 取消转让
					cancelTransfer(debtDto.getId());
				}
			}
		});

		holder.dateTextView.setText(debtDto.getBeginTime());
		holder.dayTextView.setText(debtDto.getTotalDay() + "天");
		holder.amountTextView.setText(debtDto.getPrincipal() + "元");
		holder.earningTextView.setText("当前收益:" + debtDto.getEarnings() + "元");

		holder.progressBar.setProgress(debtDto.getProgress() - debtDto.getDeductProgress());
		holder.progressBar.setSecondaryProgress(debtDto.getProgress());

		// 当两者进度相等时，替换一下颜色。恶心就忍忍吧。。。
		if (debtDto.getProgress() == debtDto.getDeductProgress()) {
			holder.progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.withdrawal_progressbar_equals));
		}

		// 转让和赎回状态，a正常 b转让中或赎回中 c完成
		if (this.type == WithdrawalQTActivity.TYPE_DT) {
			holder.deductionTextView.setText("赎回放弃:" + debtDto.getTransferDeductEarnings() + "元");

			switch (debtDto.getZrStatus()) {
			case 'a':
				holder.actionBtn.setText("赎回");
				holder.actionBtn.setEnabled(debtDto.isTransfer());
				action = ACTION_REDEMPTION;
				break;

			case 'b':
				holder.actionBtn.setText("赎回中");
				holder.actionBtn.setEnabled(false);
				break;

			case 'c':
				holder.actionBtn.setText("已赎回");
				holder.actionBtn.setEnabled(false);
				break;
			}

		} else if (this.type == WithdrawalQTActivity.TYPE_QT) {
			holder.deductionTextView.setText("扣除" + debtDto.getDeduct() + "%: " + debtDto.getTransferDeductEarnings() + "元");

			// 转让/赎回状态 a正常 b转让中 c完成
			switch (debtDto.getZrStatus()) {
			case 'a':
				holder.actionBtn.setText("转让");
				holder.actionBtn.setEnabled(debtDto.isTransfer());
				action = ACTION_TRANSFER;
				break;

			case 'b':
				if (debtDto.isCancelTransfer()) {
					holder.actionBtn.setText("取消转让");
					holder.actionBtn.setEnabled(true);
					action = ACTION_CANCEL_TRANSFER;
				} else {
					holder.actionBtn.setText("转让中");
					holder.actionBtn.setEnabled(false);
				}
				break;

			case 'c':
				holder.actionBtn.setText("已转让");
				holder.actionBtn.setEnabled(false);
				break;
			}
		}

		return convertView;

	}

	public final class ViewHolder {
		private RelativeLayout progressLayout = null;
		private ProgressBar progressBar = null;
		private TextView dateTextView = null;
		private TextView dayTextView = null;
		private TextView amountTextView = null;
		private TextView earningTextView = null;
		private TextView deductionTextView = null;

		private Button actionBtn = null;
	}

	private void redemption(final RansomDebtAppDto dto) {
		verifyTransferPwdDialog = new VerifyTransferPWDDialog(context);
		verifyTransferPwdDialog.setTitle("确定赎回该债权吗？");
		verifyTransferPwdDialog.setTip(dto.getRansomHint2());
		verifyTransferPwdDialog.setOnConfirmListener(new OnConfirmListener() {
			@Override
			public void onConfirm(String pwdStr) {
				requestRedemption(dto.getId(), pwdStr);
			}
		});
		verifyTransferPwdDialog.show();
	}

	private void transfer(final int id) {
		verifyTransferPwdDialog = new VerifyTransferPWDDialog(context);
		verifyTransferPwdDialog.setTitle("确定转让该债权吗？");
		verifyTransferPwdDialog.setTip("待他人收购您转让的债权后我们将本金及您所得利息收益的50％打入您的资金账户。债权没有被他人收购前可以取消转让。");
		verifyTransferPwdDialog.setOnConfirmListener(new OnConfirmListener() {
			@Override
			public void onConfirm(String pwdStr) {
				requestTransfer(id, pwdStr);
			}
		});
		verifyTransferPwdDialog.show();
	}

	private void cancelTransfer(final int id) {
		verifyTransferPwdDialog = new VerifyTransferPWDDialog(context);
		verifyTransferPwdDialog.setTitle("确定取消转让债权吗？");
		verifyTransferPwdDialog.setTip("取消转让债权后您将继续获取收益");
		verifyTransferPwdDialog.setOnConfirmListener(new OnConfirmListener() {
			@Override
			public void onConfirm(String pwdStr) {
				requestCancelTransfer(id, pwdStr);
			}
		});
		verifyTransferPwdDialog.show();
	}

	// 赎回债权
	private void requestRedemption(final int id, String pwdStr) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("id", "" + id);
		tempMap.put("password", pwdStr);

		JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_RANSOM, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Toast.makeText(context, "赎回成功", Toast.LENGTH_SHORT).show();

						verifyTransferPwdDialog.dismiss();

						requestRansomInfo(id);

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

	// 转让债权
	private void requestTransfer(final int id, String pwdStr) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("id", "" + id);
		tempMap.put("password", pwdStr);

		JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_TRANSFER, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Toast.makeText(context, "转让成功", Toast.LENGTH_SHORT).show();

						verifyTransferPwdDialog.dismiss();

						requestRansomInfo(id);

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
	private void requestCancelTransfer(final int id, String pwdStr) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("id", "" + id);
		tempMap.put("password", pwdStr);

		JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_TRANSFER_CANCEL, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Toast.makeText(context, "取消转让成功", Toast.LENGTH_SHORT).show();

						verifyTransferPwdDialog.dismiss();

						requestRansomInfo(id);

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

	// 取得详情
	private void requestRansomInfo(int id) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("id", "" + id);

		JSONRequest request = new JSONRequest(context, RequestEnum.USER_DEBTPACKAGE_RANSOM_INFO, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, RansomDebtAppDto.class);
					AppMessageDto<RansomDebtAppDto> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						for (int i = 0; i < deptList.size(); i++) {
							if (deptList.get(i).getId() == dto.getData().getId()) {
								deptList.remove(i);
								deptList.add(i, dto.getData());

								break;
							}
						}

						WithdrawalQDItemAdapter.this.notifyDataSetChanged();

					} else {
						Toast.makeText(context, dto.getMsg(), Toast.LENGTH_SHORT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		context.addToRequestQueue(request, "正在请求数据...");
	}

}
