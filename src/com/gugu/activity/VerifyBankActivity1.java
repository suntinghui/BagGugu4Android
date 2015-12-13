package com.gugu.activity;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wufriends.gugu.R;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.IDCardValidate;

/**
 * 绑定银行卡
 * 
 * @author sth
 * 
 */
public class VerifyBankActivity1 extends BaseActivity implements OnClickListener {

	private List<String> bankNameList = Arrays.asList(new String[] { "中国银行", "中国农业银行", "中国建设银行", "中国工商银行", "交通银行", "招商银行", "浦发银行", "广发银行", "中信银行", "光大银行", "兴业银行", "中国民生银行", "华夏银行", "中国平安银行", "中国邮政储蓄银行" });
	private List<Integer> bankLogoList = Arrays.asList(new Integer[] { R.drawable.bank_1, R.drawable.bank_2, R.drawable.bank_3, R.drawable.bank_4, R.drawable.bank_5, R.drawable.bank_6, R.drawable.bank_7, R.drawable.bank_8, R.drawable.bank_9, R.drawable.bank_10, R.drawable.bank_11, R.drawable.bank_12, R.drawable.bank_13, R.drawable.bank_14, R.drawable.bank_15 });

	private EditText realnameEditText;
	private EditText idCardEditText;
	private TextView bankTextView;
	private EditText bankNoEditText;
	private Button confirmBtn;
	private Spinner bankSpinner;

	private int selectedBankIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_verify_bank_1);

		initView();
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("绑定银行卡");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);

		realnameEditText = (EditText) this.findViewById(R.id.realnameEditText);
		idCardEditText = (EditText) this.findViewById(R.id.idCardEditText);

		bankTextView = (TextView) this.findViewById(R.id.bankTextView);
		bankTextView.setOnClickListener(this);
		bankTextView.setText(bankNameList.get(0));
		bankTextView.setCompoundDrawables(getSelectedDrawable(bankLogoList.get(0)), null, null, null);

		bankNoEditText = (EditText) this.findViewById(R.id.bankNoEditText);
		bankSpinner = (Spinner) this.findViewById(R.id.bankSpinner);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.setResult(RESULT_CANCELED);
			this.finish();
			break;

		case R.id.bankTextView:
			chooseBank();
			break;

		case R.id.confirmBtn:
			if (checkValue()) {
				requestVerifyBank();
			}
			break;
		}
	}

	private boolean checkValue() {
		String realname = realnameEditText.getText().toString().trim();
		String idCard = idCardEditText.getText().toString().trim();
		String bank = bankTextView.getText().toString().trim();
		String bankNo = bankNoEditText.getText().toString().trim();

		String idValidate = "";
		try {
			idValidate = IDCardValidate.IDCardValidate(idCard);
		} catch (Exception e) {
			e.printStackTrace();
			idValidate = "身份证号输入错误";
		}

		if (TextUtils.isEmpty(realname)) {
			Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(realnameEditText);
			return false;

		} else if (TextUtils.isEmpty(idCard)) {
			Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(idCardEditText);
			return false;

		} else if (!idValidate.equals("")) {
			Toast.makeText(this, idValidate, Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(idCardEditText);
			return false;
		} else if (TextUtils.isEmpty(bank)) {
			Toast.makeText(this, "请选择银行", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(bankTextView);
			return false;
		} else if (TextUtils.isEmpty(bankNo)) {
			Toast.makeText(this, "请输入银行卡号", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(bankNoEditText);
			return false;

		} else if (bankNo.length() < 16) {
			Toast.makeText(this, "银行卡号格式不正确", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(bankNoEditText);
			return false;
		}

		return true;
	}

	private void requestVerifyBank() {
		Toast.makeText(this, "去绑定", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

	// 选择关系
	private void chooseBank() {
		final SpinnerAdapter adapter = new SpinnerAdapter(this);
		// bankSpinner.setPrompt("请选择银行");
		bankSpinner.setAdapter(adapter);
		adapter.setSelectedIndex(0);
		bankSpinner.setSelection(selectedBankIndex);
		bankSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				bankTextView.setText(bankNameList.get(position));
				bankTextView.setCompoundDrawables(getSelectedDrawable(bankLogoList.get(position)), null, null, null);
				selectedBankIndex = position;

				adapter.setSelectedIndex(position);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		bankSpinner.performClick();
	}

	private class ViewHolder {
		private LinearLayout contentLayout;
		private TextView bankNameTextView;
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
			return bankNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return bankNameList.get(position);
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
				convertView = mInflater.inflate(R.layout.spinner_bank_item, null);

				holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
				holder.bankNameTextView = (TextView) convertView.findViewById(R.id.bankNameTextView);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.bankNameTextView.setText(bankNameList.get(position));
			holder.bankNameTextView.setCompoundDrawables(getSelectedDrawable(bankLogoList.get(position)), null, null, null);
			holder.contentLayout.setSelected(selectedIndex == position);

			return convertView;
		}

		private int selectedIndex = 0;

		public void setSelectedIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
		}

	}

	private Drawable getSelectedDrawable(int resId) {
		Drawable drawable = getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

}
