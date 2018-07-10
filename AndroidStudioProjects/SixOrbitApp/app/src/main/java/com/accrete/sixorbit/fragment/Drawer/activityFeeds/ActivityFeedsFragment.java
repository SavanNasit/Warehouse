package com.accrete.sixorbit.fragment.Drawer.activityFeeds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.ActivityFeedsPagerAdapter;


/**
 * Created by poonam on 10/4/17.
 */

public class ActivityFeedsFragment extends Fragment {

    ActivityFeedsPagerAdapter mPagerAdapter;
    ViewPager viewPager;

    public static ActivityFeedsFragment newInstance(String title) {
        ActivityFeedsFragment f = new ActivityFeedsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public static ActivityFeedsFragment newInstance() {
        return new ActivityFeedsFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.all)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.mine)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new ActivityFeedsPagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabTextColors(
                ContextCompat.getColor(getActivity(), R.color.White_White),
                ContextCompat.getColor(getActivity(), R.color.White_AntiqueWhite)
        );
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    AllActivitiesFragment allActivitiesFragment =
                            (AllActivitiesFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    allActivitiesFragment.getDataFromDB();
                } else if (tab.getPosition() == 1) {
                    MineFeedFragment mineFeedFragment =
                            (MineFeedFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    mineFeedFragment.getDataFromDB();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.activity_feeds));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.activity_feeds));
    }

    public void onRefresh() {
        if (viewPager.getCurrentItem() == 0) {
            AllActivitiesFragment allActivitiesFragment =
                    (AllActivitiesFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            allActivitiesFragment.getDataFromDB();
        } else if (viewPager.getCurrentItem() == 1) {
            MineFeedFragment mineFeedFragment =
                    (MineFeedFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            mineFeedFragment.getDataFromDB();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity()
                .setTitle(getString(R.string.activity_feeds));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}