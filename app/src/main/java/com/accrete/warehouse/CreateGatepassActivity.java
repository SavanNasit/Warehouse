package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.accrete.warehouse.fragment.ConfirmGatepassFragment;
import com.accrete.warehouse.fragment.GatepassFragment;
import com.accrete.warehouse.fragment.PackageSelectionFragment;
import com.accrete.warehouse.model.ShippingBy;
import com.accrete.warehouse.model.ShippingType;
import com.accrete.warehouse.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by poonam on 12/21/17.
 */

public class CreateGatepassActivity extends AppCompatActivity implements PackageSelectionFragment.SendDataListener {
    public static NonSwipeableViewPager createGatepassViewpager;
    private List<String> packageIdList = new ArrayList<>();
    private List<ShippingType> shippingTypesList = new ArrayList<>();
    private List<ShippingBy> shippingByList = new ArrayList<>();

    public void getResult(ArrayList<String> packageIdListToAdd, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy){
        packageIdList = packageIdListToAdd;
        shippingTypesList=shippingTypes;
        shippingByList=shippingBy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_gatepass);
        createGatepassViewpager = (NonSwipeableViewPager) findViewById(R.id.create_gatepass_viewpager);
        createGatepassViewpager.setAdapter(new CreateGatepassViewpagerAdapter(
                getSupportFragmentManager()));
        createGatepassViewpager.setOffscreenPageLimit(3);

        PackageSelectionFragment packageSelectionFragment = new PackageSelectionFragment();
        packageSelectionFragment.sendData(CreateGatepassActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.create_gatepass));
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

        createGatepassViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 1) {
                    GatepassFragment gatepassFragment = new GatepassFragment();
                    gatepassFragment.setShippingData(packageIdList,shippingTypesList,shippingByList);
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

    @Override
    public void callback(List<String> packageList, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy) {
        /*  packageIdList = packageList;
          shippingTypesList=shippingTypes;
          shippingByList=shippingBy;*/
    }


    class CreateGatepassViewpagerAdapter extends FragmentPagerAdapter {


        public CreateGatepassViewpagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PackageSelectionFragment();
                case 1:
                    return new GatepassFragment();
                case 2:
                    return new ConfirmGatepassFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
