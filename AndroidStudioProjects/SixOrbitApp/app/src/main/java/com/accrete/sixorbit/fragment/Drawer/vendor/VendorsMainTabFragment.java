package com.accrete.sixorbit.fragment.Drawer.vendor;

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
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerPendingInvoiceTabFragment;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by agt on 15/12/17.
 */

public class VendorsMainTabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String venId, email, name, walletBalance;
    private ViewPagerAdapter viewPagerAdapter;

    public static CustomerPendingInvoiceTabFragment newInstance() {
        return new CustomerPendingInvoiceTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        venId = bundle.getString(getString(R.string.venid));
        email = bundle.getString(getString(R.string.email));
        name = bundle.getString(getString(R.string.name));
        walletBalance = bundle.getString(getString(R.string.wallet_balance));
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
        setupViewPager(viewPager, venId, email, name);
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                if (position == 1) {
                    if (mFragment instanceof VendorTransactionTabFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((VendorTransactionTabFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 2) {
                    if (mFragment instanceof VendorPendingInvoiceTabFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((VendorPendingInvoiceTabFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 3) {
                    if (mFragment instanceof VendorInvoiceTabFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((VendorInvoiceTabFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 4) {
                    if (mFragment instanceof PurchaseOrderHistoryFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((PurchaseOrderHistoryFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 5) {
                    if (mFragment instanceof VendorsConsignmentsFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((VendorsConsignmentsFragment) mFragment).doRefresh();
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

    private void setupViewPager(ViewPager viewPager, String venId, String emailId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.venid), venId);
        bundle.putString(getString(R.string.email), emailId);
        bundle.putString(getString(R.string.name), name);
        bundle.putString(getString(R.string.wallet_balance), walletBalance);

        VendorDetailTabFragment vendorDetailTabFragment = new VendorDetailTabFragment();
        vendorDetailTabFragment.setArguments(bundle);

        VendorTransactionTabFragment vendorTransactionTabFragment = new VendorTransactionTabFragment();
        vendorTransactionTabFragment.setArguments(bundle);

        VendorPendingInvoiceTabFragment vendorPendingInvoiceTabFragment = new VendorPendingInvoiceTabFragment();
        vendorPendingInvoiceTabFragment.setArguments(bundle);

        PurchaseOrderHistoryFragment purchaseOrderHistoryFragment = new PurchaseOrderHistoryFragment();
        purchaseOrderHistoryFragment.setArguments(bundle);

        VendorsConsignmentsFragment vendorsConsignmentsFragment = new VendorsConsignmentsFragment();
        vendorsConsignmentsFragment.setArguments(bundle);

        VendorInvoiceTabFragment vendorInvoiceTabFragment = new VendorInvoiceTabFragment();
        vendorInvoiceTabFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Info", "Wallet", "Pending Invoice", "Invoice", "Order", "Consignment"};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, vendorDetailTabFragment);
                vendorDetailTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, vendorTransactionTabFragment);
                vendorTransactionTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, vendorPendingInvoiceTabFragment);
                vendorPendingInvoiceTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, vendorInvoiceTabFragment);
                vendorInvoiceTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 4) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, purchaseOrderHistoryFragment);
                purchaseOrderHistoryFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 5) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, vendorsConsignmentsFragment);
                vendorsConsignmentsFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
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
