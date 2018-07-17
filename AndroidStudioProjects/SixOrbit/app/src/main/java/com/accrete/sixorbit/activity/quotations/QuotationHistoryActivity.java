package com.accrete.sixorbit.activity.quotations;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.quotation.QuotationHistoryTabFragment;

public class QuotationHistoryActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private String qoId, qoIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_history);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.qo_id))) {
            qoId = getIntent().getStringExtra(getString(R.string.qo_id));
        }
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.qo_id_text))) {
            qoIdText = getIntent().getStringExtra(getString(R.string.qo_id_text));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (qoIdText != null) {
            getSupportActionBar().setTitle("Quotation History of " + qoIdText);
        } else {
            getSupportActionBar().setTitle("Quotation History ");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        QuotationHistoryTabFragment quotationHistoryTabFragment = new QuotationHistoryTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.qo_id), qoId);

        quotationHistoryTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_container, quotationHistoryTabFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
