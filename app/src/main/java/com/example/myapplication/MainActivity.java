package com.example.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.myapplication.api.HttpResponseCallBack;
import com.example.myapplication.fragment.ContactFragment;
import com.example.myapplication.fragment.InterceptFragment;
import com.example.myapplication.fragment.MessageFragment;
import com.example.myapplication.fragment.PhoneFragment;
import com.example.myapplication.service.NetworkRequest;
import com.example.myapplication.service.SIMCardInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity<TestApp> extends BaseActivity implements View.OnClickListener, InterceptFragment.MyListener {

    private static final int REQUEST_ID = 10;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.CALL_PHONE
            , Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG};
    private static final int REQUEST_PERMISSION_CODE = 666;
    private static Context PermissionUtils;
    private TelephonyManager telMgr;

    private static final String TestApp = "TestApp";
    private static final String TAG = "";
    private Boolean isintercept = false;
    private Boolean isrules = false;
    private Boolean torules = false;

    public static MainActivity instance;

    private FragmentManager fm;
    private PhoneFragment mPhoneFragment;
    private Fragment mCommonFragmentOne;
    private MessageFragment mMessageFragment;
    private ContactFragment mContactFragment;
    private InterceptFragment mInterceptFragment;
    private Fragment mCurrent;
    private RelativeLayout mPhoneLayout;
    private RelativeLayout mContactLayout;
    private RelativeLayout mMessageLayout;
    private RelativeLayout mInterceptLayout;
    private ImageView mPhoneView;
    private TextView phone_text_view;
    private ImageView mMessageView;
    private TextView message_text_view;
    private ImageView mContactView;
    private TextView contact_text_view;
    private ImageView mInterceptView;
    private TextView intercept_text_view;

    //    intercept页面的tab栏
    private RelativeLayout mDeleteLayout;
    private RelativeLayout mNumberAppealLayout;
    private RelativeLayout mInterceptRulesLayout;
    private ImageView mDeleteView;
    private TextView delete_text_view;
    private ImageView mNumberAppealView;
    private TextView numberAppeal_text_view;
    private ImageView mInterceptRulesView;
    private TextView interceptRules_text_view;
    private String TextView;
    private String deleteInfo;
    private Object TAGL;

    //  intercept_rules中的各个规则
    private RelativeLayout mInterceptPhone;
    private RelativeLayout mInterceptMessage;
    private RelativeLayout mInterceptBlacklist;

//    权限开关
    private Switch mAllPhone;
    private Switch mStrangePhone;
    private Switch mAllMessage;
    private Switch mStrangeMessage;
    private HashMap<String,String> phonePermissions = new HashMap<String,String>();
    private HashMap<String,String> messagePermissions = new HashMap<String,String>();


    private long exitTime = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getWindow().getContext();
        bpermission = checkPublishPermission();
        SIMCardInfo cin = new SIMCardInfo(getWindow().getContext());
        cin.getPhoneNumber();
        phone_number = cin.phoneNumber;
//        //动态获取权限
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//        }
        try {
            mCallServiceIntent = new Intent(this, Class.forName("android.telecom.CallScreeningService"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        bindService(mCallServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        requestRole();
//        System.out.println("onserviceconnected中的IBinder:"+binder);
    }

    //  设备电话
    private String phone_number;
    private Intent mCallServiceIntent ;
//    private EndCallService.EndCallBinder binder = null;
    ServiceConnection mServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // iBinder is an instance of CallScreeningService.CallScreenBinder
            // CallScreenBinder is an inner class present inside CallScreenService
            System.out.println("---00000000000--ONSERVICECONNECTED-----00000000--");
//            binder = (EndCallService.Binder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }


    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestRole() {
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, REQUEST_ID);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                // Your app is now the call screening app
                System.out.println("onactivityresult中的Intent实例："+data);
            } else {
                // Your app is not the call screening app
            }
        }
    }

    @Override
    protected int getLayoutId() {
        instance = this;
        return R.layout.activity_main;
    }

    private Boolean bpermission = false;
    private final int WRITE_PERMISSION_REQ_CODE = 100;
    private boolean checkPublishPermission(){
        if(Build.VERSION.SDK_INT>=23){
            List<String> permissions = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_SMS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_PHONE_NUMBERS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.READ_PHONE_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_CONTACTS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CALL_PHONE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_CALL_LOG);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_CALL_LOG);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BROADCAST_STICKY) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BROADCAST_STICKY);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(MainActivity.this,(String[]) permissions.toArray(new String[0]),WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    public void sendValue(String value) {
        deleteInfo = value;
        Log.e(TestApp,value);
        Log.e(TestApp,"001");
    }

    @Override
    protected void initView() {

        mPhoneLayout = (RelativeLayout) findViewById(R.id.phone_layout_view);
        mPhoneLayout.setOnClickListener(this);
        mMessageLayout = (RelativeLayout) findViewById(R.id.contact_layout_view);
        mMessageLayout.setOnClickListener(this);
        mContactLayout = (RelativeLayout) findViewById(R.id.message_layout_view);
        mContactLayout.setOnClickListener(this);
        mInterceptLayout = (RelativeLayout) findViewById(R.id.intercept_layout_view);
        mInterceptLayout.setOnClickListener(this);

        mPhoneView = (ImageView) findViewById(R.id.phone_image_view);
        phone_text_view = (TextView) findViewById(R.id.phone_text_view);
        mMessageView = (ImageView) findViewById(R.id.message_image_view);
        message_text_view = (TextView) findViewById(R.id.message_text_view);
        mContactView = (ImageView) findViewById(R.id.contact_image_view);
        contact_text_view = (TextView) findViewById(R.id.contact_text_view);
        mInterceptView = (ImageView) findViewById(R.id.intercept_image_view);
        intercept_text_view = (TextView) findViewById(R.id.intercept_text_view);

        mPhoneView.setImageDrawable(getResources().getDrawable(R.drawable.tab_phone_selected));
        phone_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));

        mPhoneFragment = new PhoneFragment();
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, mPhoneFragment);
        fragmentTransaction.commit();


    }

    public void initInterceptTabs(){
        mDeleteLayout = (RelativeLayout) findViewById(R.id.delete_layout_view);
        mDeleteLayout.setOnClickListener(this);
        mNumberAppealLayout = (RelativeLayout) findViewById(R.id.number_appeal_layout_view);
        mNumberAppealLayout.setOnClickListener(this);
        mInterceptRulesLayout = (RelativeLayout) findViewById(R.id.intercept_rules_layout_view);
        mInterceptRulesLayout.setOnClickListener(this);

        mDeleteView = (ImageView) findViewById(R.id.delete_image_view);
        delete_text_view = (TextView) findViewById(R.id.delete_text_view);
        mNumberAppealView = (ImageView) findViewById(R.id.number_appeal_image_view);
        numberAppeal_text_view = (TextView) findViewById(R.id.number_appeal_text_view);
        mInterceptRulesView = (ImageView) findViewById(R.id.intercept_rules_image_view);
        interceptRules_text_view = (TextView) findViewById(R.id.intercept_rules_text_view);

    }

    public void initInterceptRules(){
        mInterceptPhone = (RelativeLayout) findViewById(R.id.intercept_phone_layout_view);
        mInterceptPhone.setOnClickListener(this);
        mInterceptMessage = (RelativeLayout) findViewById(R.id.intercept_message_layout_view);
        mInterceptMessage.setOnClickListener(this);
        mInterceptBlacklist = (RelativeLayout) findViewById(R.id.intercept_blacklist_layout_view);
        mInterceptBlacklist.setOnClickListener(this);
        isrules = true;
        torules = false;
    }

    @Override
    protected void initData() {
        phonePermissions.put("allPhone","0");
        phonePermissions.put("strangePhone","0");
        messagePermissions.put("allMessage","0");
        messagePermissions.put("strangeMessage","0");
        setPermission();
        setMessagePermission();
    }

    private void hideFragment(Fragment fragment, FragmentTransaction ft) {
        if (fragment != null) {
            ft.hide(fragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_layout_view:
                labelSelection(0);
                break;
            case R.id.contact_layout_view:
                labelSelection(1);
                break;
            case R.id.message_layout_view:
                labelSelection(2);
                break;
            case R.id.intercept_layout_view:
                labelSelection(3);
                break;
            case R.id.delete_layout_view:
                labelSelection(31);
                break;
            case R.id.number_appeal_layout_view:
                labelSelection(32);
                break;
            case R.id.intercept_rules_layout_view:
                labelSelection(33);
                break;
            case R.id.intercept_phone_layout_view:
                labelSelection(331);
                break;
            case R.id.intercept_message_layout_view:
                labelSelection(332);
                break;
            case R.id.intercept_blacklist_layout_view:
                labelSelection(333);
                break;
            case R.id.addblicklist:
                labelSelection(3331);
                break;
            default:
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void labelSelection(int position) {

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (position) {
            case 0:
                mPhoneView.setImageDrawable(getResources().getDrawable(R.drawable.tab_phone_selected));
                phone_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mMessageView.setImageDrawable(getResources().getDrawable(R.drawable.tab_message));
                message_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mContactView.setImageDrawable(getResources().getDrawable(R.drawable.tab_contact));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mInterceptView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept));
                intercept_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                hideFragment(mInterceptFragment, fragmentTransaction);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                hideFragment(mContactFragment, fragmentTransaction);
                isintercept = false;
                isrules = false;
                torules= false;
                mPhoneFragment = new PhoneFragment();
                fragmentTransaction.add(R.id.content_layout, mPhoneFragment);
                break;
            case 1:
                mContactView.setImageDrawable(getResources().getDrawable(R.drawable.tab_contact_selected));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mPhoneView.setImageDrawable(getResources().getDrawable(R.drawable.tab_phone));
                phone_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mMessageView.setImageDrawable(getResources().getDrawable(R.drawable.tab_message));
                message_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mInterceptView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept));
                intercept_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                hideFragment(mInterceptFragment, fragmentTransaction);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                hideFragment(mPhoneFragment, fragmentTransaction);
                isintercept = false;
                isrules = false;
                torules= false;
                mContactFragment = new ContactFragment();
                fragmentTransaction.add(R.id.content_layout, mContactFragment);
                break;
            case 2:
                mMessageView.setImageDrawable(getResources().getDrawable(R.drawable.tab_message_selected));
                message_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mPhoneView.setImageDrawable(getResources().getDrawable(R.drawable.tab_phone));
                phone_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mContactView.setImageDrawable(getResources().getDrawable(R.drawable.tab_contact));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mInterceptView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept));
                intercept_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                hideFragment(mInterceptFragment, fragmentTransaction);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mPhoneFragment, fragmentTransaction);
                hideFragment(mContactFragment, fragmentTransaction);
                isintercept = false;
                isrules = false;
                torules= false;
                mMessageFragment = new MessageFragment();
                fragmentTransaction.add(R.id.content_layout, mMessageFragment);
                break;
            case 3:
                mInterceptView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept_selected));
                intercept_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mPhoneView.setImageDrawable(getResources().getDrawable(R.drawable.tab_phone));
                phone_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mContactView.setImageDrawable(getResources().getDrawable(R.drawable.tab_contact));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mMessageView.setImageDrawable(getResources().getDrawable(R.drawable.tab_message));
                message_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                setContentView(R.layout.intercept_bottom_layout);
                hideFragment(mContactFragment, fragmentTransaction);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mPhoneFragment, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                initInterceptTabs();
                isintercept = true;
                isrules = false;
                torules= false;
                mInterceptFragment = new InterceptFragment();
                fragmentTransaction.add(R.id.content_layout, mInterceptFragment);
                break;
            case 31:
                mDeleteView.setImageDrawable(getResources().getDrawable(R.drawable.tab_delete_selected));
                delete_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mNumberAppealView.setImageDrawable(getResources().getDrawable(R.drawable.tab_number_appeal));
                numberAppeal_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mInterceptRulesView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept_rules));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                showInformation("确定要删除全部被拦截"+deleteInfo+"?");
                isrules = false;
                torules= false;
                break;
            case 32:
                mDeleteView.setImageDrawable(getResources().getDrawable(R.drawable.tab_delete));
                delete_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mNumberAppealView.setImageDrawable(getResources().getDrawable(R.drawable.tab_number_appeal_selected));
                numberAppeal_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                mInterceptRulesView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept_rules));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                submitAppeal();
                isrules=false;
                torules= false;
                break;
            case 33:
                mDeleteView.setImageDrawable(getResources().getDrawable(R.drawable.tab_delete));
                delete_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mNumberAppealView.setImageDrawable(getResources().getDrawable(R.drawable.tab_number_appeal));
                numberAppeal_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
                mInterceptRulesView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept_rules_selected));
                contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab_selected));
                setContentView(R.layout.intercept_rules);
                initInterceptRules();
                rehabilitation();
                isrules = true;
                torules= false;
                break;
            case 331:
                setContentView(R.layout.intercept_phone_rules);
                initPhoneSwitch();
                mAllPhone = findViewById(R.id.all_switch);
                mStrangePhone = findViewById(R.id.strange_switch);
                mAllPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                        {
                        System.out.println("开启拦截所有电话");
                        phonePermissions.replace("allPhone","1");
//                        binder.setData(phonePermissions);
                        }else{
                            phonePermissions.replace("allPhone","0");
//                            binder.setData(phonePermissions);
                            System.out.println("关闭拦截所有电话");
                        }
                        setPermission();
                    }
                });
                mStrangePhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                        {
                            phonePermissions.replace("strangePhone","1");
//                            binder.setData(phonePermissions);
                            System.out.println("开启拦截陌生电话");

                        }else{
                            phonePermissions.replace("strangePhone","0");
//                            binder.setData(phonePermissions);
                            System.out.println("关闭拦截陌生电话");
                        }
                        setPermission();
                    }
                });
                torules = true;
                break;
            case 332:
                setContentView(R.layout.intercept_message_rules);
                initMessageSwitch();
                mAllMessage = findViewById(R.id.messageall_switch);
                mStrangeMessage = findViewById(R.id.messagestrange_switch);
                mAllMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                        {
                            System.out.println("开启拦截所有短信");
                            messagePermissions.replace("allMessage","1");
//                        binder.setData(phonePermissions);
                        }else{
                            messagePermissions.replace("allMessage","0");
//                            binder.setData(phonePermissions);
                            System.out.println("关闭拦截所有短信");
                        }
                        setMessagePermission();
                    }
                });
                mStrangeMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                        {
                            messagePermissions.replace("strangeMessage","1");
//                            binder.setData(phonePermissions);
                            System.out.println("开启拦截陌生短信");

                        }else{
                            messagePermissions.replace("strangeMessage","0");
//                            binder.setData(phonePermissions);
                            System.out.println("关闭拦截陌生短信");
                        }
                        setMessagePermission();
                    }
                });
                torules = true;
                break;
            case 333:
                setContentView(R.layout.intercept_blacklist);
                setBlacklistData();
                torules = true;
                break;
            case 3331:
                addBlacklist();
            default:
                break;
        }
        fragmentTransaction.commit();

    }

    private  void initPhoneSwitch(){
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                List<HashMap<String, String>> list = (List<HashMap<String, String>>) msg.obj;
                mAllPhone = findViewById(R.id.all_switch);
                mStrangePhone = findViewById(R.id.strange_switch);
                Boolean all = Integer.valueOf(list.get(0).get("allphone"))==1?true:false;
                Boolean st = Integer.valueOf(list.get(0).get("strangephone"))==1?true:false;
                if(list.get(0).get("allphone")=="1")
                    System.out.println("等于1");
                System.out.println(list.get(0).get("allphone")+list.get(0).get("strangephone"));
                mAllPhone.setChecked(all);
                mStrangePhone.setChecked(st);
                return false;
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/initphonepermission?host_number="+phone_number.substring(1), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            List<HashMap<String, String>> list = JSON.parseObject(reqGet, new TypeReference<List<Map<String, String>>>() {});
                            Message msg = new Message();
                            msg.obj = list;
                            handler.sendMessage(msg);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private  void initMessageSwitch(){
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                List<HashMap<String, String>> list = (List<HashMap<String, String>>) msg.obj;
                mAllMessage = findViewById(R.id.messageall_switch);
                mStrangeMessage = findViewById(R.id.messagestrange_switch);
                Boolean all = Integer.valueOf(list.get(0).get("allmessage"))==1?true:false;
                Boolean st = Integer.valueOf(list.get(0).get("strangemessage"))==1?true:false;
                if(list.get(0).get("allmessage")=="1")
                    System.out.println("等于1");
                System.out.println(list.get(0).get("allmessage")+list.get(0).get("strangemessage"));
                mAllMessage.setChecked(all);
                mStrangeMessage.setChecked(st);
                return false;
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/initmessagepermission?host_number="+phone_number.substring(1), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            List<HashMap<String, String>> list = JSON.parseObject(reqGet, new TypeReference<List<Map<String, String>>>() {});
                            Message msg = new Message();
                            msg.obj = list;
                            handler.sendMessage(msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private  void setPermission(){
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+msg.obj);
                return false;
            }
        });
        new Thread(new Runnable(){
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
//                String data = phone_number.substring(1) + "," + inputServer.getText().toString();
                HashMap<String,String> data = new HashMap<String,String>();
                SIMCardInfo cin = new SIMCardInfo(getWindow().getContext());
                cin.getPhoneNumber();
                data.put("host_number",cin.phoneNumber.substring(1));
                data.put("allphone",phonePermissions.get("allPhone"));
                data.put("strangephone",phonePermissions.get("strangePhone"));
                JSONObject json = new JSONObject(data);
//                try {
//                    json.put("data", data);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                System.out.println("-------data:"+data);
                try {
                    nr.postOkhttp("/updatepermission", String.valueOf(json), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            Message msg = new Message();
                            msg.obj = reqGet;
                            handler.sendMessage(msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    private  void setMessagePermission(){
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+msg.obj);
                return false;
            }
        });
        new Thread(new Runnable(){
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
//                String data = phone_number.substring(1) + "," + inputServer.getText().toString();
                HashMap<String,String> data = new HashMap<String,String>();
                SIMCardInfo cin = new SIMCardInfo(getWindow().getContext());
                cin.getPhoneNumber();
                data.put("host_number",cin.phoneNumber.substring(1));
                data.put("allmessage",messagePermissions.get("allMessage"));
                data.put("strangemessage",messagePermissions.get("strangeMessage"));
                JSONObject json = new JSONObject(data);
//                try {
//                    json.put("data", data);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                System.out.println("-------data:"+data);
                try {
                    nr.postOkhttp("/updatemessagepermission", String.valueOf(json), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            Message msg = new Message();
                            msg.obj = reqGet;
                            handler.sendMessage(msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    private  void setBlacklistData(){
//        添加黑名单
        ImageButton mInterceptItem = findViewById(R.id.addblicklist);
        mInterceptItem.setOnClickListener(this);

//        获取列表数据
        final ListView lv = (ListView) findViewById(R.id.blacklistView);
        SIMCardInfo cin = new SIMCardInfo(getWindow().getContext());
        cin.getPhoneNumber();

        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+(List<Map<String,Object>>)msg.obj);
                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, (List<Map<String,Object>>)msg.obj, R.layout.blacklist_item, new String[]{"intercept_number"}, new int[]{R.id.blacklistitem});
                //实现列表的显示
                lv.setAdapter(adapter);
                return false;
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/blacklistlist?host_number="+cin.phoneNumber.substring(1), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            List<HashMap<String, Object>> list = JSON.parseObject(reqGet, new TypeReference<List<Map<String, Object>>>() {});
//                            System.out.println("-----contact_list:"+list);
                            Message msg = new Message();
                            msg.obj = list;
                            handler.sendMessage(msg);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void rehabilitation(){
        mDeleteView.setImageDrawable(getResources().getDrawable(R.drawable.tab_delete));
        delete_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
        mNumberAppealView.setImageDrawable(getResources().getDrawable(R.drawable.tab_number_appeal));
        numberAppeal_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
        mInterceptRulesView.setImageDrawable(getResources().getDrawable(R.drawable.tab_intercept_rules));
        contact_text_view.setTextColor(getResources().getColor(R.color.comui_tab));
    }

    private void showInformation(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 执行操作
//                Toast.makeText(MainActivity.this, "你点击确定了", Toast.LENGTH_SHORT).show();
                rehabilitation();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                rehabilitation();
            }
        });
        builder.create().show();
    }

    private void addContact(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请在此处填入需要拉入黑名单的号码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rehabilitation();
                    }
                });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Handler handler=new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+msg.obj);
                        setBlacklistData();
                        return false;
                    }
                });
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        NetworkRequest nr = new NetworkRequest();
                        String data = phone_number.substring(1) + "," + inputServer.getText().toString();
                        JSONObject json = new JSONObject();
                        try {
                            json.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-------data:"+data);
                        try {
                            nr.postOkhttp("/addblacklist", String.valueOf(json), new HttpResponseCallBack() {
                                @Override
                                public void getResponse(String response) throws JSONException {
                                    String reqGet = response;
                                    Message msg = new Message();
                                    msg.obj = reqGet;
                                    handler.sendMessage(msg);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }}).start();

                rehabilitation();
            }
        });
        builder.show();
    }


    private void addBlacklist(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请在此处填入需要拉入黑名单的号码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rehabilitation();
                    }
                });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Handler handler=new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+msg.obj);
                        setBlacklistData();
                        return false;
                    }
                });
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        NetworkRequest nr = new NetworkRequest();
                        String data = phone_number.substring(1) + "," + inputServer.getText().toString();
                        JSONObject json = new JSONObject();
                        try {
                            json.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-------data:"+data);
                        try {
                            nr.postOkhttp("/addblacklist", String.valueOf(json), new HttpResponseCallBack() {
                                @Override
                                public void getResponse(String response) throws JSONException {
                                    String reqGet = response;
                                    Message msg = new Message();
                                    msg.obj = reqGet;
                                    handler.sendMessage(msg);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }}).start();

                rehabilitation();
            }
        });
        builder.show();
    }

    private void submitAppeal(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("本人或熟人号码被错误标记，可在这里申诉").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rehabilitation();
                    }
                });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               System.out.println("需要申诉的号码："+inputServer.getText().toString());
                Handler handler=new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        System.out.println("--------msg.obj------:"+msg.obj);
                        return false;
                    }
                });
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkRequest nr = new NetworkRequest();
                        try {
                            nr.getOkhttp("/deleteblacklist?host_number="+phone_number.substring(1)+"&intercept_number="+inputServer.getText().toString(), new HttpResponseCallBack() {
                                @Override
                                public void getResponse(String response) throws JSONException {
                                    String reqGet = response;
                                    Message msg = new Message();
                                    msg.obj = reqGet;
                                    handler.sendMessage(msg);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                rehabilitation();
            }
        });
        builder.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            Log.d(TestApp, String.valueOf((mCurrent instanceof InterceptFragment)));
//          详细规则跳转拦截规则页面
            if(torules){
                setContentView(R.layout.intercept_rules);
                initInterceptRules();
            }else if(isrules){
//          拦截规则页面跳转拦截页面
                setContentView(R.layout.intercept_bottom_layout);
                initInterceptTabs();
                View v = View.inflate(this,R.layout.activity_main,null);
                onClick(v.findViewById(R.id.intercept_layout_view));
            } else if(isintercept){
                setContentView(R.layout.activity_main);
//                onClick(findViewById(R.id.phone_layout_view));
                initView();
            }else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
             else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







}
