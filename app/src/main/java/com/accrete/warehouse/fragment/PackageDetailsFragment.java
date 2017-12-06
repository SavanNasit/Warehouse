package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackageDetailsAdapter;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.PassDateToCounsellor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class PackageDetailsFragment extends Fragment implements PackageDetailsAdapter.PackageDetailsAdapterListener, PassDateToCounsellor {

    PackageDetailsList packageDetails = new PackageDetailsList();
    private RecyclerView packageDetailsRecyclerView;
    private TextView packageDetailsEmptyView;
    private TextInputEditText packageDetailsName;
    private TextInputEditText packageDetailsMobile;
    private TextInputEditText packageDetailsEmail;
    private TextInputEditText packageDetailsBillingAddress;
    private TextInputEditText packageDetailsDeliveryAddress;
    private TextInputEditText packageDetailsTaxInvoiceSerialNo;
    private TextInputEditText packageDetailsInvoiceDate;
    private AutoCompleteTextView packageDetailsInvoiceType;
    private TextView packageDetailsUploadDoc;
    private PackageDetailsAdapter packageDetailsAdapter;
    private List<PackageDetailsList> packageDetailsList = new ArrayList<>();
    private AllDatePickerFragment datePickerFragment;
    private String stringDateOfInvoice;
    private ArrayList<String> invoiceTypeList = new ArrayList<>();

    public void doRefresh() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_package_details, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {

        packageDetailsName = (TextInputEditText) rootView.findViewById(R.id.package_details_name);
        packageDetailsMobile = (TextInputEditText) rootView.findViewById(R.id.package_details_mobile);
        packageDetailsEmail = (TextInputEditText) rootView.findViewById(R.id.package_details_email);
        packageDetailsBillingAddress = (TextInputEditText) rootView.findViewById(R.id.package_details_billing_address);
        packageDetailsDeliveryAddress = (TextInputEditText) rootView.findViewById(R.id.package_details_delivery_address);
        packageDetailsTaxInvoiceSerialNo = (TextInputEditText) rootView.findViewById(R.id.package_details_tax_invoice_serial_no);
        packageDetailsInvoiceDate = (TextInputEditText) rootView.findViewById(R.id.package_details_invoice_date);
        packageDetailsInvoiceType = (AutoCompleteTextView) rootView.findViewById(R.id.package_details_invoice_type);
        packageDetailsUploadDoc = (TextView) rootView.findViewById(R.id.package_details_upload_doc);
        packageDetailsRecyclerView = (RecyclerView) rootView.findViewById(R.id.package_details_recycler_view);
        packageDetailsEmptyView = (TextView) rootView.findViewById(R.id.package_details_empty_view);

        packageDetailsAdapter = new PackageDetailsAdapter(getActivity(), packageDetailsList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packageDetailsRecyclerView.setLayoutManager(mLayoutManager);
        packageDetailsRecyclerView.setHasFixedSize(true);
        packageDetailsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageDetailsRecyclerView.setNestedScrollingEnabled(false);
        packageDetailsRecyclerView.setAdapter(packageDetailsAdapter);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        packageDetails.setItem("50 Shades of Colour");
        packageDetails.setBatchNumber("1853");
        packageDetails.setQuantity("2 pieces");

        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);
        packageDetailsList.add(packageDetails);

        packageDetailsInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dialog_from));
            }
        });

        invoiceTypeAdapter();
        packageDetailsInvoiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageDetailsInvoiceType.showDropDown();
            }
        });

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }

    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            stringDateOfInvoice = s;
            Date startDate = formatter.parse(stringDateOfInvoice);
            stringDateOfInvoice = targetFormat.format(startDate);
            packageDetailsInvoiceDate.setText(s);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }


    private void invoiceTypeAdapter() {
        invoiceTypeList.add("Retail");
        invoiceTypeList.add("Tax");
        ArrayAdapter arrayAdapterInvoiceType = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, invoiceTypeList);
        arrayAdapterInvoiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packageDetailsInvoiceType.setAdapter(arrayAdapterInvoiceType);
    }

}
