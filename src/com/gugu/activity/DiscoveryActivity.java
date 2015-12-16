package com.gugu.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.DiscoveryAppDto;
import com.ares.baggugu.dto.app.ImageAppDto;
import com.ares.baggugu.dto.app.LinkArticle;
import com.ares.baggugu.dto.app.MediaReportAppDto;
import com.ares.baggugu.dto.app.MyDebtPackage;
import com.ares.baggugu.dto.app.Paginable;
import com.gugu.activity.view.CustomNetworkImageView;
import com.gugu.activity.view.MediaImagePagerAdapter;
import com.gugu.activity.view.MediaLayout;
import com.gugu.client.ActivityManager;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.ImageCacheManager;
import com.gugu.client.net.JSONRequest;
import com.gugu.client.net.ResponseErrorListener;
import com.gugu.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;
import com.whos.swiperefreshandload.view.SwipeRefreshLayout;
import com.wufriends.gugu.R;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import cn.trinea.android.view.autoscrollviewpager.ImagePagerAdapter;

/**
 * Created by sth on 11/8/15.
 */
public class DiscoveryActivity extends BaseActivity implements View.OnClickListener {

    private AutoScrollViewPager viewPager = null;
    private ImagePagerAdapter viewPagerAdapter = null;
    private List<ImageAppDto> imageURLList = new ArrayList<ImageAppDto>();
    private LinearLayout indicatorLayout;
    private ImageView[] indicatorImageViews = null;

    private TextView zizhiTextView = null;// 公司资质
    private TextView baogaoTextView = null; // 年度报告
    private TextView aboutTextView = null; // 关于我们

    private LinearLayout propertyLayout = null;
    private CustomNetworkImageView propertyImageView = null;
    private TextView propertyRateTextView = null;
    private TextView propertyNameTextView = null;

    private LinearLayout wageLayout = null;
    private CustomNetworkImageView wageImageView = null;
    private TextView wageRateTextView = null;
    private TextView wageNameTextView = null;

    private AutoScrollViewPager mediaViewPager = null;
    private MediaImagePagerAdapter mediaViewPagerAdapter = null;
    private List<LinkArticle> mediaImageURLList = new ArrayList<LinkArticle>();

    private LinearLayout mediaContentLayout = null;

    private List<DiscoveryAppDto> dtoList = new ArrayList<DiscoveryAppDto>();

    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_discocery);

        this.initView();

        requestDiscoveryTopImg();

        requestMediaImage();

        requestMediaReport();
    }

    public void onResume() {
        super.onResume();

        if (null != viewPager) {
            viewPager.startAutoScroll();
        }

        if (null != mediaViewPager) {
            mediaViewPager.startAutoScroll();
        }

        requestDiscoveryList();
    }

    public void onPause() {
        super.onPause();

        if (null != viewPager) {
            viewPager.stopAutoScroll();
        }

        if (null != mediaViewPager) {
            mediaViewPager.stopAutoScroll();
        }
    }

    private void initView() {
        zizhiTextView = (TextView) this.findViewById(R.id.zizhiTextView);
        zizhiTextView.setOnClickListener(this);

        baogaoTextView = (TextView) this.findViewById(R.id.baogaoTextView);
        baogaoTextView.setOnClickListener(this);

        aboutTextView = (TextView) this.findViewById(R.id.aboutTextView);
        aboutTextView.setOnClickListener(this);

        propertyLayout = (LinearLayout) this.findViewById(R.id.propertyLayout);
        propertyLayout.setOnClickListener(this);

        propertyImageView = (CustomNetworkImageView) this.findViewById(R.id.propertyImageView);
        propertyRateTextView = (TextView) this.findViewById(R.id.propertyRateTextView);
        propertyNameTextView = (TextView) this.findViewById(R.id.propertyNameTextView);

        wageLayout = (LinearLayout) this.findViewById(R.id.wageLayout);
        wageLayout.setOnClickListener(this);
        wageImageView = (CustomNetworkImageView) this.findViewById(R.id.wageImageView);
        wageRateTextView = (TextView) this.findViewById(R.id.wageRateTextView);
        wageNameTextView = (TextView) this.findViewById(R.id.wageNameTextView);

        mediaContentLayout = (LinearLayout) this.findViewById(R.id.mediaContentLayout);

        initSwipeRefresh();
    }

    @SuppressLint("ResourceAsColor")
    private void initSwipeRefresh() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColor(R.color.redme, R.color.blueme, R.color.orangeme, R.color.greenme);
        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
        mSwipeLayout.setLoadNoFull(true);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDiscoveryTopImg();
                requestMediaImage();
                requestMediaReport();
            }
        });
    }

    private void requestDiscoveryTopImg() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_TOPIMG, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, ImageAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<ImageAppDto>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        imageURLList = dto.getData();
                        initViewPager();
                        viewPagerAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    mSwipeLayout.setRefreshing(false);
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void initViewPager() {
        // indicator
        indicatorLayout = (LinearLayout) this.findViewById(R.id.indicatorLayout);
        indicatorLayout.removeAllViews();

        indicatorImageViews = new ImageView[imageURLList.size()];
        for (int i = 0; i < imageURLList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            indicatorImageViews[i] = imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            indicatorLayout.addView(indicatorImageViews[i], layoutParams);
        }

        // http://www.trinea.cn/android/auto-scroll-view-pager/
        // ViewPager
        viewPager = (AutoScrollViewPager) this.findViewById(R.id.viewPager);
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setAutoScrollDurationFactor(7.0);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        viewPager.setStopScrollWhenTouch(true);
        viewPagerAdapter = new ImagePagerAdapter(this, imageURLList);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int index) {
                int position = index % imageURLList.size();
                for (int i = 0; i < imageURLList.size(); i++) {
                    if (i == position) {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                    } else {
                        indicatorImageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    }

                }
            }

        });
        // viewPagerAdapter.setInfiniteLoop(true);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.startAutoScroll();

        final GestureDetector tapGestureDetector = new GestureDetector(this, new TapGestureListener());
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }


    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            try {
                ImageAppDto imageDto = imageURLList.get(viewPager.getCurrentItem());

                if (!StringUtils.isBlank(imageDto.getLinkUrl())) {

                    Intent intent = new Intent(DiscoveryActivity.this, ShowWebViewActivity.class);
                    intent.putExtra("title", imageDto.getName());
                    intent.putExtra("url", imageDto.getLinkUrl());
                    DiscoveryActivity.this.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.onSingleTapConfirmed(event);
        }
    }

    private void initMediaViewPager() {
        // ViewPager
        mediaViewPager = (AutoScrollViewPager) this.findViewById(R.id.mediaViewPager);
        mediaViewPager.setInterval(3000);
        mediaViewPager.setCycle(true);
        mediaViewPager.setAutoScrollDurationFactor(7.0);
        mediaViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mediaViewPager.setStopScrollWhenTouch(true);
        mediaViewPagerAdapter = new MediaImagePagerAdapter(this, mediaImageURLList);

        // viewPagerAdapter.setInfiniteLoop(true);
        mediaViewPager.setAdapter(mediaViewPagerAdapter);
        mediaViewPager.startAutoScroll();
    }

    // 取得媒体报道图片
    private void requestMediaImage() {
        JSONRequest request = new JSONRequest(this, RequestEnum.LINK_ARTICLE, null, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, LinkArticle.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<LinkArticle>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        mediaImageURLList = dto.getData();
                        initMediaViewPager();
                        mediaViewPagerAdapter.notifyDataSetChanged();

                        findViewById(R.id.topMediaLayout).setVisibility(mediaImageURLList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mSwipeLayout.setRefreshing(false);
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void requestDiscoveryList() {
        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_LIST, null, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, DiscoveryAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<DiscoveryAppDto>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        dtoList = dto.getData();

                        responseDiscoveryList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void responseDiscoveryList() {
        try {
            DiscoveryAppDto propertyDto = dtoList.get(0);
            propertyRateTextView.setText("免物业费，送" + propertyDto.getRate() + "%年化收益");

            if (StringUtils.isNotBlank(propertyDto.getName())) {
                propertyImageView.setImageUrl(Constants.HOST_IP + propertyDto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
                propertyNameTextView.setText(propertyDto.getName());
            } else {
                propertyNameTextView.setText("未开启");
            }

            DiscoveryAppDto wageDto = dtoList.get(1);
            wageRateTextView.setText("工资尽享 " + wageDto.getRate() + "% 活期收益");

            if (StringUtils.isNotBlank(wageDto.getName())) {
                wageImageView.setImageUrl(Constants.HOST_IP + wageDto.getLogo(), ImageCacheManager.getInstance().getImageLoader());
                wageNameTextView.setText("查看工资");
            } else {
                wageNameTextView.setText("未开启");
            }


        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "系统异常，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestMediaReport() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("pageNo", "1");
        tempMap.put("pageSize", Integer.MAX_VALUE + "");

        JSONRequest request = new JSONRequest(this, RequestEnum.DISCOVERY_MEDIA_REPORT, tempMap, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JavaType type = objectMapper.getTypeFactory().constructParametricType(Paginable.class, MediaReportAppDto.class);
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);

                AppMessageDto<Paginable<MediaReportAppDto>> dto = null;
                try {
                    dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        List<MediaReportAppDto> list = dto.getData().getList();
                        responseMediaReport(list);
                        findViewById(R.id.bottomMediaLayout).setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    mSwipeLayout.setRefreshing(false);
                }

            }
        }, new ResponseErrorListener(this) {

            @Override
            public void todo() {
            }
        });

        if (!this.addToRequestQueue(request, null)) {
        }
    }

    private void responseMediaReport(List<MediaReportAppDto> list) {
        mediaContentLayout.removeAllViews();

        for (MediaReportAppDto dto : list) {
            MediaLayout layout = new MediaLayout(this);
            layout.setData(dto);
            mediaContentLayout.addView(layout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.zizhiTextView: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "公司资质");
                intent.putExtra("url", Constants.HOST_IP + "/app/zizhi.html");
                startActivity(intent);
            }
            break;

            case R.id.baogaoTextView: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "年度报告");
                intent.putExtra("url", Constants.HOST_IP + "/app/ndbg.html");
                startActivity(intent);
            }
            break;

            case R.id.aboutTextView: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "关于我们");
                intent.putExtra("url", Constants.HOST_IP + "/app/aboutus.html");
                startActivity(intent);
            }
            break;

            case R.id.propertyLayout: {
                if (dtoList.isEmpty())
                    return;

                DiscoveryAppDto propertyDto = dtoList.get(0);

                if (StringUtils.isBlank(propertyDto.getName())) {
                    Intent intent = new Intent(this, PropertyNoOpenActivity.class);
                    intent.putExtra("rate", propertyDto.getRate());
                    this.startActivity(intent);

                } else {
                    Intent intent = new Intent(this, PropertyActivity.class);
                    this.startActivity(intent);
                }

            }
            break;

            case R.id.wageLayout: {
                if (dtoList.isEmpty())
                    return;

                DiscoveryAppDto wageDto = dtoList.get(1);

                if (StringUtils.isBlank(wageDto.getName())) {
                    Intent intent = new Intent(this, WageNoOpenActivity.class);
                    intent.putExtra("rate", wageDto.getRate());
                    this.startActivity(intent);

                } else {
                    Intent intent = new Intent(this, WageActivity.class);
                    this.startActivity(intent);
                }
            }
            break;
        }
    }

    private long exitTimeMillis = 0;

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this); // 用来保存统计数据

            for (Activity act : ActivityManager.getInstance().getAllActivity()) {
                act.finish();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
