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
import com.accrete.sixorbit.adapter.QuotationHistoryAdapter;
import com.accrete.sixorbit.adapter.QuotationsProductsInfoAdapter;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.QuotationHistory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationHistoryListFragment extends Fragment {

    private TextView textViewEmpty;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private QuotationHistoryAdapter mAdapter;
    private ArrayList<QuotationHistory> historyArrayList= new ArrayList<>();

    public QuotationHistoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotation_history_list, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View rootView) {
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mAdapter = new QuotationHistoryAdapter(getActivity(), historyArrayList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        if (historyArrayList != null && historyArrayList.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void getData(ArrayList<QuotationHistory> historyDataArrayList) {
        if (historyArrayList != null && historyArrayList.size() == 0) {
            this.historyArrayList.addAll(historyDataArrayList);

            mAdapter.notifyDataSetChanged();
        } else if (historyArrayList != null && historyArrayList.size() > 0) {
            historyArrayList.clear();
            this.historyArrayList.addAll(historyDataArrayList);
            mAdapter.notifyDataSetChanged();
        }

        if (historyArrayList != null && historyArrayList.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
