package com.gugu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gugu.utils.FileUtil;
import com.wufriends.gugu.R;
import com.gugu.activity.gesture.GestureLockVerifyActivity;
import com.gugu.activity.gesture.GestureLockUtil;
import com.gugu.client.Constants;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.BankUtil;

public class SplashActivity extends BaseActivity {

	private LinearLayout layout = null;
	private static final int SplashImageCount = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏

		setContentView(R.layout.activity_splash);

		layout = (LinearLayout) this.findViewById(R.id.rootLayout);

		setSplashImage();

		BankUtil.getBankList(this);

		new SplashTask().execute();
	}

	private void setSplashImage() {// 设置启动界面的图片随机
		ArrayList<File> list = FileUtil.fileList(DownloadSplashImageService.DIR_NAME);

		int random = 1 + (int) (Math.random() * 100);

		if (list.size() == 0) {
			int index = random % SplashImageCount;
			int resId = R.drawable.splash_1;
			switch (index) {
				case 0:
					resId = R.drawable.splash_1;
					break;

				case 1:
					resId = R.drawable.splash_2;
					break;
			}

			setBground(getResources().openRawResource(resId));

		} else {

			try {
				InputStream is = new FileInputStream(list.get(random % list.size()));

				setBground(is);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.gotoMainActivity();
	}

	private void gotoMainActivity(){
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void setBground(InputStream is){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
		this.getWindow().setBackgroundDrawable(null);
		layout.setBackgroundDrawable(bd);
	}

	class SplashTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (GestureLockUtil.hasSetGestureUtil()) {
				Intent intent = new Intent(SplashActivity.this, GestureLockVerifyActivity.class);
				intent.putExtra("TYPE", GestureLockVerifyActivity.TYPE_LOGIN);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();

			} else {
				if (ActivityUtil.getSharedPreferences().getBoolean(Constants.FIRST_LANUCH, true)) {

					// 强制用户重新登录
					ActivityUtil.getSharedPreferences().edit().putString(Constants.Base_Token, "").commit();

					startActivityForResult(new Intent(SplashActivity.this, ProductTourActivity.class), 0);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				} else {
					gotoMainActivity();
				}
			}
		}
	}

}
