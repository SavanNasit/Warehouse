package com.accrete.sixorbit.fragment.Drawer.customer;

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
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by agt on 15/12/17.
 */

public class CustomersMainTabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String cuId, email, name, walletBalance;
    private ViewPagerAdapter viewPagerAdapter;
    private int redirectionInDetail = 0;

    public static CustomersMainTabFragment newInstance() {
        return new CustomersMainTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        cuId = bundle.getString(getString(R.string.cuid));
        email = bundle.getString(getString(R.string.customer_email_id));
        name = bundle.getString(getString(R.string.name));
        walletBalance = bundle.getString(getString(R.string.wallet_balance));
        if (bundle.containsKey("redirectionInDetail")) {
            redirectionInDetail = bundle.getInt("redirectionInDetail", 0);
        }
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
        setupViewPager(viewPager, cuId, email, name);
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
                refreshFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(redirectionInDetail);
            }
        }, 500);
    }

    private void setupViewPager(ViewPager viewPager, String cuId, String emailId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.customer_email_id), emailId);
        bundle.putString(getString(R.string.name), name);
        bundle.putString(getString(R.string.wallet_balance), walletBalance);

        CustomerDetailTabFragment customerDetailTabFragment = new CustomerDetailTabFragment();
        customerDetailTabFragment.setArguments(bundle);

        CustomerWalletTabFragment customerWalletTabFragment = new CustomerWalletTabFragment();
        customerWalletTabFragment.setArguments(bundle);

        CustomerPendingInvoiceTabFragment customerPendingInvoiceTabFragment = new CustomerPendingInvoiceTabFragment();
        customerPendingInvoiceTabFragment.setArguments(bundle);

        CustomerOrderFragment customerOrderFragment = new CustomerOrderFragment();
        customerOrderFragment.setArguments(bundle);

        CustomerQuotationFragment customerQuotationFragment = new CustomerQuotationFragment();
        customerQuotationFragment.setArguments(bundle);

        CustomerInvoiceTabFragment customerInvoiceTabFragment = new CustomerInvoiceTabFragment();
        customerInvoiceTabFragment.setArguments(bundle);
        String[] title_arr;
        title_arr = new String[]{"Info", "Wallet", "Pending Invoice", "Invoice", "Quotation", "Order"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerDetailTabFragment);
                customerDetailTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerWalletTabFragment);
                customerWalletTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerPendingInvoiceTabFragment);
                customerPendingInvoiceTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerInvoiceTabFragment);
                customerInvoiceTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 4) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerQuotationFragment);
                customerQuotationFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 5) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerOrderFragment);
                customerOrderFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void refreshFragment() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            if (mFragment instanceof CustomerDetailTabFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerDetailTabFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        } else if (position == 1) {
            if (mFragment instanceof CustomerWalletTabFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerWalletTabFragment) mFragment).doRefresh();
                        ((CustomerWalletTabFragment) mFragment).getEmailAddress(email);
                    }
                }, 200);
            }
        } else if (position == 2) {
            if (mFragment instanceof CustomerPendingInvoiceTabFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerPendingInvoiceTabFragment) mFragment).doRefresh();
                        ((CustomerPendingInvoiceTabFragment) mFragment).getEmailAddress(email);
                    }
                }, 200);
            }
        } else if (position == 3) {
            if (mFragment instanceof CustomerInvoiceTabFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerInvoiceTabFragment) mFragment).doRefresh();
                        ((CustomerInvoiceTabFragment) mFragment).getEmailAddress(email);
                    }
                }, 200);
            }
        } else if (position == 4) {
            if (mFragment instanceof CustomerQuotationFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerQuotationFragment) mFragment).doRefresh();
                        ((CustomerQuotationFragment) mFragment).getEmailAddress(email);
                    }
                }, 200);
            }
        } else if (position == 5) {
            if (mFragment instanceof CustomerOrderFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerOrderFragment) mFragment).doRefresh();
                        ((CustomerOrderFragment) mFragment).getEmailAddress(email);
                    }
                }, 200);
            }
        }
    }

    public void getEmailAddress(final String emailAddress) {
        email = emailAddress;
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
