package com.accrete.sixorbit.activity.password;

/**
 * Created by poonam on 7/4/17.
 */


public interface PasswordView {
    void showProgress();

    void hideProgress();

    void setPasswordError();

    void navigateToHome();
}
