package com.gugu.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.gugu.activity.BaseActivity;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.AdapterUtil;
import com.wufriends.gugu.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.w3c.dom.Text;

import java.util.List;

import com.android.volley.Response;

/**
 * Created by sth on 8/24/15.
 */
public class TextTextLayout extends LinearLayout {

    private BaseActivity context;

    private LinearLayout contentLayout;
    private TextView titleTextView;
    private TextView valueTextView;

    public TextTextLayout(BaseActivity context) {
        super(context);

        this.initView(context);
    }

    public TextTextLayout(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_text_text, this);

        contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);
        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        valueTextView = (TextView) this.findViewById(R.id.valueTextView);
    }

    public void setData(String title, String value) {
        titleTextView.setText(title);
        valueTextView.setText(value);
    }

    public void setBgColor(boolean gray){
        if (gray) {
            contentLayout.setBackgroundResource(R.drawable.bg_orange_gray);
        } else {
            contentLayout.setBackgroundResource(R.drawable.bg_white_gray);
        }
    }

    public TextView getTitleTextView() {
        return this.titleTextView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }


}
