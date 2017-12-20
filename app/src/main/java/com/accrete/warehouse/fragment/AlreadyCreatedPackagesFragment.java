package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.AlreadyCreatedPackagesAdapter;
import com.accrete.warehouse.adapter.PackedAdapter;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.Packed;
import com.accrete.warehouse.model.RunningOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/12/17.
 */

public class AlreadyCreatedPackagesFragment extends Fragment implements AlreadyCreatedPackagesAdapter.AlreadyCreatedPackagesAdapterListener {
    private SwipeRefreshLayout alreadyCreatedPackagesSwipeRefreshLayout;
    private RecyclerView alreadyCreatedPackagesRecyclerView;
    private TextView alreadyCreatedPackagesEmptyView;
    private AlreadyCreatedPackagesAdapter packedAdapter;
    private List<Packages> packedList = new ArrayList<>();
    private Packages packages = new Packages();


    private void findViews(View rootView) {
        alreadyCreatedPackagesSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById( R.id.already_created_packages_swipe_refresh_layout );
        alreadyCreatedPackagesRecyclerView = (RecyclerView)rootView.findViewById( R.id.already_created_packages_recycler_view );
        alreadyCreatedPackagesEmptyView = (TextView)rootView.findViewById( R.id.already_created_packages_empty_view );

        packedAdapter = new AlreadyCreatedPackagesAdapter(getActivity(), packedList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        alreadyCreatedPackagesRecyclerView.setLayoutManager(mLayoutManager);
        alreadyCreatedPackagesRecyclerView.setHasFixedSize(true);
        alreadyCreatedPackagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        alreadyCreatedPackagesRecyclerView.setNestedScrollingEnabled(false);
        alreadyCreatedPackagesRecyclerView.setAdapter(packedAdapter);

        alreadyCreatedPackagesSwipeRefreshLayout.setEnabled(false);

        if(packedList.size()>0){
            alreadyCreatedPackagesRecyclerView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.GONE);
        }else{
            alreadyCreatedPackagesRecyclerView.setVisibility(View.GONE);
            alreadyCreatedPackagesEmptyView.setVisibility(View.VISIBLE);
            alreadyCreatedPackagesEmptyView.setText(getString(R.string.no_data_available));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_already_created_packages, container, false);
        Bundle bundle = this.getArguments();
    /*    if (bundle != null) {
            packedList = bundle.getParcelableArrayList("packagesList");
        }*/
        findViews(rootView);
        return rootView;
    }



    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }
}

