package com.accrete.sixorbit.fragment.Drawer.customer;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.barCodeScanner.ScannerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 7/12/17.
 */

public class CustomersQuotationMainFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_MULTIPLE_PERMISSION = 123;
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private final String[] PERMISSION_STORAGE = new String[]{android.Manifest.permission.CAMERA};
    List<String> permissionsNeeded = new ArrayList<>();
    boolean isFirstTime = true;
    private String cuid;
    private EditText scanEditText;
    private ImageView scanImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        cuid = bundle.getString(getString(R.string.cuid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_quotation_main_fragment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        scanEditText = (EditText) view.findViewById(R.id.scan_editText);
        scanImageView = (ImageView) view.findViewById(R.id.scan_imageView);

        scanImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == scanImageView) {
            int resultCode = 0;
            if (Build.VERSION.SDK_INT >= 23) {
                if (verifyPermission()) return;
            }
            resultCode = REQUEST_IMAGE_CAPTURE;
            scanBarcode(resultCode);

        }
    }

    public void scanBarcode(int resultCode) {
        Intent intentScan = new Intent(getActivity(), ScannerActivity.class);
        getActivity().startActivityForResult(intentScan, resultCode);
    }

    public void getScanCode(String str) {
        scanEditText.setText(str);
        scanEditText.post(new Runnable() {
            @Override
            public void run() {
                scanEditText.setSelection(scanEditText.getText().toString().length());
            }
        });
    }

    @TargetApi(23)
    private boolean verifyPermission() {
        if (checkAllPermission()) {
            if (permissionsNeeded.size() > 0 && !isFirstTime) {
                for (String permission : permissionsNeeded) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        displayPermissionDialog("Would like to grant access to take picture from camera", PERMISSION_STORAGE, REQUEST_MULTIPLE_PERMISSION);
                        break;
                    }
                }
            } else {
                isFirstTime = false;
                getActivity().requestPermissions(PERMISSION_STORAGE, REQUEST_MULTIPLE_PERMISSION);
            }
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private void displayPermissionDialog(String msg, final String[] permission, final int resultCode) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog
                .Builder(getActivity())
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        requestPermissions(permission, resultCode);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.show();

    }

    @TargetApi(23)
    private boolean checkAllPermission() {
        boolean isPermissionRequired = false;
        for (String permission : PERMISSION_STORAGE) {
            if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
                isPermissionRequired = true;
            }
        }
        return isPermissionRequired;
    }

}
