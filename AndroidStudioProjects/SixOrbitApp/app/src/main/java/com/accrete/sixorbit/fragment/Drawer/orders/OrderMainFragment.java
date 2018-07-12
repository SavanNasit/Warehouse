package com.accrete.sixorbit.fragment.Drawer.orders;

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
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

/**
 * Created by {Anshul} on 29/3/18.
 */

public class OrderMainFragment extends Fragment implements View.OnClickListener {
    private LinearLayout linearLayoutMain;
    private LinearLayout manageOrderLayout;
    private LinearLayout createOrderLayout;
    private DatabaseHandler databaseHandler;

    public static OrderMainFragment newInstance(String title) {
        OrderMainFragment f = new OrderMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_main_fragment, container, false);
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
        getActivity().setTitle(getString(R.string.order));
    }

    private void findViews(View view) {
        linearLayoutMain = (LinearLayout) view.findViewById(R.id.linear_layout_main);
        manageOrderLayout = (LinearLayout) view.findViewById(R.id.manage_order_layout);
        createOrderLayout = (LinearLayout) view.findViewById(R.id.create_order_layout);
        manageOrderLayout.setOnClickListener(this);
        createOrderLayout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.order));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.order));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.order));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.manage_order_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.order_view_permission))) {
                    FragmentManager fragmentManagerManageOrder = getFragmentManager();
                    FragmentTransaction fragmentTransactionManageOrder = fragmentManagerManageOrder.beginTransaction();
                    fragmentTransactionManageOrder.add(R.id.frame_container, new ManageOrderFragment(),
                            getString(R.string.manage_order));
                    fragmentTransactionManageOrder.addToBackStack(null);
                    fragmentTransactionManageOrder.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view manage orders.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.create_order_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.order_add_permission))) {
                    FragmentManager fragmentManagerCreateOrder = getFragmentManager();
                    FragmentTransaction fragmentTransactionCreateOrder = fragmentManagerCreateOrder.beginTransaction();
                    fragmentTransactionCreateOrder.replace(R.id.frame_container, new OrderTemplatesFragment(),
                            getActivity().getString(R.string.create_order));
                    fragmentTransactionCreateOrder.addToBackStack(null);
                    fragmentTransactionCreateOrder.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to add order.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

