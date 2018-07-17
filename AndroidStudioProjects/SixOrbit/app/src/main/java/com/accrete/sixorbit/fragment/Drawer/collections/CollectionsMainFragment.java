package com.accrete.sixorbit.fragment.Drawer.collections;

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
 * Created by {Anshul} on 15/5/18.
 */

public class CollectionsMainFragment extends Fragment implements View.OnClickListener {
    private DatabaseHandler databaseHandler;
    private LinearLayout drawerLinearLayoutMain;
    private LinearLayout invoiceWiseCollectionsLayout;
    private LinearLayout myTransactionsLayout;
    private LinearLayout createCollectionsLayout, customerWiseCollectionsLayout;

    public static CollectionsMainFragment newInstance(String title) {
        CollectionsMainFragment f = new CollectionsMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.collections));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collections_main_fragment, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        databaseHandler = new DatabaseHandler(getActivity());
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        drawerLinearLayoutMain = (LinearLayout) view.findViewById(R.id.drawer_linear_layout_main);
        invoiceWiseCollectionsLayout = (LinearLayout) view.findViewById
                (R.id.invoicewise_collections_layout);
        myTransactionsLayout = (LinearLayout) view.findViewById(R.id.my_transactions_layout);
        createCollectionsLayout = (LinearLayout) view.findViewById(R.id.create_collections_layout);
        customerWiseCollectionsLayout = (LinearLayout) view.findViewById
                (R.id.customerWise_collections_layout);
        invoiceWiseCollectionsLayout.setOnClickListener(this);
        myTransactionsLayout.setOnClickListener(this);
        createCollectionsLayout.setOnClickListener(this);
        customerWiseCollectionsLayout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.collections));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.collections));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.collections));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.my_transactions_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.my_transactions_permission))) {
                    CollectionsMainFragment collectionsMainFragment = new CollectionsMainFragment();
                    FragmentManager fragmentManagerMyTransactions = getFragmentManager();
                    FragmentTransaction fragmentTransactionMyTransactions = fragmentManagerMyTransactions.beginTransaction();
                    fragmentTransactionMyTransactions.add(R.id.frame_container, new MyTransactionsFragment(),
                            getString(R.string.manage_quotation));
                    fragmentTransactionMyTransactions.hide(collectionsMainFragment);
                    fragmentTransactionMyTransactions.addToBackStack(null);
                    fragmentTransactionMyTransactions.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view my transactions.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.invoicewise_collections_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.my_collections_permission))) {
                    FragmentManager fragmentManagerMyCollections = getFragmentManager();
                    FragmentTransaction fragmentTransactionMyCollections = fragmentManagerMyCollections.beginTransaction();
                    fragmentTransactionMyCollections.add(R.id.frame_container, new InvoiceWiseCollectionsMainFragment(),
                            getString(R.string.manage_quotation));
                    fragmentTransactionMyCollections.addToBackStack(null);
                    fragmentTransactionMyCollections.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view invoice wise outstanding.",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.create_collections_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.collections_create_permission))) {
                    FragmentManager fragmentManagerCreateCollections = getFragmentManager();
                    FragmentTransaction fragmentTransactionCreateCollections = fragmentManagerCreateCollections.beginTransaction();
                    fragmentTransactionCreateCollections.replace(R.id.frame_container, new CreateCollectionSelectCustomerFragment(),
                            getActivity().getString(R.string.create_collection));
                    fragmentTransactionCreateCollections.addToBackStack(null);
                    fragmentTransactionCreateCollections.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to add collections.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.customerWise_collections_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.collections_create_permission))) {
                    FragmentManager customerWiseCollections = getFragmentManager();
                    FragmentTransaction fragmentTransactionCustomerWiseCollections = customerWiseCollections.beginTransaction();
                    fragmentTransactionCustomerWiseCollections.add(R.id.frame_container, new CustomerWiseCollectionsFragment(),
                            getActivity().getString(R.string.create_collection));
                    fragmentTransactionCustomerWiseCollections.addToBackStack(null);
                    fragmentTransactionCustomerWiseCollections.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view customer wise outstanding.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
}
