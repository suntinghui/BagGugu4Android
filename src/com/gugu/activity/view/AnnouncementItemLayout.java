package com.gugu.activity.view;

import java.util.Map;

import com.wufriends.gugu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自动滚动的公告显示控件
 * 
 * @author sth
 * 
 */
public class AnnouncementItemLayout extends LinearLayout {

	private TextView telphoneTextView;
	private TextView typeTextView;
	private TextView moneyTextView;
	private TextView rateTextView;

	public AnnouncementItemLayout(Context context) {
		super(context);

		this.initView(context);
	}

	private AnnouncementItemLayout initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.item_rotation_ann, this);

		telphoneTextView = (TextView) this.findViewById(R.id.telphoneTextView);
		typeTextView = (TextView) this.findViewById(R.id.typeTextView);
		moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
		rateTextView = (TextView) this.findViewById(R.id.rateTextView);

		return this;
	}

	public AnnouncementItemLayout setData(Map<String, String> map) {
		telphoneTextView.setText(map.get("telphone"));
		moneyTextView.setText("￥" + map.get("money"));
		typeTextView.setText(map.get("type"));
		rateTextView.setText(map.get("earnings"));

		return this;
	}

}
