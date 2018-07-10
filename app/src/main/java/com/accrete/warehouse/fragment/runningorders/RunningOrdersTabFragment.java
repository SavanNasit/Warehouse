package com.accrete.warehouse.fragment.runningorders;

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
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.utils.NonSwipeableViewPager;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by poonam on 6/4/18.
 */

public class RunningOrdersTabFragment extends Fragment {

    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;
    private String stringSearchText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            menu.clear();
            inflater.inflate(R.menu.search_view, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            if (searchTextView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchTextView, R.drawable.cursor_white); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //  Log.e("OnTextChange", query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String searchText) {
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
                                stringSearchText = searchText;
                                refreshFragment();
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
          //  new Handler().postDelayed(new Runnable() {
               // @Override
              //  public void run() {
                    ((RunningOrdersFragment) mFragment).callAPI(stringSearchText);
              //  }
        //    }, 200);
        } else if (mFragment instanceof RunningStockRequestFragment) {
           // new Handler().postDelayed(new Runnable() {
               // @Override
              //  public void run() {
                      ((RunningStockRequestFragment) mFragment).callAPI(stringSearchText);
              //  }
           // }, 200);
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


