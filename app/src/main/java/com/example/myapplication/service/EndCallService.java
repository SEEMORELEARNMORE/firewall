package com.example.myapplication.service;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.myapplication.api.HttpResponseCallBack;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EndCallService extends CallScreeningService {

    private String Caller_ID ;
    private String Native_ID;
    private HashMap<String, Boolean> data;

    @Override
    public void onCreate() {
        super.onCreate();
//        System.out.println("输出data："+data);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
        String s = callDetails.getHandle().getSchemeSpecificPart();
        Caller_ID = s.contains("(")?s.substring(1,2).substring(5,6):s;
        SIMCardInfo cin = new SIMCardInfo(getBaseContext());
        cin.getPhoneNumber();
        Native_ID = cin.phoneNumber;
        System.out.println("本机号码："+Native_ID+"；来电号码："+Caller_ID);
//        判断是否为来电
        if (callDetails.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
            disconnectCall(callDetails, Caller_ID);
        }
    }

    public static String getDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis()+28800000);
        return simpleDateFormat.format(date);
    }
    public void record ( Boolean isintercept){
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
                HashMap<String,String> data = new HashMap<String,String>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.now();
                data.put("host_number",Native_ID.substring(1));
                data.put("contact_number",Caller_ID);
                data.put("time",getDateString());
                data.put("isintercept",isintercept.toString());
                JSONObject json = new JSONObject(data);
                System.out.println("-------data:"+data);
                try {
                    nr.postOkhttp("/addcall_logs", String.valueOf(json), new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws org.json.JSONException {
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

    private void disconnectCall(Call.Details details ,String phoneNumber) {
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
//                System.out.println("MainActivity传过来的数据："+data);
                System.out.println("--------disconnetCall handleMessage------:"+msg.obj);
                HashMap<String, String> list = (HashMap<String, String>) msg.obj;
                if(list.get("result")=="true"){
                    respondToCall(details, buildResponse());
                    record(true);
                }
                else{
                    record(false);
                }
                return false;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //操作内容
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/phonepermission?host_number="+Native_ID.substring(1)+"&intercept_number="+Caller_ID, new HttpResponseCallBack() {
                        @Override
                        public void getResponse(String response) throws JSONException {
                            String reqGet = response;
                            Message msg = new Message();
                            HashMap<String, String> list = JSON.parseObject(reqGet, new TypeReference<Map<String, String>>() {});
                            msg.obj =list;
                            handler.sendMessage(msg);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },4000);
    }
    private CallResponse buildResponse(){
        return new CallResponse.Builder()
                .setRejectCall(true)
                .setDisallowCall(true)
                .setSkipNotification(true)
                .setSkipCallLog(true)
                .build();
    }



    @Override
public void onDestroy() {
    super.onDestroy();
}

}
