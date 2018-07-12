package com.accrete.sixorbit.activity.collections;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.collectionsInvoice.CollectionsInvoiceDetailTabFragment;

public class CollectionsViewInvoiceActivity extends AppCompatActivity {
    private FrameLayout frameContainer;
    private String invid, invoiceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections_view_invoice);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.invid))) {
            invid = getIntent().getStringExtra(getString(R.string.invid));
        }
        if (getIntent() != null && getIntent().hasExtra("text")) {
            invoiceNumber = getIntent().getStringExtra("text");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameContainer = (FrameLayout) findViewById(R.id.frame_container);

        toolbar.setTitle("Invoice " + invoiceNumber);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        CollectionsInvoiceDetailTabFragment collectionsInvoiceDetailTabFragment = new
                CollectionsInvoiceDetailTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.invid), invid);

        collectionsInvoiceDetailTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, collectionsInvoiceDetailTabFragment);
        fragmentTransaction.commit();

    }

}
