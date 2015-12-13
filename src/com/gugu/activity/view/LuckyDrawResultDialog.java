package com.gugu.activity.view;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gugu.client.Constants;
import com.gugu.utils.FileUtil;
import com.gugu.utils.LotteryModel;
import com.gugu.utils.WechatUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wufriends.gugu.R;

public class LuckyDrawResultDialog extends Dialog {

	private String kScreenName = "gugu_order.jpg";

	private TextView rewardValueTextView = null;
	private ImageView closeImageView = null;
	private Button confirmBtn = null;

	private OnConfirmListener confirmListener = null;

	private Activity context = null;

	private LotteryModel lottery;

	public LuckyDrawResultDialog(Activity context, LotteryModel lottery) {
		this(context, R.style.ProgressHUD, lottery);
	}

	public LuckyDrawResultDialog(Activity context, int theme, LotteryModel lottery) {
		super(context, theme);

		this.lottery = lottery;

		this.initView(context);
	}

	private void initView(final Activity context) {
		this.context = context;

		this.setContentView(R.layout.layout_luckydraw_result);

		this.setCanceledOnTouchOutside(false);
		this.setCancelable(true);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.dimAmount = 0.8f;
		this.getWindow().setAttributes(lp);

		rewardValueTextView = (TextView) this.findViewById(R.id.rewardValueTextView);
		rewardValueTextView.setText(this.lottery.getDesc());

		closeImageView = (ImageView) this.findViewById(R.id.closeImageView);
		closeImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		confirmBtn = (Button) this.findViewById(R.id.confirmBtn);

		if (this.lottery.getId().equals("INTEGRAL20")) {
			confirmBtn.setText("知道了");
		} else if (this.lottery.getId().equals("QT_BONUS2")) {
			confirmBtn.setText("请到抢投红包中领取");
		} else {
			confirmBtn.setText("已存入您的账户余额");
		}

		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// screenshot();
				// shareToTimeline();

				dismiss();

				if (confirmListener != null) {
					confirmListener.onConfirm();
				}
			}
		});

	}

	public void setOnConfirmListener(OnConfirmListener listener) {
		this.confirmListener = listener;
	}

	public interface OnConfirmListener {
		public void onConfirm();
	}

	// 分享到朋友圈
	private void shareToTimeline() {
		File file = new File(FileUtil.getFilePath() + kScreenName);
		if (!file.exists()) {
			Toast.makeText(context, "分享失败，没有找到图片", Toast.LENGTH_LONG).show();
			return;
		}

		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(FileUtil.getFilePath() + kScreenName);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.title = "鼓鼓理财－员工理财神器";
		msg.description = "鼓鼓理财";

		Bitmap bmp = BitmapFactory.decodeFile(FileUtil.getFilePath() + kScreenName);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		bmp.recycle();
		msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID).sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	private void screenshot() {
		// 截取全屏,不带弹出框，肯定不行
		// View view = context.getWindow().getDecorView();

		// 只截取弹出框
		View view = this.findViewById(android.R.id.content);
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap != null) {
			try {
				FileOutputStream out = new FileOutputStream(FileUtil.getFilePath() + kScreenName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.e("===", "screen shot  is null !!!");
		}
	}

}
