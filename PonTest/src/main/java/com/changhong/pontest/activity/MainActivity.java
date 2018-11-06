package com.changhong.pontest.activity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changhong.pontest.R;
import com.changhong.pontest.adapter.MyPagerAdapter;
import com.changhong.pontest.fragment.LogFragment;
import com.changhong.pontest.fragment.MyFragment;
import com.changhong.pontest.fragment.TelnetFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面程序
 *
 * @author fdw
 * @version 1.0
 */
public class MainActivity extends FragmentActivity {

    private List<Fragment> fragmentList;
    //private List<String> titleList;
    private ViewPager pager;
    //private PagerTabStrip tab;//顶部标题

    private ImageView telnetImageView, logImageView, myImageView;
    private TextView telnetTextView, logTextView, myTextView;
    private LinearLayout linear_telnet, linear_log, linear_my;

    /**
     * Fragment
     */
    private MyFragment myFragment;
    private TelnetFragment telnetFragment;
    private LogFragment logFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        //初始化控件
        initView();

        //初始化ViewPager,实现Tab滑动效果
        initViewPager();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        telnetImageView = findViewById(R.id.main_wifi_image);
        logImageView = findViewById(R.id.main_tools_image);
        myImageView = findViewById(R.id.main_my_image);

        telnetTextView = findViewById(R.id.main_wifi_tv);
        logTextView = findViewById(R.id.main_tools_tv);
        myTextView = findViewById(R.id.main_my_tv);

        linear_telnet = findViewById(R.id.main_wifi);
        linear_log = findViewById(R.id.main_tools);
        linear_my = findViewById(R.id.main_my);

        linear_telnet.setOnClickListener(new MyOnClickListener(0));
        linear_log.setOnClickListener(new MyOnClickListener(1));
        linear_my.setOnClickListener(new MyOnClickListener(2));

    }

    /**
     * 初始化ViewPager,实现Tab滑动效果
     */
    private void initViewPager() {
        fragmentList = new ArrayList<Fragment>();
        //titleList = new ArrayList<>();
        //tab = findViewById(R.id.tab);

        telnetFragment = new TelnetFragment();
        logFragment = new LogFragment();
        myFragment = new MyFragment();


        fragmentList.add(telnetFragment);
        fragmentList.add(logFragment);
        fragmentList.add(myFragment);

//        View view1 = View.inflate(this, R.dialog_loading.fragment_telnet_connect, null);
//        View view2 = View.inflate(this, R.dialog_loading.fragment_log, null);
//        View view3 = View.inflate(this, R.dialog_loading.fragment_my, null);
//
//        viewList.add(view1);
//        viewList.add(view2);
//        viewList.add(view3);
//        //为ViewPager页卡设置标题
//        titleList.add("第一页");
//        titleList.add("第二页");
//        titleList.add("第三页");

//        //为PagerTabStrip设置一些属性
//        tab.setBackgroundColor(Color.WHITE);
//        tab.setDrawFullUnderline(false);
//        tab.setTabIndicatorColor(Color.BLUE);
        //初始化ViewPager
        pager = findViewById(R.id.view_pager);

        MyPagerAdapter adapter = new MyPagerAdapter(this.getSupportFragmentManager(), fragmentList);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        pager.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


    /**
     * 图标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            pager.setCurrentItem(index);
        }
    }

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            //Toast.makeText(getApplicationContext(),"当前是第 " + (position + 1) + "个界面",Toast.LENGTH_SHORT).show();
            switch (position) {
                case 0:
                    telnetImageView.setImageResource(R.mipmap.icon_wifi_blue);
                    logImageView.setImageResource(R.mipmap.icon_tools_gray);
                    myImageView.setImageResource(R.mipmap.icon_my_gray);
                    telnetTextView.setTextColor(getResources().getColor(R.color.table_text_color));
                    logTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    myTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    break;
                case 1:
                    telnetImageView.setImageResource(R.mipmap.icon_wifi_gray);
                    logImageView.setImageResource(R.mipmap.icon_tools_blue);
                    myImageView.setImageResource(R.mipmap.icon_my_gray);
                    telnetTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    logTextView.setTextColor(getResources().getColor(R.color.table_text_color));
                    myTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    break;
                case 2:
                    telnetImageView.setImageResource(R.mipmap.icon_wifi_gray);
                    logImageView.setImageResource(R.mipmap.icon_tools_gray);
                    myImageView.setImageResource(R.mipmap.icon_my_blue);
                    telnetTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    logTextView.setTextColor(getResources().getColor(R.color.table_text_color_uncheck));
                    myTextView.setTextColor(getResources().getColor(R.color.table_text_color));
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
    }
}
