package com.gugu.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.ares.baggugu.dto.app.AppMessageDto;
import com.ares.baggugu.dto.app.AppResponseStatus;
import com.ares.baggugu.dto.app.PhonebookAppDto;
import com.gugu.client.Constants;
import com.gugu.client.RequestEnum;
import com.gugu.client.net.JSONRequest;
import com.gugu.utils.ActivityUtil;
import com.gugu.utils.Util;
import com.my.sortlistview.CharacterParser;
import com.my.sortlistview.PinyinComparator;
import com.my.sortlistview.SideBar;
import com.my.sortlistview.SortAdapter;
import com.my.sortlistview.SortModel;
import com.wufriends.gugu.R;

// 通讯录好友列表
public class InviteContactListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private SideBar sideBar = null;
    private TextView dialog = null;
    private SortAdapter adapter = null;

    private ListView listView = null;

    // 手机通讯录
    private String phoneBook = "";

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> sourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_contact_list);

        initView();

        new processContactBookTask().execute();
    }

    private void initView() {
        ((Button) this.findViewById(R.id.backBtn)).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("邀请好友");

        listView = (ListView) this.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new processContactBookTask().execute();
            }
        });

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        sideBar.setVisibility(View.INVISIBLE);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });
    }

    private void requestPhonebookFilter() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("phoneBook", this.phoneBook);

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_PHONEBOOK_FILTER, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, PhonebookAppDto.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<List<PhonebookAppDto>> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        ArrayList<PhonebookAppDto> mList = (ArrayList<PhonebookAppDto>) dto.getData();
                        sourceDateList = filledData(mList);

                        if (sourceDateList.size() > 0) {
                            sideBar.setVisibility(View.VISIBLE);
                        }

                        // 根据a-z进行排序源数据
                        Collections.sort(sourceDateList, pinyinComparator);
                        adapter = new SortAdapter(InviteContactListActivity.this, sourceDateList);
                        listView.setAdapter(adapter);

                    } else {

                        Toast.makeText(InviteContactListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    /**
     * 为ListView填充数据
     */
    private List<SortModel> filledData(List<PhonebookAppDto> tempList) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < tempList.size(); i++) {
            SortModel sortModel = new SortModel();

            sortModel.setName(tempList.get(i).getName());
            sortModel.setCode(tempList.get(i).getTelphone());
            sortModel.setInvite(tempList.get(i).isInvite());

            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(tempList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    // 告知服务器已邀请用户
    private void requestInvitePhonebook(final int position, final SortModel phonebook) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("telphone", phonebook.getCode());
        tempMap.put("name", phonebook.getName());

        JSONRequest request = new JSONRequest(this, RequestEnum.USER_INVITE_PHONEBOOK, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, String.class);
                    AppMessageDto<String> dto = objectMapper.readValue(jsonObject, type);

                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        phonebook.setInvite(true);

                        sourceDateList.remove(position);
                        sourceDateList.add(position, phonebook);

                        adapter.updateListView(sourceDateList);


                    } else {
                        Toast.makeText(InviteContactListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private class processContactBookTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            InviteContactListActivity.this.showProgress("正在查询通讯录...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                phoneBook = new ObjectMapper().writeValueAsString(Util.getContactList(InviteContactListActivity.this));

                if (phoneBook == null) {
                    cannotAccessContact();
                }

            } catch (Exception e) {
                e.printStackTrace();
                phoneBook = "";

                Looper.prepare();
                cannotAccessContact();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (TextUtils.isEmpty(phoneBook)) {

            } else {
                InviteContactListActivity.this.requestPhonebookFilter();
            }
        }
    }

    private void cannotAccessContact() {
        Intent intent = new Intent(InviteContactListActivity.this, AccessContactTipActivity.class);
        intent.putExtra("TYPE", "ERROR");
        InviteContactListActivity.this.startActivity(intent);

        InviteContactListActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                this.setResult(RESULT_OK);
                this.finish();
                break;
        }
    }

    public void onBackPressed() {
        this.setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final SortModel dto = sourceDateList.get(position);
        if (dto.isInvite())
            return;

        InviteContactListActivity.this.showProgress("正在发送短信...");

        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sendIntent = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        // register the Broadcast Receivers
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(InviteContactListActivity.this, "短信发送成功", Toast.LENGTH_SHORT).show();

                        requestInvitePhonebook(position, dto);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        InviteContactListActivity.this.hideProgress();

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        InviteContactListActivity.this.hideProgress();

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        InviteContactListActivity.this.hideProgress();

                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        String linkUrl = Constants.HOST_IP + "/yq/" + ActivityUtil.getSharedPreferences().getString(Constants.USERID, "");
        String shareContent = Constants.shareContent + linkUrl;

        List<String> divideContents = smsManager.divideMessage(shareContent);
        for (String text : divideContents) {
            smsManager.sendTextMessage(dto.getCode(), null, text, sendIntent, null);
        }

    }

}
