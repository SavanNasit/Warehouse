package com.accrete.sixorbit.fragment.Drawer.quotation;


import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerInfo;
import com.accrete.sixorbit.model.ExtraInfo;
import com.accrete.sixorbit.model.QuotationHistory;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationHistoryTabFragment extends Fragment {

    public ViewPager viewPager;
    private String qoId;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private CustomerInfo customerInfo;
    private ArrayList<QuotationHistory> historyArrayList = new ArrayList<>();
    private ExtraInfo extraInfo;

    public QuotationHistoryTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        qoId = bundle.getString(getString(R.string.qo_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager, String qoId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.qo_id), qoId);

        QuotationHistoryInfoFragment quotationHistoryInfoFragment = new QuotationHistoryInfoFragment();
        quotationHistoryInfoFragment.setArguments(bundle);

        QuotationHistoryListFragment quotationHistoryListFragment = new QuotationHistoryListFragment();
        quotationHistoryListFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Basic Info", "History"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, quotationHistoryInfoFragment);
                quotationHistoryInfoFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, quotationHistoryListFragment);
                quotationHistoryListFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    private void initializeView(View view) {

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(viewPager, qoId);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                refreshFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getQuotationsHistory(qoId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshFragment() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            if (mFragment instanceof QuotationHistoryInfoFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((QuotationHistoryInfoFragment) mFragment).getCustomerExtraInfo(customerInfo, extraInfo);
                    }
                }, 200);
            }
        } else if (position == 1) {
            if (mFragment instanceof QuotationHistoryListFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((QuotationHistoryListFragment) mFragment).getData(historyArrayList);
                    }
                }, 200);
            }
        }
    }

    public void getQuotationsHistory(String qoId) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.quotation_history_task);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.fetchExistsQuotationData(version, key, task, userId, accessToken, qoId);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));


                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            if (apiResponse.getData().getCustomerInfo() != null) {
                                customerInfo = apiResponse.getData().getCustomerInfo();
                            }
                            if (apiResponse.getData().getExtraInfo() != null) {
                                extraInfo = apiResponse.getData().getExtraInfo();
                            }

                            if (apiResponse.getData().getQuotationHistory() != null) {
                                for (QuotationHistory quotationHistory :
                                        apiResponse.getData().getQuotationHistory()) {
                                    historyArrayList.add(quotationHistory);
                                }
                            }
                            sendDataToChildAndParent();

                        }   //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        } else {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        if (getActivity() != null && isAdded()) {
                            Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDataToChildAndParent() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        if (mFragment instanceof QuotationHistoryInfoFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((QuotationHistoryInfoFragment) mFragment).getCustomerExtraInfo(customerInfo, extraInfo);
                }
            }, 200);
        } else if (mFragment instanceof QuotationHistoryListFragment) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((QuotationHistoryListFragment) mFragment).getData(historyArrayList);
                }
            }, 200);
        }
    }


    public class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static final String KEY_TITLE = "fragment_title";
        private static final String KEY_FRAGMENT = "fragment";
        private List<Map<String, Object>> maps = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragmentAndTitle(Map<String, Object> map) {
            maps.add(map);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) maps.get(position).get(KEY_TITLE);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) maps.get(position).get(KEY_FRAGMENT);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getCount() {
            return maps.size();
        }
    }
}
