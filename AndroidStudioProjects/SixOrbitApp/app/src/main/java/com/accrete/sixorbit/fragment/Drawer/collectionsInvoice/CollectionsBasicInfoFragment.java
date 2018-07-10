package com.accrete.sixorbit.fragment.Drawer.collectionsInvoice;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerData;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 5/6/18.
 */

public class CollectionsBasicInfoFragment extends Fragment {
    private LinearLayout customerNameLayout;
    private TextView customerNameTextView;
    private TextView customerNameValueTextView;
    private LinearLayout invoiceNumberLayout;
    private TextView invoiceNumberTextView;
    private TextView invoiceNumberValueTextView;
    private LinearLayout nameLayout;
    private TextView nameTextView;
    private TextView nameValueTextView;
    private LinearLayout emailLayout;
    private TextView emailTextView;
    private TextView emailValueTextView;
    private LinearLayout mobileLayout;
    private TextView mobileTextView;
    private TextView mobileValueTextView;
    private LinearLayout shippingAddressLayout;
    private TextView shippingAddressTextView;
    private TextView shippingAddressValueTextView;
    private LinearLayout billingAddressLayout;
    private TextView billingAddressTextView;
    private TextView billingAddressValueTextView;
    private LinearLayout dateLayout;
    private TextView dateTextView;
    private TextView dateValueTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView statusValueTextView;
    private CustomerData customerData = null;
    private ImageView imageView;
    private TextView textViewEmpty;
    private String invId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        invId = bundle.getString(getString(R.string.invid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collections_customers_basic_info, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        customerNameLayout = (LinearLayout) view.findViewById(R.id.customer_name_layout);
        customerNameTextView = (TextView) view.findViewById(R.id.customer_name_textView);
        customerNameValueTextView = (TextView) view.findViewById(R.id.customer_name_value_textView);
        invoiceNumberLayout = (LinearLayout) view.findViewById(R.id.invoice_number_layout);
        invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
        invoiceNumberValueTextView = (TextView) view.findViewById(R.id.invoice_number_value_textView);
        nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
        nameTextView = (TextView) view.findViewById(R.id.name_textView);
        nameValueTextView = (TextView) view.findViewById(R.id.name_value_textView);
        emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        emailTextView = (TextView) view.findViewById(R.id.email_textView);
        emailValueTextView = (TextView) view.findViewById(R.id.email_value_textView);
        mobileLayout = (LinearLayout) view.findViewById(R.id.mobile_layout);
        mobileTextView = (TextView) view.findViewById(R.id.mobile_textView);
        mobileValueTextView = (TextView) view.findViewById(R.id.mobile_value_textView);
        shippingAddressLayout = (LinearLayout) view.findViewById(R.id.shipping_address_layout);
        shippingAddressTextView = (TextView) view.findViewById(R.id.shipping_address_textView);
        shippingAddressValueTextView = (TextView) view.findViewById(R.id.shipping_address_value_textView);
        billingAddressLayout = (LinearLayout) view.findViewById(R.id.billing_address_layout);
        billingAddressTextView = (TextView) view.findViewById(R.id.billing_address_textView);
        billingAddressValueTextView = (TextView) view.findViewById(R.id.billing_address_value_textView);
        dateLayout = (LinearLayout) view.findViewById(R.id.date_layout);
        dateTextView = (TextView) view.findViewById(R.id.date_textView);
        dateValueTextView = (TextView) view.findViewById(R.id.date_value_textView);
        statusLayout = (LinearLayout) view.findViewById(R.id.status_layout);
        statusTextView = (TextView) view.findViewById(R.id.status_textView);
        statusValueTextView = (TextView) view.findViewById(R.id.status_value_textView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textViewEmpty = (TextView) view.findViewById(R.id.textView_empty);

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            getCustomersInfo(invId);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            if (getActivity() != null && isAdded()) {
                textViewEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCustomersInfo(String invId) {
        try {
            if (getActivity() != null) {
                task = getString(R.string.collection_invoice_detail);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.collectionInvoiceDetail(version, key, task, userId,
                        accessToken, invId);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                customerData = apiResponse.getData().getCustomerData();
                                setCustomerData(customerData);
                            }
                            //Deleted User
                            else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            }

                            hideLoader();
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideLoader();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                            hideLoader();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
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
                    });
                }
            }
        });
        thread.start();
    }

    private void setCustomerData(CustomerData customerData) {
        textViewEmpty.setVisibility(View.VISIBLE);

        if (customerData.getCustomerName() != null && !customerData.getCustomerName().isEmpty() &&
                !customerData.getCustomerName().equals("null")) {
            customerNameValueTextView.setText(customerData.getCustomerName().toString().trim());
            customerNameLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            customerNameLayout.setVisibility(View.GONE);
        }

        if (customerData.getInvoiceNumber() != null && !customerData.getInvoiceNumber().isEmpty() &&
                !customerData.getInvoiceNumber().equals("null")) {
            invoiceNumberValueTextView.setText(customerData.getInvoiceNumber().toString().trim());
            invoiceNumberLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            invoiceNumberLayout.setVisibility(View.GONE);
        }

        if (customerData.getName() != null && !customerData.getName().isEmpty() &&
                !customerData.getName().equals("null")) {
            nameValueTextView.setText(customerData.getName().toString().trim());
            nameLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            nameLayout.setVisibility(View.GONE);
        }

        if (customerData.getEmail() != null && !customerData.getEmail().isEmpty() &&
                !customerData.getEmail().equals("null")) {
            emailValueTextView.setText(customerData.getEmail().toString().trim());
            emailLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            emailLayout.setVisibility(View.GONE);
        }

        if (customerData.getMobile() != null && !customerData.getMobile().isEmpty() &&
                !customerData.getMobile().equals("null")) {
            mobileValueTextView.setText(customerData.getMobile().toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        if (customerData.getShippingAddress() != null && !customerData.getShippingAddress().isEmpty() &&
                !customerData.getShippingAddress().equals("null")) {
            shippingAddressValueTextView.setText(customerData.getShippingAddress().toString().trim());
            shippingAddressLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            shippingAddressLayout.setVisibility(View.GONE);
        }

        if (customerData.getBillingAddress() != null && !customerData.getBillingAddress().isEmpty() &&
                !customerData.getBillingAddress().equals("null")) {
            billingAddressValueTextView.setText(customerData.getBillingAddress().toString().trim());
            billingAddressLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            billingAddressLayout.setVisibility(View.GONE);
        }

        if (customerData.getDate() != null && !customerData.getDate().isEmpty() &&
                !customerData.getDate().equals("null")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(customerData.getDate());
                dateValueTextView.setText(outputFormat.format(date).toString().trim());
                dateLayout.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } catch (ParseException e) {
                e.printStackTrace();
                dateLayout.setVisibility(View.GONE);
            }

        } else {
            dateLayout.setVisibility(View.GONE);
        }

        if (customerData.getStatus() != null && !customerData.getStatus().isEmpty() &&
                !customerData.getStatus().equals("null")) {
            statusValueTextView.setText(customerData.getStatus().toString().trim());
            statusValueTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) statusValueTextView.getBackground();

            if (customerData.getStatus() != null && !customerData.getStatus().isEmpty()) {
                if (customerData.getStatus().toLowerCase().equals("active")) {
                    drawable.setColor(getResources().getColor(R.color.green_purchase_order));
                } else {
                    drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
                }
            }

            statusLayout.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            statusLayout.setVisibility(View.GONE);
        }
    }
}
