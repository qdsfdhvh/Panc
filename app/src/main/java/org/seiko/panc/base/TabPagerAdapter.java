package org.seiko.panc.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment[] fragment;
    private String[] title;

    public TabPagerAdapter(FragmentManager fm, BaseFragment[] fragment, String[] title) {
        super(fm);
        this.fragment = fragment;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment[position];
    }

    @Override
    public int getCount() {
        return fragment.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

}
