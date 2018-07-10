package com.accrete.sixorbit.fragment.Drawer.collections;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by {Anshul} on 17/5/18.
 */

public class MyTransactionsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout.Tab mTabStrip;

    public static MyTransactionsFragment newInstance(String title) {
        MyTransactionsFragment f = new MyTransactionsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.my_transaction));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.my_transaction));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.my_transaction));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.my_transaction));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private int getWidthScreen(Activity activity) {
        Configuration configuration = activity.getResources().getConfiguration();
        int mWidthScreen = configuration.smallestScreenWidthDp;
        return mWidthScreen;
    }

    private void initializeView(View view) {

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager();
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (getWidthScreen(getActivity()) > 500) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            int minWidth = 30;
            ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

            for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
                View v = slidingTabStrip.getChildAt(i);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.rightMargin = minWidth;
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                refreshFragments();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void refreshFragments() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof PendingCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((PendingCollectionFragment) mFragment).doRefresh();
                }
            }, 200);
        } else if (mFragment instanceof ReConciliationCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ReConciliationCollectionFragment) mFragment).doRefresh();
                }
            }, 200);
        } else if (mFragment instanceof ApprovedCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ApprovedCollectionFragment) mFragment).doRefresh();
                }
            }, 200);
        }
    }

    private void setupViewPager() {

        PendingCollectionFragment pendingCollectionFragment = new PendingCollectionFragment();
        ReConciliationCollectionFragment reConciliationCollectionFragment = new ReConciliationCollectionFragment();
        ApprovedCollectionFragment approvedCollectionFragment = new ApprovedCollectionFragment();

        String[] title_arr;
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        title_arr = new String[]{" Pending ", " Reconciliation Waiting ", " Approved "};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, pendingCollectionFragment);
                pendingCollectionFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, reConciliationCollectionFragment);
                reConciliationCollectionFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, approvedCollectionFragment);
                approvedCollectionFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.collections));
        }
    }

    public void onBackPressed() {
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        getChildFragmentManager().popBackStack();
    }

    public static class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
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
