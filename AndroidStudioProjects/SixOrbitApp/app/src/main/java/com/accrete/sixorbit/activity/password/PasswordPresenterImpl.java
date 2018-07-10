package com.accrete.sixorbit.activity.password;

/**
 * Created by poonam on 7/4/17.
 */

public class PasswordPresenterImpl implements PasswordPresenter, PasswordInteractor.OnLoginFinishedListener {

    private PasswordView passwordView;
    private PasswordInteractor passwordInteractor;

    public PasswordPresenterImpl(PasswordView passwordView) {
        this.passwordView = passwordView;
        this.passwordInteractor = new PasswordInteractorImpl();
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (passwordView != null) {
            passwordView.showProgress();
        }

        passwordInteractor.login(username, password, this);
    }

    @Override
    public void onDestroy() {
        passwordView = null;
    }


    @Override
    public void onPasswordError() {
        if (passwordView != null) {
            passwordView.setPasswordError();
            passwordView.hideProgress();
        }
    }

    @Override
    public void onSuccess() {
        if (passwordView != null) {
            passwordView.navigateToHome();
        }
    }
}
