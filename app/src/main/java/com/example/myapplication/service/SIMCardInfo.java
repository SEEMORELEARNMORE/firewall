package com.example.myapplication.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class SIMCardInfo {

    private static final String TAG = "";
    private TelephonyManager telephonyManager;
    private String IMSI;// 国际移动用户识别码
    private Context context;
    public String phoneNumber;

    public SIMCardInfo(Context context) {
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        this.context = context;
    }


    //获取本机号码
    public void getPhoneNumber() {
        String PhoneNumber = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PhoneNumber = telephonyManager.getLine1Number();//返回设备的电话号码
        System.out.println("------------------"+PhoneNumber);
        phoneNumber = PhoneNumber;
    }

    //获取本机运营商信息

    /**
     * 方法一：getSubscriberId()，获取IMSI号
     * 方法二：getSimOperator()获取SIM运营商代码(MCCMNC码)/getSimOperatorName()获取运营商名称（英文）
     * 【getNetworkOperator()获取网络运营商代码/getNetworkOperatorName()获取网络运营商名称（英文）】
     * 网络运营商是网络的运营商。一般SIM运营商和网络运营商是同一个，如用移动的就只能开移动的手机网络。
     * 从卡获取信息有所不同。
     *
     * @return
     */
    public String getProvidersName() {
        String ProvidersName = null;
        // <p>Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
        IMSI = telephonyManager.getSubscriberId();//使用这个需要在manifest文件里面添加 权限<uses-permission android:name="android.permission.READ_PHONE_STATE" />
        //或者：IMSI=telephonyManager.getSimOperator();
        android.util.Log.d(TAG, "IMSI== " + IMSI);
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信
        try {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    /**
     * 测试：getSimOperator()获取运营商代码/getSimOperatorName()获取运营商名称
     */
    public String getOperatorName() {
        String simOperator = telephonyManager.getSimOperator();
        android.util.Log.d(TAG, "SIM运营商代码: --" + simOperator);
        String simOperatorName = telephonyManager.getSimOperatorName();
        android.util.Log.d(TAG, "SIM运营商:-- " + simOperatorName);
        String networkOperator = telephonyManager.getNetworkOperator();//联网才有用
        android.util.Log.d(TAG, "网络运营商代码:-- " + networkOperator);
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        android.util.Log.d(TAG, "网络运营商:-- " + networkOperatorName);
        return simOperatorName;
    }
}

