package com.example.myapplication.api;

import org.json.JSONException;

public interface HttpResponseCallBack {
    void getResponse(String response) throws JSONException;
}
