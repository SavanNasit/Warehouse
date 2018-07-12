package com.accrete.sixorbit.fragment.Drawer.customer;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.QuotationDetails;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 14/12/17.
 */

public class CustomerQuotationDetailsTabFragment extends Fragment {

    private String cuId, qoId;
    private LinearLayout layoutQuotationsDetail;
    private TextView textViewQuotationDetails;
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
    private TextView emptyTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private String strName, strEmail, strMobile, strShippingAddress = "", strBillingAddress = "", qosId = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        cuId = bundle.getString(getString(R.string.cuid));
        qoId = bundle.getString(getString(R.string.qo_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_customer_quotation_details, container, false);
        //Find all widgets
        findViews(rootView);
        return rootView;
    }

    private void findViews(final View view) {
        layoutQuotationsDetail = (LinearLayout) view.findViewById(R.id.layout_quotations_detail);
        textViewQuotationDetails = (TextView) view.findViewById(R.id.textView_quotation_details);
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
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);
        statusLayout = (LinearLayout) view.findViewById(R.id.status_layout);
        statusTextView = (TextView) view.findViewById(R.id.status_textView);

        emptyTextView.setText("No data.");
        emptyTextView.setVisibility(View.GONE);
        nameLayout.setVisibility(View.GONE);
        emailLayout.setVisibility(View.GONE);
        mobileLayout.setVisibility(View.GONE);
        shippingAddressLayout.setVisibility(View.GONE);
        billingAddressLayout.setVisibility(View.GONE);


        //Call API
        doRefresh();

        //Load data after getting connection
        emptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyTextView.getText().toString().trim().equals(getString(R.string.not_connected_to_internet))) {
                    doRefresh();
                }
            }
        });
    }

    public void doRefresh() {
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getQuotationInfo(cuId, qoId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.not_connected_to_internet));
            layoutQuotationsDetail.setVisibility(View.GONE);
        }

    }

    private void getQuotationInfo(String cuId, final String qoId) {
        task = getString(R.string.quotation_details);
        userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getCustomerQuotationInfo(version, key, task, userId, accessToken, cuId, qoId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    QuotationDetails quotationDetails = apiResponse.getData().getQuotationDetails();
                    if (quotationDetails != null) {
                        strName = quotationDetails.getName();
                        strEmail = quotationDetails.getEmail();
                        strMobile = quotationDetails.getMobile();
                        qosId = quotationDetails.getQosId();

                        if (quotationDetails.getShippingAddressLine1() != null
                                && !quotationDetails.getShippingAddressLine1().isEmpty()
                                && !quotationDetails.getShippingAddressLine1().equals("null")) {
                            strShippingAddress = strShippingAddress + quotationDetails.getShippingAddressLine1() + ", ";
                        }
                        if (quotationDetails.getShippingAddressLine2() != null
                                && !quotationDetails.getShippingAddressLine2().isEmpty()
                                && !quotationDetails.getShippingAddressLine2().equals("null")) {
                            strShippingAddress = strShippingAddress + quotationDetails.getShippingAddressLine2() + ", ";
                        }
                        if (quotationDetails.getShippingAddressCity() != null
                                && !quotationDetails.getShippingAddressCity().isEmpty()
                                && !quotationDetails.getShippingAddressCity().equals("null")) {
                            strShippingAddress = strShippingAddress + quotationDetails.getShippingAddressCity() + ", ";
                        }
                        if (quotationDetails.getShippingAddressZipcode() != null
                                && !quotationDetails.getShippingAddressZipcode().isEmpty()
                                && !quotationDetails.getShippingAddressZipcode().equals("null")) {
                            strShippingAddress = strShippingAddress + quotationDetails.getShippingAddressZipcode() + ", ";
                        }
                        if (quotationDetails.getShippingAddressCountry() != null
                                && !quotationDetails.getShippingAddressCountry().isEmpty()
                                && !quotationDetails.getShippingAddressCountry().equals("null")) {
                            strShippingAddress = strShippingAddress + quotationDetails.getShippingAddressCountry() + "";
                        }

                        //Billing Address
                        if (quotationDetails.getBillingAddressLine1() != null
                                && !quotationDetails.getBillingAddressLine1().isEmpty()
                                && !quotationDetails.getBillingAddressLine1().equals("null")) {
                            strBillingAddress = strBillingAddress + quotationDetails.getBillingAddressLine1() + ", ";
                        }
                        if (quotationDetails.getBillingAddressLine2() != null
                                && !quotationDetails.getBillingAddressLine2().isEmpty()
                                && !quotationDetails.getBillingAddressLine2().equals("null")) {
                            strBillingAddress = strBillingAddress + quotationDetails.getBillingAddressLine2() + ", ";
                        }
                        if (quotationDetails.getBillingAddressCity() != null
                                && !quotationDetails.getBillingAddressCity().isEmpty()
                                && !quotationDetails.getBillingAddressCity().equals("null")) {
                            strBillingAddress = strBillingAddress + quotationDetails.getBillingAddressCity() + ", ";
                        }
                        if (quotationDetails.getBillingAddressZipcode() != null
                                && !quotationDetails.getBillingAddressZipcode().isEmpty()
                                && !quotationDetails.getBillingAddressZipcode().equals("null")) {
                            strBillingAddress = strBillingAddress + quotationDetails.getBillingAddressZipcode() + ", ";
                        }
                        if (quotationDetails.getBillingAddressCountry() != null
                                && !quotationDetails.getBillingAddressCountry().isEmpty()
                                && !quotationDetails.getBillingAddressCountry().equals("null")) {
                            strBillingAddress = strBillingAddress + quotationDetails.getBillingAddressCountry() + "";
                        }

                        //Set Data
                        setData();
                    }
                    emptyTextView.setVisibility(View.GONE);
                    emptyTextView.setText("No data");
                    layoutQuotationsDetail.setVisibility(View.VISIBLE);
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                } else {
                    if (isAdded() && getActivity() != null) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText(apiResponse.getMessage());
                        layoutQuotationsDetail.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (isAdded() && getActivity() != null) {
                        //   Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No data");
                        layoutQuotationsDetail.setVisibility(View.GONE);
                    }
                }
            }

        });

    }

    public void setData() {

        //Name
        if (strName != null && !strName.isEmpty() && !strName.equals("null")) {
            nameValueTextView.setText(strName.toString().trim());
            nameLayout.setVisibility(View.VISIBLE);
        } else {
            nameLayout.setVisibility(View.GONE);
        }

        //Email
        if (strEmail != null && !strEmail.isEmpty() && !strEmail.equals("null")) {
            emailValueTextView.setText(strEmail.toString().trim());
            emailLayout.setVisibility(View.VISIBLE);
        } else {
            emailLayout.setVisibility(View.GONE);
        }

        //Mobile
        if (strMobile != null && !strMobile.isEmpty() && !strMobile.equals("null")) {
            mobileValueTextView.setText(strMobile.toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        //Shipping Address
        if (strShippingAddress != null && !strShippingAddress.isEmpty() && !strShippingAddress.equals("null")) {
            shippingAddressValueTextView.setText(strShippingAddress.toString().trim());
            shippingAddressLayout.setVisibility(View.VISIBLE);
        } else {
            shippingAddressLayout.setVisibility(View.GONE);
        }

        //Billing address
        if (strBillingAddress != null && !strBillingAddress.isEmpty() && !strBillingAddress.equals("null")) {
            billingAddressValueTextView.setText(strBillingAddress.toString().trim());
            billingAddressLayout.setVisibility(View.VISIBLE);
        } else {
            billingAddressLayout.setVisibility(View.GONE);
        }

        //Status
        statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) statusTextView.getBackground();

        statusLayout.setVisibility(View.VISIBLE);
        if (qosId.equals("1")) {
            drawable.setColor(getResources().getColor(R.color.green_purchase_order));
            statusTextView.setText("Converted");
        } else if (qosId.equals("2")) {
            drawable.setColor(getResources().getColor(R.color.orange_purchase_order));
            statusTextView.setText("Pending");
        } else if (qosId.equals("3")) {
            drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            statusTextView.setText("Cancelled");
        } else if (qosId.equals("4")) {
            drawable.setColor(getResources().getColor(R.color.blue_order));
            statusTextView.setText("To be Approved");
        } else {
            statusLayout.setVisibility(View.GONE);
        }

    }

}
