package com.infosolutions.factory;

import android.content.Context;
import android.content.Intent;
import com.infosolutions.ui.user.truckdelivery.TruckDeliveryActivity;

/**
 * Created by Noman Khan on 12/09/17.
 */

public class IntentFactory {
    public static final String TRUCKDELIVERYACTIVITY = "TruckDeliveryActivity";

    public static Intent getIntent(Context context, String type) {
        switch (type) {
            case TRUCKDELIVERYACTIVITY:
                return  new Intent(context, TruckDeliveryActivity.class);
            default:
                break;
        }

        return  null;
    }
}
