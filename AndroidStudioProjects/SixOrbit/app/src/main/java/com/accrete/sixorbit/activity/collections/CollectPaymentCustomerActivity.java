package com.accrete.sixorbit.activity.collections;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.PaymentModesAdapter;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Outlet;
import com.accrete.sixorbit.model.PaymentModeData;
import com.accrete.sixorbit.model.PaymentOptionAttribute;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 6/6/18.
 */

public class CollectPaymentCustomerActivity extends AppCompatActivity implements View.OnClickListener,
        PaymentModesAdapter.PaymentModesAdapterListener, PassDateToCounsellor {
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
    private CardView customerCardView;
    private TextView customerInfoTextView;
    private TextView customerNameTitleTextView;
    private EditText customerNameValueEditText;
    private ImageButton clearCustomerInfoImageButton;
    private LinearLayout receiveAmountLayout;
    private TextView receiveAmountTextView;
    private EditText receiveAmountValueEditText;
    private LinearLayout dateLayout;
    private TextView dateTextView;
    private EditText dateValueEditText;
    private CardView paymentTypeCardview;
    private TextView paymentTypeTitleTextView;
    private Spinner paymentTypeSpinner;
    private CardView outletCardview;
    private TextView outletTitleTextView;
    private Spinner outletSpinner;
    private TextView addCustomerTextView;
    private ImageView loaderImageView;
    private LinearLayout pendingAmountLayout;
    private TextView pendingAmountTextView;
    private EditText pendingAmountValueEditText;
    private List<PaymentModeData> paymentModeDataList = new ArrayList<>();
    private List<PaymentOptionAttribute> paymentAttributesList = new ArrayList<>();
    private PaymentModesAdapter paymentModesAdapter;
    private AllDatePickerFragment allDatePickerFragment;
    private Date startDate;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private ArrayList<Outlet> outletArrayList = new ArrayList<>();
    private RecyclerView recyclerViewAdvancePaymentType;
    private String stringStartDate, status, strCuid, strAlid, strCustomerName, pendingAmount,
            chkoid, invId;
    private int clickedItemPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_payment_customer);

        if (getIntent() != null) {
            if (getIntent().hasExtra(getString(R.string.cuid))) {
                strCuid = getIntent().getStringExtra(getString(R.string.cuid));
            } else {
                strCuid = "";
            }
            if (getIntent().hasExtra(getString(R.string.customer))) {
                strCustomerName = getIntent().getStringExtra(getString(R.string.customer));
            } else {
                strCustomerName = "";
            }
            if (getIntent().hasExtra(getString(R.string.alid))) {
                strAlid = getIntent().getStringExtra(getString(R.string.alid));
            } else {
                strAlid = "";
            }
            if (getIntent().hasExtra(getString(R.string.pending_amount))) {
                pendingAmount = getIntent().getStringExtra(getString(R.string.pending_amount));
            } else {
                pendingAmount = "";
            }
            if (getIntent().hasExtra(getString(R.string.invid))) {
                invId = getIntent().getStringExtra(getString(R.string.invid));
            } else {
                invId = "";
            }
            if (getIntent().hasExtra(getString(R.string.chkoid))) {
                chkoid = getIntent().getStringExtra(getString(R.string.chkoid));
            } else {
                chkoid = "";
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        customerCardView = (CardView) findViewById(R.id.customer_cardView);
        customerInfoTextView = (TextView) findViewById(R.id.customer_info_textView);
        customerNameTitleTextView = (TextView) findViewById(R.id.customer_name_title_textView);
        customerNameValueEditText = (EditText) findViewById(R.id.customer_name_value_editText);
        clearCustomerInfoImageButton = (ImageButton) findViewById(R.id.clear_customerInfo_imageButton);
        receiveAmountLayout = (LinearLayout) findViewById(R.id.receive_amount_layout);
        receiveAmountTextView = (TextView) findViewById(R.id.receive_amount_textView);
        receiveAmountValueEditText = (EditText) findViewById(R.id.receive_amount_value_editText);
        dateLayout = (LinearLayout) findViewById(R.id.date_layout);
        dateTextView = (TextView) findViewById(R.id.date_textView);
        dateValueEditText = (EditText) findViewById(R.id.date_value_editText);
        paymentTypeCardview = (CardView) findViewById(R.id.payment_type_cardview);
        paymentTypeTitleTextView = (TextView) findViewById(R.id.payment_type_title_textView);
        paymentTypeSpinner = (Spinner) findViewById(R.id.payment_type_spinner);
        recyclerViewAdvancePaymentType = (RecyclerView) findViewById(R.id.recyclerView_payment_type);
        outletCardview = (CardView) findViewById(R.id.outlet_cardview);
        outletTitleTextView = (TextView) findViewById(R.id.outlet_title_textView);
        outletSpinner = (Spinner) findViewById(R.id.outlet_spinner);
        addCustomerTextView = (TextView) findViewById(R.id.addCustomer_textView);
        loaderImageView = (ImageView) findViewById(R.id.loader_imageView);
        pendingAmountLayout = (LinearLayout) findViewById(R.id.pending_amount_layout);
        pendingAmountTextView = (TextView) findViewById(R.id.pending_amount_textView);
        pendingAmountValueEditText = (EditText) findViewById(R.id.pending_amount_value_editText);

        toolbar.setTitle("Collect Payment");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //TODO User can't edit customer in the case of collecting payment
        clearCustomerInfoImageButton.setEnabled(false);
        clearCustomerInfoImageButton.setVisibility(View.GONE);
        pendingAmountValueEditText.setKeyListener(null);
        customerNameValueEditText.setText(strCustomerName);
        pendingAmountValueEditText.setText(pendingAmount);

        //DatePicker
        allDatePickerFragment = new AllDatePickerFragment();
        allDatePickerFragment.setListener(this);

        paymentModesAdapter = new PaymentModesAdapter(CollectPaymentCustomerActivity.this, paymentAttributesList, this);

        //Payment Types RecyclerView
        RecyclerView.LayoutManager paymentModeLayoutManager = new LinearLayoutManager(CollectPaymentCustomerActivity.this);
        recyclerViewAdvancePaymentType.setLayoutManager(paymentModeLayoutManager);
        recyclerViewAdvancePaymentType.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdvancePaymentType.setAdapter(paymentModesAdapter);
        recyclerViewAdvancePaymentType.setNestedScrollingEnabled(false);


        //Mandatory Outlet title
        String mainText = "Outlet";
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        outletTitleTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));

        //Mandatory Payment Type title
        String paymentTypeText = "Payment Type";
        paymentTypeTitleTextView.setText(TextUtils.concat(paymentTypeText, spannableStringBuilder));

        //TODO - Date for cheques
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        dateValueEditText.setText(formattedDate);

        //TODO - Payment Mode Spinner Selection
        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.clear();
                }
                if (paymentModeDataList.get(position).getPaymentOptionAttribute() != null &&
                        paymentModeDataList.get(position).getPaymentOptionAttribute().size() > 0) {
                    paymentAttributesList.addAll(paymentModeDataList.get(position).getPaymentOptionAttribute());
                }
                paymentModesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //OnClick
        addCustomerTextView.setOnClickListener(this);

        status = NetworkUtil.getConnectivityStatusString(CollectPaymentCustomerActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getPaymentModeAndOutlets();
        } else {
            Toast.makeText(CollectPaymentCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }

    }

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void getPaymentModeAndOutlets() {
        task = getString(R.string.collection_fetch_form_data);
        if (AppPreferences.getIsLogin(CollectPaymentCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(CollectPaymentCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(CollectPaymentCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(CollectPaymentCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {

                    if (apiResponse.getSuccess()) {

                        //TODO - Payment Mode Data Type
                        PaymentModeData paymentModeData = new PaymentModeData();
                        paymentModeData.setName("Select");
                        paymentModeDataList.add(paymentModeData);
                        if (apiResponse.getData().getPaymentModeData() != null &&
                                !apiResponse.getData().getPaymentModeData().isEmpty() &&
                                !apiResponse.getData().getPaymentModeData().equals("null")) {
                            for (PaymentModeData type : apiResponse.getData().getPaymentModeData()) {
                                paymentModeDataList.add(type);
                            }
                        }

                        ArrayAdapter<PaymentModeData> modeDataArrayAdapter =
                                new ArrayAdapter<PaymentModeData>(CollectPaymentCustomerActivity.this,
                                        R.layout.simple_spinner_item, paymentModeDataList);
                        modeDataArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        paymentTypeSpinner.setAdapter(modeDataArrayAdapter);
                        paymentTypeSpinner.setSelection(0);

                        //TODO Outlet
                        if (outletArrayList != null && outletArrayList.size() > 0) {
                            outletArrayList.clear();
                        }

                        //One Static
                        Outlet outletStatic = new Outlet();
                        outletStatic.setName("Select Outlet");
                        outletArrayList.add(outletStatic);
                        if (apiResponse.getData().getOutlet() != null) {
                            for (final Outlet outlet : apiResponse.getData().getOutlet()) {
                                if (outlet != null) {
                                    outletArrayList.add(outlet);
                                }
                            }
                        }
                        //Outlet Adapter
                        ArrayAdapter<Outlet> outletArrayAdapter =
                                new ArrayAdapter<Outlet>(CollectPaymentCustomerActivity.this, R.layout.simple_spinner_item, outletArrayList);
                        outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        outletSpinner.setAdapter(outletArrayAdapter);
                        outletSpinner.setSelection(0);

                        if (apiResponse.getData().isOutletEnable()) {
                            outletCardview.setVisibility(View.VISIBLE);
                        } else {
                            outletCardview.setVisibility(View.GONE);
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(CollectPaymentCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        if (CollectPaymentCustomerActivity.this != null) {
                            Toast.makeText(CollectPaymentCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (CollectPaymentCustomerActivity.this != null) {
                    Toast.makeText(CollectPaymentCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void updateAttributes(int position, String value) {
        if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
            if (value != null) {
                paymentAttributesList.get(position).setValue(value);
            } else {
                paymentAttributesList.get(position).setValue("");
            }
        }
    }

    @Override
    public void updateDate(int position) {
        allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.cheque_date));
        this.clickedItemPos = position;
    }

    @Override
    public void passDate(String s) {
        stringStartDate = s;
        if (allDatePickerFragment.getTag().equals(getString(R.string.cheque_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                //Date Of cheque
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.get(clickedItemPos).setValue(from);
                    paymentModesAdapter.notifyDataSetChanged();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.select_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                dateValueEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void passTime(String s) {

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.addCustomer_textView:
                    addCustomerTextView.setEnabled(false);

                    boolean isPayment = false;
                    String paymentField = "";
                    ArrayList<String> stringArray = new ArrayList<String>();

                    if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                        for (int q = 0; q < paymentAttributesList.size(); q++) {
                            if (paymentAttributesList.get(q).getRequired() != null &&
                                    !paymentAttributesList.get(q).getRequired().isEmpty() &&
                                    paymentAttributesList.get(q).getRequired().equals("1")) {
                                if (paymentAttributesList.get(q).getValue() == null ||
                                        paymentAttributesList.get(q).getValue().isEmpty() ||
                                        paymentAttributesList.get(q).getValue().toString().length() == 0) {
                                    isPayment = true;
                                    paymentField = paymentAttributesList.get(q).getName();
                                }
                            }
                        }
                        //TODO - Payment Modes Attributes
                        for (int i = 0; i < paymentAttributesList.size(); i++) {
                            JSONObject paymentModesAttributesJsonObject = new JSONObject();

                            //TODO Updated on 8th June
                            if (paymentAttributesList.get(i).getName().toLowerCase().equals("date")) {
                                //Date Format
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String chequeDate = "";
                                if (paymentAttributesList.get(i).getValue().toString().trim() != null &&
                                        !paymentAttributesList.get(i).getValue().toString().trim().isEmpty() &&
                                        !paymentAttributesList.get(i).getValue().toString().trim().contains("0000")) {
                                    try {
                                        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = simpleDateFormat.parse(paymentAttributesList.get(i).getValue().toString());
                                        paymentAttributesList.get(i).setValue(outputFormat.format(date).toString().trim());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            paymentModesAttributesJsonObject.put(paymentAttributesList.get(i).getCpoaid(),
                                    paymentAttributesList.get(i).getValue());
                            stringArray.add(paymentModesAttributesJsonObject.toString());
                        }
                    }

                    if (paymentTypeSpinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(CollectPaymentCustomerActivity.this, "Please select payment type.", Toast.LENGTH_SHORT).show();
                    } else if (isPayment) {
                        Toast.makeText(CollectPaymentCustomerActivity.this, "Please enter " + paymentField + "", Toast.LENGTH_SHORT).show();
                    } else if (outletCardview.getVisibility() == View.VISIBLE && outletSpinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(CollectPaymentCustomerActivity.this, "Please select an outlet.", Toast.LENGTH_SHORT).show();
                    } else if (receiveAmountValueEditText.getText().toString() == null ||
                            receiveAmountValueEditText.getText().toString().isEmpty() ||
                            receiveAmountValueEditText.getText().toString().length() == 0 ||
                            roundTwoDecimals(Constants.ParseDouble(receiveAmountValueEditText.getText().toString())) == 0.00) {
                        Toast.makeText(CollectPaymentCustomerActivity.this, "Please enter valid receive amount.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Date Format
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String collectionDate = "";
                        if (dateValueEditText.getText().toString().trim() != null &&
                                !dateValueEditText.getText().toString().trim().isEmpty() &&
                                !dateValueEditText.getText().toString().trim().contains("0000")) {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = simpleDateFormat.parse(dateValueEditText.getText().toString());
                                collectionDate = outputFormat.format(date).toString().trim();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(CollectPaymentCustomerActivity.this, CreateCollectionsInvoiceActivity.class);
                        intent.putExtra("payment_type", paymentModeDataList.get(paymentTypeSpinner.getSelectedItemPosition())
                                .getCpoid());
                        intent.putExtra("payment_type_text", paymentModeDataList.get(paymentTypeSpinner.getSelectedItemPosition())
                                .getName());
                        intent.putStringArrayListExtra("attributes_array", stringArray);
                        intent.putExtra("date", collectionDate);
                        intent.putExtra("cuId", strCuid);
                        intent.putExtra("customer_name", customerNameValueEditText.getText().toString());
                        intent.putExtra("alId", strAlid);
                        intent.putExtra("amount", receiveAmountValueEditText.getText().toString());
                        intent.putExtra("outletId",
                                outletArrayList.get(outletSpinner.getSelectedItemPosition()).getChkid());
                        intent.putExtra("title", "Collect Payment");
                        intent.putExtra(getString(R.string.invid), invId);
                        intent.putExtra(getString(R.string.chkoid), chkoid);
                        startActivity(intent);
                    }

                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addCustomerTextView.setEnabled(true);
                        }
                    }, 3000);
                    break;
                case R.id.date_value_editText:
                    allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.select_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dateValueEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
