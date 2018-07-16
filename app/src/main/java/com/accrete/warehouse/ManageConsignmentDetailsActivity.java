package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.accrete.warehouse.fragment.manageConsignment.ChooseEventsForManageConsignmentFragment;

public class ManageConsignmentDetailsActivity extends AppCompatActivity {
    private FrameLayout container;
    private String iscid, iscsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_consignment_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Manage Consignments");
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

        if (getIntent() != null && getIntent().hasExtra("iscsid")) {
            iscsId = getIntent().getStringExtra("iscsid");
        }
        if (getIntent() != null && getIntent().hasExtra("iscid")) {
            iscid = getIntent().getStringExtra("iscid");
        }

        container = (FrameLayout) findViewById(R.id.container);

        ChooseEventsForManageConsignmentFragment chooseEventsForManageConsignmentFragment =
                new ChooseEventsForManageConsignmentFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, chooseEventsForManageConsignmentFragment)
                .commitAllowingStateLoss();
        Bundle bundle = new Bundle();
        bundle.putString("iscid", iscid);
        bundle.putString("iscsid", iscsId);
        chooseEventsForManageConsignmentFragment.setArguments(bundle);

    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof ChooseEventsForManageConsignmentFragment) {
            super.onBackPressed();
            return;
        }
    }
}
