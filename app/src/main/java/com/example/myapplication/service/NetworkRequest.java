package com.example.myapplication.service;


import com.alibaba.fastjson.JSONException;
import com.example.myapplication.api.HttpResponseCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkRequest {
    //    网络请求方法
    public void getOkhttp(String url, final HttpResponseCallBack responseCallBack) throws IOException{
        final MediaType JSON1 = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        Request getRequest = new Request.Builder()
                .url("http://10.0.2.2:8088/firewall"+url)
                .build();
        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = response.body().string();
                try {
//                    List<HashMap<String, Object>> list = JSON.parseObject(response.body().string(), new TypeReference<List<Map<String, Object>>>() {});
                    responseCallBack.getResponse(str);
//                    System.out.println("list"+list);
                } catch (JSONException | org.json.JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public Boolean postOkhttp(String url,String data,final HttpResponseCallBack responseCallBack) throws JSONException, IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestJsonBody = RequestBody.create(
                data,
                MediaType.parse("application/json")
        );

        Request postRequest = new Request.Builder()
                .url("http://10.0.2.2:8088/firewall" + url)
                .post(requestJsonBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "keep-alive")
                .build();
        try {
            Response response = client.newCall(postRequest).execute();
            String res = response.body().string();
            responseCallBack.getResponse(res);
            System.out.println(res);
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

}
