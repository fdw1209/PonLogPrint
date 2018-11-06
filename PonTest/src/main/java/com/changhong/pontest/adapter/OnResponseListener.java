package com.changhong.pontest.adapter;

public interface OnResponseListener {
    void onSucess(String response);
    void onError(String error);
}