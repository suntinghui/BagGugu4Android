package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.gugu.activity.MyRewardListExActivity;
import com.wufriends.gugu.R;

public class RewardGiveMoneyDialog extends Dialog implements View.OnClickListener {

    private Activity context = null;
    private ImageView mainImageView;

    public RewardGiveMoneyDialog(Context context) {
        this(context, R.style.ProgressHUD);

    }

    public RewardGiveMoneyDialog(Context context, int theme) {
        super(context, theme);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (Activity) context;

        this.setContentView(R.layout.layout_give_money_tip);

        this.mainImageView = (ImageView) this.findViewById(R.id.mainImageView);
        this.mainImageView.setOnClickListener(this);

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.6f;
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainImageView:
                this.dismiss();
                break;
        }
    }
}
