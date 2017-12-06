package com.accrete.warehouse.splash;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.accrete.warehouse.AppIllustration.AppIllustrationActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.navigationView.DrawerActivity;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;


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
     /*   animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);

        animFlip = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.flip);
        animTogether = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.together);
        imgLogo.startAnimation(animBounce);*/

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
                    Intent introIntent = new Intent(SplashActivity.this, AppIllustrationActivity.class);
                    startActivity(introIntent);
                }

                finish();
            }

        }, SPLASH_TIME_OUT);
    }
}