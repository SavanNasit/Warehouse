package com.accrete.sixorbit.fragment.Drawer.quotation;

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
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by agt on 28/12/17.
 */

public class ManageQuotationFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;

    public static ManageQuotationFragment newInstance(String title) {
        ManageQuotationFragment f = new ManageQuotationFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.quotation));
        }
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.manage_quotation));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.manage_quotation));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.manage_quotation));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.manage_quotation));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        // setHasOptionsMenu(true);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager();
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                refreshFragment("");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void refreshFragment(String str) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof PendingManageQuotationFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((PendingManageQuotationFragment) mFragment).callAPI(str);
                }
            }, 200);
        } else if (mFragment instanceof ConvertedManageFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ConvertedManageFragment) mFragment).callAPI(str);
                }
            }, 200);
        } else if (mFragment instanceof CancelledManageQuotationFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CancelledManageQuotationFragment) mFragment).callAPI(str);
                }
            }, 200);
        } else if (mFragment instanceof ToBeApprovedQuotationFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ToBeApprovedQuotationFragment) mFragment).callAPI(str);
                }
            }, 200);
        }
    }

    public void searchInFragment(String str) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof PendingManageQuotationFragment) {
            ((PendingManageQuotationFragment) mFragment).callAPI(str);
        } else if (mFragment instanceof ConvertedManageFragment) {
            ((ConvertedManageFragment) mFragment).callAPI(str);
        } else if (mFragment instanceof CancelledManageQuotationFragment) {
            ((CancelledManageQuotationFragment) mFragment).callAPI(str);
        } else if (mFragment instanceof ToBeApprovedQuotationFragment) {
            ((ToBeApprovedQuotationFragment) mFragment).callAPI(str);
        }
    }

    private void setupViewPager() {

        PendingManageQuotationFragment pendingManageQuotationFragment = new PendingManageQuotationFragment();
        ConvertedManageFragment convertedFragment = new ConvertedManageFragment();
        CancelledManageQuotationFragment cancelledManageQuotationFragment = new CancelledManageQuotationFragment();
        ToBeApprovedQuotationFragment toBeApprovedQuotationFragment = new ToBeApprovedQuotationFragment();

        String[] title_arr;
        title_arr = new String[]{"Pending", "Converted", "Cancelled", "To be approved"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, pendingManageQuotationFragment);
                pendingManageQuotationFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, convertedFragment);
                convertedFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, cancelledManageQuotationFragment);
                cancelledManageQuotationFragment.getParentFragment();
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, toBeApprovedQuotationFragment);
                toBeApprovedQuotationFragment.getParentFragment();
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

