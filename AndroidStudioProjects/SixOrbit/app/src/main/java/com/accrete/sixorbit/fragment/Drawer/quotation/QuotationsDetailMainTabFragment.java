package com.accrete.sixorbit.fragment.Drawer.quotation;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.QuotationDetails;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.widgets.SmartFragmentStatePagerAdapter;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Created by {Anshul} on 26/3/18.
 */

public class QuotationsDetailMainTabFragment extends Fragment {
    public ViewPager viewPager;
    public QuotationDetails quotationDetails;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private String qoId, copiedText = "", qoSId, cuId;
    private ArrayList<ItemData> dataArrayList = new ArrayList<>();
    private ArrayList<FollowUp> followUpArrayList = new ArrayList<>();
    private PassMobileListener mMobileCallback;
    private PassSharingTextListener mInfoCallBack;
    private PassUsersEmailListener mEmailCallBack;
    private String sharingStr;
    private DatabaseHandler databaseHandler;
    private ImageView imageViewLoader;
    private RelativeLayout mainLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            qoId = bundle.getString(getString(R.string.qo_id));
            cuId = bundle.getString(getString(R.string.cuid));
        }
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
    public void onDetach() {
        mMobileCallback = null;
        mInfoCallBack = null;
        mEmailCallBack = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment_tabs, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        databaseHandler = new DatabaseHandler(getActivity());

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        imageViewLoader = (ImageView) view.findViewById(R.id.imageView_loader);
        mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);

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
                    showLoader();
                    getQuotationsInfo(qoId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshFragment() {
        try {
            final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
            int position = viewPager.getCurrentItem();
            if (position == 0) {
                if (mFragment instanceof QuotationBasicInfoFragment) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((QuotationBasicInfoFragment) mFragment).getCustomerInfo(quotationDetails);
                        }
                    }, 200);
                }
            } else if (position == 1) {
                if (mFragment instanceof QuotationProductInfoFragment) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((QuotationProductInfoFragment) mFragment).getData(dataArrayList);
                        }
                    }, 200);
                }
            } else if (position == 2) {
                if (mFragment instanceof QuotationFollowUpsFragment) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (quotationDetails != null && quotationDetails.getQosId() != null &&
                                    !quotationDetails.getQosId().isEmpty()) {
                                ((QuotationFollowUpsFragment) mFragment).
                                        updateList(quotationDetails.getQosId());
                            }
                        }
                    }, 200);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuotationFollowUps() {
        if (viewPager.getCurrentItem() == 2) {
            QuotationFollowUpsFragment quotationFollowUpsFragment =
                    (QuotationFollowUpsFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            quotationFollowUpsFragment.updateList(quotationDetails.getQosId());
        }
    }

    private void setupViewPager(ViewPager viewPager, String qoId) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.qo_id), qoId);
        bundle.putString(getString(R.string.qosid), qoId);
        bundle.putString(getString(R.string.cuid), cuId);

        QuotationBasicInfoFragment quotationBasicInfoFragment = new QuotationBasicInfoFragment();
        quotationBasicInfoFragment.setArguments(bundle);

        QuotationProductInfoFragment quotationProductInfoFragment = new QuotationProductInfoFragment();
        quotationProductInfoFragment.setArguments(bundle);

        QuotationFollowUpsFragment quotationFollowUpsFragment = new QuotationFollowUpsFragment();
        quotationFollowUpsFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Basic Info", "Product Details", "Follow Ups"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, quotationBasicInfoFragment);
                quotationBasicInfoFragment.getParentFragment();
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, quotationProductInfoFragment);
                quotationProductInfoFragment.getParentFragment();
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, quotationFollowUpsFragment);
                quotationFollowUpsFragment.getParentFragment();
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    private void sendDataToChildAndParent() {
        try {
            final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
            if (mFragment instanceof QuotationBasicInfoFragment) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((QuotationBasicInfoFragment) mFragment).getCustomerInfo(quotationDetails);
                    }
                }, 200);
            }

            if (getActivity() != null) {


                if (copiedText != null && !copiedText.isEmpty() && copiedText.length() > 0) {
                    copiedText = "";
                }


                //Name
                if (quotationDetails.getName() != null && !quotationDetails.getName().isEmpty()) {
                    copiedText = getString(R.string.customer_name) + " " + quotationDetails.getName() + "\n";
                }

                //Mobile
                if (quotationDetails.getMobile() != null && !quotationDetails.getMobile().isEmpty()) {
                    copiedText = copiedText + getString(R.string.customer_mobile) + " " + quotationDetails.getMobile() + "\n";
                }

                //Email
                if (quotationDetails.getEmail() != null && !quotationDetails.getEmail().isEmpty()) {
                    copiedText = copiedText + getString(R.string.customer_email) + " " + quotationDetails.getEmail() + "\n";
                }

                //Remark
        /*if (quotationDetails.getQuotationRemark() != null && !quotationDetails.getQuotationRemark().isEmpty()) {
            copiedText = copiedText+getString(R.string.quotation_remarks_title) + " " + quotationDetails.getQuotationRemark()+"\n";
        }*/

                sharingStr = copiedText;

                //Pass Info
                mInfoCallBack.passSharingTextOnLoad(sharingStr);

                //Pass Mobile
                mMobileCallback.passMobileOnLoad(quotationDetails.getMobile());

                //Pass Email
                mEmailCallBack.passUsersEmailOnLoad(quotationDetails.getEmail());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getQuotationsInfo(String qoId) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.task_fetch_quotations_info);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.fetchExistsQuotationData(version, key, task, userId, accessToken, qoId);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                if (dataArrayList != null && dataArrayList.size() > 0) {
                    dataArrayList.clear();
                }

                if (followUpArrayList != null && followUpArrayList.size() > 0) {
                    followUpArrayList.clear();
                }

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            quotationDetails = apiResponse.getData().getQuotationDetailsInfo().getQuotationDetails();


                            if (apiResponse.getData().getQuotationDetailsInfo().getQuotationItemsData() != null) {
                                for (ItemData itemData : apiResponse.getData().getQuotationDetailsInfo().getQuotationItemsData()) {
                                    //TODO - Updated On 10th May
                                    if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                                            !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                                        itemData.setPrice(new BigDecimal(((Constants.ParseDouble(itemData.getPrice())
                                                * Constants.ParseDouble(itemData.getItemTax())) / 100) +
                                                Constants.ParseDouble(itemData.getPrice()))
                                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                    }
                                    dataArrayList.add(itemData);
                                }
                            }


                            if (apiResponse.getData().getQuotationDetailsInfo().getFollowupData() != null) {
                                for (FollowUp followUp : apiResponse.getData().getQuotationDetailsInfo().getFollowupData()) {
                                    if (followUp.getContactPersonMobile() == null ||
                                            followUp.getContactPersonMobile().isEmpty()) {
                                        followUp.setContactPersonMobile(quotationDetails.getMobile());
                                    }
                                    if (followUp.getContactPerson() == null ||
                                            followUp.getContactPerson().isEmpty()) {
                                        followUp.setContactPerson(quotationDetails.getName());
                                    }
                                    if (followUp.getContactPersonEmail() == null ||
                                            followUp.getContactPersonEmail().isEmpty()) {
                                        followUp.setContactPersonEmail(quotationDetails.getEmail());
                                    }
                                    followUpArrayList.add(followUp);
                                    if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                        if (databaseHandler.checkFollowUpResult(followUp.getFoid())) {
                                            databaseHandler.updateFollowUp(followUp);
                                        } else {
                                            if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty()
                                                    && databaseHandler.checkSyncIdFollowUp(followUp.getSyncId())) {
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
                        if (getActivity() != null) {
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