package com.changhong.pontest.utils;

import android.util.Log;

import com.changhong.pontest.adapter.OnResponseListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * HTTP请求工具类
 * Created by fdw on 2018/8/3.
 */
public class HttpUtils {
    /**
     *get请求封装
     */
    public static void getRequest(String url, Map<String,String> params, String encode, OnResponseListener listener) {
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry:params.entrySet()) {    //增强for遍历循环添加拼接请求内容
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            if (listener!=null) {
                try {
                    URL path = new URL(sb.toString());
                    if (path!=null) {
                        HttpURLConnection con = (HttpURLConnection) path.openConnection();
                        con.setRequestMethod("GET");    //设置请求方式
                        con.setConnectTimeout(3000);    //链接超时3秒
                        con.setDoOutput(true);
                        con.setDoInput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(sb.toString().getBytes(encode));
                        os.close();
                        if (con.getResponseCode() == 200) {    //应答码200表示请求成功
                            onSucessResopond(encode, listener, con);
                        }
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    onError(listener, error);
                }
            }
        }
    }

    /**
     * POST请求
     */
    public static void postRequest(String url,Map<String,String> params,String encode,OnResponseListener listener) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry: params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        if (listener!=null) {
            try {
                URL path = new URL(url);
                if (path!=null){
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    con.setRequestMethod("POST");   //设置请求方法POST
                    con.setConnectTimeout(3000);
                    //设置可读写操作
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    byte[] bytes = sb.toString().getBytes();
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();
                    if (con.getResponseCode()==200){
                        onSucessResopond(encode, listener,con);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(listener, e);
            }
        }
    }

    private static void onError(OnResponseListener listner,Exception onError) {
        listner.onError(onError.toString());
    }

    private static void onSucessResopond(String encode, OnResponseListener listner, HttpURLConnection con) throws IOException {
        InputStream inputStream = con.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//创建内存输出流
        int len = 0;
        byte[] bytes = new byte[1024];
        if (inputStream != null) {
            while ((len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            String str = new String(baos.toByteArray(), encode);
            Log.i("pppp",str);
            listner.onSucess(str);
        }
    }

}
