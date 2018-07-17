package com.accrete.sixorbit.fragment.Drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerInterface;
import com.accrete.sixorbit.adapter.HomefollowUpViewPagerAdapter;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadFragment;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.interfaces.SendRecentFollowUpMobileInterface;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.NonSwipeableViewPager;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by poonam on 10/4/17 .
 */

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerInterface,
        SendRecentFollowUpMobileInterface {
    private static final String KEY_TITLE = "title";
    Drawer drawerSelection;
    FrameLayout frameLayout;
    DrawerActivity drawerActivity;
    FrameLayout recentFollowUp;
    String nLeadCount, dLeadCount;
    String nFollowUpCount, dFollowUpCount;
    ImageView viewPagerPreviousButton, viewPagerNextButton;
    TextView leadsCount, followupsCount, textViewRecentFollowUps;
    String mobile_number;
    private DatabaseHandler db;
    private RelativeLayout linearLayoutFollowUp, linearLayoutLeads;
    private NonSwipeableViewPager mViewPager;
    private List<FollowUp> followUpArrayList = new ArrayList<>();
    private HomefollowUpViewPagerAdapter homefollowUpViewPagerAdapter;

    public static HomeFragment newInstance(String title) {
        HomeFragment f = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        db = DatabaseHandler.getInstance(getActivity());
        textViewRecentFollowUps = (TextView) rootView.findViewById(R.id.home_textview_recent);
        leadsCount = (TextView) rootView.findViewById(R.id.textview_leads_count);
        followupsCount = (TextView) rootView.findViewById(R.id.textview_followups_count);
        recentFollowUp = (FrameLayout) rootView.findViewById(R.id.home_recent_followup);
        getDataFromDB();
        getCountFromDB();

        drawerActivity = new DrawerActivity();
        drawerActivity.setCallback(this);

        linearLayoutFollowUp = (RelativeLayout) rootView.findViewById(R.id.home_followups);
        linearLayoutLeads = (RelativeLayout) rootView.findViewById(R.id.home_leads);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.home_container);

        viewPagerPreviousButton = (ImageView) rootView.findViewById(R.id.img_previous);
        viewPagerNextButton = (ImageView) rootView.findViewById(R.id.img_next);


        // intialize ViewPager
        mViewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.viewpager);
//         setting recent followups for view pager restricting to 6 followups
     /*   if (followUpArrayList != null && followUpArrayList.size() != 0) {
            for (int i = 0; i < followUpArrayList.size(); i++) {
                if (followUpArrayList.size() > 5) {
                    limitFollowUps .addAll(followUpArrayList.subList(followUpArrayList.size() - 6, followUpArrayList.size()));
                } else {
                    limitFollowUps.add(followUpArrayList.get(i));
                }
            }

        }*/
        homefollowUpViewPagerAdapter = new HomefollowUpViewPagerAdapter(getActivity(), followUpArrayList,
                getFragmentManager(), this);
        // Binds the Adapter to the ViewPager
        mViewPager.setAdapter(homefollowUpViewPagerAdapter);

        nLeadCount = AppPreferences.getLeadsCount(getActivity(), AppUtils.LEADS_COUNT);
        nFollowUpCount = AppPreferences.getFollowupsCount(getActivity(), AppUtils.FOLLOWUPS_COUNT);
        Log.d("lCoun", "fcount" + nLeadCount + " " + nFollowUpCount);
        if (getActivity().getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.is_first_run), true)) {

            Log.d("lCountinFR", "fcountinFR" + nLeadCount + " " + nFollowUpCount);
            leadsCount.setText(nLeadCount);
            followupsCount.setText(nFollowUpCount);
            getActivity().getPreferences(MODE_PRIVATE).edit().putBoolean(getString(R.string.is_first_run), false).commit();
        }


        linearLayoutFollowUp.setOnClickListener(this);
        linearLayoutLeads.setOnClickListener(this);

//         Handling previous and next buttons in view pager

        viewPagerPreviousButton.setVisibility(View.GONE);

        viewPagerPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            }
        });

        viewPagerNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        });

        if (mViewPager.getChildCount() == followUpArrayList.size() - 1) {
            viewPagerNextButton.setVisibility(View.GONE);

        } else {
            viewPagerNextButton.setVisibility(View.VISIBLE);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0 && followUpArrayList.size() > 0) {
                    viewPagerNextButton.setVisibility(View.VISIBLE);
                    viewPagerPreviousButton.setVisibility(View.GONE);
                } else if (position == followUpArrayList.size() - 1 && followUpArrayList.size() > 0) {
                    viewPagerNextButton.setVisibility(View.GONE);
                    viewPagerPreviousButton.setVisibility(View.VISIBLE);
                } else {
                    viewPagerNextButton.setVisibility(View.VISIBLE);
                    viewPagerPreviousButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        String stringLoginSuccess = getActivity().getIntent().getStringExtra(getString(R.string.intent));

        if (stringLoginSuccess != null && !stringLoginSuccess.isEmpty() && stringLoginSuccess.equals(getString(R.string.password))) {
            Snackbar snackbar = Snackbar
                    .make(frameLayout, getString(R.string.login_success), Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundResource(R.color.green);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            getActivity().getIntent().removeExtra(getString(R.string.intent));
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_followups:
                //Permission Check and hide follow ups
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || db.checkUsersPermission(getString(R.string.followup_view_permission))) {
                    FragmentManager fragmentManagerFollowUp = getFragmentManager();
                    FragmentTransaction fragmentTransactionFollowUp = fragmentManagerFollowUp.beginTransaction();
                    fragmentTransactionFollowUp.replace(R.id.frame_container, new FollowUpsFragment(), getActivity().getString(R.string.followup_fragment_tag));
                    fragmentTransactionFollowUp.addToBackStack(null);
                    fragmentTransactionFollowUp.commitAllowingStateLoss();
                    if (drawerSelection != null) {
                        drawerSelection.setSelection(3);
                    } else {
                        DrawerActivity.drawer.setSelection(3);
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view follow ups.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.home_leads:
                //Permission Check and hide leads
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || db.checkUsersPermission(getString(R.string.lead_view_permission))) {
                    FragmentManager fragmentManagerLeads = getFragmentManager();
                    FragmentTransaction fragmentTransactionLeads = fragmentManagerLeads.beginTransaction();
                    fragmentTransactionLeads.replace(R.id.frame_container, new LeadFragment(), getActivity().getString(R.string.lead_fragment_tag));
                    fragmentTransactionLeads.addToBackStack(null);
                    fragmentTransactionLeads.commitAllowingStateLoss();
                    if (drawerSelection != null) {
                        drawerSelection.setSelection(2);
                    } else {
                        DrawerActivity.drawer.setSelection(2);
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view leads.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Enable Touch Back
        try {
            if (getActivity() != null && isAdded()) {
                getActivity().setTitle(R.string.navigation_view_activity);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                try {
                    if (dLeadCount.equals("0") || dLeadCount.equals("null")) {
                        leadsCount.setText("" + nLeadCount);
                        Log.e("dLC is Equals to 0 ", String.valueOf(nLeadCount));
                    } else {
                        leadsCount.setText("" + dLeadCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (dFollowUpCount.equals("0") || dFollowUpCount.equals("null")) {
                        followupsCount.setText("" + nFollowUpCount);
                        Log.e("dFC is Equals to 0 ", String.valueOf(nFollowUpCount));
                    } else {
                        followupsCount.setText("" + dFollowUpCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // leadsCount.setText(dLeadCount);
        // followupsCount.setText(dFollowUpCount);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.navigation_view_activity));

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.navigation_view_activity));
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.navigation_view_activity));
        }
    }

    public int getViewPagerCount() {
        int i = mViewPager.getChildCount();
        return i;
    }

    @Override
    public void sendDrawer(Drawer drawer) {
        drawerSelection = drawer;
    }

    //  Followups data username database
    private void getDataFromDB() {
        try {
            followUpArrayList = db.getHomePendingFollowupsData("1");

            if (followUpArrayList.size() > 0) {
                textViewRecentFollowUps.setVisibility(View.VISIBLE);
                recentFollowUp.setVisibility(View.VISIBLE);
            } else {
                textViewRecentFollowUps.setVisibility(View.GONE);
                recentFollowUp.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //     Followups data username database
    private void getCountFromDB() {
        try {
            int countFollowup = db.followUpsPendingCount();
            int countLead = db.leadNewCount();
            dFollowUpCount = String.valueOf(countFollowup);
            dLeadCount = String.valueOf(countLead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile_number));
        if (mobile_number == null || mobile_number == "") {
            Toast.makeText(getContext(), "No Number", Toast.LENGTH_SHORT).show();
        } else {
            getActivity().startActivity(intentCall);
        }
    }

    @Override
    public void sendRecentFollowupNumber(String mnumber) {
        this.mobile_number = mnumber;
    }

}