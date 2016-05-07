package com.spuds.eventapp.MyEvents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by tina on 4/30/16.
 */
public class MyEventsViewPagerAdapter extends FragmentStatePagerAdapter {

    Fragment myEventsFeedTabsFragment;

    public MyEventsViewPagerAdapter(FragmentManager fm, Fragment myEventsFeedTabsFragment) {
        super(fm);
        this.myEventsFeedTabsFragment = myEventsFeedTabsFragment;
    }

    @Override
    public Fragment getItem(int position) {

        return new MyEventsFeedFragment();

    }

    @Override
    public int getCount() {
        return 2;
    }

}