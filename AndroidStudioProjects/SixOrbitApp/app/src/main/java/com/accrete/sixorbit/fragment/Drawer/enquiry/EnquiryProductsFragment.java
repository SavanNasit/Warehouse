package com.accrete.sixorbit.fragment.Drawer.enquiry;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.EnquiryProductsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.EnquiryProduct;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnquiryProductsFragment extends Fragment {
    private TextView textViewEmpty;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private EnquiryProductsAdapter mAdapter;
    private ImageView imageView;
    private List<EnquiryProduct> enquiryProductList = new ArrayList<>();
    private String enId;

    public EnquiryProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        enId = bundle.getString(getString(R.string.en_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotation_product_info, container, false);
        initializeView(view);
        return view;
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                // if (quotationList != null && quotationList.size() == 0) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                                //  }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void initializeView(View rootView) {
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

        mAdapter = new EnquiryProductsAdapter(getActivity(), enquiryProductList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (enquiryProductList != null && enquiryProductList.size() > 0) {
                enquiryProductList.clear();
            }
            showLoader();
            getProductsList();
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getProductsList() {
        String task = getString(R.string.fetch_enquiry_products_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getEnquiryProductsList(version, key, task, userId, accessToken,
                enId);

        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse != null && apiResponse.getSuccess()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewEmpty.setVisibility(View.GONE);

                        for (EnquiryProduct enquiryProduct : apiResponse.getData().getEnquiryProducts()) {
                            if (enquiryProduct != null) {
                                enquiryProductList.add(enquiryProduct);
                            }
                        }

                        if (enquiryProductList != null && enquiryProductList.size() == 0) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            textViewEmpty.setText("No products.");
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {
                        if (apiResponse != null && apiResponse != null &&
                                apiResponse.getSuccessCode() != null && apiResponse.getSuccessCode().equals("10001")) {
                            textViewEmpty.setText(getString(R.string.no_data_available));
                            recyclerView.setVisibility(View.GONE);
                            textViewEmpty.setVisibility(View.VISIBLE);
                        }
                        //Deleted User
                        else if (apiResponse != null && apiResponse != null &&
                                apiResponse.getSuccessCode() != null &&
                                (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN))) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                        if (enquiryProductList != null && enquiryProductList.size() == 0) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            textViewEmpty.setText(getString(R.string.no_data_available));
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    hideLoader();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }


}
