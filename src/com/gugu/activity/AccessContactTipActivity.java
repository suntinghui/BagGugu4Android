package com.gugu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gugu.utils.ActivityUtil;
import com.wufriends.gugu.R;

public class AccessContactTipActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_access_contact_tip);

		initView();
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("访问通讯录");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);
		
		Button accessBtn = (Button) this.findViewById(R.id.accessBtn);
		accessBtn.setOnClickListener(this);
		
		TextView tipTextView = (TextView) this.findViewById(R.id.tipTextView);
		
		// 可能是真的没有权限了。
		String tip = this.getIntent().getStringExtra("TYPE");
		if (tip != null && tip.equals("ERROR")) {
			tipTextView.setText("请确认应用程序有访问通讯录的权限");
			tipTextView.setTextColor(getResources().getColor(R.color.redme));
		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;
			
		case R.id.accessBtn:
			ActivityUtil.getSharedPreferences().edit().putBoolean(InviteContactActivity.kACCESS_CONSTANT, true).commit();
			
			Intent intent = new Intent(this, InviteContactListActivity.class);
			this.startActivityForResult(intent, 0);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

}
