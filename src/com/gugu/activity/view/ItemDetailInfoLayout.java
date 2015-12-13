package com.gugu.activity.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.baggugu.dto.app.DebtPackageInfoAppDto;
import com.wufriends.gugu.R;
import com.gugu.activity.ShowWebViewActivity;
import com.gugu.client.Constants;

public class ItemDetailInfoLayout extends LinearLayout {

    private TextView titleTextView;
    private TextView valueTextView;

    private Context context;

    private DebtPackageInfoAppDto infoDto;

    public ItemDetailInfoLayout(Context context, DebtPackageInfoAppDto infoDto) {
        super(context);

        this.infoDto = infoDto;

        this.initView(context);
    }

    public ItemDetailInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_item_detail_info, this);

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        valueTextView = (TextView) this.findViewById(R.id.valueTextView);
    }

    public void setValue(String title, String value) {
        titleTextView.setText(title);
        valueTextView.setText(value);
    }

    // 设置管理费率
    public void setManageRate() {
        titleTextView.setText("管理费率");
        String text = "见鼓鼓投资协议";
        this.setClickableSpan(text, 1, text.length(), "鼓鼓投资协议", Constants.PROTOCOL_IP);
    }

    // 提前赎回转让费
    public void setTransferRate() {
        titleTextView.setText("提前赎回转让费");

        String text = "";
        if (infoDto.getType() == 'b') {
            text = "保障本金，只收取所获得收益的50%服务费，全行业最低。见鼓鼓投资协议";
            this.setClickableSpan(text, 28, text.length(), "鼓鼓投资协议", Constants.PROTOCOL_IP);
        } else if (infoDto.getType() == 'a') {
            text = "扣除15天收益，不足15天全部扣除";
            valueTextView.setText(text);
        }
    }

    // 红包奖励
    public void setLuckMoney() {
        titleTextView.setText("红包奖励");
        String text = "见红包说明";
        this.setClickableSpan(text, 1, text.length(), "红包说明", Constants.PROTOCOL_IP);
    }

    // 有偿担保
    public void setGuarantee() {
        titleTextView.setText("有偿担保");
        String text = "见第三方有偿担保协议";
        this.setClickableSpan(text, 1, text.length(), "第三方有偿担保协议", Constants.PROTOCOL_IP);
    }

    // ///////////////////////////

    private void setClickableSpan(String text, int start, int end, String webTitle, String webUrl) {
        String htmlLinkText = text;
        SpannableString spStr = new SpannableString(htmlLinkText);

        TouchableSpan clickSpan = new TouchableSpan();
        spStr.setSpan(clickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        clickSpan.setTitleAndURL(webTitle, webUrl);

        valueTextView.setText(spStr);
        valueTextView.setMovementMethod(new LinkTouchMovementMethod());
    }

    private class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }

    public class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;
        private int mNormalBackgroundColor;
        private int mPressedBackgroundColor;
        private int mNormalTextColor;
        private int mPressedTextColor;

        private String title;
        private String url;

        public TouchableSpan() {
            this(Color.parseColor("#1caff6"), Color.parseColor("#8dd9fd"), Color.parseColor("#ffffff"), Color.parseColor("#999999"));
        }

        public TouchableSpan(int normalTextColor, int pressedTextColor, int mNormalBackgroundColor, int pressedBackgroundColor) {
            mNormalTextColor = normalTextColor;
            mPressedTextColor = pressedTextColor;
            mPressedBackgroundColor = pressedBackgroundColor;
        }

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
            ds.bgColor = mIsPressed ? mPressedBackgroundColor : mNormalBackgroundColor;
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(context, ShowWebViewActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }

        public void setTitleAndURL(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }

}
