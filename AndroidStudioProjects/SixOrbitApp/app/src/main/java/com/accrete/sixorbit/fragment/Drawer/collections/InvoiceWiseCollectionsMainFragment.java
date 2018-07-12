package com.accrete.sixorbit.fragment.Drawer.collections;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
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
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by {Anshul} on 28/5/18.
 */

public class InvoiceWiseCollectionsMainFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;

    public static InvoiceWiseCollectionsMainFragment newInstance(String title) {
        InvoiceWiseCollectionsMainFragment f = new InvoiceWiseCollectionsMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.my_collections));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.my_collections));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.my_collections));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.my_collections));
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

        if (getWidthScreen(getActivity()) > 400) {
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
                refreshFragments("");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void refreshFragments(String str) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof OrderInvoiceCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((OrderInvoiceCollectionFragment) mFragment).doRefresh(str);
                }
            }, 200);
        } else if (mFragment instanceof JobCardInvoiceCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((JobCardInvoiceCollectionFragment) mFragment).doRefresh(str);
                }
            }, 200);
        } else if (mFragment instanceof OpenInvoiceCollectionFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((OpenInvoiceCollectionFragment) mFragment).doRefresh(str);
                }
            }, 200);
        }
    }

    private void setupViewPager() {

        OrderInvoiceCollectionFragment orderInvoiceCollectionFragment = new OrderInvoiceCollectionFragment();
        JobCardInvoiceCollectionFragment jobCardInvoiceCollectionFragment = new JobCardInvoiceCollectionFragment();
        OpenInvoiceCollectionFragment openInvoiceCollectionFragment = new OpenInvoiceCollectionFragment();

        String[] title_arr;
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        title_arr = new String[]{"  Order Invoice  ", "  Jobcard Invoice  ", "  Open Invoice  "};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, orderInvoiceCollectionFragment);
                orderInvoiceCollectionFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, jobCardInvoiceCollectionFragment);
                jobCardInvoiceCollectionFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, openInvoiceCollectionFragment);
                openInvoiceCollectionFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }


    public void searchInFragment(String str) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof OrderInvoiceCollectionFragment) {
            ((OrderInvoiceCollectionFragment) mFragment).doRefresh(str);
        } else if (mFragment instanceof JobCardInvoiceCollectionFragment) {
            ((JobCardInvoiceCollectionFragment) mFragment).doRefresh(str);
        } else if (mFragment instanceof OpenInvoiceCollectionFragment) {
            ((OpenInvoiceCollectionFragment) mFragment).doRefresh(str);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.collections));
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void onBackPressed() {
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

