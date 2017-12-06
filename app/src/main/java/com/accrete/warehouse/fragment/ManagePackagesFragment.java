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


public class ManagePackagesFragment extends Fragment {
    private static final String KEY_TITLE = "ManagePackages";
    private ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;

    public static ManagePackagesFragment newInstance(String title) {
        ManagePackagesFragment f = new ManagePackagesFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_packages, container, false);
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
        PackedFragment packedFragment = new PackedFragment();
        OutForDeliveryFragment outForDeliveryFragment = new OutForDeliveryFragment();
        DeliveredFragment deliveredFragment = new DeliveredFragment();
        AttemptFailedFragment attemptFailedFragment = new AttemptFailedFragment();
        ReAttemptFragment reAttemptFragment = new ReAttemptFragment();
        DeliveryFailedFragment deliveryFailedFragment = new DeliveryFailedFragment();

        viewPagerExecuteAdapter adapter = new viewPagerExecuteAdapter(getChildFragmentManager());
        adapter.addFragment(packedFragment, "Packed");
        adapter.addFragment(outForDeliveryFragment, "Out for Delivery");
        adapter.addFragment(deliveredFragment, "Delivered");
        adapter.addFragment(attemptFailedFragment, "Attempt Failed");
        adapter.addFragment(reAttemptFragment, "Re-Attempt");
        adapter.addFragment(deliveryFailedFragment, "Delivery Failed");
        viewPagerExecute.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_packages_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_packages_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_packages_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_packages_fragment));
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
