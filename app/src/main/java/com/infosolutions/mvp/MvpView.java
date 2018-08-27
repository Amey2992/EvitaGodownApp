package com.infosolutions.mvp;

/**
 * Created by Noman Khan on 12/09/17.
 */

public interface MvpView {
    void showProgress();

    void hideProgress();

    void showError(Exception arg0);

    void showError(Throwable e);
}