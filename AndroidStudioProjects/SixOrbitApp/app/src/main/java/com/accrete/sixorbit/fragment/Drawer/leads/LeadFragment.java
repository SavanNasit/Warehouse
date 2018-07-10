package com.accrete.sixorbit.fragment.Drawer.leads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.lead.AddLeadActivity;
import com.accrete.sixorbit.activity.lead.LeadInfoActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.adapter.LeadAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.interfaces.sendLeadMobileNumber;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.LeadShippingAddress;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 10/4/17
 */

public class LeadFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LeadAdapter.LeadAdapterListener, View.OnClickListener, sendLeadMobileNumber {
    private static final int PICK_CONTACT_ADDLEAD = 1000;
    private static String KEY_TITLE = "title";
    public List<Lead> leadList = new ArrayList<>();
    public LeadAdapter mAdapter;
    public FloatingActionButton floatingActionButtonAddLead, floatingActionButtonCompany, floatingActionButtonIndividual,
            floatingActionButtonPhonebook;
    public LinearLayout fabLayoutPhonebook, fabLayoutIndividual, fabLayoutCompany;
    public TextView textViewEmptyView;
    String leadMobileNumber;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private String phoneNumber, phonebookEmail;
    private DatabaseHandler db;
    private String status;
    private LeadShippingAddress leadShippingAddress = new LeadShippingAddress();

    public static LeadFragment newInstance(String title) {
        LeadFragment f = new LeadFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lead, container, false);
        db = DatabaseHandler.getInstance(getActivity());
        initalizeView(rootView);
        ((DrawerActivity) getActivity()).setFragmentRefreshListener(new DrawerActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh Your Fragment
                displayDataInView();
            }
        });

        return rootView;
    }

    private void initalizeView(final View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.lead_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.lead_swipe_refresh_layout);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.lead_empty_view);

        fabLayoutPhonebook = (LinearLayout) rootView.findViewById(R.id.fab_phonebook_layout);
        fabLayoutIndividual = (LinearLayout) rootView.findViewById(R.id.fab_individual_layout);
        fabLayoutCompany = (LinearLayout) rootView.findViewById(R.id.fab_company_layout);

        floatingActionButtonAddLead = (FloatingActionButton) rootView.findViewById(R.id.lead_fab);
        floatingActionButtonCompany = (FloatingActionButton) rootView.findViewById(R.id.lead_company);
        floatingActionButtonIndividual = (FloatingActionButton) rootView.findViewById(R.id.lead_individual);
        floatingActionButtonPhonebook = (FloatingActionButton) rootView.findViewById(R.id.lead_phonebook);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        mAdapter = new LeadAdapter(getActivity(), leadList, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        displayDataInView();
                    }
                }
        );

        floatingActionButtonCompany.setOnClickListener(this);
        floatingActionButtonIndividual.setOnClickListener(this);
        floatingActionButtonAddLead.setOnClickListener(this);
        floatingActionButtonPhonebook.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void getDataFromDB() {
        if (db != null) {
            leadList.clear();
            leadList.addAll(db.getAllLeads());

            /*for (int i = 0; i < leadList.size(); i++) {
                if (leadList.get(i).getLeaid() != null && !leadList.get(i).getLeaid().isEmpty()
                        && !leadList.get(i).getLeaid().equals("null")) {
                    db.deleteCancelledLead(leadList.get(i).getLeaid());
                 *//*   leadList.remove(i);
                    mAdapter.notifyDataSetChanged();*//*
                }
            }*/
        }
    }

    public void displayDataInView() {
        getDataFromDB();
        if (leadList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmptyView.setVisibility(View.GONE);
        }

        mAdapter = new LeadAdapter(getActivity(), leadList, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.lead_fragment));
    }

//    requesting permissions to add the lead username contact

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.lead_fab:
                //Permission Check and disable button of adding a new lead
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || db.checkUsersPermission(getString(R.string.lead_add_permission))) {
                    animateFAB();
                } else {
                    Toast.makeText(getActivity(), "You have no permission to add any lead.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lead_company:
                Intent intentAddLeadCompany = new Intent(getActivity(), AddLeadActivity.class);
                intentAddLeadCompany.putExtra(getString(R.string.mode), getString(R.string.company_small));
                startActivityForResult(intentAddLeadCompany, 1000);
                Intent resultIntent = new Intent();
                getActivity().setResult(1000, resultIntent);

                Log.d("Lead", "company");
                isFabOpen = true;
                animateFAB();
                break;
            case R.id.lead_individual:
                Log.d("Lead", "individual");
                Intent intentAddLeadIndividual = new Intent(getActivity(), AddLeadActivity.class);
                intentAddLeadIndividual.putExtra(getString(R.string.mode), getString(R.string.individual_mode));
                startActivityForResult(intentAddLeadIndividual, 1000);
                Intent resultIntentIndividual = new Intent();
                getActivity().setResult(1000, resultIntentIndividual);
                isFabOpen = true;
                animateFAB();
                break;
            case R.id.lead_phonebook:
                requestPermissiontoAddLeadFromContact();
                isFabOpen = true;
                animateFAB();
                break;

        }
    }
//     Method to pick the pick contact username phonebook to add the lead

    public void requestPermissiontoAddLeadFromContact() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkPermissionWithRationale(getActivity(), new LeadFragment(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS)) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
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
        } else {
            addLeadFromPhonebook();
        }
    }
//    Handling pick contact data and move to add lead activity to add the lead

    public void addLeadFromPhonebook() {
        Intent intentPhonebook = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intentPhonebook.putExtra(getString(R.string.mode), getString(R.string.phonebook_mode));
        startActivityForResult(intentPhonebook, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_CONTACT_ADDLEAD) {
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    String number = "";
                    Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.e("Display Name", name);

                    if (hasPhone.equals("1")) {
                        Cursor phones = getActivity().getContentResolver().query
                                (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            number = phones.getString(phones.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                            phoneNumber = number;
                        }

                        phones.close();

                        Cursor emailCur = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);

                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            String emailType = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                            System.out.println("Email " + email + " Email Type : " + emailType);
                            phonebookEmail = email;


                            isFabOpen = true;
                            animateFAB();
                        }

                        emailCur.close();

                        //                    Intent to move to Add lead activity after selecting the contact

                        Intent intentAddLeadIndividual = new Intent(getActivity(), AddLeadActivity.class);
                        intentAddLeadIndividual.putExtra(getString(R.string.mode), getString(R.string.phonebook_mode));
                        String lastName = "";
                        String firstName = "";
                        if (name != null && !name.isEmpty() && name.split("\\w+").length > 1) {

                            lastName = name.substring(name.lastIndexOf(" ") + 1);
                            Log.e("Lastnme", lastName);
                            firstName = name.substring(0, name.lastIndexOf(' '));
                            Log.e("Firstnme", firstName);
                        } else {
                            firstName = name;
                        }
                        intentAddLeadIndividual.putExtra(getString(R.string.fName), firstName);
                        intentAddLeadIndividual.putExtra(getString(R.string.lName), lastName);
                        intentAddLeadIndividual.putExtra(getString(R.string.email), phonebookEmail);
                        intentAddLeadIndividual.putExtra(getString(R.string.phoneNumber), phoneNumber);
                        startActivityForResult(intentAddLeadIndividual, 1000);

                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.contact_has_no_number), Toast.LENGTH_LONG).show();
                    }
                    cursor.close();

                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            floatingActionButtonAddLead.startAnimation(rotate_backward);
            floatingActionButtonCompany.startAnimation(fab_close);
            floatingActionButtonIndividual.startAnimation(fab_close);
            floatingActionButtonPhonebook.startAnimation(fab_close);

            fabLayoutCompany.startAnimation(fab_close);
            fabLayoutPhonebook.startAnimation(fab_close);
            fabLayoutIndividual.startAnimation(fab_close);

            floatingActionButtonCompany.setClickable(false);
            floatingActionButtonIndividual.setClickable(false);
            floatingActionButtonPhonebook.setClickable(false);
            fabLayoutIndividual.setVisibility(View.GONE);
            fabLayoutCompany.setVisibility(View.GONE);
            fabLayoutPhonebook.setVisibility(View.GONE);
            isFabOpen = false;
            Log.d("Lead", "close");
        } else {
            floatingActionButtonAddLead.startAnimation(rotate_forward);
            floatingActionButtonCompany.startAnimation(fab_open);
            floatingActionButtonIndividual.startAnimation(fab_open);
            floatingActionButtonPhonebook.startAnimation(fab_open);

            fabLayoutCompany.startAnimation(fab_open);
            fabLayoutPhonebook.startAnimation(fab_open);
            fabLayoutIndividual.startAnimation(fab_open);

            fabLayoutIndividual.setVisibility(View.VISIBLE);
            fabLayoutCompany.setVisibility(View.VISIBLE);
            fabLayoutPhonebook.setVisibility(View.VISIBLE);
            floatingActionButtonCompany.setClickable(true);
            floatingActionButtonIndividual.setClickable(true);
            floatingActionButtonPhonebook.setClickable(true);
            isFabOpen = true;
            Log.d("Lead", "open");
        }
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getLeads();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            CustomisedToast.error(getActivity(), getActivity().getString(R.string.no_internet_try_later)).show();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        //Check Permission to view details of lead
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) || db.checkUsersPermission(getString(R.string.lead_view_details_permission))) {
            startActivityForResult(new Intent(getActivity(), LeadInfoActivity.class)
                    .putExtra(getString(R.string.id), leadList.get(position).getSyncId())
                    .putExtra(getString(R.string.leaid), leadList.get(position).getLeaid())
                    .putExtra(getString(R.string.leasid), leadList.get(position).getLeasid())
                    .putExtra(getString(R.string.lead_id), leadList.get(position).getLeadId())
                    .putExtra(getString(R.string.name), leadList.get(position).getName()), 1000);
        } else {
            Toast.makeText(getActivity(), "You have no permission to view details.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSyncClicked(String id) {
    }

    private void getLeads() {
        try {
            task = getString(R.string.lead);
            String lastUpdated;

            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.LAST_DOMAIN);
            }

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getLead(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            for (final Lead lead : apiResponse.getData().getLead()) {
                                if (apiResponse.getData().getLead() != null) {
                                    lead.setLeadSync("true");
                                    int localLeadId, localContacts;
                                    boolean flagLeadForUpdate = db.checkLeadIdInLead(lead.getLeaid());
                                    Lead dbLead = db.getLead(lead.getLeaid(), "leaid");
                                    if (flagLeadForUpdate) {
                                        Log.d("Update: ", "dbLeadForUpdate" + lead.getName());
                                        String specialInstruction = lead.getSpecialInstructions().replaceAll("'", "''");
                                        lead.setSpecialInstructions(specialInstruction);
                                        db.updateLead(lead, getString(R.string.leaid));
                                        db.deleteLeadContacts(lead.getLeaid());
                                        db.deleteLeadAddress(lead.getLeaid());
                                        db.deleteLeadFollowUp(lead.getLeaid());
                                    } else {
                                        if (lead.getSyncId() != null && !lead.getSyncId().isEmpty()) {
                                            boolean flagSyncForUpdate = db.checkSyncIdInLead(lead.getSyncId());
                                            Lead dbLeadForAdd = db.getLead(lead.getSyncId(), getString(R.string.syncID));
                                            if (flagSyncForUpdate) {
                                                Log.d("Update: ", "dbLeadForAdd" + lead.getName());
                                                String specialInstruction = lead.getSpecialInstructions().replaceAll("'", "''");
                                                lead.setSpecialInstructions(specialInstruction);
                                                db.updateLead(lead, getString(R.string.leaid));
                                                db.deleteLeadContacts(lead.getLeaid());
                                                db.deleteLeadAddress(lead.getLeaid());
                                                db.deleteLeadFollowUp(lead.getLeaid());
                                            } else {
                                                lead.setID(db.addLeads(lead));
                                                db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()));
                                            }
                                        } else {
                                            lead.setID(db.addLeads(lead));
                                            db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()));
                                        }
                                    }


                                    if (lead.getContacts() != null && lead.getContacts().size() > 0) {
                                        for (int i = 0; i < lead.getContacts().size(); i++) {
                                            Contacts contacts = new Contacts();
                                            //  db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()), String.valueOf(leadContacts.getID()));
                                            contacts.setCodeid(lead.getContacts().get(i).getCodeid());
                                            contacts.setName(lead.getContacts().get(i).getName());
                                            contacts.setPhoneNo(lead.getContacts().get(i).getPhoneNo());
                                            contacts.setEmail(lead.getContacts().get(i).getEmail());
                                            contacts.setDesignation(lead.getContacts().get(i).getDesignation());
                                            contacts.setSyncID(lead.getSyncId());
                                            contacts.setLeaid(lead.getLeaid());
                                            contacts.setIsOwner(lead.getContacts().get(i).getIsOwner());

                                            //TODO - Added on 2nd May
                                            if (contacts.getCodeid() != null &&
                                                    !contacts.getCodeid().isEmpty()) {
                                                List<FollowUp> upList = new ArrayList<>();
                                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                                if (upList != null && upList.size() > 0) {
                                                    for (int j = 0; j < upList.size(); j++) {
                                                        if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                            db.updateFollowUpContacts(contacts.getName(),
                                                                    contacts.getPhoneNo(),
                                                                    contacts.getEmail(),
                                                                    contacts.getCodeid());
                                                        }
                                                    }
                                                }
                                            }
                                            contacts.setID(db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId()));
                                            //  db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()), String.valueOf(contacts.getID()));

                                        }
                                    }

                                    Log.d("Insert: ", "Inserting Leads.." + lead.getName());
                                    Log.d("leadAddress size", String.valueOf(lead.getShippingAddress().size()));
                                    if (lead.getShippingAddress() != null && lead.getShippingAddress().size() > 0) {
                                        for (int i = 0; i < lead.getShippingAddress().size(); i++) {
                                            leadShippingAddress.setSaid(lead.getShippingAddress().get(i).getSaid());
                                            leadShippingAddress.setFirstName(lead.getShippingAddress().get(i).getFirstName());
                                            leadShippingAddress.setLastName(lead.getShippingAddress().get(i).getLastName());
                                            leadShippingAddress.setLine1(lead.getShippingAddress().get(i).getLine1());
                                            leadShippingAddress.setLine2(lead.getShippingAddress().get(i).getLine2());
                                            leadShippingAddress.setCoverid(lead.getShippingAddress().get(i).getCoverid());
                                            leadShippingAddress.setStid(lead.getShippingAddress().get(i).getStid());
                                            leadShippingAddress.setCtid(lead.getShippingAddress().get(i).getCtid());
                                            leadShippingAddress.setZipCode(lead.getShippingAddress().get(i).getZipCode());
                                            leadShippingAddress.setMobile(lead.getShippingAddress().get(i).getMobile());
                                            leadShippingAddress.setSatid(lead.getShippingAddress().get(i).getSatid());
                                            leadShippingAddress.setSasid(lead.getShippingAddress().get(i).getSasid());
                                            leadShippingAddress.setCity(lead.getShippingAddress().get(i).getCity());
                                            leadShippingAddress.setCountry(lead.getShippingAddress().get(i).getCountry());
                                            leadShippingAddress.setIsoCode(lead.getShippingAddress().get(i).getIsoCode());
                                            leadShippingAddress.setState(lead.getShippingAddress().get(i).getState());
                                            leadShippingAddress.setStateCode(lead.getShippingAddress().get(i).getStateCode());
                                            leadShippingAddress.setSyncID(lead.getSyncId());
                                            leadShippingAddress.setSiteName(lead.getShippingAddress().get(i).getSiteName());
                                            leadShippingAddress.setLeaid(lead.getLeaid());
                                            db.addLeadAddress(leadShippingAddress, lead.getLeaid());
                                        }
                                    }
                                }
                            }

                            SyncCheck syncCheck = new SyncCheck();
                            syncCheck.setService(getString(R.string.lead));
                            if (!apiResponse.getData().getLead().equals("") && apiResponse.getData().getLead().size() > 0) {
                                syncCheck.setCallTime(apiResponse.getData().getLead().get(apiResponse.getData().getLead().size() - 1).getUpdatedTs());
                            } else {
                                syncCheck.setCallTime(db.getLeadUpdatedTs());
                            }

                            if (db != null) {
                                db.updateSyncCheck(syncCheck);
                            }
                            displayDataInView();
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        } else {
                            if (leadList == null && leadList.size() == 0) {
                                Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
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

    @SuppressLint("MissingPermission")
    public void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + leadMobileNumber));
        if (leadMobileNumber == null || leadMobileNumber == "") {
            Toast.makeText(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        } else {
            getActivity().startActivity(intentCall);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.lead_fragment));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.lead_fragment));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Your callback initialization here
    }


    @Override
    public void sendLeadMobileNumber(String mnumber) {
        this.leadMobileNumber = mnumber;
        Log.e("LeadMobileNumber", mnumber);

    }
}
