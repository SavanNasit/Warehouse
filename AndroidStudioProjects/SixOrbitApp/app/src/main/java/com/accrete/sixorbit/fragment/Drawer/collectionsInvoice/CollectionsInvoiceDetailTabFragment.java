package com.accrete.sixorbit.fragment.Drawer.collectionsInvoice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by {Anshul} on 5/6/18.
 */

public class CollectionsInvoiceDetailTabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageViewLoader;
    private String invId;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        invId = bundle.getString(getString(R.string.invid));
    }

    private void initializeView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        imageViewLoader = (ImageView) view.findViewById(R.id.imageView_loader);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(viewPager, invId);
        viewPager.setOffscreenPageLimit(3);
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

        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager, String invId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.invid), invId);

        CollectionsBasicInfoFragment collectionsBasicInfoFragment = new CollectionsBasicInfoFragment();
        collectionsBasicInfoFragment.setArguments(bundle);

        CollectionsInvoiceItemsFragment collectionsInvoiceItemsFragment = new CollectionsInvoiceItemsFragment();
        collectionsInvoiceItemsFragment.setArguments(bundle);

        CollectionsInvoicePaymentDetailsFragment collectionsInvoicePaymentDetailsFragment = new
                CollectionsInvoicePaymentDetailsFragment();
        collectionsInvoicePaymentDetailsFragment.setArguments(bundle);

        CollectionsInvoiceEwayBillDetailsFragment ewayBillDetailsFragment = new
                CollectionsInvoiceEwayBillDetailsFragment();
        ewayBillDetailsFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Customer", "Items", "  Payment Details  ", "  Eway Bill Details  "};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, collectionsBasicInfoFragment);
                collectionsBasicInfoFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, collectionsInvoiceItemsFragment);
                collectionsInvoiceItemsFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, collectionsInvoicePaymentDetailsFragment);
                collectionsInvoicePaymentDetailsFragment.getParentFragment();
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, ewayBillDetailsFragment);
                ewayBillDetailsFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void refreshFragment() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        int position = viewPager.getCurrentItem();
        if (position == 1) {
            if (mFragment instanceof CollectionsInvoiceItemsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CollectionsInvoiceItemsFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        } else if (position == 2) {
            if (mFragment instanceof CollectionsInvoicePaymentDetailsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CollectionsInvoicePaymentDetailsFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        } else if (position == 3) {
            if (mFragment instanceof CollectionsInvoiceEwayBillDetailsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CollectionsInvoiceEwayBillDetailsFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
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

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
