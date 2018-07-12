package com.accrete.warehouse.fragment.manageConsignment;

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

import com.accrete.warehouse.R;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by agt on 18/1/18.
 */

public class ConsignmentMainTabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String iscId;
    private ViewPagerAdapter viewPagerAdapter;

    public static ConsignmentMainTabFragment newInstance() {
        return new ConsignmentMainTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        iscId = bundle.getString(getString(R.string.iscid));
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

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(viewPager, iscId);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                if (position == 0) {
                    if (mFragment instanceof DetailsFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // ((DetailsFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 1) {
                    if (mFragment instanceof InventoryFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //((InventoryFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager, String iscId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.iscid), iscId);

        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(bundle);

        InventoryFragment inventoryFragment = new InventoryFragment();
        inventoryFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Details", "Inventory"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, detailsFragment);
                detailsFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, inventoryFragment);
                inventoryFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
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
