package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.warehouse.R;

/**
 * Created by poonam on 12/5/17.
 */

public class ReceiveConsignmentFragment extends Fragment {

    private static final String KEY_TITLE = "ReceiveConsignment";

    public static ReceiveConsignmentFragment newInstance(String title) {
        ReceiveConsignmentFragment f = new ReceiveConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_consignment, container, false);
        //findViews(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.recieve_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.recieve_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.recieve_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.recieve_consignment_fragment));
        }
    }
}
