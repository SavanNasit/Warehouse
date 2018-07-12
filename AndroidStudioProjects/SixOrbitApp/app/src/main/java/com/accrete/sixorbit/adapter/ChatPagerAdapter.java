package com.accrete.sixorbit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.accrete.sixorbit.fragment.Drawer.ChatContactsFragment;
import com.accrete.sixorbit.fragment.Drawer.ChatListFragment;

/**
 * Created by poonam on 9/6/17.
 */


public class ChatPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChatPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatContactsFragment chatContactsFragment = new ChatContactsFragment();
                return chatContactsFragment;
            case 1:
                ChatListFragment chatListFragment = new ChatListFragment();
                return chatListFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}