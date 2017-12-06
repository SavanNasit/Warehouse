package com.accrete.warehouse.password;

/**
 * Created by poonam on 7/4/17.
 */


public interface PasswordInteractor {

    interface OnLoginFinishedListener {


        void onPasswordError();

        void onSuccess();
    }

    void login(String username, String password, OnLoginFinishedListener listener);

}
