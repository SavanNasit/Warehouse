package com.accrete.warehouse.fragment.receiveConsignment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;

/**
 * Created by poonam on 12/5/17.
 */

public class ReceiveConsignmentFragment extends Fragment {

    private static final String KEY_TITLE = "ReceiveConsignment";
    private ViewPager viewPagerReceive;
    private TabLayout tabLayoutReceive;
    private TextView fragmentReceiveConsignmentReceiveDirectly;
    private TextView fragmentReceiveConsignmentReceivePo;

    public static ReceiveConsignmentFragment newInstance(String title) {
        ReceiveConsignmentFragment f = new ReceiveConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_consignment, container, false);
        findViews(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_consignment_fragment));
        }
    }


    private void findViews(View rootView) {
            fragmentReceiveConsignmentReceiveDirectly = (TextView)rootView.findViewById( R.id.fragment_receive_consignment_receive_directly );
            fragmentReceiveConsignmentReceivePo = (TextView)rootView.findViewById( R.id.fragment_receive_consignment_receive_po );

        fragmentReceiveConsignmentReceiveDirectly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceiveDirectlyFragment receiveDirectlyFragment = new ReceiveDirectlyFragment();
                  //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.receive_consignment_container, receiveDirectlyFragment).addToBackStack(null).commit();

                }
            });

        fragmentReceiveConsignmentReceivePo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Fragment receiveAgainstPurchaseOrderFragment = ReceiveAgainstPurchaseOrderFragment.newInstance(getString(R.string.receive_po));
                   // getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveAgainstPurchaseOrderFragment).commitAllowingStateLoss();

                    ReceiveAgainstPurchaseOrderFragment receiveAgainstPurchaseOrderFragment = new ReceiveAgainstPurchaseOrderFragment();
                    //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.receive_consignment_container, receiveAgainstPurchaseOrderFragment).addToBackStack(null).commit();



                }
            });

    }


  /*  private void findViews(View rootView) {
        viewPagerReceive = (ViewPager)rootView.findViewById(R.id.view_pager_receive_consignment);
        setupViewPagerExecute(viewPagerReceive);
        viewPagerReceive.setOffscreenPageLimit(2);
        tabLayoutReceive = (TabLayout)rootView.findViewById(R.id.tabs_receive_consignment);
        tabLayoutReceive.setupWithViewPager(viewPagerReceive);

        tabLayoutReceive.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerReceive.setCurrentItem(tab.getPosition());
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
        ReceiveDirectlyFragment receiveDirectlyFragment = new ReceiveDirectlyFragment();
        ReceiveAgainstPurchaseOrderFragment receiveAgainstPurchaseOrderFragment = new ReceiveAgainstPurchaseOrderFragment();

        viewPagerExecuteAdapter adapter = new viewPagerExecuteAdapter(getChildFragmentManager());
        adapter.addFragment(receiveDirectlyFragment, "Receive Directly");
        adapter.addFragment(receiveAgainstPurchaseOrderFragment, "Receive PO");
        viewPagerExecute.setAdapter(adapter);
    }

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
    }*/


}
