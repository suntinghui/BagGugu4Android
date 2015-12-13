package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gugu.activity.BaseActivity;
import com.wufriends.gugu.R;

/**
 * Created by sth on 10/17/15.
 */
public class ProgressPercentLayout extends LinearLayout {

    public static final int TYPE_BLUE = 0x01;
    public static final int TYPE_RED = 0x02;

    private BaseActivity context;

    private ProgressBar progressBar;
    private TextView valueTextView;
    private TextView percentTextView;

    public ProgressPercentLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public ProgressPercentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private ProgressPercentLayout initView(Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_progress_percent, this);

        this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        this.valueTextView = (TextView) this.findViewById(R.id.valueTextView);
        this.percentTextView = (TextView) this.findViewById(R.id.percentTextView);

        this.setProgress(0);

        return this;
    }

    public void setProgress(int progress) {
        this.progressBar.setProgress(progress);
        this.valueTextView.setText(progress + "");
    }

    public void setType(int type) {
        if (type == TYPE_BLUE) {
            this.progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar_blue_bg));
            this.valueTextView.setTextColor(context.getResources().getColor(R.color.blueme));
            this.percentTextView.setTextColor(context.getResources().getColor(R.color.blueme));

        } else if (type == TYPE_RED) {
            this.progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar_red_bg));
            this.valueTextView.setTextColor(context.getResources().getColor(R.color.redme));
            this.percentTextView.setTextColor(context.getResources().getColor(R.color.redme));
        }

        this.invalidate();
    }
}
