package com.accrete.sixorbit.fragment.Drawer.enquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.orders.ManageOrderFragment;
import com.accrete.sixorbit.fragment.Drawer.orders.OrderTemplatesFragment;
import com.accrete.sixorbit.helper.DatabaseHandler;

/**
 * Created by {Anshul} on 27/6/18.
 */

public class EnquiryMainFragment extends Fragment implements View.OnClickListener {
    private DatabaseHandler databaseHandler;
    private LinearLayout linearLayoutMain;
    private LinearLayout manageEnquiryLayout;
    private LinearLayout createEnquiryLayout;

    public static EnquiryMainFragment newInstance(String title) {
        EnquiryMainFragment f = new EnquiryMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enquiry_main_fragment, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        databaseHandler = new DatabaseHandler(getActivity());
        findViews(view);
        return view;
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

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.enquiry));
    }

    private void findViews(View view) {
        linearLayoutMain = (LinearLayout) view.findViewById(R.id.linear_layout_main);
        manageEnquiryLayout = (LinearLayout) view.findViewById(R.id.manage_enquiry_layout);
        createEnquiryLayout = (LinearLayout) view.findViewById(R.id.create_enquiry_layout);
        manageEnquiryLayout.setOnClickListener(this);
        createEnquiryLayout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.enquiry));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.enquiry));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.enquiry));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.manage_enquiry_layout:
                /*if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.order_view_permission))) {
                */
                FragmentManager fragmentManagerManageEnquiry = getFragmentManager();
                FragmentTransaction fragmentTransactionManageEnquiry = fragmentManagerManageEnquiry.beginTransaction();
                fragmentTransactionManageEnquiry.add(R.id.frame_container, new ManageEnquiryFragment(),
                        getString(R.string.manage_enquiry));
                fragmentTransactionManageEnquiry.addToBackStack(null);
                fragmentTransactionManageEnquiry.commit();
                /*} else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view manage " +
                            "enquiries.", Toast.LENGTH_SHORT).show();
                }*/
                break;
            case R.id.create_enquiry_layout:
               /* if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.order_add_permission))) {
               */
                FragmentManager fragmentManagerCreateOrder = getFragmentManager();
                FragmentTransaction fragmentTransactionCreateOrder = fragmentManagerCreateOrder.beginTransaction();
                fragmentTransactionCreateOrder.replace(R.id.frame_container, new OrderTemplatesFragment(),
                        getActivity().getString(R.string.create_order));
                fragmentTransactionCreateOrder.addToBackStack(null);
                fragmentTransactionCreateOrder.commit();
                /*} else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to add any enquiry.",
                            Toast.LENGTH_SHORT).show();
                }*/
                break;
        }
    }
}


