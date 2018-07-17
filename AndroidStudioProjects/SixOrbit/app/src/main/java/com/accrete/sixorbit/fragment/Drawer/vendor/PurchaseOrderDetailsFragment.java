package com.accrete.sixorbit.fragment.Drawer.vendor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.sixorbit.R;

/**
 * Created by agt on 14/12/17.
 */

public class PurchaseOrderDetailsFragment extends Fragment {
    private String venid, orderId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        venid = bundle.getString(getString(R.string.venid));
        orderId = bundle.getString(getString(R.string.order_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purhcase_orders_details, container, false);
        //Find all widgets
     //   findViews(rootView);
        return rootView;
    }
}
