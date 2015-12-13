package com.gugu.activity.view;

import com.wufriends.gugu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class WithdrawalHeadView extends LinearLayout implements OnClickListener {

	private TextView typeTextView = null; // 定投本金
	private TextView transferTextView = null; // 赎回到
	private TextView descTextView = null; // 账户余额
	private TextView tipTextView = null; // 您可以随时赎回定投投资金额，这是我们对“鼓鼓理财”用户最好的安全保障！
	private TextView showTextView = null; // 折叠  展开
	private TextView markTextView = null;

	private int type;

	public static final int TYPE_DT = 0x001;
	public static final int TYPE_QT = 0x002;

	public WithdrawalHeadView(Context context, int type) {
		super(context);

		this.type = type;

		this.initView(context);
	}

	public WithdrawalHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private WithdrawalHeadView initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_withdrawal_header, this);

		typeTextView = (TextView) this.findViewById(R.id.typeTextView);
		transferTextView = (TextView) this.findViewById(R.id.transferTextView);
		descTextView = (TextView) this.findViewById(R.id.descTextView);
		tipTextView = (TextView) this.findViewById(R.id.tipTextView);
		
		showTextView = (TextView) this.findViewById(R.id.showTextView);
		showTextView.setOnClickListener(this);
		
		markTextView = (TextView) this.findViewById(R.id.markTextView);
		markTextView.setVisibility(View.GONE);

		this.refreshView();

		return this;
	}

	private void refreshView() {
		if (this.type == TYPE_DT) {
			this.typeTextView.setText("定投金额");
			this.transferTextView.setText("赎回到");
			this.descTextView.setText("账户余额");
			this.tipTextView.setText("随时赎回，提前赎回按年化收益3%计算");
			this.markTextView.setText("因您的收益按日到账，和分期(每个自然月平均为30日)存在账期冲突，为保证债务债权再次顺利对接，我们会扣除定投期内最近30天的收益。");

		} else if (this.type == TYPE_QT) {
			this.typeTextView.setText("抢投金额");
			this.transferTextView.setText("转让到");
			this.descTextView.setText("账户余额");
			this.tipTextView.setText("30天后可转让，待转让成功后扣除50%投资收益。");
			this.markTextView.setText("因抢投到的收益率具有极强的差异性，为保障最透明的债权对接关系，我们需要扣除您的相应收益，以保证对下一位投资人的利益。");
		}
	}

	// *_*
	public void setData(String value){
		if (this.type == TYPE_DT) {
			this.tipTextView.setText(value);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.showTextView:
			if (this.markTextView.getVisibility() == View.GONE) {
				this.markTextView.setVisibility(View.VISIBLE);
				this.showTextView.setText("折叠");
			} else {
				this.markTextView.setVisibility(View.GONE);
				this.showTextView.setText("展开");
			}
			
			
			break;
		}
	}

}
