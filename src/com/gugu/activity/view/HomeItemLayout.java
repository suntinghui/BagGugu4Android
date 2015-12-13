package com.gugu.activity.view;

import com.wufriends.gugu.R;
import com.gugu.utils.StringUtil;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeItemLayout extends FrameLayout {

    private ImageView leftImageView;
    private View topLine;
    private View bottomLine;

    private ShimmerTextView titleTextView;
    private TextView tipTextView;

    private TextView sbTextView;
    private TextView mbTextView;
    private TextView userCountTextView;
    private TextView completeTextView;
    private TextView surplusMoneyTextView;

    private FrameLayout circleLayout;
    private CircleProgress circleProgress;
    private TextView rateTextView;
    private TextView percentTextView;
    private TextView addTextView;
    private TextView stateTextView;

    public HomeItemLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public HomeItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_home_item, this);

        leftImageView = (ImageView) this.findViewById(R.id.leftImageView);
        topLine = this.findViewById(R.id.topLine);
        bottomLine = this.findViewById(R.id.bottomLine);
        titleTextView = (ShimmerTextView) this.findViewById(R.id.titleTextView);
        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        mbTextView = (TextView) this.findViewById(R.id.mbTextView);
        sbTextView = (TextView) this.findViewById(R.id.sbTextView);
        userCountTextView = (TextView) this.findViewById(R.id.userCountTextView);
        completeTextView = (TextView) this.findViewById(R.id.completeTextView);
        surplusMoneyTextView = (TextView) this.findViewById(R.id.surplusMoneyTextView);

        circleLayout = (FrameLayout) this.findViewById(R.id.circleLayout);

        circleProgress = (CircleProgress) this.findViewById(R.id.circleProgress);
        circleProgress.setType(CircleProgress.ARC);
        circleProgress.setPaintWidth(12);
        circleProgress.setBottomPaintColor(android.R.color.transparent);

        stateTextView = (TextView) this.findViewById(R.id.stateTextView);
        rateTextView = (TextView) this.findViewById(R.id.rateTextView);
        percentTextView = (TextView) this.findViewById(R.id.percentTextView);
        addTextView = (TextView) this.findViewById(R.id.addTextView);
        stateTextView = (TextView) this.findViewById(R.id.stateTextView);
    }

    public void setLeftImageView(int resId) {
        leftImageView.setImageResource(resId);
    }

    public void setLineColor(int color) {
        topLine.setBackgroundColor(color);
        bottomLine.setBackgroundColor(color);
        circleProgress.setSubPaintColor(color);
    }

    public void setTitle(String title, boolean shimmerFlag) {
        titleTextView.setText(title);

        if (shimmerFlag) {
            Shimmer shimmer = new Shimmer();
            shimmer.setDuration(1800);
            shimmer.start(titleTextView);
        }
    }

    public TextView getSbTextView() {
        return sbTextView;
    }

    public TextView getMbTextView() {
        return mbTextView;
    }

    public TextView getTipTextView() {
        return tipTextView;
    }

    public TextView getUserCountTextView() {
        return userCountTextView;
    }

    public TextView getCompleteTextView() {
        return completeTextView;
    }

    public TextView getSurplusMoneyTextView() {
        return surplusMoneyTextView;
    }

    public TextView getRateTextView() {
        return rateTextView;
    }

    public TextView getPercentTextView() {
        return percentTextView;
    }

    public TextView getAddTextView() {
        return addTextView;
    }

    public TextView getStateTextView() {
        return stateTextView;
    }

    public CircleProgress getCircleProgress() {
        return circleProgress;
    }

    public FrameLayout getCircleLayout() {
        return circleLayout;
    }

    public void setColor(int color) {
        rateTextView.setTextColor(color);
        percentTextView.setTextColor(color);
        addTextView.setTextColor(color);
        stateTextView.setTextColor(color);
    }

    public void setProgress(final int progress) {
        new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                for (int i = 0; i <= 100; i++) {
                    if (i > progress)
                        break;

                    publishProgress(i);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                circleProgress.setmSubCurProgress(values[0]);
            }
        }.execute(0);
    }
}
