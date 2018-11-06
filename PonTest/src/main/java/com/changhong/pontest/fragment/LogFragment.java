package com.changhong.pontest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.changhong.pontest.R;
import com.changhong.pontest.activity.LogItemActivity;

/**
 * 日志模块
 */
public class LogFragment extends Fragment {

    private Context mContext;
    private CardView log_card_olt, log_card_rms, log_card_plugin, log_card_web;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        initView();

        //查看日志事件
        viewLog();
    }

    /**
     * 查看日志
     */
    private void viewLog() {
        log_card_olt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LogItemActivity.class);
                intent.putExtra("LOG_TYPE", "OLTRegister");
                startActivity(intent);//startActivity(intent)实现跳转
            }
        });
        log_card_rms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LogItemActivity.class);
                intent.putExtra("LOG_TYPE", "RMSPlatForm");
                startActivity(intent);//startActivity(intent)实现跳转
            }
        });
        log_card_plugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LogItemActivity.class);
                intent.putExtra("LOG_TYPE", "PluginPlatForm");
                startActivity(intent);//startActivity(intent)实现跳转
            }
        });
        log_card_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LogItemActivity.class);
                intent.putExtra("LOG_TYPE", "WebConfig");
                startActivity(intent);//startActivity(intent)实现跳转
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        log_card_olt = getActivity().findViewById(R.id.log_olt_register);
        log_card_rms = getActivity().findViewById(R.id.log_rms_platform);
        log_card_plugin = getActivity().findViewById(R.id.log_plugin_platform);
        log_card_web = getActivity().findViewById(R.id.log_web_config);

        mContext = getContext();
    }
}
