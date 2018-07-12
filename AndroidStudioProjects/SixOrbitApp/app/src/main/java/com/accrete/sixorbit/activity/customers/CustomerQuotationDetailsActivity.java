package com.accrete.sixorbit.activity.customers;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerQuotationMainFragment;

public class CustomerQuotationDetailsActivity extends AppCompatActivity {
    private String cuId, qoId, quotationTitle;
    private Toolbar toolbarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_main);

        if (getIntent().hasExtra(getString(R.string.cuid))) {
            cuId = getIntent().getStringExtra(getString(R.string.cuid));
        }
        if (getIntent().hasExtra(getString(R.string.qo_id))) {
            qoId = getIntent().getStringExtra(getString(R.string.qo_id));
        }
        if (getIntent().hasExtra(getString(R.string.qo_id_text))) {
            quotationTitle = getIntent().getStringExtra(getString(R.string.qo_id_text));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbarBottom.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(quotationTitle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomerQuotationMainFragment customerQuotationMainFragment = new CustomerQuotationMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.cuid), cuId);
        bundle.putString(getString(R.string.qo_id), qoId);
        customerQuotationMainFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, customerQuotationMainFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
