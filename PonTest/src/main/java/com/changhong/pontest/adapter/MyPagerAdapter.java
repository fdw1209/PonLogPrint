package com.changhong.pontest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager适配器
 *
 * @author fdw
 * @version 1.0
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList;


    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    public MyPagerAdapter(FragmentManager fm,List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    /**
     * 返回页卡的数量
     */
    @Override
    public int getCount() {

        return fragmentList.size();
    }

    /**
     * view是否来自对象
     */
//    @Override
//    public boolean isViewFromObject(View arg0, Object arg1) {
//
//        return arg0 == arg1;
//    }

    /**
     * 通过position设置对应的Fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * 实例化一个页卡
     */
//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//        View view = viewList.get(position);
//        container.addView(view);
//        return view;
//    }

    /**
     * 销毁一个页卡
     */
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//
//        container.removeView(viewList.get(position));
//
//    }

    /**
     * 设置ViewPager的标题
     */
//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        return titleList.get(position);
//    }
}
