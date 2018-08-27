package com.infosolutions.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by shailesh on 1/9/17.
 */

public class InternetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            // CONNECTED
            Log.d("CONNECTION STATUS", "CONNECTED!");

            //set alarm manager and then call for pending intent
            Intent service_intent = new Intent(context, UploadService.class);
            context.startService(service_intent);
        } else {
            // NOT CONNECTED
            Log.d("CONNECTION STATUS", "NOT CONNECTED!");
        }
    }

}

