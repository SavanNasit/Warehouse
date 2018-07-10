package com.accrete.sixorbit.fragment.Drawer.customer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CustomerAddressAdapter;
import com.accrete.sixorbit.adapter.CustomerContactDetailAdapter;
import com.accrete.sixorbit.adapter.CustomerMainFragmentAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.ContactDetail;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Customer;
import com.accrete.sixorbit.model.CustomerShippingAddress;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;


/**
 * Created by poonam on 1/11/17.
 */

public class CustomerDetailTabFragment extends Fragment implements CustomerMainFragmentAdapter.CustomersListener {

    private static final String TAG = "CustomerDetailTabFragment";
    public List<Customer> customerInformation = new ArrayList<Customer>();
    private PassMobileListener mMobileCallback;
    private PassSharingTextListener mInfoCallBack;
    private PassUsersEmailListener mEmailCallBack;
    private String customerName, companyName, customerMobile, customerTelephone, customerEmail, customerGstReg, customerReferrelType,
            customerGstinNo, customerPan, customerTin, customerCst, genderId, website, customerReferrelName,
            customerReferrelContactPerson, cuId, CopyText, copiedText = "";
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbarTop;
    private LinearLayout layoutCustomerInfoInfoCall;
    private TextView customerInfoNumber;
    private LinearLayout layoutCustomerInfoInfoEmail;
    private TextView customerInfoEmail;
    private LinearLayout layoutCustomerInfo;
    private TextView textViewPersonalDetails;
    private LinearLayout customerNameLayout;
    private TextView textViewCustomerName;
    private TextView textViewValueCustomerName;
    private LinearLayout mobileLayout;
    private TextView textviewMobile;
    private TextView textviewMobileValue;
    private LinearLayout telephoneLayout;
    private TextView textviewTelephone;
    private TextView textviewValueTelephone;
    private LinearLayout emailLayout;
    private TextView textviewEmail;
    private TextView textviewValueEmail;
    private LinearLayout gstRegLayout;
    private TextView textviewGstReg;
    private TextView textviewValueGstReg;
    private LinearLayout referalTypeLayout;
    private TextView textviewReferelType;
    private TextView textviewValueReferelType;
    private LinearLayout gstInLayout;
    private TextView textviewGstin;
    private TextView textviewGstinValue;
    private LinearLayout panLayout;
    private TextView textviewPan;
    private TextView textviewPanValue;
    private LinearLayout tinLayout;
    private TextView textviewTin;
    private TextView textviewTinValue;
    private LinearLayout cstLayout;
    private TextView textviewCst;
    private TextView textviewCstValue;
    private TextView textviewRecentAddress;
    private RelativeLayout layoutAddress;
    private TextView textviewCustomerInfoAddressEmpty;
    private Toolbar toolbarBottom;
    private LinearLayout layoutIdCopy;
    private TextView textviewCopy;
    private TextView textCopy;
    private LinearLayout layoutIdCall;
    private TextView textviewIconCall;
    private TextView textviewCall;
    private LinearLayout layoutIdShare;
    private TextView textviewShare;
    private TextView textShare;
    private CustomerShippingAddress shippingAddresses = new CustomerShippingAddress();
    private List<ContactDetail> contactDetailList = new ArrayList<ContactDetail>();
    private List<CustomerShippingAddress> customerAddress = new ArrayList<CustomerShippingAddress>();
    private CustomerAddressAdapter customerAddressAdapter;
    private TextView textviewContact;
    private RelativeLayout layoutContact;
    private RecyclerView recyclerViewContact;
    private TextView textviewCustomerInfoContactEmpty;
    private RecyclerView recyclerView;
    private CustomerContactDetailAdapter customerContactDetailAdapter;
    private LinearLayout genderLayout;
    private TextView textviewGender;
    private TextView textviewGenderValue;
    private CoordinatorLayout coordinatorLayoutMain;
    private LinearLayout websiteLayout;
    private TextView textviewWebsite;
    private TextView textviewWebsiteValue;
    private String sharingStr = "";
    private LinearLayout referalNameLayout;
    private TextView textviewReferelName;
    private TextView textviewValueReferelName;
    private LinearLayout contactPersonLayout;
    private TextView textviewContactPerson;
    private TextView textviewValueContactPerson;

    public static CustomerDetailTabFragment newInstance(String title) {
        CustomerDetailTabFragment f = new CustomerDetailTabFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onAttach(Context con) {
        super.onAttach(con);
        try {
            mMobileCallback = (PassMobileListener) con;
            mInfoCallBack = (PassSharingTextListener) con;
            mEmailCallBack = (PassUsersEmailListener) con;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        mMobileCallback = null;
        mInfoCallBack = null;
        mEmailCallBack = null;
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        cuId = bundle.getString(getString(R.string.cuid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_info, container, false);
        intializeView(rootView);
        //Address adapter
        customerAddressAdapter = new CustomerAddressAdapter(getActivity(), customerAddress);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(customerAddressAdapter);
        //Contacts adapter
        customerContactDetailAdapter = new CustomerContactDetailAdapter(getActivity(), contactDetailList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewContact.setLayoutManager(layoutManager);
        recyclerViewContact.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewContact.setAdapter(customerContactDetailAdapter);
        //Smooth Scroll
        recyclerViewContact.setNestedScrollingEnabled(false);
        recyclerView.setNestedScrollingEnabled(false);
        coordinatorLayoutMain.setVisibility(View.GONE);

        //Call API
        doRefresh();

        return rootView;
    }

    public void doRefresh() {
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getCustomerInfo(cuId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void SetDataToCustomerInfo() {

        for (int i = 0; i < customerInformation.size(); i++) {
            if (customerInformation.get(i).getFname() != null && !customerInformation.get(i).getFname().isEmpty()
                    && !customerInformation.get(i).getFname().equals("null") && !customerInformation.get(i).getFname().equals(" ")) {
                customerName = customerInformation.get(i).getFname() + " ";
            }
            if (customerInformation.get(i).getLname() != null && !customerInformation.get(i).getLname().isEmpty()
                    && !customerInformation.get(i).getLname().equals("null") && !customerInformation.get(i).getLname().equals(" ")) {
                customerName = customerName + customerInformation.get(i).getLname();
            }
            companyName = customerInformation.get(i).getCompanyName();
            customerEmail = customerInformation.get(i).getEmail();
            customerMobile = customerInformation.get(i).getMobile();
            customerTelephone = customerInformation.get(i).getTelephone();
            customerGstReg = customerInformation.get(i).getGstRegistered();
            customerReferrelType = customerInformation.get(i).getReferenceType();
            customerReferrelName = customerInformation.get(i).getReferenceName();
            customerReferrelContactPerson = customerInformation.get(i).getReferenceContactPerson();
            customerGstinNo = customerInformation.get(i).getGstin();
            customerPan = customerInformation.get(i).getPan();
            customerTin = customerInformation.get(i).getTin();
            customerCst = customerInformation.get(i).getCst();
            genderId = customerInformation.get(i).getGenderid();
            website = customerInformation.get(i).getWebsite_name();

            //Pass Mobile
            mMobileCallback.passMobileOnLoad(customerMobile);

            //Pass Email
            mEmailCallBack.passUsersEmailOnLoad(customerEmail);

        }

        customerAddressAdapter = new CustomerAddressAdapter(getActivity(), customerAddress);
        recyclerView.setAdapter(customerAddressAdapter);

        if (genderId != null && !genderId.isEmpty()) {
            textviewGender.setText("Gender :");
            if (genderId.equals("1")) {
                textviewGenderValue.setText("Male");
            } else {
                textviewGenderValue.setText("Female");
            }
            genderLayout.setVisibility(View.VISIBLE);
        } else {
            genderLayout.setVisibility(View.GONE);
        }

        if (copiedText != null) {
            copiedText = "";
        }
        if (customerName != null && !customerName.isEmpty() && !customerName.equals("null") && !customerName.equals(" ")) {
            textViewValueCustomerName.setText(customerName.toString().trim());
            copiedText = getString(R.string.customer_name) + " " + customerName.toString().trim() + "\n";
            customerNameLayout.setVisibility(View.VISIBLE);
        } else if (companyName != null && !companyName.isEmpty() && !companyName.equals("null") && !companyName.equals("")) {
            textViewValueCustomerName.setText(companyName.toString().trim());
            copiedText = copiedText + getString(R.string.customer_name) + " " + companyName.toString().trim() + "\n";
            customerNameLayout.setVisibility(View.VISIBLE);
        } else {
            customerNameLayout.setVisibility(View.GONE);
        }

        if (customerEmail != null && !customerEmail.isEmpty() && !customerEmail.equals("null")) {
            textviewValueEmail.setText(customerEmail.toString().trim());
            copiedText = copiedText + getString(R.string.customer_email) + " " + customerEmail.toString().trim() + "\n";
            emailLayout.setVisibility(View.VISIBLE);

        } else {
            emailLayout.setVisibility(View.GONE);
        }
        if (customerMobile != null && !customerMobile.isEmpty() && !customerMobile.equals("null")) {
            textviewMobileValue.setText(customerMobile);
            copiedText = copiedText + getString(R.string.customer_mobile) + " " + customerMobile + "\n";
            mobileLayout.setVisibility(View.VISIBLE);

        } else {
            mobileLayout.setVisibility(View.GONE);
        }
        if (customerTelephone != null && !customerTelephone.isEmpty() && !customerTelephone.equals("null")) {
            textviewValueTelephone.setText(customerTelephone.toString().trim());
            copiedText = copiedText + getString(R.string.telephone) + " " + customerTelephone.toString().trim() + "\n";
            telephoneLayout.setVisibility(View.VISIBLE);
        } else {
            telephoneLayout.setVisibility(View.GONE);
        }

        if (customerGstReg != null && !customerGstReg.isEmpty() && !customerGstReg.equals("null")) {
            textviewValueGstReg.setText(customerGstReg.toString().trim());
            //  copiedText = copiedText + getString(R.string.gst_reg) + " " + customerGstReg.toString().trim() + "\n";
            gstRegLayout.setVisibility(View.VISIBLE);

        } else {
            gstRegLayout.setVisibility(View.GONE);
        }

        if (customerReferrelType != null && !customerReferrelType.isEmpty() && !customerReferrelType.equals("null")) {
            textviewValueReferelType.setText(customerReferrelType.toString().trim());
            // copiedText = copiedText + getString(R.string.refferal_type) + " " + customerReferrelType.toString().trim() + "\n";
            referalTypeLayout.setVisibility(View.VISIBLE);
        } else {
            referalTypeLayout.setVisibility(View.GONE);
        }

        if (customerReferrelName != null && !customerReferrelName.isEmpty() && !customerReferrelName.equals("null")) {
            textviewValueReferelName.setText(customerReferrelName.toString().trim());
            //  copiedText = copiedText + getString(R.string.refferal_name) + " " + customerReferrelName.toString().trim() + "\n";
            referalNameLayout.setVisibility(View.VISIBLE);
        } else {
            referalNameLayout.setVisibility(View.GONE);
        }

        if (customerReferrelContactPerson != null && !customerReferrelContactPerson.isEmpty() && !customerReferrelContactPerson.equals("null")) {
            textviewValueContactPerson.setText(customerReferrelContactPerson.toString().trim());
            //   copiedText = copiedText + getString(R.string.contact_person) + " " + customerReferrelContactPerson.toString().trim() + "\n";
            contactPersonLayout.setVisibility(View.VISIBLE);
        } else {
            contactPersonLayout.setVisibility(View.GONE);
        }

        if (customerGstinNo != null && !customerGstinNo.isEmpty() && !customerGstinNo.equals("null")) {
            textviewGstinValue.setText(customerGstinNo.toString().trim());
            //  copiedText = copiedText + getString(R.string.gstin) + " " + customerGstinNo.toString().trim() + "\n";
            gstInLayout.setVisibility(View.VISIBLE);
        } else {
            gstInLayout.setVisibility(View.GONE);
        }


        if (customerPan != null && !customerPan.isEmpty() && !customerPan.equals("null")) {
            textviewPanValue.setText(customerPan.toString().trim());
            //   copiedText = copiedText + getString(R.string.pan) + " " + customerPan.toString().trim() + "\n";
            panLayout.setVisibility(View.VISIBLE);
        } else {
            panLayout.setVisibility(View.GONE);
        }

        if (customerTin != null && !customerTin.isEmpty() && !customerTin.equals("null")) {
            textviewTinValue.setText(customerTin.toString().trim());
            //   copiedText = copiedText + getString(R.string.gst_tin) + " " + customerTin.toString().trim() + "\n";
            tinLayout.setVisibility(View.VISIBLE);
        } else {
            tinLayout.setVisibility(View.GONE);
        }

        if (customerCst != null && !customerCst.isEmpty() && !customerCst.equals("null")) {
            textviewCstValue.setText(customerCst.toString().trim());
            cstLayout.setVisibility(View.VISIBLE);
            //   copiedText = copiedText + getString(R.string.cst) + " " + customerCst.toString().trim() + "\n";
        } else {
            cstLayout.setVisibility(View.GONE);
        }

        if (website != null && !website.isEmpty() && !website.equals("null")) {
            textviewWebsiteValue.setText(website.toString().trim());
            websiteLayout.setVisibility(View.VISIBLE);
            //   copiedText = copiedText + getString(R.string.website) + " " + website.toString().trim() + "\n";
        } else {
            websiteLayout.setVisibility(View.GONE);
        }

        sharingStr = copiedText;

        //Pass Info
        mInfoCallBack.passSharingTextOnLoad(sharingStr);

    }

    private void intializeView(final View rootView) {
        coordinatorLayoutMain = (CoordinatorLayout) rootView.findViewById(R.id.customer_info_info_coordinator_layout);
        layoutCustomerInfo = (LinearLayout) rootView.findViewById(R.id.layout_customer_info);
        textViewPersonalDetails = (TextView) rootView.findViewById(R.id.textView_personal_details);
        customerNameLayout = (LinearLayout) rootView.findViewById(R.id.customer_name_layout);
        textViewCustomerName = (TextView) rootView.findViewById(R.id.customer_name_textView);
        textViewValueCustomerName = (TextView) rootView.findViewById(R.id.customer_name_value_textView);
        genderLayout = (LinearLayout) rootView.findViewById(R.id.gender_layout);
        textviewGender = (TextView) rootView.findViewById(R.id.textview_gender);
        textviewGenderValue = (TextView) rootView.findViewById(R.id.textview_gender_value);
        mobileLayout = (LinearLayout) rootView.findViewById(R.id.mobile_layout);
        textviewMobile = (TextView) rootView.findViewById(R.id.textview_mobile);
        textviewMobileValue = (TextView) rootView.findViewById(R.id.textview_mobile_value);
        telephoneLayout = (LinearLayout) rootView.findViewById(R.id.telephone_layout);
        textviewTelephone = (TextView) rootView.findViewById(R.id.textview_telephone);
        textviewValueTelephone = (TextView) rootView.findViewById(R.id.textview_value_telephone);
        emailLayout = (LinearLayout) rootView.findViewById(R.id.email_layout);
        textviewEmail = (TextView) rootView.findViewById(R.id.textview_email);
        textviewValueEmail = (TextView) rootView.findViewById(R.id.textview_value_email);
        gstRegLayout = (LinearLayout) rootView.findViewById(R.id.gst_reg_layout);
        textviewGstReg = (TextView) rootView.findViewById(R.id.textview_gst_reg);
        textviewValueGstReg = (TextView) rootView.findViewById(R.id.textview_value_gst_reg);
        referalTypeLayout = (LinearLayout) rootView.findViewById(R.id.referal_type_layout);
        textviewReferelType = (TextView) rootView.findViewById(R.id.textview_referel_type);
        textviewValueReferelType = (TextView) rootView.findViewById(R.id.textview_value_referel_type);
        gstInLayout = (LinearLayout) rootView.findViewById(R.id.gst_in_layout);
        textviewGstin = (TextView) rootView.findViewById(R.id.textview_gstin);
        textviewGstinValue = (TextView) rootView.findViewById(R.id.textview_gstin_value);
        panLayout = (LinearLayout) rootView.findViewById(R.id.pan_layout);
        textviewPan = (TextView) rootView.findViewById(R.id.textview_pan);
        textviewPanValue = (TextView) rootView.findViewById(R.id.textview_pan_value);
        tinLayout = (LinearLayout) rootView.findViewById(R.id.tin_layout);
        textviewTin = (TextView) rootView.findViewById(R.id.textview_tin);
        textviewTinValue = (TextView) rootView.findViewById(R.id.textview_tin_value);
        cstLayout = (LinearLayout) rootView.findViewById(R.id.cst_layout);
        textviewCst = (TextView) rootView.findViewById(R.id.textview_cst);
        textviewCstValue = (TextView) rootView.findViewById(R.id.textview_cst_value);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_recent_address);
        textviewRecentAddress = (TextView) rootView.findViewById(R.id.textview_recent_address);
        layoutAddress = (RelativeLayout) rootView.findViewById(R.id.layout_address);
        textviewCustomerInfoAddressEmpty = (TextView) rootView.findViewById(R.id.textview_customer_info_address_empty);
        textviewContact = (TextView) rootView.findViewById(R.id.textview_contact);
        layoutContact = (RelativeLayout) rootView.findViewById(R.id.layout_contact);
        recyclerViewContact = (RecyclerView) rootView.findViewById(R.id.recycler_view_contact);
        textviewCustomerInfoContactEmpty = (TextView) rootView.findViewById(R.id.textview_customer_info_contact_empty);
        websiteLayout = (LinearLayout) rootView.findViewById(R.id.website_layout);
        textviewWebsite = (TextView) rootView.findViewById(R.id.textview_website);
        textviewWebsiteValue = (TextView) rootView.findViewById(R.id.textview_website_value);
        referalNameLayout = (LinearLayout) rootView.findViewById(R.id.referal_name_layout);
        textviewReferelName = (TextView) rootView.findViewById(R.id.textview_referel_name);
        textviewValueReferelName = (TextView) rootView.findViewById(R.id.textview_value_referel_name);
        contactPersonLayout = (LinearLayout) rootView.findViewById(R.id.contact_person_layout);
        textviewContactPerson = (TextView) rootView.findViewById(R.id.textview_contact_person);
        textviewValueContactPerson = (TextView) rootView.findViewById(R.id.textview_value_contact_person);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        recyclerViewContact.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    private void getCustomerInfo(String cuid) {
        if (getActivity() != null && isAdded()) {
            task = getString(R.string.customer_information);
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
//        progressBar.setVisibility(View.VISIBLE);
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call call = apiService.getCustomerInfo(version, key, task, userId, accessToken, cuid);
            Log.v("Request", String.valueOf(call));
            Log.v("url", String.valueOf(call.request().url()));

            //ClearArrayList
            if (customerAddress != null && customerAddress.size() > 0) {
                customerAddress.clear();
            }
            if (contactDetailList != null && contactDetailList.size() > 0) {
                contactDetailList.clear();
            }

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
//                    Toast.makeText(getActivity(), getString(R.string.customer_data_fetched), Toast.LENGTH_SHORT).show();

                        for (final Customer customer : apiResponse.getData().getCustomer()) {

                            if (apiResponse.getData().getCustomer() != null) {
                                customerInformation.add(customer);
                                customerAddress.addAll(customer.getCustomerShippingAddresses());
                                contactDetailList.addAll(customer.getContactDetails());
                            }
                        }

                        if (!customerAddress.isEmpty() && !customerAddress.equals("null")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            textviewCustomerInfoAddressEmpty.setVisibility(View.GONE);
                            customerAddressAdapter.notifyDataSetChanged();
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            textviewCustomerInfoAddressEmpty.setVisibility(View.VISIBLE);
                            textviewCustomerInfoAddressEmpty.setText("No address found");
                        }

                        if (!contactDetailList.isEmpty() && !contactDetailList.equals("null")) {
                            recyclerViewContact.setVisibility(View.VISIBLE);
                            textviewCustomerInfoContactEmpty.setVisibility(View.GONE);
                            customerContactDetailAdapter.notifyDataSetChanged();
                        } else {
                            recyclerViewContact.setVisibility(View.GONE);
                            textviewCustomerInfoContactEmpty.setVisibility(View.VISIBLE);
                            textviewCustomerInfoContactEmpty.setText("No contact");
                        }
                        if (isAdded() && getActivity() != null) {
                            SetDataToCustomerInfo();
                        }
                        Log.d("size", String.valueOf(customerInformation.size()));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                coordinatorLayoutMain.setVisibility(View.VISIBLE);
                            }
                        }, 100);
                    }  //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        coordinatorLayoutMain.setVisibility(View.VISIBLE);
                    }
                }

            });
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void customerMobile(String mobile) {

    }

}
