package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

//这个类一定要继承AdapterView.OnItemClickListener的点击事件
public class PhoneFragment extends BaseFragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //这是生命周期中的函数，经常是把初始数据写在这里
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //将碎片的XML文件转换为视图用inflate()
        View fragmentView = inflater.inflate(R.layout.fragment_phone_layout, container, false);
        //求碎片视图中的ListView控件还是使用findViewById();
        final ListView lv = (ListView) fragmentView.findViewById(R.id.phonelistView);
        SIMCardInfo cin = new SIMCardInfo(getActivity().getWindow().getContext());
        cin.getPhoneNumber();

        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println("--------(List<Map<String,Object>>)msg.obj------:"+(List<Map<String,Object>>)msg.obj);
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<Map<String,Object>>)msg.obj, R.layout.phone_item_layout, new String[]{"contact_number", "time"}, new int[]{R.id.telephone, R.id.phonetime});
                //实现列表的显示
                lv.setAdapter(adapter);
                return false;
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/call_logslist?host_number="+cin.phoneNumber.substring(1)+"&isintercept=false", new HttpResponseCallBack() {
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

        return fragmentView;
    }
}
