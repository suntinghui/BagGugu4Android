package com.gugu.activity.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.DebtPackageAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.VoteOfRushActivity;

public class VoteOfRushAdapter extends BaseAdapter {

	private Context context;
	private List<DebtPackageAppDto> deptList = new ArrayList<DebtPackageAppDto>();

	public VoteOfRushAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<DebtPackageAppDto> tempList) {
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
		ViewHolder holder = null;

		if (null == convertView) {
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.item_vote_rush, null);

			holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
			holder.numTextView = (TextView) convertView.findViewById(R.id.numTextView);
			holder.todayImageView = (ImageView) convertView.findViewById(R.id.todayImageView);
			holder.rateTextView = (TextView) convertView.findViewById(R.id.rateTextView);
			holder.minRateTextView = (TextView) convertView.findViewById(R.id.minRateTextView);
			holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
			holder.maxTimeTextView = (TextView) convertView.findViewById(R.id.maxTimeTextView);
			holder.limitAmountTextView = (TextView) convertView.findViewById(R.id.limitAmountTextView);
			holder.statusImageView = (ImageView) convertView.findViewById(R.id.statusImageView);
			holder.bottomDividerView = convertView.findViewById(R.id.bottomDividerView);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DebtPackageAppDto debtDto = deptList.get(position);

		holder.numTextView.setText(debtDto.getNum());
		holder.rateTextView.setText(debtDto.getMaxRate() + "%");
		holder.minRateTextView.setText("最低" + debtDto.getMinRate() + "%");
		holder.timeTextView.setText(debtDto.getMinPeriod() + "");
		holder.maxTimeTextView.setText("最多" + debtDto.getMaxPeriod() + "天");
		holder.limitAmountTextView.setText(debtDto.getLimitMoney());
		holder.todayImageView.setVisibility(View.GONE);

		// a发行中 b已满返息中 c已完成
		if (debtDto.getStatus() == 'a') {
			if (debtDto.isToday()) {
				holder.todayImageView.setBackgroundResource(R.drawable.today_selling);
				holder.todayImageView.setVisibility(View.VISIBLE);
			}

			holder.statusImageView.setBackgroundResource(R.drawable.investment_status_sell);

		} else if (debtDto.getStatus() == 'b') {
			if (debtDto.isToday()) {
				holder.todayImageView.setBackgroundResource(R.drawable.today_sold);
				holder.todayImageView.setVisibility(View.VISIBLE);
			}

			holder.statusImageView.setBackgroundResource(R.drawable.investment_status_repayment);

		} else if (debtDto.getStatus() == 'c') {
			holder.statusImageView.setBackgroundResource(R.drawable.investment_status_complete);

		} else if (debtDto.getStatus() == 'd') {
			// onley for rush 开抢
			holder.statusImageView.setBackgroundResource(R.drawable.investment_status_rush);
		}

		if (deptList.size() > 2 && position == deptList.size() - 1) {
			holder.bottomDividerView.setVisibility(View.VISIBLE);
		} else {
			holder.bottomDividerView.setVisibility(View.GONE);
		}

		holder.contentLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, VoteOfRushActivity.class);
				intent.putExtra("id", debtDto.getId() + "");
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	public final class ViewHolder {
		private LinearLayout contentLayout;
		private TextView numTextView;
		private ImageView todayImageView;
		private TextView rateTextView;
		private TextView minRateTextView;
		private TextView timeTextView;
		private TextView maxTimeTextView;
		private TextView limitAmountTextView;
		private ImageView statusImageView;
		private View bottomDividerView;
	}

	private SpannableString getFormatValue(String text) {
		int length = text.length();
		SpannableString ss = new SpannableString(text);
		try {
			ss.setSpan(new AbsoluteSizeSpan(20, true), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(14, true), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}

}
