package com.gugu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gugu.client.Constants;
import com.wufriends.gugu.R;

/**
 * Created by sth on 11/9/15.
 */
public class WageNoOpenActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_wage_no_open);

        this.initView();
    }

    private void initView() {
        ((TextView) this.findViewById(R.id.titleTextView)).setText("薪资宝");
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        ((TextView) this.findViewById(R.id.rateTextView)).setText(this.getIntent().getStringExtra("rate") + "%");
        ((TextView) this.findViewById(R.id.telphoneTextView)).setText(Constants.PHONE_SERVICE);

        this.findViewById(R.id.dialLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constants.PHONE_SERVICE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
