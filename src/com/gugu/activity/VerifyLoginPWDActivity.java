package com.gugu.activity;

import org.codehaus.jackson.map.DeserializationConfig;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.wufriends.gugu.R;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.ViewUtil;

/**
 * 验证登录密码
 * 
 * @author sth
 * 
 */
public class VerifyLoginPWDActivity extends BaseActivity implements OnClickListener {

	private TextView stepTextView;
	private EditText pwdEditText;
	private Button confirmBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_verify_login_pwd);

		initView();
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("验证登录密码");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);

		stepTextView = (TextView) this.findViewById(R.id.stepTextView);
		SpannableString ss = new SpannableString("1/2");
		ss.setSpan(new AbsoluteSizeSpan(18, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new AbsoluteSizeSpan(14, true), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		stepTextView.setText(ss);
		stepTextView.setVisibility(View.GONE);

		pwdEditText = (EditText) this.findViewById(R.id.pwdEditText);
	}

	public void onResume() {
		super.onResume();

		ViewUtil.dropoutView(stepTextView, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.setResult(RESULT_CANCELED);
			this.finish();
			break;

		case R.id.confirmBtn:
			if (checkValue()) {
				requestVerifyLoginPwd();
			}
			break;
		}

	}

	private boolean checkValue() {
		String pwd = pwdEditText.getText().toString().trim();

		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(pwdEditText);
			return false;
		} else if (pwd.length() < 6) {
			Toast.makeText(this, "登录密码为6－20位", Toast.LENGTH_SHORT).show();
			ActivityUtil.shakeView(pwdEditText);
			return false;
		}

		return true;
	}

	private void requestVerifyLoginPwd() {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("password", pwdEditText.getText().toString().trim());

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_VALID_LOGIN_PWD, tempMap, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						Intent intent = new Intent(VerifyLoginPWDActivity.this, SetTransferPWDActivity.class);
						intent.putExtra("TYPE", SetTransferPWDActivity.TYPE_UPDATE);
						intent.putExtra("loginPassword", pwdEditText.getText().toString().trim());
						VerifyLoginPWDActivity.this.startActivityForResult(intent, 0);

					} else {
						Toast.makeText(VerifyLoginPWDActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

}
