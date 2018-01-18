package com.accrete.warehouse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ShippingBy;
import com.accrete.warehouse.model.ShippingType;

import java.util.ArrayList;
import java.util.List;

import static com.accrete.warehouse.CreateGatepassActivity.createGatepassViewpager;

/**
 * Created by poonam on 12/21/17.
 */

public class GatepassFragment extends Fragment {
    private AutoCompleteTextView dialogCreateGatepassShippingBy;
    private EditText dialogCreateGatepassVehicleNumber;
    private AutoCompleteTextView dialogCreateGatepassShippingType;
    private AutoCompleteTextView dialogCreateGatepassShippingCompany;
    private TextView dialogCreateGatepassBack;

    private List<String> packageIdAddList = new ArrayList<>();
    private List<ShippingType> shippingTypesList = new ArrayList<>();
    private List<ShippingBy> shippingByList = new ArrayList<>();
    private ArrayList<String> arrayListShippingType = new ArrayList<>();
    private ArrayList<String> arrayListShippingBy = new ArrayList<>();
    private ArrayAdapter arrayAdapterShippingType,arrayAdapterShippingBy;
    private String strShippingType;
    private String strShippingBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatepass, container,
                false);

        findViews(rootView);
        return rootView;
    }

    public void setShippingData(List<String> packageIdList, List<ShippingType> shippingTypes, List<ShippingBy> shippingBy) {
        // Toast.makeText(getActivity(), "hello PK", Toast.LENGTH_SHORT).show();
        packageIdAddList = packageIdList;
        shippingTypesList=shippingTypes;
        shippingByList=shippingBy;

    }

    private void findViews(View rootView) {
        dialogCreateGatepassShippingBy = (AutoCompleteTextView)rootView.findViewById(R.id.dialog_create_gatepass_shipping_by);
        dialogCreateGatepassVehicleNumber = (EditText)rootView.findViewById(R.id.dialog_create_gatepass_vehicle_number);
        dialogCreateGatepassShippingType = (AutoCompleteTextView)rootView.findViewById(R.id.dialog_create_gatepass_shipping_type);
        dialogCreateGatepassShippingCompany = (AutoCompleteTextView)rootView.findViewById(R.id.dialog_create_gatepass_shipping_company);
        dialogCreateGatepassBack = (TextView)rootView.findViewById(R.id.dialog_create_gatepass_back);


        for (int i = 0; i < shippingByList.size(); i++) {
            Log.d("called", "happy" + shippingByList.get(i).getName());
            arrayListShippingBy.add(shippingByList.get(i).getName());
        }


        for (int i = 0; i < shippingTypesList.size(); i++) {
            Log.d("called", "happy" + shippingTypesList.get(i).getName());
            arrayListShippingType.add(shippingTypesList.get(i).getName());
        }

        arrayAdapterShippingType = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingType);
        arrayAdapterShippingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCreateGatepassShippingType.setAdapter(arrayAdapterShippingType);
        arrayAdapterShippingType.notifyDataSetChanged();

        arrayAdapterShippingBy = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, arrayListShippingBy);
        arrayAdapterShippingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogCreateGatepassShippingBy.setAdapter(arrayAdapterShippingType);
        arrayAdapterShippingBy.notifyDataSetChanged();

        dialogCreateGatepassShippingType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateGatepassShippingType.showDropDown();
            }
        });

        dialogCreateGatepassShippingBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateGatepassShippingBy.showDropDown();
            }
        });

        dialogCreateGatepassShippingType .setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strShippingType = dialogCreateGatepassShippingType.getText().toString();
                    for (int i = 0; i < arrayListShippingType.size(); i++) {
                        String temp = arrayListShippingType.get(i);
                        if (strShippingType.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    dialogCreateGatepassShippingType.setText("");
                }
            }

        });

        dialogCreateGatepassShippingBy.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strShippingBy = dialogCreateGatepassShippingBy.getText().toString();
                    for (int i = 0; i < arrayListShippingBy.size(); i++) {
                        String temp = arrayListShippingBy.get(i);
                        if (strShippingBy.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    dialogCreateGatepassShippingBy.setText("");
                }
            }
        });

        dialogCreateGatepassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGatepassViewpager.setCurrentItem(0);
            }
        });
    }

}
