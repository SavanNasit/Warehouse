package com.accrete.warehouse.fragment.manageConsignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.accrete.warehouse.R;

/**
 * Created by poonam on 7/3/18.
 */

public class ChooseEventsForManageConsignmentFragment extends Fragment {
    private static final String KEY_TITLE = "ChooseEventsForManageConsignmentFragment";
    private LinearLayout linearLayoutMain;
    private LinearLayout fragmentManageConsignmentView;
    private LinearLayout fragmentManageConsignmentEdit;
    private LinearLayout fragmentManageConsignmentConsumption;
    private LinearLayout fragmentManageConsignmentAllocation;
    private LinearLayout fragmentManageConsignmentApprove;
    //private LinearLayout fragmentManageConsignmentGrin;
    private String iscid, iscsid;

    public static ChooseEventsForManageConsignmentFragment newInstance(String title) {
        ChooseEventsForManageConsignmentFragment f = new ChooseEventsForManageConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_events_for_manage_consignment, container, false);
        iscid = getArguments().getString("iscid");
        iscsid = getArguments().getString("iscsid");
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.manage_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.manage_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.manage_consignment_fragment));
        }
    }

    private void findViews(View rootView) {
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linear_layout_main);
        fragmentManageConsignmentView = (LinearLayout) rootView.findViewById(R.id.fragment_manage_consignment_view);
        fragmentManageConsignmentEdit = (LinearLayout) rootView.findViewById(R.id.fragment_manage_consignment_edit);
        fragmentManageConsignmentConsumption = (LinearLayout) rootView.findViewById(R.id.fragment_manage_consignment_consumption);
        fragmentManageConsignmentAllocation = (LinearLayout) rootView.findViewById(R.id.fragment_manage_consignment_allocation);
        fragmentManageConsignmentApprove = (LinearLayout) rootView.findViewById(R.id.fragment_manage_consignment_approve);
        //fragmentManageConsignmentGrin = (LinearLayout)rootView.findViewById( R.id.fragment_manage_consignment_grin );

        if (iscsid != null && !iscsid.isEmpty() && iscsid.equals("6")) {
            fragmentManageConsignmentAllocation.setVisibility(View.GONE);
            fragmentManageConsignmentConsumption.setVisibility(View.GONE);
            fragmentManageConsignmentApprove.setVisibility(View.VISIBLE);
        } else {
            fragmentManageConsignmentAllocation.setVisibility(View.VISIBLE);
            fragmentManageConsignmentConsumption.setVisibility(View.VISIBLE);
            fragmentManageConsignmentApprove.setVisibility(View.GONE);
        }


        fragmentManageConsignmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(getActivity(), ViewConsignmentActivity.class);
                intentView.putExtra("iscid", iscid);
                startActivity(intentView);
            }
        });

        fragmentManageConsignmentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(getActivity(), EditConsignmentActivity.class);
                intentView.putExtra("iscid", iscid);
                startActivity(intentView);
            }
        });

        fragmentManageConsignmentConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(getActivity(), ConsumptionActivity.class);
                intentView.putExtra("iscid", iscid);
                startActivity(intentView);
            }
        });

        fragmentManageConsignmentAllocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(getActivity(), AllocationActivity.class);
                intentView.putExtra("iscid", iscid);
                startActivity(intentView);
            }
        });

        fragmentManageConsignmentApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(getActivity(), ApproveActivity.class);
                intentView.putExtra("iscid", iscid);
                startActivity(intentView);
            }
        });
    }

}
