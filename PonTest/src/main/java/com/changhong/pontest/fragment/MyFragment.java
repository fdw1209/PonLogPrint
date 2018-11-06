package com.changhong.pontest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.changhong.pontest.R;
import com.changhong.pontest.activity.AboutActivity;

/**
 * "我的"信息模块
 */
public class MyFragment extends Fragment {

    private RelativeLayout relative_about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        relative_about = getActivity().findViewById(R.id.my_fragment_about);
        relative_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过使用Intent实现页面跳转
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);//startActivity(intent)实现跳转
            }
        });
    }


}
