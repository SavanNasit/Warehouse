package com.accrete.sixorbit.fragment.Drawer.followups;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.FollowUpAdapter;
import com.accrete.sixorbit.adapter.LeadContactsAdapter;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.interfaces.SendFollowUpMobileInterface;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowUpFilter;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;


/**
 * Created by poonam on 7/4/17.
 */

public class FollowUpsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SendFollowUpMobileInterface,
        View.OnClickListener, PassDateToCounsellor,
        FollowUpAdapter.FollowUpAdapterListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    public static TextView textViewEmptyView;
    public FollowUpAdapter mAdapter;
    public FloatingActionButton floatingActionButtonFilter;
    public List<Lead> leadList = new ArrayList<>();
    public boolean isSwipeable, swipeToRecord;
    RecyclerView recyclerView;
    List<Contacts> contactsList = new ArrayList<>();
    List<FollowUpFilter> listFilter = new ArrayList<>();
    private String mobileNumber, leadMobileNumber;
    private List<FollowUp> FollowUps = new ArrayList<>();
    private List<FollowUp> tempList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View dialogView;
    private AlertDialog alertDialog;
    private DatabaseHandler databaseHandler;
    private TextView textViewFrom;
    private ImageView imageViewClear;
    private AllDatePickerFragment datePickerFragment;
    private TextView textViewTo;
    private FollowUpFilter mainFollowUpFilter = new FollowUpFilter();
    private Date startDate, enddate;
    private Paint p = new Paint();
    private ItemTouchHelper itemTouchHelper;
    private int swipedPosition;
    private LeadContactsAdapter contactAdapter;
    private Lead leadLists = new Lead();
    private LeadContactsAdapter.LeadContactsAdapterListener listener;
    private RelativeLayout filterLayout;
    private TextView sortTextView;
    //    This will be called whenever an Intent with an action named "refresh_followups" is broadcasted.
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(getString(R.string.message));
            if (message.equals(getString(R.string.update_list))) {
                //  if (getActivity() != null)
                displayDataInView();
            }
        }
    };
    private ImageButton clearFilterImageButton;
    private String status;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayout;
    private FollowUpFilter testFilter;

    public static FollowUpsFragment newInstance(String title) {
        FollowUpsFragment f = new FollowUpsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
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

    public FollowUpsFragment getInstance() {
        return new FollowUpsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow_up, container, false);
        databaseHandler = new DatabaseHandler(getActivity());
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (listFilter != null) {
            listFilter = databaseHandler.getAllfollowUpfilter();
        }
        initializeView(rootView);
        // Register receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(getString(R.string.refresh_followups)));

        return rootView;
    }

    private void initializeView(final View rootView) {
        try {
            filterLayout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
            sortTextView = (TextView) rootView.findViewById(R.id.sort_textView);
            clearFilterImageButton = (ImageButton) rootView.findViewById(R.id.clear_filter_imageButton);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            floatingActionButtonFilter = (FloatingActionButton) rootView.findViewById(R.id.followup_fab);
            textViewEmptyView = (TextView) rootView.findViewById(R.id.empty_view);
            swipeRefreshLayout.setOnRefreshListener(this);
            mAdapter = new FollowUpAdapter(getActivity(), FollowUps, this, recyclerView, getFragmentManager(), this);
            contactAdapter = new LeadContactsAdapter(getActivity(), contactsList, leadList, listener, getActivity());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(mAdapter);
            floatingActionButtonFilter.setOnClickListener(this);
            swipeRefreshLayout.setOnRefreshListener(this);
            // show loader and fetch messages
            swipeRefreshLayout.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            displayDataInView();
                        }
                    }
            );

            clearFilterImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Clear Filter and hide its layout
                    if (databaseHandler != null) {
                        databaseHandler.deleteFollowUpFilters();
                    }
                    listFilter.clear();
                    mainFollowUpFilter = new FollowUpFilter();
                    displayDataInView();

                    if (filterLayout.getVisibility() == View.VISIBLE) {
                        hideFilterLayout();
                    }
                }
            });


            setSwipeForRecyclerView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setSwipeForRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this, swipeToRecord);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    public void displayDataInView() {
        try {
            if (listFilter != null && listFilter.size() > 0) {

                for (int i = 0; i < listFilter.size(); i++) {
                    mainFollowUpFilter = listFilter.get(i);
                }
                if (FollowUps.size() > 0) {
                    FollowUps.clear();
                }

                String type = "";
                if (mainFollowUpFilter.getTaken() != null && mainFollowUpFilter.getPending() == null) {
                    type = "2";
                    sortTextView.setText("Taken followups");
                } else if (mainFollowUpFilter.getPending() != null && mainFollowUpFilter.getTaken() == null) {
                    type = "1";
                    sortTextView.setText("Pending followups");
                } else if (mainFollowUpFilter.getTaken() != null && mainFollowUpFilter.getPending() != null) {
                    type = "(1,2)";
                    sortTextView.setText("Pending/Taken followups");
                } else {
                    type = "(1,2)";
                    sortTextView.setText("Followups");
                }

                if (mainFollowUpFilter.getYesterday() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of yesterday");
                } else if (mainFollowUpFilter.getToday() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of today");
                } else if (mainFollowUpFilter.getThisWeek() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of this week");
                } else if (mainFollowUpFilter.getStartDate() != null && mainFollowUpFilter.getEndDate() != null) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                    try {
                        sortTextView.setText(sortTextView.getText().toString().trim()
                                + " from " + dateFormat.format(format.parse(mainFollowUpFilter.getStartDate()))
                                + " to " + dateFormat.format(format.parse(mainFollowUpFilter.getEndDate())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!type.isEmpty() && type != null) {
                    tempList = databaseHandler.getFilteredListType(type, mainFollowUpFilter.getLead(), mainFollowUpFilter.getEnquiry(), mainFollowUpFilter.getQuotation(),
                            mainFollowUpFilter.getPurchaseOrder(), mainFollowUpFilter.getCustomerSalesOrder(),
                            mainFollowUpFilter.getStartDate(), mainFollowUpFilter.getEndDate());
                } else {
                    tempList = databaseHandler.getAllfollowUp();
                }

                for (int i = 0; i < tempList.size(); i++) {
                    FollowUps.add(tempList.get(i));
                }

                if (mainFollowUpFilter.getToday() != null || mainFollowUpFilter.getYesterday() != null || mainFollowUpFilter.getStartDate() != null
                        || mainFollowUpFilter.getEndDate() != null || mainFollowUpFilter.getPending() != null || mainFollowUpFilter.getTaken() != null) {
                    showFilterLayout();
                } else {
                    hideFilterLayout();
                }

            } else {
                getDataFromDB();

                hideFilterLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (FollowUps.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmptyView.setVisibility(View.GONE);
        }
        mAdapter = new FollowUpAdapter(getActivity(), FollowUps, this, recyclerView, getFragmentManager(), this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void showFilterLayout() {
        //Make visible to filter layout
        filterLayout.setVisibility(View.VISIBLE);
    }

    private void hideFilterLayout() {
        //Make visibility gone of filter layout
        filterLayout.setVisibility(View.GONE);
        sortTextView.setText("");
    }

    public List<String> getDaysBetweenDates(Date startdate, Date enddate) {
        List<String> dates = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            String reportDate = df.format(result);
            dates.add(reportDate);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.follow_up_fragment));
    }


    private void openFilterDialog(final FollowUpFilter followUpFilter) {
        try {
            testFilter = new FollowUpFilter();
            dialogView = View.inflate(getActivity(), R.layout.dialog_followup_filter, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialogView)
                    .setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCancelable(true);
            datePickerFragment = new AllDatePickerFragment();
            datePickerFragment.setListener(this);
            View.OnClickListener checkBoxListener;
            final RadioGroup radiogroup = (RadioGroup) dialogView.findViewById(R.id.filter_sort_radioGroup);
            imageViewClear = (ImageView) dialogView.findViewById(R.id.dialog_filter_clear_all);
            textViewFrom = (TextView) dialogView.findViewById(R.id.dialog_filter_edt_from);
            textViewTo = (TextView) dialogView.findViewById(R.id.dialog_filter_edt_to);
            final CheckBox checkboxTaken = (CheckBox) dialogView.findViewById(R.id.taken);
            final CheckBox checkboxPending = (CheckBox) dialogView.findViewById(R.id.pending);
            final CheckBox checkboxEnquiry = (CheckBox) dialogView.findViewById(R.id.dialog_filter_checkbox_enquiry);
            final CheckBox checkboxLead = (CheckBox) dialogView.findViewById(R.id.dialog_filter_checkbox_lead);
            final CheckBox checkboxQuotation = (CheckBox) dialogView.findViewById(R.id.dialog_filter_checkbox_quotation);
            final CheckBox checkboxPurchaseOrder = (CheckBox) dialogView.findViewById(R.id.dialog_filter_checkbox_purchase_order);
            final CheckBox checkboxSalesOrder = (CheckBox) dialogView.findViewById(R.id.dialog_filter_checkbox_salesOrder);

            dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.GONE);

            if (followUpFilter.getTaken() != null && followUpFilter.getPending() == null) {
                checkboxTaken.setChecked(true);
            } else if (followUpFilter.getPending() != null && followUpFilter.getTaken() == null) {
                checkboxPending.setChecked(true);
            } else if (followUpFilter.getTaken() != null && followUpFilter.getPending() != null) {
                checkboxTaken.setChecked(true);
                checkboxPending.setChecked(true);
            } else {
                checkboxTaken.setChecked(false);
                checkboxPending.setChecked(false);
            }

            if (followUpFilter.getLead() != null && !followUpFilter.getLead().isEmpty()) {
                checkboxLead.setChecked(true);
            }

            if (followUpFilter.getQuotation() != null && !followUpFilter.getQuotation().isEmpty()) {
                checkboxQuotation.setChecked(true);
            }

            if (followUpFilter.getEnquiry() != null && !followUpFilter.getEnquiry().isEmpty()) {
                checkboxEnquiry.setChecked(true);
            }

            if (followUpFilter.getPurchaseOrder() != null && !followUpFilter.getPurchaseOrder().isEmpty()) {
                checkboxPurchaseOrder.setChecked(true);
            }

            if (followUpFilter.getCustomerSalesOrder() != null && !followUpFilter.getCustomerSalesOrder().isEmpty()) {
                checkboxSalesOrder.setChecked(true);
            }

            checkBoxListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkboxTaken.isChecked()) {
                        //  Toast.makeText(getActivity(), "Taken", Toast.LENGTH_SHORT).show();
                        followUpFilter.setTaken("2");

                    } else {
                        followUpFilter.setTaken(null);
                    }

                    if (checkboxPending.isChecked()) {
                        //    Toast.makeText(getActivity(), "Pending", Toast.LENGTH_SHORT).show();
                        followUpFilter.setPending("1");

                    } else {
                        followUpFilter.setPending(null);
                    }

                    if (checkboxPending.isChecked() && checkboxTaken.isChecked()) {
                        //   Toast.makeText(getActivity(), "Taken & Pending", Toast.LENGTH_SHORT).show();
                        followUpFilter.setTaken("2");
                        followUpFilter.setPending("1");
                    }

                    if (checkboxLead.isChecked()) {
                        followUpFilter.setLead("1");

                    } else {
                        followUpFilter.setLead("");
                    }

                    if (checkboxEnquiry.isChecked()) {
                        followUpFilter.setEnquiry("2");

                    } else {
                        followUpFilter.setEnquiry("");
                    }

                    if (checkboxQuotation.isChecked()) {
                        followUpFilter.setQuotation("3");
                    } else {
                        followUpFilter.setQuotation("");
                    }

                    if (checkboxPurchaseOrder.isChecked()) {
                        followUpFilter.setPurchaseOrder("4");
                    } else {
                        followUpFilter.setPurchaseOrder("");
                    }

                    if (checkboxSalesOrder.isChecked()) {
                        followUpFilter.setCustomerSalesOrder("5");
                    } else {
                        followUpFilter.setCustomerSalesOrder("");
                    }
                }
            };

            checkboxPending.setOnClickListener(checkBoxListener);
            checkboxTaken.setOnClickListener(checkBoxListener);
            checkboxLead.setOnClickListener(checkBoxListener);
            checkboxEnquiry.setOnClickListener(checkBoxListener);
            checkboxQuotation.setOnClickListener(checkBoxListener);
            checkboxPurchaseOrder.setOnClickListener(checkBoxListener);
            checkboxSalesOrder.setOnClickListener(checkBoxListener);

            final RadioButton radioButtonToday = (RadioButton) dialogView.findViewById(R.id.radioButtonToday);
            final RadioButton radioButtonYesterday = (RadioButton) dialogView.findViewById(R.id.radioButtonYesterday);
            final RadioButton radioButtonRange = (RadioButton) dialogView.findViewById(R.id.radioButtonCustomRange);
            final RadioButton radioButtonThisWeek = (RadioButton) dialogView.findViewById(R.id.radioButtonThisWeek);


            if (followUpFilter.getStartDate() == null) {
                textViewFrom.setText(getString(R.string.start_date));
                if (startDate == null) {
                    textViewFrom.setText(getString(R.string.start_date));
                }
            }
            if (followUpFilter.getEndDate() == null) {
                textViewTo.setText(getString(R.string.end_date));
                if (enddate == null) {
                    textViewTo.setText(getString(R.string.end_date));
                }
            }
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //   int selectedId = radiogroup.getCheckedRadioButtonId();
                    //    final RadioButton radioButton = (RadioButton) dialogView.findViewById(selectedId);
                    //    String selectedText = (String) radioButton.getText();

                    //Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                    if (checkedId == R.id.radioButtonCustomRange) {
                        RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                        // This puts the value (true/false) into the variable
                        boolean isChecked = checkedRadioButton.isChecked();
                        // if (selectedText.equals(getString(R.string.range))) {
                        if (isChecked) {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonCustomRange)).setChecked(true);
                            dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.VISIBLE);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setToday(null);
                            followUpFilter.setThisWeek(null);
                        } else {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonCustomRange)).setChecked(false);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setToday(null);
                            followUpFilter.setStartDate(null);
                            followUpFilter.setEndDate(null);
                            followUpFilter.setThisWeek(null);
                        }
                    } else if (checkedId == R.id.radioButtonToday) {
                        RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                        // This puts the value (true/false) into the variable
                        boolean isChecked = checkedRadioButton.isChecked();
                        //    if (selectedText.equals(getString(R.string.today))) {
                        if (isChecked) {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonToday)).setChecked(true);
                            dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.GONE);
                            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            Log.e("TodayDate", date);
                            followUpFilter.setToday(date);
                            followUpFilter.setStartDate(followUpFilter.getToday());
                            followUpFilter.setEndDate(followUpFilter.getToday());
                            followUpFilter.setYesterday(null);
                            followUpFilter.setThisWeek(null);
                        } else {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonToday)).setChecked(false);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setToday(null);
                            followUpFilter.setStartDate(null);
                            followUpFilter.setEndDate(null);
                            followUpFilter.setThisWeek(null);
                        }
                    } else if (checkedId == R.id.radioButtonYesterday) {
                        RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                        // This puts the value (true/false) into the variable
                        boolean isChecked = checkedRadioButton.isChecked();
                        //    if (selectedText.equals(getString(R.string.today))) {
                        if (isChecked) {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonYesterday)).setChecked(true);
                            //    if (selectedText.equals(getString(R.string.yesterday))) {
                            Calendar cal = Calendar.getInstance();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            System.out.println("Today's date is " + dateFormat.format(cal.getTime()));
                            cal.add(Calendar.DATE, -1);
                            System.out.println("Yesterday's date was " + dateFormat.format(cal.getTime()));
                            dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.GONE);
                            followUpFilter.setYesterday(dateFormat.format(cal.getTime()));
                            followUpFilter.setStartDate(followUpFilter.getYesterday());
                            followUpFilter.setEndDate(followUpFilter.getYesterday());
                            followUpFilter.setToday(null);
                            followUpFilter.setThisWeek(null);
                        } else {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonYesterday)).setChecked(false);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setToday(null);
                            followUpFilter.setStartDate(null);
                            followUpFilter.setEndDate(null);
                            followUpFilter.setThisWeek(null);
                        }
                    }
                    //TODO Added on 22nd June 2k18
                    else if (checkedId == R.id.radioButtonThisWeek) {
                        RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                        // This puts the value (true/false) into the variable
                        boolean isChecked = checkedRadioButton.isChecked();
                        //    if (selectedText.equals(getString(R.string.today))) {
                        if (isChecked) {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonThisWeek)).setChecked(true);
                            //    if (selectedText.equals(getString(R.string.yesterday))) {
                            Calendar calMonday = Calendar.getInstance();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            calMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                            System.out.println("monday " + " " + dateFormat.format(calMonday.getTime()));
                            Calendar calSunday = Calendar.getInstance();
                            calSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                            calSunday.set(Calendar.HOUR_OF_DAY, 0);
                            calSunday.set(Calendar.MINUTE, 0);
                            calSunday.set(Calendar.SECOND, 0);
                            calSunday.add(Calendar.DATE, 7);

                            System.out.println("sunday " + " " + dateFormat.format(calSunday.getTime()));

                            dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.GONE);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setStartDate(dateFormat.format(calMonday.getTime()));
                            followUpFilter.setEndDate(dateFormat.format(calSunday.getTime()));
                            followUpFilter.setToday(null);
                            followUpFilter.setThisWeek(dateFormat.format(calMonday.getTime()));
                        } else {
                            ((RadioButton) dialogView.findViewById(R.id.radioButtonThisWeek)).setChecked(false);
                            followUpFilter.setYesterday(null);
                            followUpFilter.setToday(null);
                            followUpFilter.setStartDate(null);
                            followUpFilter.setEndDate(null);
                            followUpFilter.setThisWeek(null);
                        }
                    }
                }
            });

            imageViewClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (checkboxPending.isChecked()) {
                            checkboxPending.setChecked(false);
                        }
                        if (checkboxTaken.isChecked()) {
                            checkboxTaken.setChecked(false);
                        }

                        if (textViewFrom != null && textViewFrom.getText().toString() != null &&
                                !textViewFrom.getText().toString().isEmpty()) {
                            textViewFrom.setText(getString(R.string.start_date));
                        }

                        if (textViewTo != null && textViewTo.getText().toString() != null &&
                                !textViewTo.getText().toString().isEmpty()) {
                            textViewTo.setText(getString(R.string.end_date));
                        }

                        radiogroup.clearCheck();

                        if (checkboxLead.isChecked()) {
                            checkboxLead.setChecked(false);
                        }
                        if (checkboxEnquiry.isChecked()) {
                            checkboxEnquiry.setChecked(false);
                        }

                        if (checkboxQuotation.isChecked()) {
                            checkboxQuotation.setChecked(false);
                        }
                        if (checkboxPurchaseOrder.isChecked()) {
                            checkboxPurchaseOrder.setChecked(false);
                        }
                        if (checkboxSalesOrder.isChecked()) {
                            checkboxSalesOrder.setChecked(false);
                        }
                        dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.GONE);
                        databaseHandler.deleteFollowUpFilters();
                        listFilter.clear();
                        mainFollowUpFilter = new FollowUpFilter();
                        displayDataInView();

                        hideFilterLayout();

                        testFilter = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialogView.findViewById(R.id.dialog_filter_edt_from).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_from));

                }
            });

            dialogView.findViewById(R.id.dialog_filter_edt_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_to));
                }
            });


            dialogView.findViewById(R.id.dialog_filter_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (testFilter != null) {
                            mainFollowUpFilter = testFilter;
                        }
                        alertDialog.dismiss();
                        //revealShow(dialogView, false, alertDialog);
                    }
                }
            });

            dialogView.findViewById(R.id.dialog_filter_apply).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    databaseHandler.deleteFollowUpFilters();
                    mainFollowUpFilter = followUpFilter;
                    if (radioButtonToday.isChecked()) {
                        if (checkboxPending.isChecked() && !checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("1", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());

                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));

                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxTaken.isChecked() && !checkboxPending.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("2", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());

                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }
                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxPending.isChecked() && checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("(1,2)", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else {
                            CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();
                        }
                    } else if (radioButtonYesterday.isChecked()) {
                        if (checkboxPending.isChecked() && !checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("1", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {

                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxTaken.isChecked() && !checkboxPending.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("2", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxPending.isChecked() && checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("(1,2)", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else {
                            //   CustomisedToast.error(getActivity(), "Please check at least one option from taken and pending.").show();
                            CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();
                        }
                    }
                    //TODO Added on 22nd June 2k18
                    else if (radioButtonThisWeek.isChecked()) {
                        if (checkboxPending.isChecked() && !checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("1", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {

                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxTaken.isChecked() && !checkboxPending.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("2", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxPending.isChecked() && checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("(1,2)", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else {
                            //TODO Updated on 22nd June 2k18
                            //CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();

                            alertDialog.dismiss();
                            //revealShow(dialogView, false, alertDialog);
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("(1,2)", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        }
                    } else if (radioButtonRange.isChecked()) {
                        if (startDate != null && enddate != null && startDate.before(enddate)) {
                            String type = "";
                            if (followUpFilter.getTaken() != null && followUpFilter.getPending() == null) {
                                // selectedFilter("2");
                                type = "2";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else if (followUpFilter.getPending() != null && followUpFilter.getTaken() == null) {
                                //selectedFilter("1");
                                type = "1";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else if (followUpFilter.getTaken() != null && followUpFilter.getPending() != null) {
                                type = "(1,2)";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else {
                                CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();
                            }

                        } else if (startDate != null && enddate != null && startDate.equals(enddate)) {
                            //alertDialog.dismiss();
                            String type = "";
                            if (followUpFilter.getTaken() != null && followUpFilter.getPending() == null) {
                                type = "2";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else if (followUpFilter.getPending() != null && followUpFilter.getTaken() == null) {
                                type = "1";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else if (followUpFilter.getTaken() != null && followUpFilter.getPending() != null) {
                                type = "(1,2)";
                                //Clear
                                if (FollowUps.size() > 0) {
                                    FollowUps.clear();
                                }
                                alertDialog.dismiss();
                                tempList = databaseHandler.getFilteredListType(type, followUpFilter.getLead(),
                                        followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                        followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                        followUpFilter.getStartDate(), followUpFilter.getEndDate());
                                for (int i = 0; i < tempList.size(); i++) {
                                    FollowUps.add(tempList.get(i));
                                }

                                //Show Filter
                                showFilterLayout();

                                updateFilteredText();
                            } else {
                                CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();
                            }

                        } else {
                            CustomisedToast.error(getActivity(), getString(R.string.date_error)).show();
                            //textViewTo.setError(getString(R.string.date_error));
                        }
                    } else {
                        if (checkboxPending.isChecked() && !checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) {
                                FollowUps.clear();
                            }
                            tempList = databaseHandler.getFilteredListType("1", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));

                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else if (checkboxTaken.isChecked() && !checkboxPending.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) FollowUps.clear();
                            tempList = databaseHandler.getFilteredListType("2", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();

                        } else if (checkboxPending.isChecked() && checkboxTaken.isChecked()) {
                            alertDialog.dismiss();
                            if (FollowUps.size() > 0) FollowUps.clear();
                            tempList = databaseHandler.getFilteredListType("(1,2)", followUpFilter.getLead(),
                                    followUpFilter.getEnquiry(), followUpFilter.getQuotation(),
                                    followUpFilter.getPurchaseOrder(), followUpFilter.getCustomerSalesOrder(),
                                    followUpFilter.getStartDate(), followUpFilter.getEndDate());
                            for (int i = 0; i < tempList.size(); i++) {
                                FollowUps.add(tempList.get(i));
                            }

                            //Show Filter
                            showFilterLayout();

                            updateFilteredText();
                        } else {

                            // CustomisedToast.error(getActivity(), "Please check at least one option from taken and pending.").show();
                            CustomisedToast.error(getActivity(), getString(R.string.select_either_taken_or_pending)).show();
                        }
                    }

                    if (FollowUps.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewEmptyView.setVisibility(View.GONE);

                        //TODO Updated On 22nd June 2k18
                        databaseHandler.addFollowUpFilter(new FollowUpFilter(followUpFilter.getToday(), followUpFilter.getYesterday(),
                                followUpFilter.getTaken(), followUpFilter.getPending(), followUpFilter.getStartDate(),
                                followUpFilter.getEndDate(), followUpFilter.getLead(), followUpFilter.getEnquiry(),
                                followUpFilter.getQuotation(), followUpFilter.getPurchaseOrder(),
                                followUpFilter.getCustomerSalesOrder(), followUpFilter.getThisWeek()));
                        // }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        textViewEmptyView.setVisibility(View.VISIBLE);
                    }

                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }

            //TODO Updated on 22nd June
            if (followUpFilter.getToday() != null) {
                radioButtonToday.setChecked(true);
            } else if (followUpFilter.getYesterday() != null) {
                radioButtonYesterday.setChecked(true);
            } else if (followUpFilter.getThisWeek() != null) {
                radioButtonThisWeek.setChecked(true);
            } else if (followUpFilter.getStartDate() != null && followUpFilter.getEndDate() != null) {
                radioButtonRange.setChecked(true);
                dialogView.findViewById(R.id.expand_custom_range).setVisibility(View.VISIBLE);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                try {
                    textViewFrom.setText(dateFormat.format(format.parse(followUpFilter.getStartDate())));
                    textViewTo.setText(dateFormat.format(format.parse(followUpFilter.getEndDate())));
                    startDate = format.parse(followUpFilter.getStartDate());
                    enddate = format.parse(followUpFilter.getEndDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO Added on 22nd June
                if (followUpFilter.getPending() == null &&
                        followUpFilter.getTaken() == null) {
                    radioButtonThisWeek.setChecked(true);
                }
            }

            testFilter.setYesterday(followUpFilter.getYesterday());
            testFilter.setToday(followUpFilter.getToday());
            testFilter.setThisWeek(followUpFilter.getThisWeek());
            testFilter.setTaken(followUpFilter.getTaken());
            testFilter.setPending(followUpFilter.getPending());
            testFilter.setStartDate(followUpFilter.getStartDate());
            testFilter.setEndDate(followUpFilter.getEndDate());
            testFilter.setLead(followUpFilter.getLead());
            testFilter.setEnquiry(followUpFilter.getEnquiry());
            testFilter.setCustomerSalesOrder(followUpFilter.getCustomerSalesOrder());
            testFilter.setPurchaseOrder(followUpFilter.getPurchaseOrder());
            testFilter.setQuotation(followUpFilter.getQuotation());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.followup_fab:
                if (alertDialog == null || !alertDialog.isShowing()) {
                    openFilterDialog(mainFollowUpFilter);
                }
                break;
        }
    }

    private void updateFilteredText() {

        try {
            if (mainFollowUpFilter.getTaken() != null && mainFollowUpFilter.getPending() == null) {
                sortTextView.setText("Taken followups");
                if (mainFollowUpFilter.getThisWeek() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of this week");
                }
            } else if (mainFollowUpFilter.getPending() != null && mainFollowUpFilter.getTaken() == null) {
                sortTextView.setText("Pending followups");
                if (mainFollowUpFilter.getThisWeek() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of this week");
                }
            } else if (mainFollowUpFilter.getTaken() != null && mainFollowUpFilter.getPending() != null) {
                sortTextView.setText("Pending/Taken followups");
                if (mainFollowUpFilter.getThisWeek() != null) {
                    sortTextView.setText(sortTextView.getText().toString().trim() + " of this week");
                }
            } else {
                //sortTextView.setText("Followups");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mainFollowUpFilter.getYesterday() != null) {
                sortTextView.setText(sortTextView.getText().toString().trim() + " of yesterday");
            } else if (mainFollowUpFilter.getToday() != null) {
                sortTextView.setText(sortTextView.getText().toString().trim() + " of today");
            } else if (mainFollowUpFilter.getThisWeek() != null) {
                if (mainFollowUpFilter.getTaken() == null && mainFollowUpFilter.getPending() == null) {
                    sortTextView.setText("Followups" + " of this week");
                }
            } else if (mainFollowUpFilter.getStartDate() != null && mainFollowUpFilter.getEndDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                try {
                    sortTextView.setText(sortTextView.getText().toString().trim()
                            + " from " + dateFormat.format(format.parse(mainFollowUpFilter.getStartDate()))
                            + " to " + dateFormat.format(format.parse(mainFollowUpFilter.getEndDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFollowUps() {
        try {
            task = getString(R.string.follow_up_fetch);

            String lastUpdated;
            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            if (databaseHandler.getSyncTime(task).getCallTime() != null) {
                lastUpdated = databaseHandler.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            String type = "";
            Call<ApiResponse> call = apiService.getFollowUp(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();

                        for (FollowUp followUp : apiResponse.getData().getFollowups()) {

                            if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                if (databaseHandler.checkFollowUpResult(followUp.getFoid())) {
                                    databaseHandler.updateFollowUp(followUp);
                                } else {
                                    if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && databaseHandler.checkSyncIdFollowUp(followUp.getSyncId())) {
                                        databaseHandler.updateFollowUpDataSyncId(followUp);
                                    } else {
                                        if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                                && databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                                ) {

                                            for (int i = 0; i < databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                                if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                        .equals(followUp.getContactedPerson())) {
                                                    followUp.setContactsId(String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                                }
                                            }
                                        }
                                        databaseHandler.insertFollowUpData(followUp);
                                        Log.d("Follow up ID 1", String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                                    }
                                }
                            } else {
                                if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                        && databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                        ) {

                                    for (int i = 0; i < databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                        if (databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                .equals(followUp.getContactedPerson())) {
                                            followUp.setContactsId(String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                        }
                                    }
                                }
                                databaseHandler.insertFollowUpData(followUp);
                                Log.d("Follow up ID 2", String.valueOf(databaseHandler.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                            }


                            SyncCheck syncCheck = new SyncCheck();
                            syncCheck.setService(getString(R.string.follow_up_fetch));
                            if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                            } else {
                                syncCheck.setCallTime(databaseHandler.getFollowUpUpdatedTs());
                                Log.d("Last updated Ts", databaseHandler.getFollowUpUpdatedTs());
                            }
                            if (databaseHandler != null) {
                                databaseHandler.updateSyncCheck(syncCheck);
                            }

                            if (listFilter.size() == 0) {
                                displayDataInView();
                            }
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (getActivity() != null && isAdded()) {
                            if (swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromDB() {
        try {
            if (databaseHandler != null) {
                FollowUps.clear();
                FollowUps.addAll(databaseHandler.getAllfollowUp());
            }

            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void callAction() {

        Intent intentCall = null;
        try {
            intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));

            if (mobileNumber != null && !mobileNumber.isEmpty()) {
                getActivity().startActivity(intentCall);
            } else {
                CustomisedToast.error(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        try {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                getFollowUps();
            } else {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                CustomisedToast.error(getActivity(), getActivity().getString(R.string.no_internet_try_later)).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passDate(String s) {
        try {
            String stringStartDate = null, stringEndDate = null;

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (datePickerFragment.getTag().equals(getString(R.string.dailogue_from))) {
                stringStartDate = s;

                try {
                    startDate = formatter.parse(stringStartDate);
                    System.out.println(startDate);
                    stringStartDate = targetFormat.format(startDate);
                    String from = formatter.format(startDate);
                    textViewFrom.setText(from);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                mainFollowUpFilter.setStartDate(stringStartDate);
            } else {
                try {
                    stringEndDate = s;
                    enddate = formatter.parse(stringEndDate);
                    stringEndDate = targetFormat.format(enddate);
                    String to = formatter.format(enddate);
                    textViewTo.setText(to);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                mainFollowUpFilter.setEndDate(stringEndDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.follow_up_fragment));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Enable Touch Back
        if (getActivity() != null && isAdded()) {
            getActivity()
                    .setTitle(getString(R.string.follow_up_fragment));
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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

    @Override
    public void onMessageRowClicked(int position) {
        try {
            recyclerView.smoothScrollToPosition(position);
            mAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        try {
            swipedPosition = viewHolder.getAdapterPosition();
            //Permission Check and disable record follow up
            if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || databaseHandler.checkUsersPermission(getString(R.string.followup_take_permission))) {
                if (FollowUps.get(swipedPosition) != null && FollowUps.get(swipedPosition).getFosid() != null
                        && FollowUps.get(swipedPosition).getFosid().equals("1") && ((FollowUpAdapter.MyViewHolder) viewHolder).isSwipeToRecord) {
                    if (!(databaseHandler.getLeadStatusId(FollowUps.get(swipedPosition).getLeadId())).equals("4") &&
                            !(databaseHandler.getLeadStatusId(FollowUps.get(swipedPosition).getLeadId())).equals("3")) {
                        mAdapter.next(swipedPosition);
                        isSwipeable = true;
                    } else {
                        isSwipeable = false;
                    }

                } else {
                    isSwipeable = false;
                }
            } else {
                Toast.makeText(getActivity(), "You have no permission to take any followup.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
