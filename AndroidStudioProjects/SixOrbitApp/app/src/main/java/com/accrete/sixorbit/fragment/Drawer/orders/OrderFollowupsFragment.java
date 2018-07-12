package com.accrete.sixorbit.fragment.Drawer.orders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.adapter.FollowUpAdapter;
import com.accrete.sixorbit.fragment.Drawer.followups.RecyclerItemTouchHelper;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.interfaces.SendFollowUpMobileInterface;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by {Anshul} on 4/4/18.
 */

public class OrderFollowupsFragment extends android.support.v4.app.Fragment implements FollowUpAdapter.FollowUpAdapterListener,
        SendFollowUpMobileInterface, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private TextView textViewEmpty;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private FollowUpAdapter mAdapter;
    private List<FollowUp> followUpArrayList = new ArrayList<FollowUp>();
    private FragmentManager fragmentManager;
    private String mobileNumber, orderId, leadMobileNumber;
    private boolean swipeToRecord;
    private DatabaseHandler databaseHandler;
    private int swipedPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        orderId = bundle.getString(getString(R.string.order_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotation_follow_ups, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View rootView) {
        databaseHandler = new DatabaseHandler(getActivity());

        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mAdapter = new FollowUpAdapter(getActivity(), followUpArrayList, this, recyclerView, fragmentManager,
                this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        setSwipeForRecyclerView();


    }

    public void updateList() {
        if (followUpArrayList != null && followUpArrayList.size() > 0) {
            followUpArrayList.clear();
        }
        followUpArrayList.addAll(databaseHandler.getOrderFollowUps(orderId));
        mAdapter.notifyDataSetChanged();
        if (followUpArrayList != null && followUpArrayList.size() > 0) {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void sendMobileNumber(String mnumber) {
        this.mobileNumber = mnumber;
        callAction();
    }

    @Override
    public void sendLeadMobileNumber(String lead_mobile_number) {
        this.leadMobileNumber = lead_mobile_number;
    }

    @SuppressLint("MissingPermission")
    public void callAction() {

        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            getActivity().startActivity(intentCall);
        } else {
            CustomisedToast.error(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        swipedPosition = viewHolder.getAdapterPosition();

        //Permission Check and disable record follow up
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.followup_take_permission))) {
            Intent intent = new Intent(getActivity(), RecordFollowUpActivity.class);
            if (followUpArrayList.get(swipedPosition).getChkoid() != null && !followUpArrayList.get(swipedPosition).getChkoid().isEmpty()) {
                intent.putExtra(getString(R.string.order_id), followUpArrayList.get(swipedPosition).getChkoid());
            }
            if (followUpArrayList.get(swipedPosition).getFoid() != null && !followUpArrayList.get(swipedPosition).getFoid().isEmpty()) {
                intent.putExtra(getString(R.string.foid), followUpArrayList.get(swipedPosition).getFoid());
                // intent.putExtra(getString(R.string.leasid), strLeasId);
            } else if (followUpArrayList.get(swipedPosition).getSyncId() != null && !followUpArrayList.get(swipedPosition).getSyncId().isEmpty()) {
                intent.putExtra(getString(R.string.sync_id), followUpArrayList.get(swipedPosition).getSyncId());
            }
            startActivityForResult(intent, 1000);
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "You have no permission to take any followup.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSwipeForRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this, swipeToRecord);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        // attaching the touch helper to recycler view
    }


}
