package com.accrete.warehouse.fragment.managePackages;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by poonam on 11/28/17.
 */


public class ManagePackagesFragment extends Fragment {
    private static final String KEY_TITLE = "ManagePackages";
    public ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;
    private ViewPagerAdapter viewPagerAdapter;


    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;


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
                                refreshFragment(searchText);
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
        tabLayoutExecute = (TabLayout) rootView.findViewById(R.id.tabs_execute);
        viewPagerExecute = (ViewPager) rootView.findViewById(R.id.view_pager_execute);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPagerExecute(viewPagerExecute);
        viewPagerExecute.setOffscreenPageLimit(7);
        tabLayoutExecute.setupWithViewPager(viewPagerExecute);
        viewPagerExecute.setAdapter(viewPagerAdapter);
        viewPagerExecute.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
/*

                final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPagerExecute.getCurrentItem());

                if (mFragment instanceof PackedFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((PackedFragment) mFragment).clearListAndRefresh();

                    }
                }

                if (mFragment instanceof PackedAgainstStockFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((PackedAgainstStockFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof ShippedPackageFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((ShippedPackageFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof OutForDeliveryFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((OutForDeliveryFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof DeliveredFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((DeliveredFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof AttemptFailedFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((AttemptFailedFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof ReAttemptFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        ((ReAttemptFragment) mFragment).clearListAndRefresh();
                    }
                }

                if (mFragment instanceof DeliveryFailedFragment) {

                    if (mFragment != null && mFragment.isAdded()) {
                        ((DeliveryFailedFragment) mFragment).clearListAndRefresh();
                    }
                }
*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(getArguments().getString("flagToRedirect")!=null &&
                !getArguments().getString("flagToRedirect").isEmpty() &&
                getArguments().getString("flagToRedirect").equals("runningOrders")){
           viewPagerExecute.setCurrentItem(0);
        }else {
            viewPagerExecute.setCurrentItem(1);
        }
    }

    private void refreshFragment(String stringSearchText) {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPagerExecute.getCurrentItem());

            if (mFragment instanceof PackedFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((PackedFragment) mFragment).searchAPI(stringSearchText);

                }
            }

          if (mFragment instanceof PackedAgainstStockFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((PackedAgainstStockFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof ShippedPackageFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((ShippedPackageFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof OutForDeliveryFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((OutForDeliveryFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof DeliveredFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((DeliveredFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof AttemptFailedFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((AttemptFailedFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof ReAttemptFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((ReAttemptFragment) mFragment).searchAPI(stringSearchText);
                }
            }

            if (mFragment instanceof DeliveryFailedFragment) {
                if (mFragment != null && mFragment.isAdded()) {
                    ((DeliveryFailedFragment) mFragment).searchAPI(stringSearchText);
                }
            }
    }

    private void setupViewPagerExecute(ViewPager viewPagerExecute) {
        PackedFragment packedFragment = new PackedFragment();
        PackedAgainstStockFragment packedAgainstStockFragment = new PackedAgainstStockFragment();
        ShippedPackageFragment shippedPackageFragment = new ShippedPackageFragment();
        OutForDeliveryFragment outForDeliveryFragment = new OutForDeliveryFragment();
        DeliveredFragment deliveredFragment = new DeliveredFragment();
        AttemptFailedFragment attemptFailedFragment = new AttemptFailedFragment();
        ReAttemptFragment reAttemptFragment = new ReAttemptFragment();
        DeliveryFailedFragment deliveryFailedFragment = new DeliveryFailedFragment();


        String[] title_arr;
        title_arr = new String[]{"  Packed  ", "  Packed Against Stock Request  ", "  Shipped  ",
                "  Out for Delivery  ", "  Delivered  ", "  Attempt Failed  ", "  Re-Attempt  ",
                "  Delivery Failed  "};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, packedFragment);
                packedFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, packedAgainstStockFragment);
                packedAgainstStockFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, shippedPackageFragment);
                shippedPackageFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, outForDeliveryFragment);
                outForDeliveryFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 4) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, deliveredFragment);
                deliveredFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 5) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, attemptFailedFragment);
                attemptFailedFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 6) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, reAttemptFragment);
                reAttemptFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 7) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, deliveryFailedFragment);
                deliveryFailedFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
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
        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 0) {
            Log.e("TAG_ORDERS", "" + str);
           /* PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.getData(str);*/

        }
    }

    public void checkFragmentAndDownloadPDF() {

        if (viewPagerExecute.getCurrentItem() == 0) {
            PackedFragment packedFragment =
                    (PackedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            packedFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 1) {
            PackedAgainstStockFragment packedAgainstStockFragment =
                    (PackedAgainstStockFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            packedAgainstStockFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 2) {
            ShippedPackageFragment shippedPackageFragment =
                    (ShippedPackageFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            shippedPackageFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 3) {
            OutForDeliveryFragment outForDeliveryFragment =
                    (OutForDeliveryFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            outForDeliveryFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 4) {
            DeliveredFragment deliveredFragment =
                    (DeliveredFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            deliveredFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 5) {
            AttemptFailedFragment attemptFailedFragment =
                    (AttemptFailedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            attemptFailedFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 6) {
            ReAttemptFragment reAttemptFragment =
                    (ReAttemptFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            reAttemptFragment.downloadPDF();
        } else if (viewPagerExecute.getCurrentItem() == 7) {
            DeliveryFailedFragment deliveryFailedFragment =
                    (DeliveryFailedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            deliveryFailedFragment.downloadPDF();
        }
    }

    public void checkFragmentAndRefresh() {
        if (viewPagerExecute.getCurrentItem() == 3) {
            OutForDeliveryFragment outForDeliveryFragment = (OutForDeliveryFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                    viewPagerExecute.getCurrentItem());
            outForDeliveryFragment.clearListAndRefresh();

        } else if (viewPagerExecute.getCurrentItem() == 5) {
            AttemptFailedFragment attemptFailedFragment = (AttemptFailedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                    viewPagerExecute.getCurrentItem());
            attemptFailedFragment.clearListAndRefresh();

        }
    }

    public void sendDocument(String selectedFilePath, String fileName) {
        if (viewPagerExecute.getCurrentItem() == 0) {
            PackedFragment packedFragment =
                    (PackedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            packedFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 1) {
            PackedAgainstStockFragment packedAgainstStockFragment =
                    (PackedAgainstStockFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            packedAgainstStockFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 2) {
            ShippedPackageFragment shippedPackageFragment =
                    (ShippedPackageFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            shippedPackageFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 3) {
            OutForDeliveryFragment outForDeliveryFragment =
                    (OutForDeliveryFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            outForDeliveryFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 4) {
            DeliveredFragment deliveredFragment =
                    (DeliveredFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            deliveredFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 5) {
            AttemptFailedFragment attemptFailedFragment =
                    (AttemptFailedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            attemptFailedFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 6) {
            ReAttemptFragment reAttemptFragment =
                    (ReAttemptFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            reAttemptFragment.addDocument(selectedFilePath, fileName);
        } else if (viewPagerExecute.getCurrentItem() == 7) {
            DeliveryFailedFragment deliveryFailedFragment =
                    (DeliveryFailedFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
            deliveryFailedFragment.addDocument(selectedFilePath, fileName);
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
