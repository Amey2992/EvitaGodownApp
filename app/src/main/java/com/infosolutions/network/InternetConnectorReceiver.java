package com.infosolutions.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.infosolutions.core.EvitaApplication;


/**
 * Created by shailesh on 14/7/17.
 */

public class InternetConnectorReceiver extends BroadcastReceiver{

    public InternetConnectorReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            boolean isVisible = EvitaApplication.isActivityVisible();
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

            if (isVisible == true) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();

                //new SplashActivity().changeTextStatus(true);
//new SplashActivity().changeTextStatus(false);
                Constants.isNetworkAvailable = networkInfo != null && networkInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
