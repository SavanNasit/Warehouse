package com.accrete.warehouse.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersFragment;
import com.accrete.warehouse.utils.NonSwipeableViewPager;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by poonam on 6/4/18.
 */

public class RunningOrdersTabFragment extends Fragment {

    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    public static RunningOrdersTabFragment newInstance(String title) {
        RunningOrdersTabFragment f = new RunningOrdersTabFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }


    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.running_orders_tab_title));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.running_orders_tab_title));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.running_orders_tab_title));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.running_orders_tab_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(viewPagerAdapter.getTabView(i));
        }
        return rootView;
    }


    public void updateCount(String count, int pos) {
        TabLayout.Tab tab = tabLayout.getTabAt(pos);
        View view = tab.getCustomView();
        TextView countTextView = (TextView) view.findViewById(R.id.count_textView);
        countTextView.setText("[" + count + "]");
    }

    private void initializeView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (NonSwipeableViewPager) view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
        setupViewPager();
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
                refreshFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void refreshFragment() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof RunningOrdersFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //((RunningOrdersFragment) mFragment).callAPI();
                }
            }, 200);
        } else if (mFragment instanceof RunningStockRequestFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //  ((RunningStockRequestFragment) mFragment).callAPI();
                }
            }, 200);
        }
    }

    private void setupViewPager() {

        RunningOrdersFragment runningOrdersFragment = new RunningOrdersFragment();
        RunningStockRequestFragment runningStockRequestFragment = new RunningStockRequestFragment();

        String[] title_arr;
        title_arr = new String[]{"Running Orders", "Stock Request"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, runningOrdersFragment);
                runningOrdersFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, runningStockRequestFragment);
                runningStockRequestFragment.getParentFragment();
            }

            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void onBackPressed() {
        getChildFragmentManager().popBackStack();
    }

    public void callAction() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof RunningOrdersFragment) {
            ((RunningOrdersFragment) mFragment).callAction();
        }
    }


    public static class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static final String KEY_TITLE = "fragment_title";
        private static final String KEY_FRAGMENT = "fragment";
        Activity activityToPass;
        private List<Map<String, Object>> maps = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm, Activity activity) {
            super(fm);
            activityToPass = activity;
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

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(activityToPass).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) v.findViewById(R.id.textView);
            tv.setText((CharSequence) maps.get(position).get(KEY_TITLE));
            TextView countTextView = (TextView) v.findViewById(R.id.count_textView);
            countTextView.setText("[0]");
            return v;
        }
    }
}


