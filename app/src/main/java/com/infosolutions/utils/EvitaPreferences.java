package com.infosolutions.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Noman Khan on 12/09/17.
 */

public class EvitaPreferences {

    private static EvitaPreferences preferences;
    private static SharedPreferences sharedPreferences;

    public static synchronized EvitaPreferences getInstance(Context context) {
        synchronized (EvitaPreferences.class) {
            if (preferences == null) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences = new EvitaPreferences();
                return preferences;
            } else {
                return preferences;
            }
        }
    }
}
