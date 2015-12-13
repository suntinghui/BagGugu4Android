package com.gugu.activity.view;

import java.util.ArrayList;
import java.util.List;

import com.ares.baggugu.dto.app.WithdrawalMoneyAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.WithdrawalHistoryActivity;
import com.gugu.utils.BankUtil;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WithdrawalAdapter extends BaseAdapter {

	private WithdrawalHistoryActivity context;
	private List<WithdrawalMoneyAppDto> withdrawalList = new ArrayList<WithdrawalMoneyAppDto>();

	public WithdrawalAdapter(WithdrawalHistoryActivity context) {
		this.context = context;
	}

	public void setData(List<WithdrawalMoneyAppDto> tempList) {
		if (tempList == null)
			return;

		this.withdrawalList = tempList;
	}

	@Override
	public int getCount() {
		return withdrawalList.size();
	}

	@Override
	public Object getItem(int position) {
		return withdrawalList.get(position);
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

			convertView = LayoutInflater.from(context).inflate(R.layout.item_withdrawal, parent, false);

			holder.topLayout = (LinearLayout) convertView.findViewById(R.id.topLayout);
			holder.indicatorImageView = (ImageView) convertView.findViewById(R.id.indicatorImageView);
			holder.withdrawalMoneyTextView = (TextView) convertView.findViewById(R.id.withdrawalMoneyTextView);
			holder.withdrawalTimeTextView = (TextView) convertView.findViewById(R.id.withdrawalTimeTextView);
			holder.withdrawalStateTextView = (TextView) convertView.findViewById(R.id.withdrawalStateTextView);

			holder.detailLayout = (LinearLayout) convertView.findViewById(R.id.detailLayout);
			holder.detailLayout.setVisibility(View.GONE);

			holder.bankNameTextView = (TextView) convertView.findViewById(R.id.bankNameTextView);
			holder.tailNumTextView = (TextView) convertView.findViewById(R.id.tailNumTextView);
			holder.transferNumTextView = (TextView) convertView.findViewById(R.id.transferNumTextView);
			holder.remarkTextView = (TextView) convertView.findViewById(R.id.remarkTextView);
			holder.remarkTextView.setVisibility(View.GONE);

			holder.applyImageView = (ImageView) convertView.findViewById(R.id.applyImageView);
			holder.progressImageView_1 = (ImageView) convertView.findViewById(R.id.progressImageView_1);
			holder.bankProcessImageView = (ImageView) convertView.findViewById(R.id.bankProcessImageView);
			holder.progressImageView_2 = (ImageView) convertView.findViewById(R.id.progressImageView_2);
			holder.successImageView = (ImageView) convertView.findViewById(R.id.successImageView);

			holder.applyStateTextView = (TextView) convertView.findViewById(R.id.applyStateTextView);
			holder.applyTimeTextView = (TextView) convertView.findViewById(R.id.applyTimeTextView);
			holder.bankProcessStateTextView = (TextView) convertView.findViewById(R.id.bankProcessStateTextView);
			holder.bankProcessTimeTextView = (TextView) convertView.findViewById(R.id.bankProcessTimeTextView);
			holder.successStateTextView = (TextView) convertView.findViewById(R.id.successStateTextView);
			holder.successTimeTextView = (TextView) convertView.findViewById(R.id.successTimeTextView);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		WithdrawalMoneyAppDto dto = withdrawalList.get(position);

		holder.topLayout.setOnClickListener(new MyOnClickListener(convertView));

		holder.withdrawalMoneyTextView.setText("提现 ￥" + dto.getMoney() + " 到银行卡");
		holder.withdrawalTimeTextView.setText(dto.getBeginTime());
		holder.bankNameTextView.setText(BankUtil.getBankFromCode(dto.getBankId(), this.context).getName());
		holder.transferNumTextView.setText("提现流水号：" + dto.getOrderNum());
		holder.tailNumTextView.setText(dto.getTailNumber());
		if (dto.getRemark() == null || TextUtils.isEmpty(dto.getRemark()) || TextUtils.equals("null", dto.getRemark())) {
			holder.remarkTextView.setVisibility(View.GONE);
		} else {
			holder.remarkTextView.setVisibility(View.VISIBLE);
			holder.remarkTextView.setText(dto.getRemark());
		}

		// 从业务逻辑上，一开始的状态就到银行处理中
		holder.applyImageView.setBackgroundResource(R.drawable.withdrawal_apply);
		holder.progressImageView_1.setBackgroundResource(R.drawable.withdrawal_progess_done);
		holder.bankProcessImageView.setBackgroundResource(R.drawable.withdrawal_bankprocess);

		holder.applyStateTextView.setText("转出申请成功");
		holder.applyTimeTextView.setText(dto.getBeginTime());
		holder.bankProcessStateTextView.setText("银行处理中");
		holder.bankProcessTimeTextView.setText(dto.getSendTime());
		holder.successStateTextView.setText("转出到卡");
		holder.successTimeTextView.setText(dto.getEndTime());

		// 提现状态 b提现失败 c提现确认中 d提现成功
		switch (dto.getStatus()) {
		case 'b':
			holder.withdrawalStateTextView.setText("提现失败");
			holder.withdrawalStateTextView.setTextColor(context.getResources().getColor(R.color.redme));
			holder.progressImageView_2.setBackgroundResource(R.drawable.withdrawal_progess_done);
			holder.successImageView.setBackgroundResource(R.drawable.withdrawal_failure);
			holder.bankProcessStateTextView.setTextColor(context.getResources().getColor(R.color.gray_1));
			holder.successStateTextView.setTextColor(context.getResources().getColor(R.color.redme));
			break;

		case 'c':
			holder.withdrawalStateTextView.setText("银行处理中");
			holder.withdrawalStateTextView.setTextColor(context.getResources().getColor(R.color.orange));
			holder.progressImageView_2.setBackgroundResource(R.drawable.withdrawal_progess_watting);
			holder.successImageView.setBackgroundResource(R.drawable.withdrawal_waitting);
			holder.bankProcessStateTextView.setTextColor(context.getResources().getColor(R.color.orange));
			holder.successStateTextView.setTextColor(context.getResources().getColor(R.color.gray_1));
			break;

		case 'd':
			holder.withdrawalStateTextView.setText("提现成功");
			holder.withdrawalStateTextView.setTextColor(context.getResources().getColor(R.color.blueme));
			holder.progressImageView_2.setBackgroundResource(R.drawable.withdrawal_progess_done);
			holder.successImageView.setBackgroundResource(R.drawable.withdrawal_success);
			holder.bankProcessStateTextView.setTextColor(context.getResources().getColor(R.color.gray_1));
			holder.successStateTextView.setTextColor(context.getResources().getColor(R.color.blueme));
			break;
		}

		return convertView;
	}

	private class ViewHolder {
		private LinearLayout topLayout;
		private ImageView indicatorImageView;
		private TextView withdrawalMoneyTextView; // 提现 0.00元
		private TextView withdrawalTimeTextView; // 提现申请时间
		private TextView withdrawalStateTextView; // 提现状态

		// 下面是详情
		private LinearLayout detailLayout;
		private TextView bankNameTextView;
		private TextView tailNumTextView;
		private TextView transferNumTextView;
		private TextView remarkTextView;

		private ImageView applyImageView;
		private ImageView progressImageView_1;
		private ImageView bankProcessImageView;
		private ImageView progressImageView_2;
		private ImageView successImageView;

		private TextView applyStateTextView;
		private TextView applyTimeTextView;
		private TextView bankProcessStateTextView;
		private TextView bankProcessTimeTextView;
		private TextView successStateTextView;
		private TextView successTimeTextView;

	}

	private class MyOnClickListener implements OnClickListener {
		private View convertView;

		public MyOnClickListener(View convertView) {
			this.convertView = convertView;
		}

		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) convertView.getTag();

			if (holder.detailLayout.getVisibility() == View.VISIBLE) {
				holder.detailLayout.setVisibility(View.GONE);
				holder.indicatorImageView.setBackgroundResource(R.drawable.withdrawal_indicator_right);

			} else {
				holder.detailLayout.setVisibility(View.VISIBLE);
				holder.indicatorImageView.setBackgroundResource(R.drawable.withdrawal_indicator_down);
			}

			holder.detailLayout.invalidate();

		}

	}

}
