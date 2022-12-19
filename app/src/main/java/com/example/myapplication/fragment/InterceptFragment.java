package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class InterceptFragment<override, mStr> extends BaseFragment implements View.OnClickListener {

    private View view;
    private View ib_layout;
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
    //定义回调接口
    public interface MyListener{
        public void sendValue(String value);
    }

    private MyListener myListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //获取实现接口的activity
//        myListener = (MyListener) getActivity();//或者myListener=(MainActivity) context;
        myListener = (MyListener) context;
        //这是生命周期中的函数，经常是把初始数据写在这里
        myListener.sendValue("短信");
    }

    public void messageData(){
        SIMCardInfo cin = new SIMCardInfo(getActivity().getWindow().getContext());
        cin.getPhoneNumber();

        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<Map<String,Object>>)msg.obj, R.layout.message_item_layout,
                        new String[]{"name", "time","message_content"}, new int[]{R.id.messagename, R.id.message_time,R.id.message_content});
                final ListView lv = view.findViewById(R.id.interceptlistView);
                lv.setAdapter(adapter);
                return false;
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/message_logslist?host_number="+cin.phoneNumber.substring(1)+"&isintercept=true", new HttpResponseCallBack() {
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

    public  void phoneData(){

        SIMCardInfo cin = new SIMCardInfo(getActivity().getWindow().getContext());
        cin.getPhoneNumber();

        Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<Map<String,Object>>)msg.obj, R.layout.phone_item_layout,
                        new String[]{"contact_number", "time"}, new int[]{R.id.telephone, R.id.phonetime});
                final ListView lv = view.findViewById(R.id.interceptlistView);
                lv.setAdapter(adapter);
                return false;
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NetworkRequest nr = new NetworkRequest();
                try {
                    nr.getOkhttp("/call_logslist?host_number="+cin.phoneNumber.substring(1)+"&isintercept=true", new HttpResponseCallBack() {
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //将碎片的XML文件转换为视图用inflate()
        View fragmentView = inflater.inflate(R.layout.fragment_intercept_layout, container, false);
        View fragmentView1 = inflater.inflate(R.layout.intercept_bottom_layout, container, false);
        view = fragmentView;
        ib_layout = fragmentView1;
        TextView im = fragmentView.findViewById(R.id.intercept_message);
        TextView ip = fragmentView.findViewById(R.id.intercept_phone);
        im.setOnClickListener(this);
        ip.setOnClickListener(this);
        messageData();
//        initTabs();
        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intercept_message:
                View itercept = view;
                TextView im = itercept.findViewById(R.id.intercept_message);
                TextView ip = itercept.findViewById(R.id.intercept_phone);
                im.setTextColor(this.getResources().getColor(R.color.theme_color));
                ip.setTextColor(this.getResources().getColor(R.color.color_666666));
                im.setBackgroundResource(R.drawable.textview_border);
                ip.setBackgroundResource(R.drawable.textview_transparentborder);
                messageData();
                myListener.sendValue("短信");
                break;
            case R.id.intercept_phone:
                View itercept1 = view;
                 TextView im1 = itercept1.findViewById(R.id.intercept_message);
                 TextView ip1 = itercept1.findViewById(R.id.intercept_phone);
                ip1.setTextColor(this.getResources().getColor(R.color.theme_color));
                 im1.setTextColor(this.getResources().getColor(R.color.color_666666));
                ip1.setBackgroundResource(R.drawable.textview_border);
                im1.setBackgroundResource(R.drawable.textview_transparentborder);
                  phoneData();
                myListener.sendValue("电话");
                break;
            default:
                break;
        }
    }


}
