package com.accrete.sixorbit.fragment.Drawer.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.customers.AddCustomerActivity;
import com.accrete.sixorbit.activity.customers.CustomersTabActivity;
import com.accrete.sixorbit.adapter.CustomerMainFragmentAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Customers;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 3/11/17.
 */

public class CustomerMainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        CustomerMainFragmentAdapter.CustomersListener, View.OnClickListener {
    String customerMobileNumber;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewEmptyView;
    private CustomerMainFragmentAdapter customerMainFragmentAdapter;
    private List<Customers> customersList = new ArrayList<Customers>();
    private List<Customers> tempList = new ArrayList<Customers>();
    private boolean loading;
    private String dataChanged;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, sortValue = 0;
    private int lastVisibleItem, totalItemCount;
    private EditText customerDetailSearchEditText;
    private RelativeLayout bottomLayout;
    private TextView sortTextView;
    private ImageButton closeImageButton;
    // private FloatingActionButton fabFilter;
    private LinearLayout fabFilterLayout;
    private FloatingActionButton fabFilter;
    private LinearLayout fabAddCustomerLayout;
    private FloatingActionButton fabAddCustomer;
    private FloatingActionButton mainFab;
    private TextView filterTextView;
    private TextView addTextView;
    //Filter Dialog
    private ImageView dialogClearAll;
    private RadioGroup filterSortRadioGroup;
    private RadioButton radioButtonAmount;
    private RadioButton radioButtonName;
    private LinearLayout childLayout;
    private RadioGroup sortAmountRadioGroup;
    private RadioButton radioBtnAmtAsc;
    private RadioButton radioBtnAmtDesc;
    private RadioGroup sortNameRadioGroup;
    private RadioButton radioBtnNameAsc;
    private RadioButton radioBtnNameDesc;
    private Button dialogFilterApply;
    private Button dialogFilterCancel;
    private View dialogView;
    private AlertDialog alertDialog;
    private TextView orderTextView;
    private SpannableString sortSpannable, sortedBySpannable;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private Timer timer;
    private ImageView imageView;
    private DatabaseHandler databaseHandler;

    public static CustomerMainFragment newInstance(String title) {
        CustomerMainFragment f = new CustomerMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.customer_fragment));

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.customer_fragment));
        if (customerMainFragmentAdapter != null) {
            customerMainFragmentAdapter.restoreStates(savedInstanceState);
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.customer_fragment));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.customer_fragment));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_details, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(final View rootView) {
        databaseHandler = new DatabaseHandler(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.customer_detail_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.customer_detail_swipe_refresh_layout);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.customer_detail_empty_view);
        customerDetailSearchEditText = (EditText) rootView.findViewById(R.id.customer_detail_search_editText);
        bottomLayout = (RelativeLayout) rootView.findViewById(R.id.bottom_layout);
        sortTextView = (TextView) rootView.findViewById(R.id.sort_textView);
        closeImageButton = (ImageButton) rootView.findViewById(R.id.clear_customerInfo_imageButton);
        fabFilter = (FloatingActionButton) rootView.findViewById(R.id.fab_filter);
        fabFilterLayout = (LinearLayout) rootView.findViewById(R.id.fab_filter_layout);
        fabAddCustomerLayout = (LinearLayout) rootView.findViewById(R.id.fab_add_customer_layout);
        fabAddCustomer = (FloatingActionButton) rootView.findViewById(R.id.fab_add_customer);
        mainFab = (FloatingActionButton) rootView.findViewById(R.id.main_fab);
        filterTextView = (TextView) rootView.findViewById(R.id.filter_textView);
        addTextView = (TextView) rootView.findViewById(R.id.add_textView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        //Animations of FAB
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        textViewEmptyView.setText("There is no customer.");

        customerMainFragmentAdapter = new CustomerMainFragmentAdapter(getActivity(), customersList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(customerMainFragmentAdapter);
        // show loader and fetch messages
      /*  swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        //  displayDataInView();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );*/

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && customersList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        showLoader();
                        getCustomersList(customersList.get(totalItemCount - 1).getCreatedTs(), "2"
                                , customerDetailSearchEditText.getText().toString().trim(),
                                customersList.get(totalItemCount - 1).getCustomerNumber());
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        loading = true;
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Code to be executed after desired time
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        showLoader();
                        getCustomersList(getString(R.string.last_updated_date), "1",
                                customerDetailSearchEditText.getText().toString().trim(),
                                "0");
                    }
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

        //Filter Edittext
        customerDetailSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getActivity() != null && isAdded()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // do your actual work here
                            Activity activity = getActivity();
                            if (activity != null && isAdded()) {
                                if (customersList != null && customersList.size() > 0) {
                                    customersList.clear();
                                }
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        customerMainFragmentAdapter.notifyDataSetChanged();
                                    }
                                });
                                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                    showLoader();
                                    getCustomersList(getString(R.string.last_updated_date), "1", customerDetailSearchEditText.getText().toString().trim(),
                                            "0");
                                }
                            }
                        }
                    }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask
                }
            }
        });

        closeImageButton.setOnClickListener(this);
        fabFilter.setOnClickListener(this);
        fabAddCustomer.setOnClickListener(this);
        mainFab.setOnClickListener(this);
        fabAddCustomerLayout.setOnClickListener(this);
        fabFilterLayout.setOnClickListener(this);
        bottomLayout.setVisibility(View.GONE);
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (customersList != null && customersList.size() == 0) {
                                    if (imageView.getVisibility() == View.GONE) {
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                    //Disable Touch
                                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Ion.with(imageView)
                                            .animateGif(AnimateGifMode.ANIMATE)
                                            .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                            .withBitmapInfo();
                                }
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        if (v == closeImageButton) {
            // Handle clicks for closeImageButton
            bottomLayout.setVisibility(View.GONE);
            sortTextView.setText("");
            sortValue = 0;

            //Call API again
            callAPI();
        } else if (v == fabFilter || v == fabFilterLayout) {
            // Handle clicks for fabFilter
            filterDialog();
            animateFAB();
        } else if (v == mainFab) {
            // Handle clicks for mainFab
            animateFAB();
        } else if (v == fabAddCustomer || v == fabAddCustomerLayout) {
            // Handle clicks for fabAddCustomer
            animateFAB();
            openAddCustomerActivity();
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            mainFab.startAnimation(rotate_backward);
            fabFilter.startAnimation(fab_close);
            fabAddCustomer.startAnimation(fab_close);

            //    fabFilterLayout.startAnimation(fab_close);
            //   fabAddCustomerLayout.startAnimation(fab_close);

            addTextView.setVisibility(View.GONE);
            filterTextView.setVisibility(View.GONE);

            fabFilter.setClickable(false);
            fabAddCustomer.setClickable(false);

            fabFilterLayout.setVisibility(View.GONE);
            fabAddCustomerLayout.setVisibility(View.GONE);
            isFabOpen = false;
        } else {
            mainFab.startAnimation(rotate_forward);
            fabFilter.startAnimation(fab_open);
            fabAddCustomer.startAnimation(fab_open);

            //     fabFilterLayout.startAnimation(fab_open);
            //     fabAddCustomerLayout.startAnimation(fab_open);

            addTextView.setVisibility(View.VISIBLE);
            filterTextView.setVisibility(View.VISIBLE);

            fabFilterLayout.setVisibility(View.VISIBLE);
            fabAddCustomerLayout.setVisibility(View.VISIBLE);
            fabFilter.setClickable(true);
            fabAddCustomer.setClickable(true);
            isFabOpen = true;
        }
    }

    public void filterDialog() {
        dialogView = View.inflate(getActivity(), R.layout.customer_sort_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);

        dialogClearAll = (ImageView) dialogView.findViewById(R.id.dialog_clear_all);
        filterSortRadioGroup = (RadioGroup) dialogView.findViewById(R.id.filter_sort_radioGroup);
        radioButtonAmount = (RadioButton) dialogView.findViewById(R.id.radioButtonAmount);
        radioButtonName = (RadioButton) dialogView.findViewById(R.id.radioButtonName);
        childLayout = (LinearLayout) dialogView.findViewById(R.id.childLayout);
        sortAmountRadioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_amountRadioGroup);
        radioBtnAmtAsc = (RadioButton) dialogView.findViewById(R.id.radioBtnAmtAsc);
        radioBtnAmtDesc = (RadioButton) dialogView.findViewById(R.id.radioBtnAmtDesc);
        sortNameRadioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_nameRadioGroup);
        radioBtnNameAsc = (RadioButton) dialogView.findViewById(R.id.radioBtnNameAsc);
        radioBtnNameDesc = (RadioButton) dialogView.findViewById(R.id.radioBtnNameDesc);
        orderTextView = (TextView) dialogView.findViewById(R.id.order_textView);
        orderTextView.setVisibility(View.GONE);

        radioButtonAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sortAmountRadioGroup.setVisibility(View.VISIBLE);
                    orderTextView.setVisibility(View.VISIBLE);
                    //Hide Names
                    sortNameRadioGroup.setVisibility(View.GONE);
                    //radioBtnNameAsc.setChecked(false);
                    //radioBtnNameDesc.setChecked(false);
                    sortNameRadioGroup.clearCheck();
                } else {
                    sortAmountRadioGroup.setVisibility(View.GONE);
                    //radioBtnAmtAsc.setChecked(false);
                    //radioBtnAmtDesc.setChecked(false);
                    sortAmountRadioGroup.clearCheck();
                }
            }
        });

        radioButtonName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sortNameRadioGroup.setVisibility(View.VISIBLE);
                    orderTextView.setVisibility(View.VISIBLE);
                    //Hide Amount
                    sortAmountRadioGroup.setVisibility(View.GONE);
                    //radioBtnAmtAsc.setChecked(false);
                    //radioBtnAmtDesc.setChecked(false);
                    sortAmountRadioGroup.clearCheck();
                } else {
                    sortNameRadioGroup.setVisibility(View.GONE);
                    //radioBtnNameAsc.setChecked(false);
                    //radioBtnNameDesc.setChecked(false);
                    sortNameRadioGroup.clearCheck();
                }
            }
        });

        radioButtonAmount.setOnClickListener(this);
        radioButtonName.setOnClickListener(this);
        radioBtnAmtAsc.setOnClickListener(this);
        radioBtnAmtDesc.setOnClickListener(this);
        radioBtnNameAsc.setOnClickListener(this);
        radioBtnNameDesc.setOnClickListener(this);

        dialogView.findViewById(R.id.dialog_filter_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.dismiss();
                }
            }
        });

        dialogView.findViewById(R.id.dialog_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonAmount.setChecked(false);
                radioButtonName.setChecked(false);
                sortValue = 0;
                alertDialog.dismiss();
                callAPI();
            }
        });

        dialogView.findViewById(R.id.dialog_filter_apply).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!radioButtonAmount.isChecked() && !radioButtonName.isChecked()) {
                    Toast.makeText(getActivity(), "Please check atleast one option from name and amount.",
                            Toast.LENGTH_SHORT).show();
                } else if (radioButtonName.isChecked() && !radioButtonAmount.isChecked()) {
                    if (!radioBtnNameAsc.isChecked() && !radioBtnNameDesc.isChecked()) {
                        Toast.makeText(getActivity(), "Please check atleast one option from A-Z and Z-A.",
                                Toast.LENGTH_SHORT).show();
                    } else if (radioBtnNameAsc.isChecked() && !radioBtnNameDesc.isChecked()) {
                        sortValue = 3;
                        alertDialog.dismiss();
                        callAPI();
                    } else if (!radioBtnNameAsc.isChecked() && radioBtnNameDesc.isChecked()) {
                        sortValue = 4;
                        alertDialog.dismiss();
                        callAPI();
                    }
                } else if (!radioButtonName.isChecked() && radioButtonAmount.isChecked()) {
                    if (!radioBtnAmtAsc.isChecked() && !radioBtnAmtDesc.isChecked()) {
                        Toast.makeText(getActivity(), "Please check atleast one option from ascending and descending.",
                                Toast.LENGTH_SHORT).show();
                    } else if (radioBtnAmtAsc.isChecked() && !radioBtnAmtDesc.isChecked()) {
                        sortValue = 1;
                        alertDialog.dismiss();
                        callAPI();
                    } else if (!radioBtnAmtAsc.isChecked() && radioBtnAmtDesc.isChecked()) {
                        sortValue = 2;
                        alertDialog.dismiss();
                        callAPI();
                    }
                }

            }
        });

        if (sortValue == 1) {
            radioButtonAmount.setChecked(true);
            radioBtnAmtAsc.setChecked(true);
        } else if (sortValue == 2) {
            radioButtonAmount.setChecked(true);
            radioBtnAmtDesc.setChecked(true);
        } else if (sortValue == 3) {
            radioButtonName.setChecked(true);
            radioBtnNameAsc.setChecked(true);
        } else if (sortValue == 4) {
            radioButtonName.setChecked(true);
            radioBtnNameDesc.setChecked(true);
        }

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (customerMainFragmentAdapter != null) {
            customerMainFragmentAdapter.saveStates(outState);
        }
    }

    public void filter(String text) {
        List<Customers> temp = new ArrayList();
        List<Customers> originalList = new ArrayList<>();
        originalList.addAll(customersList);
        if (text.length() != 0) {
            for (Customers customers : originalList) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (customers.getFname().contains(text)) {
                    temp.add(customers);
                }
            }
            customerMainFragmentAdapter.updateList(temp);
            if (temp.size() > 0) {
                textViewEmptyView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                textViewEmptyView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
        } else {

            customerMainFragmentAdapter.updateList(customersList);
            if (originalList.size() > 0) {
                textViewEmptyView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                textViewEmptyView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onRefresh() {
        loading = true;
        //calling API
        if (getActivity() != null && !NetworkUtil.getConnectivityStatusString(getActivity()).
                equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
        }
        callAPI();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.customers_view_details_permission))) {
            Intent intent = new Intent(getActivity(), CustomersTabActivity.class);
            intent.putExtra(getString(R.string.cuid), customersList.get(position).getCuid());
            intent.putExtra(getString(R.string.customer_email_id), customersList.get(position).getEmail());
            intent.putExtra(getString(R.string.wallet_balance), customersList.get(position).getWalletBalance());
            if (customersList.get(position).getCompanyName() != null && !customersList.get(position).getCompanyName().isEmpty()) {
                intent.putExtra(getString(R.string.name), customersList.get(position).getCompanyName().trim());
            } else {
                intent.putExtra(getString(R.string.name), customersList.get(position).getFname().trim() + " " + customersList.get(position).getLname().trim());
            }
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Sorry, you've no permission to view details of a customer.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void customerMobile(String mobile) {

        this.customerMobileNumber = mobile;

    }

    @SuppressLint("MissingPermission")
    public void callCustomer() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + customerMobileNumber));
        if (customerMobileNumber == null || customerMobileNumber == "") {
            Toast.makeText(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        } else {
            getActivity().startActivity(intentCall);
        }
    }

    private void getCustomersList(final String time, final String traversalValue, final String searchText,
                                  final String cNo) {
        task = getString(R.string.customer_fetch);
        /*if (time.equals(getString(R.string.last_updated_date)) && customersList != null && customersList.size() > 0) {
            customersList.clear();
            customerMainFragmentAdapter.notifyDataSetChanged();
        }*/

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getCustomersList(version, key, task, userId, accessToken, time, traversalValue,
                searchText, sortValue, customersList.size(), cNo);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {

                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse != null && apiResponse.getSuccess()) {
                        for (Customers customers : apiResponse.getData().getCustomers()) {
                            if (apiResponse.getData().getCustomers() != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(customers.getCreatedTs())) {
                                        customersList.add(customers);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicate items
                                        if (!time.equals(customers.getCreatedTs())) {
                                            customersList.add(0, customers);
                                        }
                                    } else {
                                        customersList.add(customers);
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        // loading = false;
                    }
                    //Deleted User
                    else if (apiResponse != null && apiResponse.getSuccessCode() != null &&
                            (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN))) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    }
                    if (customersList != null && customersList.size() == 0) {
                        textViewEmptyView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    } else {
                        textViewEmptyView.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                    if (sortValue != 0) {
                        bottomLayout.setVisibility(View.VISIBLE);
                        if (sortValue == 1 || sortValue == 2) {
                            //Sorted
                            sortSpannable = new SpannableString("Sorted by : ");
                            sortSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, 9, 0); // set size
                            sortTextView.setText(sortSpannable);

                            if (sortValue == 1) {
                                sortedBySpannable = new SpannableString("Amount(Asc)");
                            } else {
                                sortedBySpannable = new SpannableString("Amount(Desc)");
                            }
                            sortTextView.append(sortedBySpannable);
                        } else if (sortValue == 3 || sortValue == 4) {
                            //Sorted
                            sortSpannable = new SpannableString("Sorted by : ");
                            sortSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, 9, 0); // set size
                            sortTextView.setText(sortSpannable);

                            if (sortValue == 3) {
                                sortedBySpannable = new SpannableString("Name(A-Z)");
                            } else {
                                sortedBySpannable = new SpannableString("Name(Z-A)");
                            }
                            sortTextView.append(sortedBySpannable);
                        }
                    } else {
                        bottomLayout.setVisibility(View.GONE);
                    }
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (traversalValue.equals("2")) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    customerMainFragmentAdapter.notifyDataSetChanged();
                                    loading = false;

                                }
                            });
                        }
                        if (dataChanged != null && dataChanged.equals("yes")) {
                            // recyclerView.smoothScrollToPosition(customerMainFragmentAdapter.getItemCount() + 1);
                        }
                    } else if (traversalValue.equals("1")) {
                        if (dataChanged != null && dataChanged.equals("yes")) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        customerMainFragmentAdapter.notifyDataSetChanged();
                                        recyclerView.smoothScrollToPosition(0);
                                        loading = false;
                                    }
                                });
                            }
                        }
                    }

                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                }
            }
        });
    }

    private void hideLoader() {
        if (getActivity() != null) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void callAPI() {
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Code to be executed after desired time
                    Activity activity = getActivity();
                    if (customersList != null && customersList.size() > 0) {
                        customersList.clear();
                        customerMainFragmentAdapter.notifyDataSetChanged();
                    }
                    if (activity != null && isAdded()) {
                        showLoader();
                        getCustomersList(getString(R.string.last_updated_date), "1", customerDetailSearchEditText.getText().toString().trim(), "0");
                    }
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddCustomerActivity() {
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.customer_add_permission))) {
            Intent intent = new Intent(getActivity(), AddCustomerActivity.class);
            startActivityForResult(intent, AppUtils.ADD_CUSTOMER_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), "Sorry, you've no permission to add a customer.", Toast.LENGTH_SHORT).show();
        }
    }
}
