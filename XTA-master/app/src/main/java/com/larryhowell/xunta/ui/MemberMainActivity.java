package com.larryhowell.xunta.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.bean.Person;
import com.larryhowell.xunta.widget.CustomViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberMainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.container)
    CustomViewPager mViewPager;

    public MemberMapFragment mMapFragment;
    public MemberPlanFragment mPlanFragment;
    public MemberLocationListFragment mLocationListFragment;
    public Person mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        ButterKnife.bind(this);

        mPerson = (Person) getIntent().getSerializableExtra("person");

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 && !mPlanFragment.loaded) {
                    mPlanFragment.refresh();
                    mPlanFragment.loaded = true;
                } else if (tab.getPosition() == 2 && !mLocationListFragment.loaded) {
                    mLocationListFragment.refresh();
                    mLocationListFragment.loaded = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mToolbar.setTitle(mPerson.getNickname());
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mMapFragment == null) {
                    mMapFragment = new MemberMapFragment();
                }

                return mMapFragment;
            } else if (position == 1) {
                if (mPlanFragment == null) {
                    mPlanFragment = new MemberPlanFragment();
                }

                return mPlanFragment;
            } else {
                if (mLocationListFragment == null) {
                    mLocationListFragment = new MemberLocationListFragment();
                }

                return mLocationListFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ta的位置";
                case 1:
                    return "出行计划";
                case 2:
                    return "历史位置";
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mMapFragment.mMapView.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
