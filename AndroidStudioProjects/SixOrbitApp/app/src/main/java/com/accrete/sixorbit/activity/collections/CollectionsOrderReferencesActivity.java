package com.accrete.sixorbit.activity.collections;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.collectionsOrders.OrderReferenceMainTabFragment;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomersMainTabFragment;

public class CollectionsOrderReferencesActivity extends AppCompatActivity {
    private FrameLayout container;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_order_references);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.order_id))) {
            orderId = getIntent().getStringExtra(getString(R.string.order_id));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Order References");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        container = (FrameLayout) findViewById(R.id.container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        OrderReferenceMainTabFragment orderReferenceMainTabFragment = new OrderReferenceMainTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.order_id), orderId);

        orderReferenceMainTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, orderReferenceMainTabFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
