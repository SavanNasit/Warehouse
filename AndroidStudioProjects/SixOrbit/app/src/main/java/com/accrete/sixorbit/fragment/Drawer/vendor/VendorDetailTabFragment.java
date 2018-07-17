package com.accrete.sixorbit.fragment.Drawer.vendor;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CustomerContactDetailAdapter;
import com.accrete.sixorbit.adapter.VendorShippingAddressAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.ContactDetail;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.WordUtils;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.BankDetail;
import com.accrete.sixorbit.model.Vendor;
import com.accrete.sixorbit.model.VendorShippingAddress;
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

public class VendorDetailTabFragment extends Fragment {
    PassMobileListener mMobileCallback;
    PassUsersEmailListener mEmailCallBack;
    PassSharingTextListener usersDetailListener;
    private String venId, vendorName, vendorMobile, vendorTelephone, vendorEmail, vendorGstRegistered, vendorContactPerson,
            vendorGstIn, vendorPan, vendorTin, venderExciseDuty, vendorDescription, accountNo, accountHolder, bankName,
            branchName, address, ifscCode, accountType, status, paymentTerms;
    private String sharingStr = "";
    private LinearLayout layoutVendorInfo;
    private TextView textViewPersonalDetails;
    private LinearLayout vendorNameLayout;
    private TextView vendorNameTextView;
    private TextView vendorNameValueTextView;
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
    private LinearLayout contactPersonLayout;
    private TextView textviewContactPerson;
    private TextView textviewValueContactPerson;
    private LinearLayout gstInLayout;
    private TextView textviewGstin;
    private TextView textviewGstinValue;
    private LinearLayout panLayout;
    private TextView textviewPan;
    private TextView textviewPanValue;
    private LinearLayout tinLayout;
    private TextView textviewTin;
    private TextView textviewTinValue;
    private LinearLayout exciseDutyLayout;
    private TextView textviewExciseDuty;
    private TextView textviewValueExciseDuty;
    private LinearLayout descriptionLayout;
    private TextView textviewDescription;
    private TextView textviewValueDescription;
    private List<Vendor> vendorList = new ArrayList<Vendor>();
    private List<BankDetail> bankDetailList = new ArrayList<BankDetail>();
    private TextView textviewRecentAddress;
    private RelativeLayout layoutAddress;
    private RecyclerView recyclerViewRecentAddress;
    private TextView textviewVendorInfoAddressEmpty;
    private VendorShippingAddressAdapter adapter;
    private List<VendorShippingAddress> vendorShippingAddresses = new ArrayList<VendorShippingAddress>();
    private RecyclerView recyclerViewContact;
    private TextView textviewCustomerInfoContactEmpty;
    private CustomerContactDetailAdapter customerContactDetailAdapter;
    private List<ContactDetail> contactDetailList = new ArrayList<ContactDetail>();
    private TextView textviewContact;
    private RelativeLayout layoutContact;
    private TextView textviewBankDetailsTitle;
    private LinearLayout accountNumberLayout;
    private TextView accountNumberTextView;
    private TextView accountNumberValueTextView;
    private LinearLayout accountHolderLayout;
    private TextView accountHolderTextView;
    private TextView accountHolderValueTextView;
    private LinearLayout bankNameLayout;
    private TextView bankNameTextView;
    private TextView bankNameValueTextView;
    private LinearLayout branchNameLayout;
    private TextView branchNameTextView;
    private TextView branchNameValueTextView;
    private LinearLayout addressLayout;
    private TextView addressTextView;
    private TextView addressValueTextView;
    private LinearLayout ifscCodeLayout;
    private TextView ifscCodeTextView;
    private TextView ifscCodeValueTextView;
    private LinearLayout accountTypeLayout, bankDetailsLayout;
    private TextView accountTypeTextView;
    private TextView accountTypeValueTextView;
    private LinearLayout paymentTermsLayout;
    private TextView textviewPaymentTerms;
    private TextView textviewValuePaymentTerms;
    private LinearLayout statusLayout;
    private TextView textviewStatus;
    private TextView textviewValueStatus;
    private CoordinatorLayout coordinatorLayoutMain;

    @Override
    public void onAttach(Context con) {
        super.onAttach(con);
        try {
            mMobileCallback = (PassMobileListener) con;
            usersDetailListener = (PassSharingTextListener) con;
            mEmailCallBack = (PassUsersEmailListener) con;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetach() {
        mMobileCallback = null;
        usersDetailListener = null;
        mEmailCallBack = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendor_details, container, false);
        initializeView(rootView);
        coordinatorLayoutMain.setVisibility(View.GONE);
        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getVendorInfo(venId);
                    }
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        venId = bundle.getString(getString(R.string.venid));
    }

    private void initializeView(final View rootView) {
        coordinatorLayoutMain = (CoordinatorLayout) rootView.findViewById(R.id.vendor_info_info_coordinator_layout);
        layoutVendorInfo = (LinearLayout) rootView.findViewById(R.id.layout_vendor_info);
        textViewPersonalDetails = (TextView) rootView.findViewById(R.id.textView_personal_details);
        vendorNameLayout = (LinearLayout) rootView.findViewById(R.id.vendor_name_layout);
        vendorNameTextView = (TextView) rootView.findViewById(R.id.vendor_name_textView);
        vendorNameValueTextView = (TextView) rootView.findViewById(R.id.vendor_name_value_textView);
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
        contactPersonLayout = (LinearLayout) rootView.findViewById(R.id.contact_person_layout);
        textviewContactPerson = (TextView) rootView.findViewById(R.id.textview_contact_person);
        textviewValueContactPerson = (TextView) rootView.findViewById(R.id.textview_value_contact_person);
        gstInLayout = (LinearLayout) rootView.findViewById(R.id.gst_in_layout);
        textviewGstin = (TextView) rootView.findViewById(R.id.textview_gstin);
        textviewGstinValue = (TextView) rootView.findViewById(R.id.textview_gstin_value);
        panLayout = (LinearLayout) rootView.findViewById(R.id.pan_layout);
        textviewPan = (TextView) rootView.findViewById(R.id.textview_pan);
        textviewPanValue = (TextView) rootView.findViewById(R.id.textview_pan_value);
        tinLayout = (LinearLayout) rootView.findViewById(R.id.tin_layout);
        textviewTin = (TextView) rootView.findViewById(R.id.textview_tin);
        textviewTinValue = (TextView) rootView.findViewById(R.id.textview_tin_value);
        exciseDutyLayout = (LinearLayout) rootView.findViewById(R.id.excise_duty_layout);
        textviewExciseDuty = (TextView) rootView.findViewById(R.id.textview_excise_duty);
        textviewValueExciseDuty = (TextView) rootView.findViewById(R.id.textview_value_excise_duty);
        descriptionLayout = (LinearLayout) rootView.findViewById(R.id.description_layout);
        textviewDescription = (TextView) rootView.findViewById(R.id.textview_description);
        textviewValueDescription = (TextView) rootView.findViewById(R.id.textview_value_description);
        textviewRecentAddress = (TextView) rootView.findViewById(R.id.textview_recent_address);
        layoutAddress = (RelativeLayout) rootView.findViewById(R.id.layout_address);
        recyclerViewRecentAddress = (RecyclerView) rootView.findViewById(R.id.recycler_view_recent_address);
        textviewVendorInfoAddressEmpty = (TextView) rootView.findViewById(R.id.textview_vendor_info_address_empty);
        textviewContact = (TextView) rootView.findViewById(R.id.textview_contact);
        layoutContact = (RelativeLayout) rootView.findViewById(R.id.layout_contact);
        recyclerViewContact = (RecyclerView) rootView.findViewById(R.id.recycler_view_contact);
        textviewCustomerInfoContactEmpty = (TextView) rootView.findViewById(R.id.textview_customer_info_contact_empty);
        textviewBankDetailsTitle = (TextView) rootView.findViewById(R.id.textview_bankDetails_title);
        accountNumberLayout = (LinearLayout) rootView.findViewById(R.id.account_number_layout);
        accountNumberTextView = (TextView) rootView.findViewById(R.id.account_number_textView);
        accountNumberValueTextView = (TextView) rootView.findViewById(R.id.account_number_value_textView);
        accountHolderLayout = (LinearLayout) rootView.findViewById(R.id.account_holder_layout);
        accountHolderTextView = (TextView) rootView.findViewById(R.id.account_holder_textView);
        accountHolderValueTextView = (TextView) rootView.findViewById(R.id.account_holder_value_textView);
        bankNameLayout = (LinearLayout) rootView.findViewById(R.id.bank_name_layout);
        bankNameTextView = (TextView) rootView.findViewById(R.id.bank_name_textView);
        bankNameValueTextView = (TextView) rootView.findViewById(R.id.bank_name_value_textView);
        branchNameLayout = (LinearLayout) rootView.findViewById(R.id.branch_name_layout);
        branchNameTextView = (TextView) rootView.findViewById(R.id.branch_name_textView);
        branchNameValueTextView = (TextView) rootView.findViewById(R.id.branch_name_value_textView);
        addressLayout = (LinearLayout) rootView.findViewById(R.id.address_layout);
        addressTextView = (TextView) rootView.findViewById(R.id.address_textView);
        addressValueTextView = (TextView) rootView.findViewById(R.id.address_value_textView);
        ifscCodeLayout = (LinearLayout) rootView.findViewById(R.id.ifsc_code_layout);
        ifscCodeTextView = (TextView) rootView.findViewById(R.id.ifsc_code_textView);
        ifscCodeValueTextView = (TextView) rootView.findViewById(R.id.ifsc_code_value_textView);
        accountTypeLayout = (LinearLayout) rootView.findViewById(R.id.account_type_layout);
        accountTypeTextView = (TextView) rootView.findViewById(R.id.account_type_textView);
        accountTypeValueTextView = (TextView) rootView.findViewById(R.id.account_type_value_textView);
        bankDetailsLayout = (LinearLayout) rootView.findViewById(R.id.bank_details_layout);
        paymentTermsLayout = (LinearLayout) rootView.findViewById(R.id.payment_terms_layout);
        textviewPaymentTerms = (TextView) rootView.findViewById(R.id.textview_payment_terms);
        textviewValuePaymentTerms = (TextView) rootView.findViewById(R.id.textview_value_payment_terms);
        statusLayout = (LinearLayout) rootView.findViewById(R.id.status_layout);
        textviewStatus = (TextView) rootView.findViewById(R.id.textview_status);
        textviewValueStatus = (TextView) rootView.findViewById(R.id.textview_value_status);

        recyclerViewRecentAddress.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
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

        //Smooth Scroll
        recyclerViewContact.setNestedScrollingEnabled(false);
        recyclerViewRecentAddress.setNestedScrollingEnabled(false);

        adapter = new VendorShippingAddressAdapter(getActivity(), vendorShippingAddresses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRecentAddress.setLayoutManager(mLayoutManager);
        recyclerViewRecentAddress.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewRecentAddress.setAdapter(adapter);

        //Contacts adapter
        customerContactDetailAdapter = new CustomerContactDetailAdapter(getActivity(), contactDetailList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewContact.setLayoutManager(layoutManager);
        recyclerViewContact.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewContact.setAdapter(customerContactDetailAdapter);

        //Main Layout is not visible till API doesn't get called
        layoutVendorInfo.setVisibility(View.GONE);
    }

    private void getVendorInfo(String venId) {
        task = getString(R.string.vendor_information);
        userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getVendorInfo(version, key, task, userId, accessToken, venId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        //ClearArrayList
        if (vendorShippingAddresses != null && vendorShippingAddresses.size() > 0) {
            vendorShippingAddresses.clear();
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
                    for (final Vendor vendor : apiResponse.getData().getVendor()) {
                        if (apiResponse.getData().getVendor() != null) {
                            vendorList.add(vendor);
                            if (isAdded() && getActivity() != null) {
                                setData(vendorList);
                            }
                            vendorShippingAddresses.addAll(vendor.getVendorShippingAddress());
                            contactDetailList.addAll(vendor.getContactDetails());
                            for (final BankDetail bankDetail : vendor.getBankDetails()) {
                                if (bankDetail != null) {
                                    bankDetailList.add(bankDetail);
                                    if (isAdded() && getActivity() != null) {
                                        setBankData(bankDetailList);
                                    }
                                }
                            }
                        }
                    }
                    if (vendorShippingAddresses != null && vendorShippingAddresses.size() > 0) {
                        recyclerViewRecentAddress.setVisibility(View.VISIBLE);
                        textviewVendorInfoAddressEmpty.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } else {
                        recyclerViewRecentAddress.setVisibility(View.GONE);
                        textviewVendorInfoAddressEmpty.setVisibility(View.VISIBLE);
                        textviewVendorInfoAddressEmpty.setText("No address found");
                    }
                    if (!contactDetailList.isEmpty() && !contactDetailList.equals("null")) {
                        recyclerViewContact.setVisibility(View.VISIBLE);
                        textviewCustomerInfoContactEmpty.setVisibility(View.GONE);
                        customerContactDetailAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewContact.setVisibility(View.GONE);
                        textviewCustomerInfoContactEmpty.setVisibility(View.VISIBLE);
                        textviewCustomerInfoContactEmpty.setText("No contact.");
                    }
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                } else {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        coordinatorLayoutMain.setVisibility(View.VISIBLE);
                    }
                }, 100);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
                coordinatorLayoutMain.setVisibility(View.VISIBLE);

            }

        });

    }

    public void setData(List<Vendor> vendorList) {
        vendorName = vendorList.get(0).getName();
        vendorMobile = vendorList.get(0).getMobile();
        vendorTelephone = vendorList.get(0).getLandline();
        vendorEmail = vendorList.get(0).getEmail();
        vendorGstRegistered = vendorList.get(0).getGstRegistered();
        vendorContactPerson = vendorList.get(0).getContactName();
        vendorGstIn = vendorList.get(0).getGstin();
        vendorPan = vendorList.get(0).getPan();
        vendorTin = vendorList.get(0).getTin();
        venderExciseDuty = vendorList.get(0).getExciseDuty();
        vendorDescription = vendorList.get(0).getDescription();
        paymentTerms = vendorList.get(0).getPaymentTerms();
        status = vendorList.get(0).getStatus();

        //Pass Mobile
        mMobileCallback.passMobileOnLoad(vendorMobile);

        //Pass Email
        mEmailCallBack.passUsersEmailOnLoad(vendorEmail);

        if (sharingStr != null) {
            sharingStr = "";
        }
        //Name
        if (vendorName != null && !vendorName.isEmpty()) {
            vendorNameTextView.setText("Vendor : ");
            vendorNameValueTextView.setText(WordUtils.capitalize(vendorName.toString().trim()));
            vendorNameLayout.setVisibility(View.VISIBLE);

            sharingStr = sharingStr + "Vendor : " + WordUtils.capitalize(vendorName.toString().trim()) + "\n";

        } else {
            vendorNameLayout.setVisibility(View.GONE);
        }

        //Mobile
        if (vendorMobile != null && !vendorMobile.isEmpty()) {
            textviewMobile.setText("Mobile : ");
            textviewMobileValue.setText(vendorMobile.toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);

            sharingStr = sharingStr + "Mobile : " + vendorMobile.toString().trim() + "\n";

        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        //Landline
        if (vendorTelephone != null && !vendorTelephone.isEmpty()) {
            textviewTelephone.setText("Landline : ");
            textviewValueTelephone.setText(vendorTelephone.toString().trim());
            telephoneLayout.setVisibility(View.VISIBLE);

            sharingStr = sharingStr + "Landline : " + vendorTelephone.toString().trim() + "\n";

        } else {
            telephoneLayout.setVisibility(View.GONE);
        }

        //Email
        if (vendorEmail != null && !vendorEmail.isEmpty()) {
            textviewEmail.setText("Email : ");
            textviewValueEmail.setText(vendorEmail.toString().trim());
            emailLayout.setVisibility(View.VISIBLE);

            sharingStr = sharingStr + "Email : " + vendorEmail.toString().trim() + "\n";

        } else {
            emailLayout.setVisibility(View.GONE);
        }

        //Gst Registered
        if (vendorGstRegistered != null && !vendorGstRegistered.isEmpty()) {
            textviewGstReg.setText("GST Registered : ");
            textviewValueGstReg.setText(vendorGstRegistered.toString().trim());
            gstRegLayout.setVisibility(View.VISIBLE);

            //  sharingStr.append("GST Registered : " + vendorGstRegistered.toString().trim() + "\n");

        } else {
            gstRegLayout.setVisibility(View.GONE);
        }

        //Contact Person
        if (vendorContactPerson != null && !vendorContactPerson.isEmpty()) {
            textviewContactPerson.setText("Contact Name : ");
            textviewValueContactPerson.setText(WordUtils.capitalize(vendorContactPerson.toString().trim()));
            contactPersonLayout.setVisibility(View.VISIBLE);

            // sharingStr.append("Email : " + vendorEmail.toString().trim() + "\n");

        } else {
            contactPersonLayout.setVisibility(View.GONE);
        }

        //GST In
        if (vendorGstIn != null && !vendorGstIn.isEmpty()) {
            textviewGstin.setText("GSTIN : ");
            textviewGstinValue.setText(vendorGstIn.toString().trim());
            gstInLayout.setVisibility(View.VISIBLE);

            // sharingStr.append("GSTIN : " + vendorGstIn.toString().trim() + "\n");

        } else {
            gstInLayout.setVisibility(View.GONE);
        }

        //PAN
        if (vendorPan != null && !vendorPan.isEmpty()) {
            textviewPan.setText("Vendor's PAN : ");
            textviewPanValue.setText(vendorPan.toString().trim());
            panLayout.setVisibility(View.VISIBLE);

            // sharingStr.append("PAN : " + vendorPan.toString().trim() + "\n");

        } else {
            panLayout.setVisibility(View.GONE);
        }

        //TIN
        if (vendorTin != null && !vendorTin.isEmpty()) {
            textviewTin.setText("TIN : ");
            textviewTinValue.setText(vendorTin.toString().trim());
            tinLayout.setVisibility(View.VISIBLE);

            //   sharingStr.append("TIN : " + vendorTin.toString().trim() + "\n");

        } else {
            tinLayout.setVisibility(View.GONE);
        }

        //Excise Duty
        if (venderExciseDuty != null && !venderExciseDuty.isEmpty()) {
            textviewExciseDuty.setText("Excise duty : ");
            textviewValueExciseDuty.setText(venderExciseDuty.toString().trim());
            exciseDutyLayout.setVisibility(View.VISIBLE);

            //  sharingStr.append("Excise duty : " + venderExciseDuty.toString().trim() + "\n");

        } else {
            exciseDutyLayout.setVisibility(View.GONE);
        }

        //Description
        if (vendorDescription != null && !vendorDescription.isEmpty()) {
            textviewDescription.setText("Description : ");
            textviewValueDescription.setText(vendorDescription.toString().trim());
            descriptionLayout.setVisibility(View.VISIBLE);

            //  sharingStr.append("Description : " + vendorDescription.toString().trim() + "\n");

        } else {
            descriptionLayout.setVisibility(View.GONE);
        }

        //Payment Terms
        if (paymentTerms != null && !paymentTerms.isEmpty()) {
            textviewPaymentTerms.setText("Payment Terms : ");
            textviewValuePaymentTerms.setText(paymentTerms.toString().trim());
            paymentTermsLayout.setVisibility(View.VISIBLE);

            //  sharingStr.append("Payment Terms : " + paymentTerms.toString().trim() + "\n");

        } else {
            paymentTermsLayout.setVisibility(View.GONE);
        }

        //status
        if (status != null && !status.isEmpty()) {
            textviewStatus.setText("Status : ");
            if (status.equals("1")) {
                textviewValueStatus.setText("Active");
                textviewValueStatus.setTextColor(getResources().getColor(R.color.green));

                //     sharingStr.append("Status : " + "Active" + "\n");

            } else {
                textviewValueStatus.setText("In-active");
                textviewValueStatus.setTextColor(getResources().getColor(R.color.lightRed));

                //     sharingStr.append("Status : " + "In-active" + "\n");
            }
            statusLayout.setVisibility(View.VISIBLE);
        } else {
            statusLayout.setVisibility(View.GONE);
        }

        layoutVendorInfo.setVisibility(View.VISIBLE);

        //Pass Users
        usersDetailListener.passSharingTextOnLoad(sharingStr);

    }

    public void setBankData(List<BankDetail> bankDetailList) {
        accountNo = bankDetailList.get(0).getAccountNo();
        accountHolder = bankDetailList.get(0).getAccountHolder();
        bankName = bankDetailList.get(0).getBankName();
        branchName = bankDetailList.get(0).getBranchName();
        address = bankDetailList.get(0).getAddress();
        ifscCode = bankDetailList.get(0).getIfscCode();
        accountType = bankDetailList.get(0).getType();

        //Account Number
        if (accountNo != null && !accountNo.isEmpty()) {
            accountNumberValueTextView.setText(accountNo.toString().trim());
            accountNumberLayout.setVisibility(View.VISIBLE);
        } else {
            accountNumberLayout.setVisibility(View.GONE);
        }

        //Account Holder
        if (accountHolder != null && !accountHolder.isEmpty()) {
            accountHolderValueTextView.setText(accountHolder.toString().trim());
            accountHolderLayout.setVisibility(View.VISIBLE);
        } else {
            accountHolderLayout.setVisibility(View.GONE);
        }

        //Bank Name
        if (bankName != null && !bankName.isEmpty()) {
            bankNameValueTextView.setText(bankName.toString().trim());
            bankNameLayout.setVisibility(View.VISIBLE);
        } else {
            bankNameLayout.setVisibility(View.GONE);
        }

        //Branch Name
        if (branchName != null && !branchName.isEmpty()) {
            branchNameValueTextView.setText(branchName.toString().trim());
            branchNameLayout.setVisibility(View.VISIBLE);
        } else {
            branchNameLayout.setVisibility(View.GONE);
        }

        //Address
        if (address != null && !address.isEmpty()) {
            addressValueTextView.setText(address.toString().trim());
            addressLayout.setVisibility(View.VISIBLE);
        } else {
            addressLayout.setVisibility(View.GONE);
        }

        //IFSC Code
        if (ifscCode != null && !ifscCode.isEmpty()) {
            ifscCodeValueTextView.setText(ifscCode.toString().trim());
            ifscCodeLayout.setVisibility(View.VISIBLE);
        } else {
            ifscCodeLayout.setVisibility(View.GONE);
        }

        //Account Type
        if (accountType != null && !accountType.isEmpty()) {
            accountTypeValueTextView.setText(accountType.toString().trim());
            accountTypeLayout.setVisibility(View.VISIBLE);
        } else {
            accountTypeLayout.setVisibility(View.GONE);
        }

        bankDetailsLayout.setVisibility(View.VISIBLE);
    }
}
