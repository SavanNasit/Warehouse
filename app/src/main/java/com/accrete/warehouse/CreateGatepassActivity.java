package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.accrete.warehouse.fragment.ConfirmGatepassFragment;
import com.accrete.warehouse.fragment.CreatePassMainTabFragment;
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

public class CreateGatepassActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_consignment);
        findViews();
    }

    private void findViews() {
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        CreatePassMainTabFragment createPassMainTabFragment = new CreatePassMainTabFragment();
  /*      Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.iscid), iscId);
        consignmentMainTabFragment.setArguments(bundle);*/
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, createPassMainTabFragment);
        fragmentTransaction.commit();
    }

}



