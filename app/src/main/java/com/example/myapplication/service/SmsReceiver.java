package com.example.myapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.api.HttpResponseCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收来了短信的广播的receiver
 *
 */
public class SmsReceiver extends BroadcastReceiver {
    private Map<String,String> data = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        //1. 得到intent短信数据, 并封装为短信对象smsMessage
        Bundle extras = intent.getExtras();
        Object[] pdus = (Object[])extras.get("pdus");
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdus[0]);
        //2. 取号码
        String number = smsMessage.getOriginatingAddress();
        String content = smsMessage.getMessageBody();
        SIMCardInfo cin = new SIMCardInfo(context.getApplicationContext());
        cin.getPhoneNumber();
        Log.e("TAG", cin.phoneNumber.substring(1)+number +" : "+content);
        //3. 存入数据库
        data = new HashMap<String,String>();
        data.put("host_number",cin.phoneNumber.substring(1));
        data.put("contact_number",number.substring(1));
        data.put("message_content",content);
        data.put("time",getDateString());
        addData();
    }
    public static String getDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis()+28800000);
        return simpleDateFormat.format(date);
    }

    private void addData(){
        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println("--------msg.obj------:"+msg.obj);
                return false;
            }
        });
        new Thread(new Runnable(){
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                JSONObject json = new JSONObject(data);
                System.out.println("-------data:"+data);
                try {
                    nr.postOkhttp("/addmessage_logs", String.valueOf(json), new HttpResponseCallBack() {
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
}