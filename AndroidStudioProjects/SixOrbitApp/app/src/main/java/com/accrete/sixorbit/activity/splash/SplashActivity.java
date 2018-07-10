package com.accrete.sixorbit.activity.splash;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AppIllustration.AppIllustrationActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.activity.password.PasswordActivity;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.time.LocalDate;
import java.util.Calendar;


/**
 * Created by poonam on 7/4/17.
 */


public class SplashActivity extends AppCompatActivity {

    // SplashActivity screen timer
    private static int SPLASH_TIME_OUT = 2000;
    Animation animFadein, animRotate, animBounce, animFlip, animTogether;
    ImageView imgLogo;
    private boolean isUserFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("chat", 0);

        // load the animation


        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                isUserFirstTime = Boolean.valueOf(AppPreferences.getIsUserFirstTime(SplashActivity.this, AppUtils.USER_FIRST_TIME));

                // isUserFirstTime = Boolean.valueOf(CommonUtils.readSharedSetting(SplashActivity.this, PREF_USER_FIRST_TIME, null));
                if (isUserFirstTime) {
                    Intent homeIntent = new Intent(SplashActivity.this, DrawerActivity.class);
                    startActivity(homeIntent);
                } else {
                    AppPreferences.setIsLogin(SplashActivity.this, AppUtils.ISLOGIN, false);
                    Intent introIntent = new Intent(SplashActivity.this, AppIllustrationActivity.class);
                    startActivity(introIntent);
                }

                finish();
            }

        }, SPLASH_TIME_OUT);
    }

}