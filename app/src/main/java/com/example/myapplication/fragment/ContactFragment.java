package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.myapplication.R;
import com.example.myapplication.api.HttpResponseCallBack;
import com.example.myapplication.service.NetworkRequest;
import com.example.myapplication.service.SIMCardInfo;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactFragment extends BaseFragment {

    private TelephonyManager telephonyManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //这是生命周期中的函数，经常是把初始数据写在这里
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //将碎片的XML文件转换为视图用inflate()
        View fragmentView = inflater.inflate(R.layout.fragment_contact_layout, container, false);
        //求碎片视图中的ListView控件还是使用findViewById();
        final ListView lv = (ListView) fragmentView.findViewById(R.id.contactlistView);
        //定义适配器的目的还是为了将字符串数组与碎片中的ListView结合起来，形成新闻条目的显示。
        //第一个参数为getActivity()的原因，是因为碎片纳入活动后，ListView它就是主活动中的视图了。
        SIMCardInfo cin = new SIMCardInfo(getActivity().getWindow().getContext());
        cin.getPhoneNumber();

        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
//                System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+(List<Map<String,Object>>)msg.obj);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<Map<String,Object>>)msg.obj, R.layout.contact_item_layout, new String[]{"name", "contact_number"}, new int[]{R.id.nickname, R.id.phone_number});
                            //实现列表的显示
                lv.setAdapter(adapter);
                return false;
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
//                System.out.println("----cin.phoneNumber"+cin.phoneNumber.substring(1));
                try {
                    nr.getOkhttp("/contactlist?host_number="+cin.phoneNumber.substring(1), new HttpResponseCallBack() {
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

        //最后返回的是碎片形成的视图
        return fragmentView;
    }

}
