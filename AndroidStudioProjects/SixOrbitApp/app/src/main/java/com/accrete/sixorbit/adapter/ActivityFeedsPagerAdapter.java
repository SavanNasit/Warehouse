package com.accrete.sixorbit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.accrete.sixorbit.fragment.Drawer.activityFeeds.AllActivitiesFragment;
import com.accrete.sixorbit.fragment.Drawer.activityFeeds.MineFeedFragment;

/**
 * Created by poonam on 9/6/17.
 */

public class ActivityFeedsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ActivityFeedsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AllActivitiesFragment allActivitiesFragment = new AllActivitiesFragment();
                return allActivitiesFragment;
            case 1:
                MineFeedFragment mineFeedFragment = new MineFeedFragment();
                return mineFeedFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}