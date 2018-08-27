package com.infosolutions.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by shailesh on 17/3/18.
 */

public class AlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
            Log.v("Alarm Manager", "ACTION_DATE_CHANGED received");
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }
}
