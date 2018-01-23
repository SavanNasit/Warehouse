package com.accrete.warehouse.fragment.runningorders;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accrete.warehouse.R;
import com.accrete.warehouse.fragment.createpackage.AlreadyCreatedPackagesFragment;
import com.accrete.warehouse.fragment.createpackage.PackageDetailsFragment;
import com.accrete.warehouse.fragment.createpackage.PendingItemsFragment;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class RunningOrdersExecuteFragment extends Fragment {

    public static ViewPager viewPagerExecute;
    private TabLayout tabLayoutExecute;
    private Packages packages = new Packages();
    private List<Packages> packagesList = new ArrayList<>();
    private Bundle bundle;
    private String chkid, chkoid;
    private ArrayList<PendingItems> pendingItems;
    private int listSize;

    List<SelectOrderItem> selectOrderItems = new ArrayList<>();
    List<PendingItems> pendingItemsLists = new ArrayList<>();
    String strChkoid;
    private int strQuantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_running_execute_orders, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        viewPagerExecute = (ViewPager) rootView.findViewById(R.id.view_pager_execute);
        bundle = this.getArguments();
        if (bundle != null) {
            packagesList = bundle.getParcelableArrayList("packages");
            pendingItems = bundle.getParcelableArrayList("pendingItems");
            chkid = bundle.getString("chkid");
            chkoid = bundle.getString("chkoid");
            Log.d("running order list size", String.valueOf(packagesList.size()));

        }
        setupViewPagerExecute(viewPagerExecute);
        viewPagerExecute.setOffscreenPageLimit(3);
        tabLayoutExecute = (TabLayout) rootView.findViewById(R.id.tabs_execute);
        tabLayoutExecute.setupWithViewPager(viewPagerExecute);

        tabLayoutExecute.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerExecute.setCurrentItem(tab.getPosition());

            if(tab.getPosition()==1){
                PackageDetailsFragment packageDetailsFragment =
                        (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
                packageDetailsFragment.getOrderItem(selectOrderItems, pendingItemsLists, chkoid,strQuantity);
            }else if(tab.getPosition()==2){
                    AlreadyCreatedPackagesFragment alreadyCreatedPackagesFragment =
                            (AlreadyCreatedPackagesFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
                    alreadyCreatedPackagesFragment.getPackageList(packagesList);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void setupViewPagerExecute(ViewPager viewPagerExecute) {
        PendingItemsFragment pendingItemsFragment = new PendingItemsFragment();
        PackageDetailsFragment packageDetailsFragment = new PackageDetailsFragment();
        AlreadyCreatedPackagesFragment alreadyCreatedPackagesFragment = new AlreadyCreatedPackagesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("packagesList", (ArrayList<? extends Parcelable>) packagesList);
        alreadyCreatedPackagesFragment.setArguments(bundle);

        Bundle bundlePendingItems = new Bundle();
        bundlePendingItems.putString("chkid", chkid);
        bundlePendingItems.putString("chkoid", chkoid);
        bundlePendingItems.putParcelableArrayList("pendingItems", pendingItems);
        pendingItemsFragment.setArguments(bundlePendingItems);

        viewPagerExecuteAdapter adapter = new viewPagerExecuteAdapter(getChildFragmentManager());
        adapter.addFragment(pendingItemsFragment, "Pending Items");
        adapter.addFragment(packageDetailsFragment, "Package Details");
        adapter.addFragment(alreadyCreatedPackagesFragment, "Packages");
        viewPagerExecute.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment) + " " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE) + chkoid);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment) + " " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE) + chkoid);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment) + " " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE) + chkoid);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.running_orders_execute_fragment) + " " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE) + chkoid);
        }
    }

    public void getData(String str) {
        /*Fragment newCurrentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.running_orders_container);
        if (newCurrentFragment instanceof PendingItemsFragment) {
            ((PendingItemsFragment) newCurrentFragment).getData(str);
            Log.e("TAG_ORDERS", "" + str);
        }*/
        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 0) {
            Log.e("TAG_ORDERS", "" + str);
            PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.getData(str);

        }
    }

    public void sendPackageDetails(List<Packages> packages) {
        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 2) {
            Log.e("TAG_ORDERS", "" + packages.size());
            AlreadyCreatedPackagesFragment alreadyCreatedPackagesFragment =
                    (AlreadyCreatedPackagesFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            alreadyCreatedPackagesFragment.sendPackageDetails(packages);

        }
    }

    public void getOrderItemList(List<SelectOrderItem> selectOrderItem, List<PendingItems> pendingItemsList, String chkoid, int allocatedQuantity, int pos) {
        listSize = pendingItemsLists.size();
        pendingItemsLists.addAll(pendingItemsList);
        selectOrderItems.addAll(selectOrderItem);
        strChkoid = chkoid;
        strQuantity = allocatedQuantity;


        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 0) {
            Log.e("TAG_ORDERS", "" + selectOrderItems.size());


            PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.getAllocatedQuantity(allocatedQuantity,pendingItemsList,pos);

        } else if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 1) {
            PackageDetailsFragment packageDetailsFragment =
                    (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            packageDetailsFragment.getOrderItem(selectOrderItems, pendingItemsList, chkoid, strQuantity);
        }
    }

    public void scanBarcode() {
        if (viewPagerExecute != null && viewPagerExecute.getCurrentItem() == 0) {
            PendingItemsFragment pendingItemsFragment =
                    (PendingItemsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            pendingItemsFragment.scanBarcode();

            Log.d("PERMISSION", "ROEF");
        }
    }


 /*   public void getOrderItemList(ArrayList<Parcelable> selectedOrderItems) {
        if(viewPagerExecute !=null && viewPagerExecute.getCurrentItem() == 1) {
            Log.e("TAG_ORDERS", "" + selectedOrderItems);
            PackageDetailsFragment packageDetailsFragment =
                    (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            packageDetailsFragment.getOrderItem(selectedOrderItems);

        }
    }*/

    class viewPagerExecuteAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public viewPagerExecuteAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
