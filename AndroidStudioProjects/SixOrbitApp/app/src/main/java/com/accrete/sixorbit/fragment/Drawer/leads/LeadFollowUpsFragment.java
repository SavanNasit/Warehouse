package com.accrete.sixorbit.fragment.Drawer.leads;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.activity.lead.LeadInfoActivity;
import com.accrete.sixorbit.adapter.FollowUpAdapter;
import com.accrete.sixorbit.fragment.Drawer.followups.RecyclerItemTouchHelper;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.interfaces.SendFollowUpMobileInterface;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by agt on 14/11/17.
 */

public class LeadFollowUpsFragment extends Fragment implements FollowUpAdapter.FollowUpAdapterListener,
        SendFollowUpMobileInterface, LeadInfoActivity.UpdatableFragment, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    public FollowUpAdapter followUpAdapter;
    private TextView leadInfoFollowUp;
    private RecyclerView recyclerViewFollowup;
    private TextView textviewLeadFollowUpEmpty;
    private DatabaseHandler databaseHandler;
    private List<FollowUp> followUps = new ArrayList<FollowUp>();
    private FragmentManager fragmentManager;
    private String strLeadId, strLeasId;
    private LinearLayoutManager mLayoutManager;
    private int swipedPosition;
    private Paint p = new Paint();
    private ItemTouchHelper itemTouchHelper;
    private String mobileNumber, leadMobileNumber;
    private boolean swipeToRecord;

    public static LeadFollowUpsFragment newInstance() {
        return new LeadFollowUpsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        strLeadId = bundle.getString(getString(R.string.leaid));
        strLeasId = bundle.getString(getString(R.string.leasid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lead_followups_fragment, container, false);
        databaseHandler = new DatabaseHandler(getActivity());

        leadInfoFollowUp = (TextView) rootView.findViewById(R.id.lead_info_follow_up);
        recyclerViewFollowup = (RecyclerView) rootView.findViewById(R.id.recycler_view_followup);
        textviewLeadFollowUpEmpty = (TextView) rootView.findViewById(R.id.textview_lead_follow_up_empty);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewFollowup.setLayoutManager(mLayoutManager);
        recyclerViewFollowup.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        followUpAdapter = new FollowUpAdapter(getActivity(), followUps, this, recyclerViewFollowup, fragmentManager,
                this);
        recyclerViewFollowup.setAdapter(followUpAdapter);

        getDataFromDB();

        setSwipeForRecyclerView();

        return rootView;
    }

    public void getDataFromDB() {
        if (databaseHandler != null) {
            if (followUps.size() > 0) ;
            {
                followUps.clear();
            }
            followUps = databaseHandler.getLeadfollowUps(strLeadId);
            //Reverse
            Collections.reverse(followUps);
            if (recyclerViewFollowup.getVisibility() == View.GONE) {
                recyclerViewFollowup.setVisibility(View.VISIBLE);
            }
            followUpAdapter = new FollowUpAdapter(getActivity(), followUps, this, recyclerViewFollowup,
                    fragmentManager,this);
            recyclerViewFollowup.setAdapter(followUpAdapter);
            followUpAdapter.notifyDataSetChanged();

            if (followUps != null && followUps.size() > 0) {
                recyclerViewFollowup.setVisibility(View.VISIBLE);
                textviewLeadFollowUpEmpty.setVisibility(View.GONE);
            } else {
                recyclerViewFollowup.setVisibility(View.GONE);
                textviewLeadFollowUpEmpty.setText("There is no follow up.");
                textviewLeadFollowUpEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        followUpAdapter.notifyDataSetChanged();
    }

    @Override
    public void sendMobileNumber(String mnumber) {
        this.mobileNumber = mnumber;
        Log.e(" mNumber in Followups", mobileNumber + "");
    }

    @Override
    public void sendLeadMobileNumber(String lead_mobile_number) {
        this.leadMobileNumber = lead_mobile_number;
        Log.e(" mNumber Lead Contact", leadMobileNumber + "");
    }

    private void setSwipeForRecyclerView() {

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,
                this, swipeToRecord);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewFollowup);
  /*      ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FollowUpAdapter.MyViewHolder
                        && ((FollowUpAdapter.MyViewHolder) viewHolder).isSwipeToRecord) {
                    if ((strLeasId != null && strLeasId.equals("3"))) {
                        CustomisedToast.error(getActivity(), getString(R.string.converted_toast)).show();
                    } else if ((strLeasId != null && strLeasId.equals("4"))) {
                        CustomisedToast.error(getActivity(), getString(R.string.cancelled_toast)).show();
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder);
                } else {
                    return 0;
                }
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


     // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerViewFollowup);*/
    }


    @Override
    public void reload() {
        getDataFromDB();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        swipedPosition = viewHolder.getAdapterPosition();

        if ((strLeasId != null && strLeasId.equals("3"))) {
        } else if ((strLeasId != null && strLeasId.equals("4"))) {
        } else {
            //Permission Check and disable record follow up
            if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.followup_take_permission))) {

                Intent intent = new Intent(getActivity(), RecordFollowUpActivity.class);
                if (followUps.get(swipedPosition).getLeadId() != null && !followUps.get(swipedPosition).getLeadId().isEmpty()) {
                    intent.putExtra(getString(R.string.lead_id), followUps.get(swipedPosition).getLeadId());
                }
                if (followUps.get(swipedPosition).getFoid() != null && !followUps.get(swipedPosition).getFoid().isEmpty()) {
                    intent.putExtra(getString(R.string.foid), followUps.get(swipedPosition).getFoid());
                    intent.putExtra(getString(R.string.leasid), strLeasId);
                } else if (followUps.get(swipedPosition).getSyncId() != null && !followUps.get(swipedPosition).getSyncId().isEmpty()) {
                    intent.putExtra(getString(R.string.sync_id), followUps.get(swipedPosition).getSyncId());
                }
                startActivityForResult(intent, 1000);
                followUpAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "You have no permission to take any followup.", Toast.LENGTH_SHORT).show();
            }
        }
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

}
