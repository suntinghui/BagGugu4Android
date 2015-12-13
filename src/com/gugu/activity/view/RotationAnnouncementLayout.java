package com.gugu.activity.view;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wufriends.gugu.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 自动滚动的公告显示控件
 * 
 * @author sth
 * 
 */
public class RotationAnnouncementLayout extends LinearLayout {

	private Context context;
	private LinearLayout tipLayout;
	private List<Map<String, String>> list;

	private AnnouncementItemLayout itemLayout;

	public RotationAnnouncementLayout(Context context) {
		super(context);

		this.initView(context);
	}

	public RotationAnnouncementLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initView(context);
	}

	private void initView(Context context) {
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_announcement, this);

		tipLayout = (LinearLayout) this.findViewById(R.id.tipLayout);
	}

	public void onDestory() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void setData(List<Map<String, String>> list) {
		if (list.size() == 0)
			return;

		if (list.size() == 1) {
			AnnouncementItemLayout itemLayout = new AnnouncementItemLayout(context).setData(list.get(0));
			tipLayout.addView(itemLayout);
			return;
		}

		this.list = list;

		timer.schedule(task, 0, 2500);
	}

	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	int i = 0;
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				try {
					tipLayout.removeView(itemLayout);
				} catch (Exception e) {
				}

				itemLayout = new AnnouncementItemLayout(context);
				itemLayout.setVisibility(View.INVISIBLE);

				Map<String, String> map = list.get(i % list.size());

				itemLayout.setData(map);
				tipLayout.addView(itemLayout);

				in();
				out();

				i++;
			}

				break;
			}
			super.handleMessage(msg);
		}
	};

	private void in() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				itemLayout.setVisibility(View.VISIBLE);
				YoYo.with(Techniques.SlideInUp).duration(500).playOn(itemLayout);
			}
		}, 0);
	}

	private void out() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				YoYo.with(Techniques.SlideOutUp).duration(500).playOn(itemLayout);
			}
		}, 2000);
	}

}
