package com.example.myapplication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class ListenCallService extends Service {

    private TelephonyManager tm;
    private PhoneStateListener listener = new PhoneStateListener() {

        // 当通话状态发生改变时调用
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲（挂断电话/未来电之前）
                    Log.e("TAG", "空闲（挂断电话/未来电之前）");
                    break;

                case TelephonyManager.CALL_STATE_RINGING:// 响铃
                    Log.e("TAG", "响铃");
                    // 如果来电是黑名单号（110），就挂断电话
                    if ("110".equals(incomingNumber)) {
                        try {
                            endCall();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:// 接通
                    Log.e("TAG", "接通");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * 挂断电话
     */
    private void endCall() throws Exception {
        // 通过反射调用隐藏的API
        // 得到隐藏类的Class对象
        Class c = Class.forName("android.os.ServiceManager");
        // 得到方法所对应的Method对象
        Method method = c.getMethod("getService", String.class);
        // 调用方法
        IBinder iBinder = (IBinder) method.invoke(null,
                Context.TELEPHONY_SERVICE);
        // 得到接口对象
        ITelephony telephony = ITelephony.Stub.asInterface(iBinder);
        // 结束通话
        telephony.endCall();

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        // 得到电话管理器
        tm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 监听电话状态
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 停止电话监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
}