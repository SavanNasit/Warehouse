package com.accrete.warehouse.fragment.receiveConsignment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;

/**
 * Created by poonam on 12/5/17.
 */

public class ReceiveConsignmentFragment extends Fragment {

    private static final String KEY_TITLE = "ReceiveConsignment";
    private ViewPager viewPagerReceive;
    private TabLayout tabLayoutReceive;
    private LinearLayout fragmentReceiveConsignmentReceiveDirectly;
    private LinearLayout fragmentReceiveConsignmentReceivePo;
    private LinearLayout fragmentReceiveConsignmentStockRequestList;

    public static ReceiveConsignmentFragment newInstance(String title) {
        ReceiveConsignmentFragment f = new ReceiveConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_consignment, container, false);
        findViews(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_consignment_fragment));
        }
    }


    private void findViews(View rootView) {
            fragmentReceiveConsignmentReceiveDirectly = (LinearLayout)rootView.findViewById( R.id.fragment_receive_consignment_receive_directly );
            fragmentReceiveConsignmentReceivePo = (LinearLayout)rootView.findViewById( R.id.fragment_receive_consignment_receive_po );
            fragmentReceiveConsignmentStockRequestList = (LinearLayout)rootView.findViewById( R.id.fragment_receive_consignment_stock_request_list );

        fragmentReceiveConsignmentReceiveDirectly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveDirectlyFragment receiveDirectlyFragment = new ReceiveDirectlyFragment();
                  //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.receive_consignment_container, receiveDirectlyFragment).addToBackStack(null).commit();

                }
            });

        fragmentReceiveConsignmentReceivePo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Fragment receiveAgainstPurchaseOrderFragment = ReceiveAgainstPurchaseOrderFragment.newInstance(getString(R.string.receive_po));
                   // getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveAgainstPurchaseOrderFragment).commitAllowingStateLoss();
                    ReceiveAgainstPurchaseOrderFragment receiveAgainstPurchaseOrderFragment = new ReceiveAgainstPurchaseOrderFragment();
                    //getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.receive_consignment_container, receiveAgainstPurchaseOrderFragment).addToBackStack(null).commit();



                }
            });

        fragmentReceiveConsignmentStockRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fragment receiveAgainstPurchaseOrderFragment = ReceiveAgainstPurchaseOrderFragment.newInstance(getString(R.string.receive_po));
                // getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveAgainstPurchaseOrderFragment).commitAllowingStateLoss();

                StockRequestListFragment stockRequestListFragment = new StockRequestListFragment();
                //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.receive_consignment_container, stockRequestListFragment).addToBackStack(null).commit();



            }
        });

    }

}
