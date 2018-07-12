package com.accrete.sixorbit.activity.vendors;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.vendor.ConsignmentDetailsTabFragment;
import com.accrete.sixorbit.fragment.Drawer.vendor.InventoryTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 14/12/17.
 */

public class VendorsPurchaseOrderActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String venid, orderId, orderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);

        if (getIntent().hasExtra(getString(R.string.venid))) {
            venid = getIntent().getStringExtra(getString(R.string.venid));
        }
        if (getIntent().hasExtra(getString(R.string.order_id))) {
            orderId = getIntent().getStringExtra(getString(R.string.order_id));
        }
        if (getIntent().hasExtra(getString(R.string.order_id_text))) {
            orderText = getIntent().getStringExtra(getString(R.string.order_id_text));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(orderText);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //Set TabLayout with ViewPager
        setupViewPager(viewPager, venid, orderId);
        viewPager.setOffscreenPageLimit(6);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            InventoryTabFragment inventoryTabFragment =
                                    (InventoryTabFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            inventoryTabFragment.doRefresh();
                        }
                    }, 1 * 200);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager, String venid, String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.venid), venid);
        bundle.putString(getString(R.string.order_id), orderId);

        ConsignmentDetailsTabFragment consignmentDetailsTabFragment = new ConsignmentDetailsTabFragment();
        consignmentDetailsTabFragment.setArguments(bundle);

        InventoryTabFragment inventoryTabFragment = new InventoryTabFragment();
        inventoryTabFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(consignmentDetailsTabFragment, "Purchase Order Details");
        adapter.addFragment(inventoryTabFragment, "Purchase Products");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

