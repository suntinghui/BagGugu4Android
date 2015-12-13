package com.gugu.activity.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wufriends.gugu.R;

public class BonusFlyComeDialog extends Dialog {

    private Button confirmBtn = null;

    private TextView telphoneNum = null;//发送至的电话号码
    private TextView remarkTextView = null; // 留言

    private OnConfirmListener confirmListener = null;

    private Activity context = null;

    private String telephone = "";
    private String remark = "";

    public BonusFlyComeDialog(Activity context, String telphoneNum, String remark) {
        this(context, R.style.ProgressHUD, telphoneNum, remark);

    }

    public BonusFlyComeDialog(Activity context, int theme, String telphoneNum, String remark) {
        super(context, theme);

        this.telephone = telphoneNum;
        this.remark = remark;

        this.initView(context);
    }

    private void initView(final Activity context) {
        this.context = context;

        this.setContentView(R.layout.layout_bonus_come_dialog);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        this.getWindow().setAttributes(lp);


        telphoneNum = (TextView) findViewById(R.id.telphoneNum);
        telphoneNum.setText(this.telephone);

        remarkTextView = (TextView) this.findViewById(R.id.remarkTextView);
        remarkTextView.setText(this.remark);

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
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
