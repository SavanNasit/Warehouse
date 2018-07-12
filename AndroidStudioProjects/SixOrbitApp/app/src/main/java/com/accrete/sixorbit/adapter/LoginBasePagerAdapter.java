package com.accrete.sixorbit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by poonam on 20/4/17.
 */

public class LoginBasePagerAdapter  extends FragmentPagerAdapter {


    List<Fragment> fragments;
    public static String _transitionName;

    public void setTransitionName(String transitionName) {
        _transitionName = transitionName;
    }

    public LoginBasePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }


    @Override
    public int getCount() {
        return this.fragments.size();
    }
}