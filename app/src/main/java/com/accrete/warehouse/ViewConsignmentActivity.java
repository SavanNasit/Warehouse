package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.accrete.warehouse.fragment.DetailsFragment;
import com.accrete.warehouse.fragment.InventoryFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ViewConsignmentActivity extends AppCompatActivity {
    private ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consignment);
        findViews();
    }

    private void findViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.view_consignment));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });

        viewPagerExecute = (ViewPager) findViewById(R.id.view_pager_execute);
        setupViewPagerExecute(viewPagerExecute);
        viewPagerExecute.setOffscreenPageLimit(2);
        tabLayoutExecute = (TabLayout) findViewById(R.id.tabs_execute);
        tabLayoutExecute.setupWithViewPager(viewPagerExecute);

        tabLayoutExecute.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerExecute.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void setupViewPagerExecute(ViewPager viewPagerExecute) {
        DetailsFragment detailsFragment = new DetailsFragment();
        InventoryFragment inventoryFragment = new InventoryFragment();

        viewPagerExecuteAdapter adapter = new viewPagerExecuteAdapter(getSupportFragmentManager());
        adapter.addFragment(detailsFragment, "Details");
        adapter.addFragment(inventoryFragment, "Inventory");
        viewPagerExecute.setAdapter(adapter);
    }

    class viewPagerExecuteAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public viewPagerExecuteAdapter(FragmentManager manager) {
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
