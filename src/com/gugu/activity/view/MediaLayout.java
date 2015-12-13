package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.MediaReportAppDto;
import com.gugu.activity.ShowWebViewActivity;
import com.gugu.client.Constants;
import com.gugu.client.net.ImageCacheManager;
import com.wufriends.gugu.R;

/**
 * Created by sth on 11/10/15.
 */
public class MediaLayout extends LinearLayout {

    private Context context = null;
    private LinearLayout rootLayout = null;
    private CustomNetworkImageView logoImageView = null;
    private TextView titleTextView = null;
    private TextView contentTextView = null;

    public MediaLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public MediaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_media, this);

        rootLayout = (LinearLayout) this.findViewById(R.id.rootLayout);
        logoImageView = (CustomNetworkImageView) this.findViewById(R.id.logoImageView);
        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        contentTextView = (TextView) this.findViewById(R.id.contentTextView);
    }

    public void setData(final MediaReportAppDto dto) {
        this.logoImageView.setImageUrl(Constants.HOST_IP + dto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
        titleTextView.setText(dto.getTitle());
        contentTextView.setText(dto.getContent());

        rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowWebViewActivity.class);
                intent.putExtra("title", dto.getTitle());
                intent.putExtra("url", dto.getUrl());
                context.startActivity(intent);
            }
        });
    }

}
