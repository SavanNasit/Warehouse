package com.accrete.sixorbit.activity.customers;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerOrderDetailsMainFragment;

public class CustomerOrderDetailsActivity extends AppCompatActivity {
    private String cuId, orderId, orderTitle;
    private Toolbar toolbarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_main);

        if (getIntent().hasExtra(getString(R.string.cuid))) {
            cuId = getIntent().getStringExtra(getString(R.string.cuid));
        }
        if (getIntent().hasExtra(getString(R.string.order_id))) {
            orderId = getIntent().getStringExtra(getString(R.string.order_id));
        }
        if (getIntent().hasExtra(getString(R.string.order_id_text))) {
            orderTitle = getIntent().getStringExtra(getString(R.string.order_id_text));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbarBottom.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(orderTitle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomerOrderDetailsMainFragment customerOrderDetailsMainFragment = new CustomerOrderDetailsMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.order_id), orderId);
        customerOrderDetailsMainFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, customerOrderDetailsMainFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
