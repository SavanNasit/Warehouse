package com.accrete.sixorbit.fragment.Drawer.orders;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerOrderPackagesFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.OrderDetails;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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
 * Created by {Anshul} on 3/4/18.
 */

public class OrderDetailsMainTabFragment extends Fragment {
    public ViewPager viewPager;
    public OrderDetails orderDetails;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private String orderId, cuId, copiedText = "";
    private ArrayList<FollowUp> followUpArrayList = new ArrayList<>();
    private PassMobileListener mMobileCallback;
    private PassSharingTextListener mInfoCallBack;
    private PassUsersEmailListener mEmailCallBack;
    private String sharingStr;
    private DatabaseHandler databaseHandler;
    private ArrayList<ItemData> dataArrayList = new ArrayList<>();
    private ImageView imageViewLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        orderId = bundle.getString(getString(R.string.order_id));
        cuId = bundle.getString(getString(R.string.cuid));
    }

    @Override
    public void onAttach(Context con) {
        super.onAttach(con);
        try {
            mMobileCallback = (PassMobileListener) con;
            mInfoCallBack = (PassSharingTextListener) con;
            mEmailCallBack = (PassUsersEmailListener) con;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        mMobileCallback = null;
        mInfoCallBack = null;
        mEmailCallBack = null;
        super.onDetach();
    }

    public void updateOrderFollowUps() {
        if (viewPager.getCurrentItem() == 2) {
            OrderFollowupsFragment orderFollowupsFragment = (OrderFollowupsFragment)
                    viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            orderFollowupsFragment.updateList();
        }
    }

    private void hideLoader() {
        if (getActivity() != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private int getWidthScreen(Activity activity) {
        Configuration configuration = activity.getResources().getConfiguration();
        int mWidthScreen = configuration.smallestScreenWidthDp;
        return mWidthScreen;
    }

    private void initializeView(View view) {
        databaseHandler = new DatabaseHandler(getActivity());

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        imageViewLoader = (ImageView) view.findViewById(R.id.imageView_loader);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        int minWidth = 30;
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = minWidth;
        }

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(viewPager, orderId);
        viewPager.setOffscreenPageLimit(4);
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
                    showLoader();
                    getOrdersInfo(orderId);
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
            if (mFragment instanceof OrdersBasicInfoFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((OrdersBasicInfoFragment) mFragment).getDataFromParent(orderDetails);
                    }
                }, 200);
            }
        } else if (position == 1) {
            if (mFragment instanceof OrdersProductsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((OrdersProductsFragment) mFragment).getData(dataArrayList);
                    }
                }, 200);
            }
        } else if (position == 2) {
            if (mFragment instanceof OrderFollowupsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((OrderFollowupsFragment) mFragment).updateList();
                    }
                }, 200);
            }
        } else if (position == 3) {
            if (mFragment instanceof OrderCollectionsFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((OrderCollectionsFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        }
        else if (position == 4) {
            if (mFragment instanceof CustomerOrderPackagesFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomerOrderPackagesFragment) mFragment).doRefresh();
                    }
                }, 200);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager, String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.order_id), orderId);
        bundle.putString(getString(R.string.cuid), cuId);

        OrdersBasicInfoFragment ordersBasicInfoFragment = new OrdersBasicInfoFragment();
        ordersBasicInfoFragment.setArguments(bundle);

        OrdersProductsFragment ordersProductsFragment = new OrdersProductsFragment();
        ordersBasicInfoFragment.setArguments(bundle);

        OrderFollowupsFragment orderFollowupsFragment = new OrderFollowupsFragment();
        orderFollowupsFragment.setArguments(bundle);

        OrderCollectionsFragment orderCollectionsFragment = new OrderCollectionsFragment();
        orderCollectionsFragment.setArguments(bundle);

        CustomerOrderPackagesFragment customerOrderPackagesFragment = new CustomerOrderPackagesFragment();
        customerOrderPackagesFragment.setArguments(bundle);

        String[] title_arr = null;
        if (cuId != null && !cuId.isEmpty()) {
            title_arr = new String[]{"  Basic Info  ", "  Product Details  ", "  Follow Ups  ", "  Collections  ",
                    "  Packages  "};
        } else {
            title_arr = new String[]{"  Basic Info  ", "  Product Details  ", "  Follow Ups  ", "  Collections  "};
        }

        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, ordersBasicInfoFragment);
                ordersBasicInfoFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, ordersProductsFragment);
                ordersProductsFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, orderFollowupsFragment);
                orderFollowupsFragment.getParentFragment();
            } else if (i == 3) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, orderCollectionsFragment);
                orderCollectionsFragment.getParentFragment();
            } else if (i == 4) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, customerOrderPackagesFragment);
                customerOrderPackagesFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void getOrdersInfo(String orderId) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.task_fetch_order_info);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.fetchOrderDataWithSiteName(version, key, task, userId,
                        accessToken, orderId, "1");
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                if (dataArrayList != null && dataArrayList.size() > 0) {
                    dataArrayList.clear();
                }
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            orderDetails = apiResponse.getData().getCheckpointOrderData().getOrderDetails();

                            if (apiResponse.getData().getCheckpointOrderData().getOrderItemsData() != null) {
                                for (ItemData itemData : apiResponse.getData().getCheckpointOrderData().getOrderItemsData()) {
                                    dataArrayList.add(itemData);
                                }
                            }

                            if (apiResponse.getData().getCheckpointOrderData().getFollowupData() != null) {
                                for (FollowUp followUp : apiResponse.getData().getCheckpointOrderData().getFollowupData()) {
                                    if (followUp.getContactPersonMobile() == null ||
                                            followUp.getContactPersonMobile().isEmpty()) {
                                        followUp.setContactPersonMobile(orderDetails.getMobile());
                                    }
                                    if (orderDetails.getChkosid() != null && !orderDetails.getChkosid().isEmpty()) {
                                        followUp.setChkosid(orderDetails.getChkosid());
                                    }
                                    if (followUp.getContactPerson() == null ||
                                            followUp.getContactPerson().isEmpty()) {
                                        followUp.setContactPerson(orderDetails.getName());
                                    }
                                    if (followUp.getContactPersonEmail() == null ||
                                            followUp.getContactPersonEmail().isEmpty()) {
                                        followUp.setContactPersonEmail(orderDetails.getEmail());
                                    }
                                    followUpArrayList.add(followUp);
                                    if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                        if (databaseHandler.checkFollowUpResult(followUp.getFoid())) {
                                            databaseHandler.updateFollowUp(followUp);
                                        } else {
                                            if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && databaseHandler.checkSyncIdFollowUp(followUp.getSyncId())) {
                                                databaseHandler.updateFollowUpDataSyncId(followUp);
                                            } else {
                                                databaseHandler.insertFollowUpData(followUp);
                                            }
                                        }
                                    }
                                }
                            }

                            sendDataToChildAndParent();

                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            if (isAdded() && getActivity() != null) {
                                Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }  //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        } else {
                            if (isAdded() && getActivity() != null) {
                                Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        hideLoader();

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        if (getActivity() != null && isAdded()) {
                            Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                            hideLoader();
                        }
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    private void sendDataToChildAndParent() {
        try {
            final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
            if (mFragment instanceof OrdersBasicInfoFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((OrdersBasicInfoFragment) mFragment).getDataFromParent(orderDetails);
                    }
                }, 200);
            }
            if (getActivity() != null) {

                if (copiedText != null && !copiedText.isEmpty() && copiedText.length() > 0) {
                    copiedText = "";
                }

                //Name
                if (orderDetails.getName() != null && !orderDetails.getName().isEmpty()) {
                    copiedText = getString(R.string.customer_name) + " " + orderDetails.getName() + "\n";
                }

                //Mobile
                if (orderDetails.getMobile() != null && !orderDetails.getMobile().isEmpty()) {
                    copiedText = copiedText + getString(R.string.customer_mobile) + " " + orderDetails.getMobile() + "\n";
                }

                //Email
                if (orderDetails.getEmail() != null && !orderDetails.getEmail().isEmpty()) {
                    copiedText = copiedText + getString(R.string.customer_email) + " " + orderDetails.getEmail() + "\n";
                }


                sharingStr = copiedText;

                //Pass Info
                mInfoCallBack.passSharingTextOnLoad(sharingStr);

                //Pass Mobile
                mMobileCallback.passMobileOnLoad(orderDetails.getMobile());

                //Pass Email
                mEmailCallBack.passUsersEmailOnLoad(orderDetails.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        public int getCount() {
            return maps.size();
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}