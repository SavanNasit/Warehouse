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
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;


/**
 * Created by poonam on 7/4/17.
 */


public class LoginSplashActivity extends AppCompatActivity {

    // SplashActivity screen timer
    private static int SPLASH_TIME_OUT = 20000;
    Animation animFadein, animRotate, animBounce, animFlip, animTogether;
    ImageView imgLogo;
    private CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_splash);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressbar);
        circularProgressBar.setProgressWithAnimation(200,2000);

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
            /*    // This method will be executed once the timer is over
                // Start your app main activity
                isUserFirstTime = Boolean.valueOf(AppPreferences.getIsUserFirstTime(LoginSplashActivity.this, AppUtils.USER_FIRST_TIME));
                // isUserFirstTime = Boolean.valueOf(CommonUtils.readSharedSetting(SplashActivity.this, PREF_USER_FIRST_TIME, null));
                if (isUserFirstTime) {
                    Intent homeIntent = new Intent(LoginSplashActivity.this, DrawerActivity.class);
                    startActivity(homeIntent);
                } else {
                    Intent introIntent = new Intent(LoginSplashActivity.this, AppIllustrationActivity.class);
                    startActivity(introIntent);
                }

                finish();*/
                Intent homeIntent = new Intent(LoginSplashActivity.this, DrawerActivity.class);
                startActivity(homeIntent);
                finish();

            }

        }, SPLASH_TIME_OUT);
    }
}