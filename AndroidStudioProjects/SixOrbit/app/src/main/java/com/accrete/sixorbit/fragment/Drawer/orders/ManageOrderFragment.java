package com.accrete.sixorbit.fragment.Drawer.orders;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageOrderFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DatabaseHandler databaseHandler;
    private int count = 0;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;

    public ManageOrderFragment() {
        // Required empty public constructor
    }

    public static ManageOrderFragment newInstance(String title) {
        ManageOrderFragment f = new ManageOrderFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.order));
        }
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.manage_order));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.manage_order));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.manage_order));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.manage_order));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        try {
            databaseHandler = new DatabaseHandler(getActivity());

            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
            setupViewPager();
            viewPager.setOffscreenPageLimit(2);
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (i == 1) {
                    tab.setCustomView(viewPagerAdapter.getTabView(i));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshFragments() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof ApproveOrderFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ApproveOrderFragment) mFragment).doRefresh("");
                }
            }, 200);
        } else if (mFragment instanceof OrderHistoryFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((OrderHistoryFragment) mFragment).doRefresh("");
                }
            }, 200);
        }
    }

    private void setupViewPager() {

        ApproveOrderFragment approveOrdersFragment = new ApproveOrderFragment();
        OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();

        String[] title_arr;
        //TODO - Check to show approve orders by having permission
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.order_show_approve_permission))) {
            title_arr = new String[]{"Order History", "Approve Orders"};
        } else {
            title_arr = new String[]{"Order History"};
        }

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, approveOrdersFragment);
                approveOrdersFragment.getParentFragment();
            } else if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, orderHistoryFragment);
                orderHistoryFragment.getParentFragment();
            }

            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void onBackPressed() {
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        getChildFragmentManager().popBackStack();
    }

    public void updateCount(String count) {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        View view = tab.getCustomView();
        TextView countTextView = (TextView) view.findViewById(R.id.count_textView);
        countTextView.setText("[" + count + "]");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void searchInFragment(String str) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof ApproveOrderFragment) {
            ((ApproveOrderFragment) mFragment).doRefresh(str);
        } else if (mFragment instanceof OrderHistoryFragment) {
            ((OrderHistoryFragment) mFragment).doRefresh(str);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            menu.clear();
            inflater.inflate(R.menu.main_activity_actions, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }

            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                } catch (Exception e) {
                }
            }

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //  Log.e("OnTextChange", query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Log.e("OnTextChangeSubmit", newText);
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // do your actual work here
                            if (getActivity() != null && isAdded()) {
                                searchInFragment(newText);
                            }
                        }
                    }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask

                    return false;
                }

            };
            searchView.setOnQueryTextListener(queryTextListener);
            super.onCreateOptionsMenu(menu, inflater);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    public static class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {

        private static final String KEY_TITLE = "fragment_title";
        private static final String KEY_FRAGMENT = "fragment";
        private List<Map<String, Object>> maps = new ArrayList<>();
        private Activity activity;

        public ViewPagerAdapter(FragmentManager fm, Activity activity) {
            super(fm);
            this.activity = activity;
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
            View v = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) v.findViewById(R.id.textView);
            tv.setText((CharSequence) maps.get(position).get(KEY_TITLE));
            TextView countTextView = (TextView) v.findViewById(R.id.count_textView);
            countTextView.setText("[0]");
            return v;
        }
    }
}