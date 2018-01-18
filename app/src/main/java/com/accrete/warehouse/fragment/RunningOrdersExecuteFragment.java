package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackageDetailsList;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.RunningOrder;
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
    private String chkid,chkoid;
    private ArrayList<PendingItems> pendingItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_running_execute_orders, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        viewPagerExecute = (ViewPager) rootView.findViewById(R.id.view_pager_execute);
        bundle = this.getArguments();
        if (bundle != null) {
            packagesList = bundle.getParcelableArrayList("packages");
            pendingItems=  bundle.getParcelableArrayList("pendingItems");
            chkid=bundle.getString("chkid");
            chkoid=bundle.getString("chkoid");
            //String packages = bundle.getString("packages");
            //  Log.d("packages list size", String.valueOf(runningOrders.size()));
            Log.d("running order list size", String.valueOf(packagesList.size()));

       /*     for (int i = 0; i < runningOrders.size(); i++) {
                for (int j = 0; j < runningOrders.get(i).getPackages().size() ; j++) {
                    Log.d("packages list size", String.valueOf(runningOrders.get(i).getPackages().size()));
                    if (runningOrders.get(i).getPackages().get(j).getInvoiceDate()!= null) {
                        Toast.makeText(getActivity(),runningOrders.get(i).getPackages().get(j).getInvoiceDate(), Toast.LENGTH_SHORT).show();
                        Log.d("packages invoice date", runningOrders.get(i).getPackages().get(j).getPacid());

                    }


                }

            }*/

        }
        setupViewPagerExecute(viewPagerExecute);
        viewPagerExecute.setOffscreenPageLimit(3);
        tabLayoutExecute = (TabLayout) rootView.findViewById(R.id.tabs_execute);
        tabLayoutExecute.setupWithViewPager(viewPagerExecute);

        tabLayoutExecute.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerExecute.setCurrentItem(tab.getPosition());
          /*      if (tab.getPosition() == 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Code to be executed after desired time
                            PackageDetailsFragment packageDetailsFragment =
                                    (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
                            packageDetailsFragment.doRefresh();
                        }
                    }, 1 * 200);
                }*/
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
        bundlePendingItems.putParcelableArrayList("pendingItems",pendingItems);
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
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment)+" " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE)+chkoid);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment)+" " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE)+chkoid);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.running_orders_execute_fragment)+" " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE)+chkoid);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.running_orders_execute_fragment)+" " + AppPreferences.getCompanyCode(getActivity(), AppUtils.COMPANY_CODE)+chkoid);
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

    public void getOrderItemList(List<SelectOrderItem>selectOrderItems, List<PendingItems> pendingItemsLists, String chkoid) {
        if(viewPagerExecute !=null && viewPagerExecute.getCurrentItem() == 1) {
            Log.e("TAG_ORDERS", "" + selectOrderItems.size());
            PackageDetailsFragment packageDetailsFragment =
                    (PackageDetailsFragment) viewPagerExecute.getAdapter().instantiateItem(viewPagerExecute, viewPagerExecute.getCurrentItem());
            packageDetailsFragment.getOrderItem(selectOrderItems,pendingItemsLists,chkoid);

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
