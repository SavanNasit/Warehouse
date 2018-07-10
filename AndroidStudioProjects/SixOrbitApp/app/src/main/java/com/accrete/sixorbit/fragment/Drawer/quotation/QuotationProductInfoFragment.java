package com.accrete.sixorbit.fragment.Drawer.quotation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.QuotationsProductsInfoAdapter;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.model.ItemData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationProductInfoFragment extends Fragment {
    private TextView textViewEmpty;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private QuotationsProductsInfoAdapter mAdapter;
    private ArrayList<ItemData> dataArrayList = new ArrayList<>();

    public QuotationProductInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotation_product_info, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View rootView) {
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mAdapter = new QuotationsProductsInfoAdapter(getActivity(), dataArrayList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        if (dataArrayList != null && dataArrayList.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void getData(ArrayList<ItemData> itemData) {
        if (dataArrayList != null && dataArrayList.size() == 0) {
            this.dataArrayList.addAll(itemData);

            mAdapter.notifyDataSetChanged();
        } else if (dataArrayList != null && dataArrayList.size() > 0) {
            dataArrayList.clear();
            this.dataArrayList.addAll(itemData);
            mAdapter.notifyDataSetChanged();
        }

        if (dataArrayList != null && dataArrayList.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
