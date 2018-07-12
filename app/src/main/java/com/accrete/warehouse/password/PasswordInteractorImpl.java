package com.accrete.warehouse.password;

import android.os.Handler;
import android.text.TextUtils;

/**
 * Created by poonam on 7/4/17.
 */

public class PasswordInteractorImpl implements PasswordInteractor {

    Boolean gh;
    @Override
    public void login(final String username, final String password, final OnLoginFinishedListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean error = false;

                if (TextUtils.isEmpty(password)){
                    listener.onPasswordError();
                    error = true;
                    return;
                }
                if (!error){
                    listener.onSuccess();


                }
            }
        }, 2000);
    }

 }
