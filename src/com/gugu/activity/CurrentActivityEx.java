package com.gugu.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyHQInfoAppDto;
import com.gugu.activity.view.CircleProgress;
import com.gugu.activity.view.InvestmentCurrentLayout;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response;

/**
 * Created by sth on 9/2/15.
 */
public class CurrentActivityEx extends BaseActivity implements View.OnClickListener {

    private InvestmentCurrentLayout layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current_ex);

        ((TextView)this.findViewById(R.id.titleTextView)).setText("鼓鼓活期");
        this.findViewById(R.id.backBtn).setOnClickListener(this);

        LinearLayout contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
        layout = new InvestmentCurrentLayout(this);
        contentLayout.addView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        layout.requestHQInfo("正在请求数据...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
