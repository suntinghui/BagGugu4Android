package com.gugu.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.view.CropImageView;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.ActivityUtil;

public class PersonalCenterActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = "PersonalCenterActivity";
	private LinearLayout userHeadLayout = null;// 用户头像
	private LinearLayout realnameLayout = null;
	private View realNameBottomLineView = null;
	private LinearLayout emergencyContactLayout = null; // 紧急联系人
	private LinearLayout wechatLayout = null;
	private LinearLayout emailLayout = null;
	private LinearLayout cardLayout = null;
	private LinearLayout transferPwdLayout = null; // 交易密码设置
	private LinearLayout modifyLoginPwdLayout = null; // 修改登录密码
	private LinearLayout modifyGesturePwdLayout = null; // 管理手势密码

	private TextView realnameStateTextView = null;
	private TextView emergencyContactStateTextView = null;
	private TextView wechatStateTextView = null;
	private TextView emailStateTextView = null;
	private TextView cardStateTextView = null;
	private TextView transferPwdStateTextView = null;
	private TextView modifyLoginStateTextView = null;
	private TextView modifyGesturePwdStateTextView = null;

	private Map<String, String> map = null;

	/* 头像上传相关 */
	private static String localTempImageFileName = "";
	private static final int FLAG_CHOOSE_IMG = 0x100;// 相册选择
	private static final int FLAG_CHOOSE_PHONE = 0x101;// 拍照
	private static final int FLAG_MODIFY_FINISH = 0x102;

	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
	public static final String IMAGE_PATH = "gugu";
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "images/screenshots");

	private CustomNetworkImageView userHeadImageView;
	private String headerImageStr = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_personal_center);

		initView();

	}

	public void onResume() {
		super.onResume();

		requestAllState();
	}

	private void initView() {
		TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
		titleTextView.setText("个人中心");

		Button backButton = (Button) this.findViewById(R.id.backBtn);
		backButton.setOnClickListener(this);

		userHeadLayout = (LinearLayout) this.findViewById(R.id.userHeadLayout);
		userHeadLayout.setOnClickListener(this);

		userHeadImageView = (CustomNetworkImageView) findViewById(R.id.user_head);
		userHeadImageView.setErrorImageResId(R.drawable.user_default_head);
		userHeadImageView.setDefaultImageResId(R.drawable.user_default_head);
		if (!TextUtils.isEmpty(this.getIntent().getStringExtra("logo"))) {
			userHeadImageView.setImageUrl(Constants.HOST_IP + this.getIntent().getStringExtra("logo") + "?random=" + ActivityUtil.getSharedPreferences().getString(Constants.HEAD_RANDOM, "0"), ImageCacheManager.getInstance().getImageLoader());
		}

		realnameLayout = (LinearLayout) this.findViewById(R.id.realnameLayout);
		realnameLayout.setOnClickListener(this);
		realNameBottomLineView = this.findViewById(R.id.realNameBottomLineView);

		emergencyContactLayout = (LinearLayout) this.findViewById(R.id.emergencyContactLayout);
		emergencyContactLayout.setOnClickListener(this);

		wechatLayout = (LinearLayout) this.findViewById(R.id.wechatLayout);
		wechatLayout.setOnClickListener(this);

		emailLayout = (LinearLayout) this.findViewById(R.id.emailLayout);
		emailLayout.setOnClickListener(this);

		cardLayout = (LinearLayout) this.findViewById(R.id.cardLayout);
		cardLayout.setOnClickListener(this);

		transferPwdLayout = (LinearLayout) this.findViewById(R.id.transferPwdLayout);
		transferPwdLayout.setOnClickListener(this);

		modifyLoginPwdLayout = (LinearLayout) this.findViewById(R.id.modifyLoginPwdLayout);
		modifyLoginPwdLayout.setOnClickListener(this);

		modifyGesturePwdLayout = (LinearLayout) this.findViewById(R.id.modifyGesturePwdLayout);
		modifyGesturePwdLayout.setOnClickListener(this);

		realnameStateTextView = (TextView) this.findViewById(R.id.realnameStateTextView);
		emergencyContactStateTextView = (TextView) this.findViewById(R.id.emergencyContactStateTextView);
		wechatStateTextView = (TextView) this.findViewById(R.id.wechatStateTextView);
		emailStateTextView = (TextView) this.findViewById(R.id.emailStateTextView);
		cardStateTextView = (TextView) this.findViewById(R.id.cardStateTextView);
		transferPwdStateTextView = (TextView) this.findViewById(R.id.transferPwdStateTextView);
		modifyLoginStateTextView = (TextView) this.findViewById(R.id.modifyLoginStateTextView);
		modifyGesturePwdStateTextView = (TextView) this.findViewById(R.id.modifyGesturePwdStateTextView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			this.finish();
			break;

		case R.id.userHeadLayout:
			CropImageView.CROP_TYPE = CropImageView.CROP_TYPE_HEADER;
			choosePic();
			break;

		case R.id.realnameLayout: {
			Intent intent = new Intent(this, VerifyRealnameActivity.class);
			intent.putExtra("status", map.get("REALNAME").charAt(0));
			this.startActivityForResult(intent, 0);
			break;
		}
		case R.id.emergencyContactLayout: { // 紧急联系人
			Intent intent = new Intent(this, VerifyEmergencyContactActivity.class);
			this.startActivityForResult(intent, 0);
			break;
		}
		case R.id.wechatLayout: {
			Intent intent = new Intent(this, VerifyWechatActivity.class);
			intent.putExtra("status", map.get("WEBCHAT").charAt(0));
			this.startActivityForResult(intent, 0);
			break;
		}
		case R.id.emailLayout: {
			Intent intent = new Intent(this, VerifyEmailActivity.class);
			intent.putExtra("status", map.get("EMAIL").charAt(0));
			this.startActivityForResult(intent, 0);
			break;
		}
		case R.id.cardLayout: {
			this.requestBankInfo();
			break;
		}
		case R.id.transferPwdLayout: {
			// 如果已经设置了交易密码，则进入提示界面;如果没有设置则直接进入设置界面
			if (map.get("TRANSACTION_PASSWORD").charAt(0) == 'a') {
				Intent intent = new Intent(this, SetTransferPWDActivity.class);
				intent.putExtra("TYPE", SetTransferPWDActivity.TYPE_SET);
				intent.putExtra("loginPassword", ""); // 初次设置不需要登录密码；修改需要登录密码。
				this.startActivityForResult(intent, 0);
			} else {
				Intent intent = new Intent(this, VerifyHasSetTransferPWDActivity.class);
				this.startActivityForResult(intent, 0);
			}
			break;
		}
		case R.id.modifyLoginPwdLayout: {
			Intent intent = new Intent(this, ModifyLoginPWDActivity.class);
			this.startActivity(intent);
			break;
		}
		case R.id.modifyGesturePwdLayout: {
			Intent intent = new Intent(this, ManageGestureLockActivity.class);
			this.startActivity(intent);
			break;
		}
		}

	}

	private void requestSetUserLogo() {
		if (null == headerImageStr || TextUtils.isEmpty(headerImageStr))
			return;

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("logo", headerImageStr);

		JSONRequest request = new JSONRequest(this, RequestEnum.USER_SET_LOGO, map, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
					AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

					Toast.makeText(PersonalCenterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						ActivityUtil.getSharedPreferences().edit().putString(Constants.HEAD_RANDOM, System.currentTimeMillis() + "").commit();

						requestHeadLogo();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在提交数据...");
	}
	
	private void requestHeadLogo() {
		JSONRequest request = new JSONRequest(this, RequestEnum.USER_ME, null, false, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, MyAppDto.class);
					AppMessageDto<MyAppDto> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						userHeadImageView.setImageUrl(Constants.HOST_IP + dto.getData().getLogoUrl() + "?random=" + ActivityUtil.getSharedPreferences().getString(Constants.HEAD_RANDOM, "0"), ImageCacheManager.getInstance().getImageLoader());
					} 

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, new ResponseErrorListener(this));

		this.addToRequestQueue(request, "正在更新头像...");
	}

	private void requestBankInfo() {
		JSONRequest request = new JSONRequest(this, RequestEnum.SECURITY_CENTER_BANK_INFO, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, Map.class);
					AppMessageDto<Map<String, String>> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						HashMap<String, String> map = (HashMap<String, String>) dto.getData();
						String bank_id = map.get("BANK_ID");
						if (null == bank_id || TextUtils.isEmpty(bank_id) || TextUtils.equals(bank_id, "null")) {
							Intent intent = new Intent(PersonalCenterActivity.this, BindingBankActivity.class);
							PersonalCenterActivity.this.startActivityForResult(intent, 0);

						} else {
							Intent intent = new Intent(PersonalCenterActivity.this, BindedBankActivity.class);
							intent.putExtra("MAP", map);
							PersonalCenterActivity.this.startActivityForResult(intent, 0);
						}

					} else {
						Toast.makeText(PersonalCenterActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, "正在请求数据...");
	}

	private void requestAllState() {
		JSONRequest request = new JSONRequest(this, RequestEnum.SECURITY_CENTER_INFO, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String jsonObject) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, Map.class);
					AppMessageDto<Map<String, String>> dto = objectMapper.readValue(jsonObject, type);

					if (dto.getStatus() == AppResponseStatus.SUCCESS) {
						map = dto.getData();

						responseAllState();
					} else {
						hideAllState();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		this.addToRequestQueue(request, null);
	}

	private void responseAllState() {
		try {
			realnameStateTextView.setText(getStateMsg(map.get("REALNAME").charAt(0)));
			emergencyContactStateTextView.setText(map.get("EMERGENCY_CONTACT").charAt(0) == 'a' ? "未设置" : "已设置");
			wechatStateTextView.setText(getStateMsg(map.get("WEBCHAT").charAt(0)));
			emailStateTextView.setText(getOtherStateMsg(map.get("EMAIL").charAt(0)));

			char cardState = map.get("BANK_CARD").charAt(0);
			cardStateTextView.setText(getOtherStateMsg(cardState));
			if (cardState == 'e') {
				realnameLayout.setVisibility(View.VISIBLE);
				realNameBottomLineView.setVisibility(View.VISIBLE);
			} else {
				realnameLayout.setVisibility(View.GONE);
				realNameBottomLineView.setVisibility(View.GONE);
			}

			transferPwdStateTextView.setText(map.get("TRANSACTION_PASSWORD").charAt(0) == 'a' ? "未设置" : "已设置");

			realnameStateTextView.setVisibility(View.VISIBLE);
			emergencyContactStateTextView.setVisibility(View.VISIBLE);
			wechatStateTextView.setVisibility(View.VISIBLE);
			emailStateTextView.setVisibility(View.VISIBLE);
			cardStateTextView.setVisibility(View.VISIBLE);
			transferPwdStateTextView.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * a未提交 b已提交待审核 c审核失败 d审核中 e审核通过
	 */
	private String getStateMsg(char c) {
		String msg = "未提交";
		switch (c) {
		case 'a':
			msg = "未提交";
			break;

		case 'b':
			msg = "已提交";
			break;

		case 'c':
			msg = "审核失败";
			break;

		case 'd':
			msg = "审核中";
			break;

		case 'e':
			msg = "已认证";
			break;

		default:
			msg = "未知状态";
			break;
		}
		return msg;
	}

	/**
	 * a未提交 b已提交待审核 c审核失败 d审核中 e审核通过
	 */
	private String getOtherStateMsg(char c) {
		String msg = "未绑定";
		switch (c) {
		case 'a':
			msg = "未绑定";
			break;

		case 'b':
			msg = "已提交";
			break;

		case 'c':
			msg = "审核失败";
			break;

		case 'd':
			msg = "确认中";
			break;

		case 'e':
			msg = "已绑定";
			break;

		default:
			msg = "未知状态";
			break;
		}
		return msg;
	}

	private void hideAllState() {
		realnameStateTextView.setVisibility(View.INVISIBLE);
		emergencyContactStateTextView.setVisibility(View.INVISIBLE);
		wechatStateTextView.setVisibility(View.INVISIBLE);
		emailStateTextView.setVisibility(View.INVISIBLE);
		cardStateTextView.setVisibility(View.INVISIBLE);
		transferPwdStateTextView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0) {
			this.requestAllState();

		} else if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) { // 选择图片
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor) {
						Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Log.i(TAG, "path=" + path);
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				} else {
					Log.i(TAG, "path=" + uri.getPath());
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == RESULT_OK) {// 拍照
			File f = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);

		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
			if (data != null) {
				final String path = data.getStringExtra("path");
				Log.i(TAG, "截取到的图片路径是 = " + path);

				Bitmap b = BitmapFactory.decodeFile(path);

				userHeadImageView.setImageBitmap(b);

				headerImageStr = bitmap2Base64(b);

				requestSetUserLogo();
			}
		}
	}

	private String bitmap2Base64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/**
	 * 用户头像上传
	 */
	/** 下面是选择照片 */
	private void choosePic() {
		final PopupWindow pop = new PopupWindow(this);

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_pick_photo, null);

		final LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.popupLayout);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		pop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				backgroundRecovery();
			}
		});

		RelativeLayout rootLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
		TextView cameraTextView = (TextView) view.findViewById(R.id.cameraTextView);
		TextView PhotoTextView = (TextView) view.findViewById(R.id.PhotoTextView);
		TextView cancelTextView = (TextView) view.findViewById(R.id.cancelTextView);
		rootLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		cameraTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();

				takePhoto();
			}
		});

		PhotoTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, FLAG_CHOOSE_IMG);
			}
		});

		cancelTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		pop.showAtLocation(findViewById(R.id.rootLayout), Gravity.BOTTOM, 0, 0);

		backgroundDarken();
	}

	private void backgroundDarken() {
		// 设置背景颜色变暗
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.6f;
		getWindow().setAttributes(lp);
	}

	private void backgroundRecovery() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	public void takePhoto() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				localTempImageFileName = "";
				localTempImageFileName = String.valueOf((new Date()).getTime()) + ".png";
				File filePath = FILE_PIC_SCREENSHOT;
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(filePath, localTempImageFileName);
				// localTempImgDir和localTempImageFileName是自己定义的名字
				Uri u = Uri.fromFile(f);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
				startActivityForResult(intent, FLAG_CHOOSE_PHONE);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
