package org.seiko.panc.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import org.seiko.panc.Constants;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.base.TabPagerAdapter;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class ReaderMainFragment extends BaseFragment {

    @BindView(R.id.comic_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.comic_view_pager)
    ViewPager mViewPager;

    @Override
    public void initViews(Bundle bundle) {
        String[] names = new String[] {"收藏", "历史", "下载"};
        BaseFragment[] fragments = new BaseFragment[] {
                ReaderFragment.newInstance(Constants.TYPE_LIKE),
                ReaderFragment.newInstance(Constants.TYPE_HIST),
                ReaderFragment.newInstance(Constants.TYPE_DOWN)
        };
        TabPagerAdapter mTabAdapter = new TabPagerAdapter(getFragmentManager(), fragments, names);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public int LayoutResID() {
        return R.layout.fragment_main_reader;
    }

    @Override
    public String LayoutTitle() {
        return null;
    }


}
