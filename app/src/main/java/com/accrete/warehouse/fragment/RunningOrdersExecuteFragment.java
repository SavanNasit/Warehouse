package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.warehouse.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class RunningOrdersExecuteFragment extends Fragment {

    private ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_running_execute_orders, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        viewPagerExecute = (ViewPager) rootView.findViewById(R.id.view_pager_execute);
        setupViewPagerExecute(viewPagerExecute);
        viewPagerExecute.setOffscreenPageLimit(2);
        tabLayoutExecute = (TabLayout) rootView.findViewById(R.id.tabs_execute);
        tabLayoutExecute.setupWithViewPager(viewPagerExecute);

        tabLayoutExecute.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerExecute.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 2) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            PackageDetailsFragment packageDetailsFragment =
                                    (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
                            packageDetailsFragment.doRefresh();
                        }
                    }, 1 * 200);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPagerExecute(ViewPager viewPagerExecute) {
        PendingItemsFragment pendingItemsFragment = new PendingItemsFragment();
        PackageDetailsFragment packageDetailsFragment = new PackageDetailsFragment();

        viewPagerExecuteAdapter adapter = new viewPagerExecuteAdapter(getChildFragmentManager());
        adapter.addFragment(pendingItemsFragment, "Pending Items");
        adapter.addFragment(packageDetailsFragment, "Package Details");
        viewPagerExecute.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.running_orders_execute_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.running_orders_execute_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.running_orders_execute_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.running_orders_execute_fragment));
        }
    }

    public void getData(String str) {
        /*Fragment newCurrentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.running_orders_container);
        if (newCurrentFragment instanceof PendingItemsFragment) {
            ((PendingItemsFragment) newCurrentFragment).getData(str);
            Log.e("TAG_ORDERS", "" + str);
        }*/
        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 0) {
            Log.e("TAG_ORDERS", "" + str);
            PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.getData(str);

        }
    }

    class viewPagerExecuteAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public viewPagerExecuteAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
