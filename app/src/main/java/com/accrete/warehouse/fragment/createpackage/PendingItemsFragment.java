package com.accrete.warehouse.fragment.createpackage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.OrderItemActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.ScannerActivity;
import com.accrete.warehouse.adapter.PendingItemsAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.fragment.runningorders.RunningOrdersExecuteFragment.viewPagerExecute;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_FOR_CAMERA;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 11/28/17.
 */

public class PendingItemsFragment extends Fragment implements PendingItemsAdapter.PendingItemsAdapterListener, View.OnClickListener {
    PendingItems pendingItems = new PendingItems();
    List<PendingItems> selectedItemList = new ArrayList<>();
    private EditText pendingItemsEdtScan;
    private ImageView pendingItemsImgScan;
    private SwipeRefreshLayout pendingItemsSwipeRefreshLayout;
    private RecyclerView pendingItemsRecyclerView;
    private TextView pendingItemsEmptyView;
    private PendingItemsAdapter pendingItemsAdapter;
    private List<PendingItems> pendingItemList = new ArrayList<>();
    private String chkid, chkoid;
    private List<SelectOrderItem> selectOrderItemList = new ArrayList<>();
    private int posToupdate;
    private boolean flagScan;

    public void getData(String str) {
        // Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        pendingItemsEdtScan.setText(str);
        for (int i = 0; i < pendingItemList.size(); i++) {
            if (pendingItemsEdtScan.getText().toString().trim().equals(pendingItemList.get(i).getIsid())) {
                if (Integer.parseInt(pendingItemList.get(i).getItemQuantity()) == 0) {
                    Toast.makeText(getActivity(), "No item available", Toast.LENGTH_SHORT).show();
                } else {
                    pendingItemList.get(i).setItemQuantity(String.valueOf(Integer.valueOf(pendingItemList.get(i).getItemQuantity()) - 1));
                    flagScan=true;
                    posToupdate=i;
                    pendingItemsAdapter.notifyDataSetChanged();
                }
                pendingItemsEdtScan.setText("");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_items, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            chkid = bundle.getString("chkid");
            chkoid = bundle.getString("chkoid");
            pendingItemList = bundle.getParcelableArrayList("pendingItems");
            for (int i = 0; i < pendingItemList.size(); i++) {
                pendingItemList.get(i).setItemQuantityDuplicate(pendingItemList.get(i).getItemQuantity());
            }
        }
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        pendingItemsEdtScan = (EditText) rootView.findViewById(R.id.pending_items_edt_scan);
        pendingItemsImgScan = (ImageView) rootView.findViewById(R.id.pending_items_img_scan);
        //  pendingItemsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pending_items_swipe_refresh_layout);
        pendingItemsRecyclerView = (RecyclerView) rootView.findViewById(R.id.pending_items_recycler_view);
        pendingItemsEmptyView = (TextView) rootView.findViewById(R.id.pending_items_empty_view);

        pendingItemsAdapter = new PendingItemsAdapter(getActivity(), pendingItemList, this, 0,posToupdate,flagScan);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        pendingItemsRecyclerView.setLayoutManager(mLayoutManager);
        pendingItemsRecyclerView.setHasFixedSize(true);
        pendingItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingItemsRecyclerView.setNestedScrollingEnabled(false);
        pendingItemsRecyclerView.setAdapter(pendingItemsAdapter);

        if (pendingItemList.size() > 0) {
            pendingItemsRecyclerView.setVisibility(View.VISIBLE);
            pendingItemsEmptyView.setVisibility(View.GONE);
        } else {
            pendingItemsRecyclerView.setVisibility(View.GONE);
            pendingItemsEmptyView.setVisibility(View.VISIBLE);
            pendingItemsEmptyView.setText(getString(R.string.no_data_available));
        }

        pendingItemsImgScan.setOnClickListener(this);
        pendingItemsEdtScan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < pendingItemList.size(); i++) {
                    if (pendingItemsEdtScan.getText().toString().trim().equals(pendingItemList.get(i).getIsid())) {
                        if (Integer.parseInt(pendingItemList.get(i).getItemQuantity()) == 0) {
                            Toast.makeText(getActivity(), "No item available", Toast.LENGTH_SHORT).show();
                        } else {
                            pendingItemList.get(i).setItemQuantity(String.valueOf(Integer.valueOf(pendingItemList.get(i).getItemQuantity()) - 1));
                            flagScan=true;
                            posToupdate=i;
                            pendingItemsAdapter.notifyDataSetChanged();
                        }
                        pendingItemsEdtScan.setText("");
                    }
                }

            }
        });
        LinearLayout linearLayoutPackageDetails =(LinearLayout) rootView.findViewById(R.id.pending_items_package_details);
        linearLayoutPackageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerExecute.setCurrentItem(1);
            }
        });
    }

    @Override
    public void onMessageRowClicked(int position) {

    }


    @Override
    public void onExecute(String isid, String oiid, final String maximumQuantity, final int position) {
        executeSelectedItems(isid, oiid);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentOrderItem = new Intent(getActivity(), OrderItemActivity.class);
                intentOrderItem.putExtra("chkoid", chkoid);
                intentOrderItem.putExtra("quantity", maximumQuantity);
                intentOrderItem.putExtra("position", position);
                intentOrderItem.putParcelableArrayListExtra("selectOrderItemList", (ArrayList<? extends Parcelable>) selectOrderItemList);
                intentOrderItem.putParcelableArrayListExtra("pendingItemsList", (ArrayList<? extends Parcelable>) pendingItemList);
                startActivityForResult(intentOrderItem, 1001);

            }
        }, 1 * 200);
    }

    private void executeSelectedItems(String isid, String oiid) {
        if (selectOrderItemList.size() > 0) {
            selectOrderItemList.clear();
        }

        task = getString(R.string.execute_order_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.executeSelectedItem(version, key, task, userId, accessToken, chkid, chkoid, oiid, isid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (SelectOrderItem selectOrderItem : apiResponse.getData().getSelectOrdersItems()) {
                            selectOrderItemList.add(selectOrderItem);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("warehouse:password", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionWithRationale((Activity) getActivity(), new PendingItemsFragment(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA)) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        } else {
            scanBarcode();
        }
    }

    public void scanBarcode() {
        Log.d("PERMISSION", "Camera3");
        Intent intentScan = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intentScan, 1000);
    }

    public void getAllocatedQuantity(int allocatedQuantity, List<PendingItems> pendingItemsLists, int position) {
        posToupdate =position;
        pendingItemsAdapter = new PendingItemsAdapter(getActivity(), pendingItemList, this, allocatedQuantity,posToupdate,false);
        pendingItemsRecyclerView.setAdapter(pendingItemsAdapter);
        selectedItemList.addAll(pendingItemsLists);
    }
}
