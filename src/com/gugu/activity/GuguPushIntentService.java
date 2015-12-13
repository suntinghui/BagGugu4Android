package com.gugu.activity;

import java.util.Map;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.wufriends.gugu.R;
import com.gugu.client.Constants;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.JsonUtil;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

public class GuguPushIntentService extends UmengBaseIntentService {

	// 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。
	@Override
	protected void onMessage(Context context, Intent intent) {
		super.onMessage(context, intent);

		Log.e("PUSH", "----------------收到推送了------------------------");

		FeedbackPush.getInstance(context).init(FeedBackactivity.class, true);
		if (FeedbackPush.getInstance(context).onFBMessage(intent)) {
			// The push message is reply from developer.
			return;
		}

		try {
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			UTrack.getInstance(context).trackMsgClick(msg);

			Map<String, String> map = JsonUtil.jsonToMap(msg.custom);
			String functionType = map.get("functionType").trim();
			String functionData = map.get("functionData").trim();

			Log.e("PUSH", functionType + " -- " + functionData);

			Intent actionIntent = jumpAction(context, Integer.parseInt(functionType), functionData);
			showNotification(context, msg, actionIntent);

			redCircleTip(Integer.parseInt(functionType));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void redCircleTip(int functionType) {
		switch (functionType) {
		case 5:
			ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.RED_CIRCLE_TIP_FRIEND, true).commit();
			break;

		case 6:
			ActivityUtil.getSharedPreferences().edit().putBoolean(Constants.RED_CIRCLE_TIP_INVESTMENT, true).commit();
			break;
		}

		Intent intent1 = new Intent(MainActivity.ACTION_RED_CIRCLE);
		sendBroadcast(intent1);

		Intent intent2 = new Intent(BagActivity.ACTION_BAG_RED_CIRCLE);
		sendBroadcast(intent2);
	}

	// 通知栏显示当前播放信息，利用通知和 PendingIntent来启动对应的activity
	public void showNotification(Context context, UMessage msg, Intent intent) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true);
		Notification mNotification = builder.build();
		mNotification.icon = R.drawable.ic_launcher;// notification通知栏图标
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		// 自定义布局
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_view);
		contentView.setImageViewResource(R.id.notification_large_icon, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.notification_title, msg.title);
		contentView.setTextViewText(R.id.notification_text, msg.text);
		mNotification.contentView = contentView;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法，但是必须定义contentIntent
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(103, mNotification);
	}

	private Intent jumpAction(Context context, int functionType, String functionData) {
		/**
		 * 跳转数据 json格式 a系统消息的时候是{"url":"{type}/{id}/html.app"} 跳转标示1定投列表 2抢投列表 3转让列表 4打开链接 5好友列表 6我的债权列表
		 */
		Intent intent = new Intent();

		try {
			switch (functionType) {

			case 0: // 无任何操作

				break;

			case 1: {
				if (!ActivityUtil.checkAppRunning(this)) {
					InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_DQ);

					intent = new Intent(context, MainActivity.class);
					intent.putExtra("INDEX", 1);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
			}
				break;

			case 2: {
				if (!ActivityUtil.checkAppRunning(this)) {
					InvestmentActivity.setDefaultType(InvestmentActivity.TYPE_HQ);

					intent = new Intent(context, MainActivity.class);
					intent.putExtra("INDEX", 1);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
			}
				break;

			case 4: {
				intent = new Intent(context, ShowWebViewActivity.class);
				intent.putExtra("title", "系统消息");
				intent.putExtra("url", Constants.HOST_IP + "/rpc/" + JsonUtil.jsonToMap(functionData).get("url"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
				break;

			case 5: {
				intent = new Intent(context, MyFriendsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
				break;

			case 6: {
				intent = new Intent(context, MyInvestmentExActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
				break;

			default:
				Log.e("PUSH", "没有找到相应的类型");
				if (!ActivityUtil.checkAppRunning(this)) {
					intent = new Intent(context, MainActivity.class);
					intent.putExtra("INDEX", 0);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return intent;
	}
}
