package com.accrete.sixorbit.activity.password;

/**
 * Created by poonam on 7/4/17.
 */


public interface PasswordPresenter {
    void validateCredentials(String username, String password);

    void onDestroy();
}
