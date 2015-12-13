package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wufriends.gugu.R;

public class BonusFlyGoneDialog extends Dialog {

    private Button confirmBtn = null;

    private TextView telphoneNum = null;//发送至的电话号码

    private OnConfirmListener confirmListener = null;

    private Activity context = null;

    private String telephone = "";

    public BonusFlyGoneDialog(Activity context, String telphoneNum) {
        this(context, R.style.ProgressHUD, telphoneNum);

    }

    public BonusFlyGoneDialog(Activity context, int theme, String telphoneNum) {
        super(context, theme);
        this.telephone = telphoneNum;


        this.initView(context);
    }

    private void initView(final Activity context) {
        this.context = context;

        this.setContentView(R.layout.layout_bonus_gone_dialog);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        this.getWindow().setAttributes(lp);


        telphoneNum = (TextView) findViewById(R.id.telphoneNum);
        telphoneNum.setText(telephone);

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setText("知道了");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


}
