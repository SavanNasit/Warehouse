package com.accrete.sixorbit.fragment.Drawer.leads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.Lead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_LEAD_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by agt on 14/11/17.
 */

public class LeadInfoFragment extends Fragment {
    public List<Lead> leadList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private String strLeadId, strLeasId, strId;
    private LinearLayout idLayout;
    private TextView idTextView;
    private TextView idValueTextView;
    private LinearLayout nameLayout;
    private TextView nameTextView;
    private TextView nameValueTextView;
    private LinearLayout contactNumberLayout;
    private TextView contactNumberTextView;
    private TextView contactNumberValueTextView;
    private LinearLayout contactEmailLayout;
    private TextView contactEmailTextView;
    private TextView contactEmailValueTextView;
    private LinearLayout cityLayout;
    private TextView cityTextView;
    private TextView cityValueTextView;
    private LinearLayout addressLayout;
    private TextView addressTextView;
    private TextView addressValueTextView;
    private Lead lead = new Lead();
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView statusValueTextView;
    private LinearLayout statesLayout;
    private TextView statesTextView;
    private TextView statesValueTextView;
    private HashMap<String, String> hashMapStates = new HashMap<String, String>();
    private ArrayList<String> listStates, listStatesCode;

    public static LeadInfoFragment newInstance() {
        return new LeadInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        strLeadId = bundle.getString(getString(R.string.leaid));
        strLeasId = bundle.getString(getString(R.string.leasid));
        strId = bundle.getString(getString(R.string.id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lead_info_fragment, container, false);
        databaseHandler = new DatabaseHandler(getActivity());

        idLayout = (LinearLayout) rootView.findViewById(R.id.id_layout);
        idTextView = (TextView) rootView.findViewById(R.id.id_textView);
        idValueTextView = (TextView) rootView.findViewById(R.id.id_value_textView);
        nameLayout = (LinearLayout) rootView.findViewById(R.id.name_layout);
        nameTextView = (TextView) rootView.findViewById(R.id.name_textView);
        nameValueTextView = (TextView) rootView.findViewById(R.id.name_value_textView);
        contactNumberLayout = (LinearLayout) rootView.findViewById(R.id.contact_number_layout);
        contactNumberTextView = (TextView) rootView.findViewById(R.id.contact_number_textView);
        contactNumberValueTextView = (TextView) rootView.findViewById(R.id.contact_number_value_textView);
        contactEmailLayout = (LinearLayout) rootView.findViewById(R.id.contact_email_layout);
        contactEmailTextView = (TextView) rootView.findViewById(R.id.contact_email_textView);
        contactEmailValueTextView = (TextView) rootView.findViewById(R.id.contact_email_value_textView);
        cityLayout = (LinearLayout) rootView.findViewById(R.id.city_layout);
        cityTextView = (TextView) rootView.findViewById(R.id.city_textView);
        cityValueTextView = (TextView) rootView.findViewById(R.id.city_value_textView);
        addressLayout = (LinearLayout) rootView.findViewById(R.id.address_layout);
        addressTextView = (TextView) rootView.findViewById(R.id.address_textView);
        addressValueTextView = (TextView) rootView.findViewById(R.id.address_value_textView);
        statusLayout = (LinearLayout) rootView.findViewById(R.id.status_layout);
        statusTextView = (TextView) rootView.findViewById(R.id.status_textView);
        statusValueTextView = (TextView) rootView.findViewById(R.id.status_value_textView);
        statesLayout = (LinearLayout) rootView.findViewById(R.id.states_layout);
        statesTextView = (TextView) rootView.findViewById(R.id.states_textView);
        statesValueTextView = (TextView) rootView.findViewById(R.id.states_value_textView);

        getDataFromDB();

        return rootView;
    }

    private void getDataFromDB() {
        if (databaseHandler != null) {
            if (strLeadId == null || strLeadId.equals("null") || strLeadId.isEmpty()) {
                lead = databaseHandler.getLead(strId, getString(R.string.syncID));
            } else {
                lead = databaseHandler.getLead(strLeadId, getString(R.string.leaid));
            }
            leadList.add(lead);
        }

        //TODO Added on 18th June 2k18
        setStatesCodeData();
        listStates = new ArrayList<>();
        listStatesCode = new ArrayList<>();
        setStatesCodeData();
        for (String key : hashMapStates.keySet()) {
            listStates.add(key);
        }
        for (String value : hashMapStates.values()) {
            listStatesCode.add(value);
        }

        if (leadList.size() > 0) {

            //Id
            if (leadList.get(0).getLeadId() != null && !leadList.get(0).getLeadId().isEmpty() &&
                    !leadList.get(0).getLeadId().equals("null")) {
                idTextView.setText("Lead ID :");
                idValueTextView.setText(leadList.get(0).getLeadId());
            } else {
                idLayout.setVisibility(View.GONE);
            }

            //Name
            if (leadList.get(0).getName() != null && !leadList.get(0).getName().isEmpty()) {
                nameValueTextView.setText(lead.getName());
                nameTextView.setText("Name :");
                nameLayout.setVisibility(View.VISIBLE);
            } else {
                nameLayout.setVisibility(View.GONE);
            }

            //Contact Number
            if (leadList.get(0).getMobile() != null && !leadList.get(0).getMobile().isEmpty()) {
                contactNumberValueTextView.setText(Html.fromHtml(leadList.get(0).getMobile()));
                contactNumberTextView.setText("Contact Number :");
                contactNumberLayout.setVisibility(View.VISIBLE);
                contactNumberValueTextView.setLinkTextColor(Color.BLUE);
                contactNumberValueTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                contactNumberValueTextView.setTextColor(Color.BLUE);
                contactNumberValueTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Constants.validCellPhone(leadList.get(0).getMobile())) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                callIntent();
                            } else {
                                callActionLead();
                            }
                        } else {
                            CustomisedToast.error(getActivity(), getString(R.string.phone_number_not_valid_error)).show();
                        }
                    }
                });
            } else {
                contactNumberLayout.setVisibility(View.GONE);
            }

            //Contact Email
            if (leadList.get(0).getEmail() != null && !leadList.get(0).getEmail().isEmpty()) {
                contactEmailValueTextView.setText(lead.getEmail());
                contactEmailTextView.setText("Email :");
                contactEmailLayout.setVisibility(View.VISIBLE);
            } else {
                contactEmailLayout.setVisibility(View.GONE);
            }

            //City
            if (leadList.get(0).getShippingAddress() != null && leadList.get(0).getShippingAddress().size() > 0) {
                if (leadList.get(0).getShippingAddress().get(0).getCity() != null &&
                        !leadList.get(0).getShippingAddress().get(0).getCity().isEmpty()) {
                    cityValueTextView.setText(lead.getShippingAddress().get(0).getCity());
                    cityTextView.setText("City :");
                    cityLayout.setVisibility(View.VISIBLE);
                } else {
                    cityLayout.setVisibility(View.GONE);
                }
            } else {
                cityLayout.setVisibility(View.GONE);
            }

            //State
            if (leadList.get(0).getShippingAddress() != null && leadList.get(0).getShippingAddress().size() > 0) {
                if (leadList.get(0).getShippingAddress().get(0).getState() != null &&
                        !leadList.get(0).getShippingAddress().get(0).getState().isEmpty()) {
                    statesValueTextView.setText(lead.getShippingAddress().get(0).getState());
                    if (Constants.isNumeric(leadList.get(0).getShippingAddress().get(0).getState())) {
                        for (int i = 0; i < listStatesCode.size(); i++) {
                            if (Integer.parseInt(leadList.get(0).getShippingAddress().get(0).getState()) ==
                                    Integer.parseInt(listStatesCode.get(i).toString())) {
                                statesValueTextView.setText(listStates.get(i).toString());
                            }
                        }
                    }
                    statesTextView.setText("State :");
                    statesLayout.setVisibility(View.VISIBLE);
                } else {
                    statesLayout.setVisibility(View.GONE);
                }
            } else {
                statesLayout.setVisibility(View.GONE);
            }

            //Address
            if (leadList.get(0).getOffAddr() != null && !leadList.get(0).getOffAddr().isEmpty()) {
                addressValueTextView.setText(lead.getOffAddr());
                addressTextView.setText("Address :");
                addressLayout.setVisibility(View.VISIBLE);
            } else {
                addressLayout.setVisibility(View.GONE);
            }

            //Status
            if (leadList.get(0).getLeasid() != null && !leadList.get(0).getLeasid().isEmpty()) {
                if (lead != null && lead.getLeasid() != null && lead.getLeasid().equals("1")) {
                    statusValueTextView.setText("New");
                    statusValueTextView.setTextColor(getResources().getColor(R.color.blueTurquoise));
                } else if (lead != null && lead.getLeasid() != null && lead.getLeasid().equals("2")) {
                    statusValueTextView.setText("Progress");
                    statusValueTextView.setTextColor(getResources().getColor(R.color.orange));
                } else if (lead != null && lead.getLeasid() != null && lead.getLeasid().equals("3")) {
                    statusValueTextView.setText("Converted");
                    statusValueTextView.setTextColor(getResources().getColor(R.color.green));
                } else if (lead != null && lead.getLeasid() != null && lead.getLeasid().equals("4")) {
                    statusValueTextView.setText("Cancelled");
                    statusValueTextView.setTextColor(getResources().getColor(R.color.red));
                }
                statusTextView.setText("Status :");
                statusLayout.setVisibility(View.VISIBLE);
            } else {
                statusLayout.setVisibility(View.GONE);
            }
        }
    }

    private void callIntent() {
        if (checkPermissionWithRationale(getActivity(), new LeadInfoFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_LEAD_CALL_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void callActionLead() {
        String leadlistsmobile = leadList.get(0).getMobile();
        if (leadlistsmobile == null || leadlistsmobile == "") {
            CustomisedToast.error(getActivity(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        } else {
            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + leadlistsmobile));
            startActivity(intentCall);
        }
    }

    private void setStatesCodeData() {
        hashMapStates.put(getString(R.string.state_andhra_pradesh), "1");
        hashMapStates.put(getString(R.string.state_arunachala_pradesh), "2");
        hashMapStates.put(getString(R.string.state_assam), "3");
        hashMapStates.put(getString(R.string.state_bihar), "4");
        hashMapStates.put(getString(R.string.state_chattisgarh), "5");
        hashMapStates.put(getString(R.string.state_goa), "6");
        hashMapStates.put(getString(R.string.state_gujarat), "7");
        hashMapStates.put(getString(R.string.state_haryana), "8");
        hashMapStates.put(getString(R.string.state_himachal_pradesh), "9");
        hashMapStates.put(getString(R.string.state_jammu_and_kashmir), "10");
        hashMapStates.put(getString(R.string.state_jarkhand), "11");
        hashMapStates.put(getString(R.string.state_andhra_pradesh), "12");
        hashMapStates.put(getString(R.string.state_kerala), "13");
        hashMapStates.put(getString(R.string.state_madya_pradesh), "14");

        hashMapStates.put(getString(R.string.state_maharashta), "15");
        hashMapStates.put(getString(R.string.state_manipur), "16");
        hashMapStates.put(getString(R.string.state_meghalaya), "17");
        hashMapStates.put(getString(R.string.state_mizoram), "18");
        hashMapStates.put(getString(R.string.state_nagaland), "19");
        hashMapStates.put(getString(R.string.state_orissa), "20");
        hashMapStates.put(getString(R.string.state_punjab), "21");
        hashMapStates.put(getString(R.string.state_sikkim), "23");
        hashMapStates.put(getString(R.string.state_tamilnadu), "24");
        hashMapStates.put(getString(R.string.state_tripura), "25");
        hashMapStates.put(getString(R.string.state_uttaranchal), "26");

        hashMapStates.put(getString(R.string.state_uttar_pradesh), "27");
        hashMapStates.put(getString(R.string.state_west_bengal), "28");
        hashMapStates.put(getString(R.string.state_andaman_and_nicobar), "29");
        hashMapStates.put(getString(R.string.state_chandigarh), "30");
        hashMapStates.put(getString(R.string.state_dadar_and_nagar), "31");
        hashMapStates.put(getString(R.string.state_daman_diu), "32");
        hashMapStates.put(getString(R.string.state_delhi), "33");
        hashMapStates.put(getString(R.string.state_lakshadeep), "34");
        hashMapStates.put(getString(R.string.state_pondicherry), "35");
        hashMapStates.put(getString(R.string.state_karnataka), "36");
    }

}
