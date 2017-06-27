package org.seiko.panc.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import org.seiko.panc.Constants;
import org.seiko.panc.R;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.base.TabPagerAdapter;
import org.seiko.panc.sited.YhSource;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/5/005.Y
 */

public class HomeActivity extends BackActivity {

    @BindView(R.id.comic_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.comic_view_pager)
    ViewPager mViewPager;

    private String source;
    private BaseFragment[] fragments;
    private String[] names;

    @Override
    public void initViews(Bundle bundle) {
        addFragment();
        TabPagerAdapter mTabAdapter = new TabPagerAdapter(getFragmentManager(), fragments, names);
        mViewPager.setOffscreenPageLimit(fragments.length);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void addFragment() {
        List<BaseFragment> fragments1 = new ArrayList<>();
        List<String> names1 = new ArrayList<>();

        YhSource sd = SourceManager.getInstance().rxgetSource(source).blockingFirst();
        if (sd.Hots() != null && !sd.Hots().isEmpty()) {
            String name = sd.Hots().title;
            fragments1.add(HotsFragment.newInstance(source));
            names1.add(name);
        }
        if (sd.Updates() != null && !sd.Updates().isEmpty()) {
            String name = sd.Updates().title;
            fragments1.add(UpdatesFragment.newInstance(source));
            names1.add(name);
        }
        if (sd.Tags() != null && !sd.Tags().isEmpty()) {
            String name = sd.Tags().title;
            fragments1.add(TagsFragment.newInstance(source));
            names1.add(name);
        }

        fragments = new BaseFragment[] {};
        fragments = fragments1.toArray(fragments);
        names = new String[] {};
        names = names1.toArray(names);
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_home;
    }

    @Override
    public String LayoutTitle() {
        source = getIntent().getStringExtra(Constants.EXTRA_SOURCE);
        return String.valueOf(source);
    }

    @Override
    public Context getContext() {
        return this;
    }

}
