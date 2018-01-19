package com.accrete.warehouse.fragment.receiveConsignment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PurchaseOrderAdapter;
import com.accrete.warehouse.model.PurchaseOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/8/17.
 */

public class ReceiveAgainstPurchaseOrderFragment extends Fragment implements PurchaseOrderAdapter.PurchaseOrderAdapterListener {
    private static String KEY_TITLE = "receive_po";
    PurchaseOrder purchaseOrder = new PurchaseOrder();
    private SwipeRefreshLayout receiveAgainstPurchaseOrderSwipeRefreshLayout;
    private RecyclerView receiveAgainstPurchaseOrderRecyclerView;
    private TextView receiveAgainstPurchaseOrderContainerEmptyView;
    private PurchaseOrderAdapter purchaseOrderAdapter;
    private List<PurchaseOrder> puchaseOrderList = new ArrayList<>();

    public static ReceiveAgainstPurchaseOrderFragment newInstance(String title) {
        ReceiveAgainstPurchaseOrderFragment f = new ReceiveAgainstPurchaseOrderFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_against_purchase_order, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        receiveAgainstPurchaseOrderSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.receive_against_purchase_order_swipe_refresh_layout);
        receiveAgainstPurchaseOrderRecyclerView = (RecyclerView) rootView.findViewById(R.id.receive_against_purchase_order_recycler_view);
        receiveAgainstPurchaseOrderContainerEmptyView = (TextView) rootView.findViewById(R.id.receive_against_purchase_order_container_empty_view);
        purchaseOrderAdapter = new PurchaseOrderAdapter(getActivity(), puchaseOrderList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        receiveAgainstPurchaseOrderRecyclerView.setLayoutManager(mLayoutManager);
        receiveAgainstPurchaseOrderRecyclerView.setHasFixedSize(true);
        receiveAgainstPurchaseOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        receiveAgainstPurchaseOrderRecyclerView.setNestedScrollingEnabled(false);
        receiveAgainstPurchaseOrderRecyclerView.setAdapter(purchaseOrderAdapter);

        purchaseOrder.setPurchaseOrderId("RPDPO2017112800168");
        purchaseOrder.setAmount("232409");
        purchaseOrder.setAmountAfterTax("12.88");
        purchaseOrder.setTax("10.00");
        purchaseOrder.setPayableAmount("787878");
        purchaseOrder.setCreatedTs("2017-06-12 12:00:00");
        purchaseOrder.setPurorsid("1");
        purchaseOrder.setWarehouseName("JP Nagar");
        purchaseOrder.setCreatedBy("Poonam Kukreti");


        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);
        puchaseOrderList.add(purchaseOrder);

        receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);


    }

    @Override
    public void onMessageRowClicked(int position, String orderId, String orderText) {

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_po);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_po));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_po));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_po));
        }
    }

}
