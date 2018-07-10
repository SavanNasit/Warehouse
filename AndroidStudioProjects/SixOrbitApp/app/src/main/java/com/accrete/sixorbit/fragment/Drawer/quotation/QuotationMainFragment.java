package com.accrete.sixorbit.fragment.Drawer.quotation;

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
 * Created by agt on 28/12/17.
 */

public class QuotationMainFragment extends Fragment implements View.OnClickListener {
    Bundle bundle = null;
    private LinearLayout manageQuotationLayout;
    private LinearLayout createQuotationLayout;
    private DatabaseHandler databaseHandler;

    public static QuotationMainFragment newInstance(String title) {
        QuotationMainFragment f = new QuotationMainFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        View view = inflater.inflate(R.layout.quotation_main_fragment, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        databaseHandler = new DatabaseHandler(getActivity());
        findViews(view);
        return view;
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.quotation));
    }

    private void findViews(View view) {
        manageQuotationLayout = (LinearLayout) view.findViewById(R.id.manage_quotation_layout);
        createQuotationLayout = (LinearLayout) view.findViewById(R.id.create_quotation_layout);
        manageQuotationLayout.setOnClickListener(this);
        createQuotationLayout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.quotation));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.quotation));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.quotation));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.manage_quotation_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.quotation_view_permission))) {
                    FragmentManager fragmentManagerManageQuotation = getFragmentManager();
                    FragmentTransaction fragmentTransactionManageQuotation = fragmentManagerManageQuotation.beginTransaction();
                    fragmentTransactionManageQuotation.add(R.id.frame_container, new ManageQuotationFragment(),
                            getString(R.string.manage_quotation));
                    fragmentTransactionManageQuotation.addToBackStack(null);
                    fragmentTransactionManageQuotation.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to view manage quotations.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.create_quotation_layout:
                if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                        databaseHandler.checkUsersPermission(getString(R.string.quotation_add_permission))) {
                    FragmentManager fragmentManagerCreateQuotation = getFragmentManager();
                    FragmentTransaction fragmentTransactionCreateQuotation = fragmentManagerCreateQuotation.beginTransaction();
                    fragmentTransactionCreateQuotation.replace(R.id.frame_container, new QuotationTemplatesFragment(), getActivity().getString(R.string.create_quotation));
                    fragmentTransactionCreateQuotation.addToBackStack(null);
                    fragmentTransactionCreateQuotation.commit();
                } else {
                    Toast.makeText(getActivity(), "Sorry, you've no permission to add quotation.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
