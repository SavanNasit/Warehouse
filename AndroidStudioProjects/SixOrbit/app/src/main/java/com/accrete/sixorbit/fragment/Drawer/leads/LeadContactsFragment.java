package com.accrete.sixorbit.fragment.Drawer.leads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.LeadContactsAdapter;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.Lead;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 14/11/17.
 */

public class LeadContactsFragment extends Fragment {
    public List<Lead> leadList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private String strLeadId, strId;
    private RecyclerView recyclerViewContacts;
    private TextView textviewLeadContactsEmpty;
    private LinearLayoutManager mLayoutManager;
    private List<Contacts> contactsList = new ArrayList<Contacts>();
    private LeadContactsAdapter contactAdapter;
    private Lead leadLists = new Lead();
    private LeadContactsAdapter.LeadContactsAdapterListener leadContactsAdapterListener;

    public static LeadContactsFragment newInstance() {
        return new LeadContactsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        strLeadId = bundle.getString(getString(R.string.leaid));
        strId = bundle.getString(getString(R.string.id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lead_contacts_fragment, container, false);
        databaseHandler = new DatabaseHandler(getActivity());

        recyclerViewContacts = (RecyclerView) rootView.findViewById(R.id.recycler_view_contacts);
        textviewLeadContactsEmpty = (TextView) rootView.findViewById(R.id.textview_lead_contacts_empty);
        getDataFromDB();
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(mLayoutManager);
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        contactAdapter = new LeadContactsAdapter(getActivity(), contactsList, leadList, leadContactsAdapterListener, getActivity());
        recyclerViewContacts.setAdapter(contactAdapter);


        if (contactsList != null && contactsList.size() > 0) {
            recyclerViewContacts.setVisibility(View.VISIBLE);
            textviewLeadContactsEmpty.setVisibility(View.GONE);
        } else {
            recyclerViewContacts.setVisibility(View.GONE);
            textviewLeadContactsEmpty.setText("There is no contact.");
            textviewLeadContactsEmpty.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void getDataFromDB() {
        try {
            if (databaseHandler != null) {
                if (leadList != null && leadList.size() > 0) {
                    leadList.clear();
                }
                if (strLeadId == null || strLeadId.equals("null") || strLeadId.isEmpty()) {
                    leadLists = databaseHandler.getLead(strId, getString(R.string.syncID));
                } else {
                    leadLists = databaseHandler.getLead(strLeadId, getString(R.string.leaid));
                }

                leadList.add(leadLists);

                if (leadLists != null && leadLists.getContacts().size() > 0) {
                    if (contactsList.size() > 0) {
                        contactsList.clear();
                    }
                    contactsList.addAll(leadLists.getContacts());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
