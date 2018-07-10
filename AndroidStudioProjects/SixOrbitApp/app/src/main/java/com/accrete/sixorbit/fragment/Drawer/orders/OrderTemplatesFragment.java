package com.accrete.sixorbit.fragment.Drawer.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.quotations.OrderQuotationsSelectCustomerActivity;
import com.accrete.sixorbit.adapter.QuotationTemplatesAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.TemplatesData;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 29/3/18.
 */

public class OrderTemplatesFragment extends Fragment implements QuotationTemplatesAdapter.QuotationTemplatesAdapterListener {
    private TextView templateTitleTextView;
    private TextView createTextView;
    private ArrayList<TemplatesData> templatesDataArrayList = new ArrayList<>();
    private String status, ordertemid;
    private RecyclerView templatesRecyclerView;
    private TextView emptyTextView;
    private QuotationTemplatesAdapter quotationTemplatesAdapter;
    private ImageView imageView;

    public static OrderTemplatesFragment newInstance(String title) {
        OrderTemplatesFragment f = new OrderTemplatesFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_select_quotation_template, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        templateTitleTextView = (TextView) view.findViewById(R.id.template_title_textView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        createTextView = (TextView) view.findViewById(R.id.create_textView);
        templatesRecyclerView = (RecyclerView) view.findViewById(R.id.templates_recyclerView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        templatesRecyclerView.setLayoutManager(mLayoutManager);
        templatesRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        quotationTemplatesAdapter = new QuotationTemplatesAdapter(getActivity(), templatesDataArrayList, this);
        templatesRecyclerView.setAdapter(quotationTemplatesAdapter);

        //Get Templates
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (imageView.getVisibility() == View.GONE) {
                imageView.setVisibility(View.VISIBLE);
            }
            Ion.with(imageView)
                    .animateGif(AnimateGifMode.ANIMATE)
                    .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                    .withBitmapInfo();
            getQuotationTemplates(getActivity());
        }

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordertemid != null && !ordertemid.isEmpty()) {
                    Intent intent = new Intent(getActivity(), OrderQuotationsSelectCustomerActivity.class);
                    if (templatesDataArrayList.size() > 0) {
                        intent.putExtra(getString(R.string.order_id), ordertemid);
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Please select a template first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.create_order));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.create_order));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.create_order));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.create_order));
    }

    public void getQuotationTemplates(final Activity activity) {
        task = getString(R.string.order_templates_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (templatesDataArrayList != null && templatesDataArrayList.size() > 0) {
                            templatesDataArrayList.clear();
                        }
                        for (final TemplatesData templatesData : apiResponse.getData().getTemplatesData()) {
                            if (templatesData != null) {
                                templatesDataArrayList.add(templatesData);
                            }
                        }

                        //Templates Adapter
                        quotationTemplatesAdapter.notifyDataSetChanged();
                        if (templatesDataArrayList.size() > 0) {
                            emptyTextView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.VISIBLE);
                        }

                    } //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (templatesDataArrayList.size() > 0) {
                            emptyTextView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                        imageView.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                        imageView.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public void onBackPressed() {
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onMessageRowClicked(int position) {
        for (int i = 0; i < templatesDataArrayList.size(); i++) {
            if (i == position) {
                ordertemid = templatesDataArrayList.get(i).getId();
                templatesDataArrayList.get(i).setChecked(true);
            } else {
                templatesDataArrayList.get(i).setChecked(false);
            }
        }
        quotationTemplatesAdapter.notifyDataSetChanged();
    }

}
