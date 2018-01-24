package com.accrete.warehouse.fragment.managePackages;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.warehouse.R;
import com.accrete.warehouse.fragment.createpackage.PendingItemsFragment;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by poonam on 11/28/17.
 */


public class ManagePackagesFragment extends Fragment {
    private static final String KEY_TITLE = "ManagePackages";
    private ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;
    private ViewPagerAdapter viewPagerAdapter;

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
                final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPagerExecute.getCurrentItem());
                if (mFragment instanceof PackedFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        //    ((PackedFragment) mFragment).clearListAndRefresh();
                    }
                }
                if (mFragment instanceof PackedAgainstStockFragment) {
                    if (mFragment != null && mFragment.isAdded()) {
                        //    ((PackedAgainstStockFragment) mFragment).clearListAndRefresh();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        title_arr = new String[]{"Packed", "Packed Against Stock Request", "Shipped", "Out for Delivery", "Delivered",
                "Attempt Failed", "Re-Attempt", "Delivery Failed"};


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
            PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.getData(str);

        }
    }

    public void checkFragmentAndDownloadPDF() {
        if (viewPagerExecute.getCurrentItem() == 1) {
            PackedAgainstStockFragment packedAgainstStockFragment =
                    (PackedAgainstStockFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute,
                            viewPagerExecute.getCurrentItem());
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
