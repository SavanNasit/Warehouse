package com.accrete.sixorbit.fragmentActivity;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.domain.DomainActivity;
import com.accrete.sixorbit.activity.login.LoginActivity;
import com.accrete.sixorbit.activity.password.PasswordActivity;
import com.accrete.sixorbit.adapter.LoginBasePagerAdapter;

import java.util.List;
import java.util.Vector;


/**
 * Created by poonam on 7/4/17.
 */

public class LoginBase extends FragmentActivity implements View.OnClickListener {


    public static ViewPager viewPager;
    private LoginBasePagerAdapter mPagerAdapter;
    private TextView textviewNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.postponeEnterTransition(this);
        setContentView(R.layout.activity_login_base);
        initializePaging();
    }

    private void initializePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, DomainActivity.class.getName()));
        fragments.add(Fragment.instantiate(this, LoginActivity.class.getName()));
        fragments.add(Fragment.instantiate(this, PasswordActivity.class.getName()));

        mPagerAdapter = new LoginBasePagerAdapter(super.getSupportFragmentManager(), fragments);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        textviewNext = (TextView) findViewById(R.id.login_base_next);
        textviewNext.setOnClickListener(this);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(this.mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                statusBarColorChange(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void statusBarColorChange(int position) {

        if (position == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.blueTurquoiseLight));
                textviewNext.setTextColor(getResources().getColor(R.color.blueTurquoiseLight));
            }
        } else if (position == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.lightGreen));
                textviewNext.setTextColor(getResources().getColor(R.color.lightGreen));
            }
        } else if (position == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.lightViolet));
                textviewNext.setTextColor(getResources().getColor(R.color.lightViolet));
            }
        }
    }

    @Override
    public void onClick(View v) {

     /* if (viewPager.getCurrentItem() == 0) {
            statusBarColorChange(0);
            LoginActivity loginFragment = new LoginActivity();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.domain_container, loginFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (viewPager.getCurrentItem() == 1) {

          statusBarColorChange(1);
          PasswordActivity passwordFragment = new PasswordActivity();
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.replace(R.id.login_container, passwordFragment);
          transaction.addToBackStack(null);
          transaction.commit();

        } else if (viewPager.getCurrentItem() == 2) {

        }*/

        //viewPager.setCurrentItem(getItem(+1), true);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


}
