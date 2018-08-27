package com.infosolutions.mvp;

/**
 * Created by Noman Khan on 12/09/17.
 */

public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}