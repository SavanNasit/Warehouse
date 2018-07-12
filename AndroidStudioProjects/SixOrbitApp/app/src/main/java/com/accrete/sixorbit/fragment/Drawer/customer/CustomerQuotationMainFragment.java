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
 * Created by agt on 21/12/17.
 */

public class CustomerQuotationMainFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String cuId, qoId;
    private ViewPagerAdapter viewPagerAdapter;

    public static CustomerQuotationMainFragment newInstance() {
        return new CustomerQuotationMainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        cuId = bundle.getString(getString(R.string.cuid));
        qoId = bundle.getString(getString(R.string.qo_id));
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
        setupViewPager(viewPager, cuId, qoId);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                final android.support.v4.app.Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                if (position == 1) {
                    if (mFragment instanceof CustomerQuotationProductsTabFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((CustomerQuotationProductsTabFragment) mFragment).doRefresh();
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

    private void setupViewPager(ViewPager viewPager, String cuId, String qoId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.qo_id), qoId);

        CustomerQuotationDetailsTabFragment customerQuotationDetailsTabFragment = new CustomerQuotationDetailsTabFragment();
        customerQuotationDetailsTabFragment.setArguments(bundle);

        CustomerQuotationProductsTabFragment customerQuotationProductsTabFragment = new CustomerQuotationProductsTabFragment();
        customerQuotationProductsTabFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Quotation Details", "Product Details"};

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerQuotationDetailsTabFragment);
                customerQuotationDetailsTabFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerQuotationProductsTabFragment);
                customerQuotationProductsTabFragment.setTargetFragment(this, getTargetRequestCode());
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

