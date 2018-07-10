package com.accrete.sixorbit.fragment.Drawer.collectionsOrders;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderReferenceMainTabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String orderId;
    private ViewPagerAdapter viewPagerAdapter;

    public static OrderReferenceMainTabFragment newInstance() {
        return new OrderReferenceMainTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        orderId = bundle.getString(getString(R.string.order_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(viewPager, orderId);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                refreshFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager, String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.order_id), orderId);

        PendingReferencesFragment pendingReferencesFragment = new PendingReferencesFragment();
        pendingReferencesFragment.setArguments(bundle);

        UsedOrderReferencesFragment usedOrderReferencesFragment = new UsedOrderReferencesFragment();
        usedOrderReferencesFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Pending", "Used"};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, pendingReferencesFragment);
                pendingReferencesFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, usedOrderReferencesFragment);
                usedOrderReferencesFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void refreshFragment() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            if (mFragment instanceof PendingReferencesFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((PendingReferencesFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        } else if (position == 1) {
            if (mFragment instanceof UsedOrderReferencesFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((UsedOrderReferencesFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        }
    }

    public class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static final String KEY_TITLE = "fragment_title";
        private static final String KEY_FRAGMENT = "fragment";
        private List<Map<String, Object>> maps = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragmentAndTitle(Map<String, Object> map) {
            maps.add(map);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) maps.get(position).get(KEY_TITLE);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) maps.get(position).get(KEY_FRAGMENT);
        }

        @Override
        public int getCount() {
            return maps.size();
        }
    }
}
