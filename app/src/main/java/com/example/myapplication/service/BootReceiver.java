package com.example.myapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收开机完成广播的receiver
 *
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动电话监听的service
        context.startService(new Intent(context, ListenCallService.class));
    }

}


