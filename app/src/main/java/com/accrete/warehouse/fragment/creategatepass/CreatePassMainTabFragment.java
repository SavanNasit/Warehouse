package com.accrete.warehouse.fragment.creategatepass;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackedItem;
import com.accrete.warehouse.model.ShippingType;
import com.accrete.warehouse.utils.NonSwipeableViewPager;
import com.accrete.warehouse.widgets.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by poonam on 1/19/18.
 */

public class CreatePassMainTabFragment extends Fragment {
    public static NonSwipeableViewPager createGatepassViewpager;
    private String iscId;
    private ViewPagerAdapter viewPagerAdapter;

    private List<String> packageIdList = new ArrayList<>();
    private List<ShippingType> shippingTypesList = new ArrayList<>();
    private List<TransportMode> shippingByList = new ArrayList<>();
    private String strPacid, strPacdelgatpactid, strPacshtid, strShippingCompanyId, strVechileNumber;
    private String chkid;
    private String strTransporter;
    List<PackedItem> selectedPackedLists = new ArrayList<>();

    public static CreatePassMainTabFragment newInstance() {
        return new CreatePassMainTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  Bundle bundle = getArguments();
        iscId = bundle.getString(getString(R.string.iscid));*/
    }

    public void getResult(ArrayList<String> packageIdListToAdd, List<ShippingType> shippingTypes, List<TransportMode> shippingBy, String chkid, List<PackedItem> selectedPackedList) {
        packageIdList = packageIdListToAdd;
        shippingTypesList = shippingTypes;
        shippingByList = shippingBy;
        chkid = chkid;
        selectedPackedLists = selectedPackedList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_create_gatepass, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        createGatepassViewpager = (NonSwipeableViewPager) view.findViewById(R.id.create_gatepass_viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupViewPager(createGatepassViewpager, iscId);
        createGatepassViewpager.setOffscreenPageLimit(3);
        createGatepassViewpager.setAdapter(viewPagerAdapter);

        createGatepassViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().supportInvalidateOptionsMenu();
                final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(createGatepassViewpager.getCurrentItem());
                if (position == 0) {
                    if (mFragment instanceof PackageSelectionFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //((DetailsFragment) mFragment).doRefresh();
                            }
                        }, 200);
                    }
                } else if (position == 1) {
                    if (mFragment instanceof GatepassFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((GatepassFragment) mFragment).setShippingData(packageIdList, shippingTypesList, shippingByList,chkid);
                            }
                        }, 200);
                    }
                }
                else if (position == 2) {
                    if (mFragment instanceof ConfirmGatepassFragment) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((ConfirmGatepassFragment) mFragment).getData(strPacid, strPacdelgatpactid, strPacshtid, strShippingCompanyId, strVechileNumber,strTransporter,selectedPackedLists);
                            }
                        }, 200);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager, String iscId) {
        /*Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.iscid), iscId);*/

        PackageSelectionFragment packageSelectionFragment = new PackageSelectionFragment();
        //  packageSelectionFragment.setArguments(bundle);

        GatepassFragment gatepassFragment = new GatepassFragment();
        //  gatepassFragment.setArguments(bundle);

        ConfirmGatepassFragment confirmGatepassFragment = new ConfirmGatepassFragment();
        // confirmGatepassFragment.setArguments(bundle);

        String[] title_arr;
        title_arr = new String[]{"Package", "Create Package", "Confirm Gatepass"};


        for (int i = 0; i < title_arr.length; i++) {
            Map<String, Object> map = new Hashtable<>();
            map.put(ViewPagerAdapter.KEY_TITLE, title_arr[i]);
            if (i == 0) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, packageSelectionFragment);
                packageSelectionFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 1) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, gatepassFragment);
                gatepassFragment.setTargetFragment(this, getTargetRequestCode());
            } else if (i == 2) {
                map.put(ViewPagerAdapter.KEY_FRAGMENT, confirmGatepassFragment);
                confirmGatepassFragment.setTargetFragment(this, getTargetRequestCode());
            }
            viewPagerAdapter.addFragmentAndTitle(map);
        }
    }

    public void sendData(String pacid, String pacdelgatpactid, String pacshtid, String shippingCompanyId, String vehicleNumber,String transporterId) {
        strPacid = pacid;
        strPacdelgatpactid = pacdelgatpactid;
        strPacshtid = pacshtid;
        strShippingCompanyId = shippingCompanyId;
        strVechileNumber = vehicleNumber;
        strTransporter = transporterId;
    }

    public void onBackPressed() {
        final Fragment mFragment = viewPagerAdapter.getRegisteredFragment(createGatepassViewpager.getCurrentItem());
        if (mFragment instanceof CreatePassMainTabFragment) {
            ((PackageSelectionFragment) mFragment).onBackPressed();
        }else if(mFragment instanceof GatepassFragment) {
            ((GatepassFragment) mFragment).onBackPressed();
        }else if(mFragment instanceof ConfirmGatepassFragment) {
            ((ConfirmGatepassFragment) mFragment).onBackPressed();
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
    }
}

