package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.PropertyDeductionAppDto;
import com.gugu.client.Constants;
import com.gugu.client.net.ImageCacheManager;
import com.wufriends.gugu.R;

/**
 * Created by sth on 11/9/15.
 */
public class PropertyRecordLayout extends LinearLayout {

    private CustomNetworkImageView logoImageView;
    private TextView moneyTextView;
    private TextView timeTextView;
    private TextView companyTextView;

    public PropertyRecordLayout(Context context) {
        super(context);

        initView(context);
    }

    public PropertyRecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_property_record, this);

        this.logoImageView = (CustomNetworkImageView) this.findViewById(R.id.logoImageView);
        this.moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        this.timeTextView = (TextView) this.findViewById(R.id.timeTextView);
        this.companyTextView = (TextView) this.findViewById(R.id.companyTextView);
    }

    public void setValue(PropertyDeductionAppDto dto) {
        this.logoImageView.setImageUrl(Constants.HOST_IP + dto.getImg(), ImageCacheManager.getInstance().getImageLoader());
        this.moneyTextView.setText(dto.getMoney() + " 元");
        this.timeTextView.setText(dto.getTimeStr());
        this.companyTextView.setText(dto.getCompanyName());
    }
}
