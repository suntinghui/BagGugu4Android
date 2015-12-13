package com.gugu.activity.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.utils.FileUtil;
import com.gugu.utils.WechatUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class RushRankingResultDialog extends Dialog {

	private String kScreenName = "gugu_order.jpg";

	private TextView rankTextView = null;
	private TextView rateTextView = null;
	private Button closeBtn = null;
	private Button confirmBtn = null;
	private LinearLayout contentLayout = null;

	private OnConfirmListener confirmListener = null;

	private Activity context = null;

	public RushRankingResultDialog(Activity context) {
		this(context, R.style.ProgressHUD);
	}

	public RushRankingResultDialog(Activity context, int theme) {
		super(context, theme);

		this.initView(context);
	}

	private void initView(Activity context) {
		this.context = context;

		this.setContentView(R.layout.layout_rush_result);

		this.setCanceledOnTouchOutside(false);
		this.setCancelable(true);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.dimAmount = 0.5f;
		this.getWindow().setAttributes(lp);

		rankTextView = (TextView) this.findViewById(R.id.rankTextView);
		rateTextView = (TextView) this.findViewById(R.id.rateTextView);
		contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

		closeBtn = (Button) this.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
		confirmBtn.setText("在朋友圈炫一下");
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				screenshot();

				shareToTimeline();

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

	public void setRank(int rank) {
		rankTextView.setText(String.format("%02d", rank));
	}

	public void setRate(String rate) {
		rateTextView.setText(rate + "%");
	}

	public void setRankList(List<Map<String, String>> list) {
		int index = 0;
		RushRankingLayout rankLayout = null;
		for (Map<String, String> map : list) {
			rankLayout = new RushRankingLayout(context);
			rankLayout.setValue(++index, map.get("telphone"), map.get("rate"));
			contentLayout.addView(rankLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
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

		// File file = new File(path);
		// if (!file.exists()) {
		// Toast.makeText(this, "分享失败，没有找到图片", Toast.LENGTH_LONG).show();
		// return;
		// }
		//
		// WXWebpageObject webpage = new WXWebpageObject();
		// webpage.webpageUrl = "http://www.nihaofenqi.com";
		// WXMediaMessage msg = new WXMediaMessage(webpage);
		// msg.title = "员工理财神器";
		// msg.description = "它的收益率竟然有这么高？！";
		// Bitmap bmp = BitmapFactory.decodeFile(path);
		// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		// bmp.recycle();
		// msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);
		//
		// SendMessageToWX.Req req = new SendMessageToWX.Req();
		// req.transaction = buildTransaction("webpage");
		// req.message = msg;
		// req.scene = SendMessageToWX.Req.WXSceneTimeline;
		// WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID).sendReq(req);
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
