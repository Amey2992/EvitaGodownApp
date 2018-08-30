package com.infosolutions.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Noman Khan on 12/09/17.
 */

public class GlobalVariables {

    public interface Globals {

        String MAIN_THREAD = "mMainThread";
        String NEW_THREAD = "mNewThread";

    }

    public interface Response {
        String SUCCESS = "Success";
        int RESPONSECODE = 200;
    }

    public interface LOGINKEY {
        String TYPE_USER = "USER";
        String TYPE_OWNER = "OWNER";
    }

    public static boolean permissionsEnabled(Activity activity, String[] requestPermissions) {
        int permissionsAvailable = 0;
        for (int i = 0; i < requestPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, requestPermissions[i]) ==
                    PackageManager.PERMISSION_GRANTED) {
                permissionsAvailable++;
            }
        }
        return permissionsAvailable == requestPermissions.length;

    }
}
