package com.infosolutions.dagger;

import com.infosolutions.core.EvitaApplication;
import com.infosolutions.ui.MainActivity;
import com.infosolutions.ui.login.LoginActivity;
import com.infosolutions.ui.owner.OwnerDashboardActivity;
import com.infosolutions.ui.user.commercial.CommercialActivity;
import com.infosolutions.ui.user.domestic.DomesticActivity;
import com.infosolutions.ui.user.setting.SettingsActivity;
import com.infosolutions.ui.user.stock.StockListActivity;
import com.infosolutions.ui.user.truckdelivery.TruckDeliveryActivity;
import com.infosolutions.ui.user.tvdetails.TVDetailsActivity;

import dagger.Component;
import javax.inject.Singleton;

/**
 * Created by Noman Khan on 12/09/17.
 */

@Component(modules = EvitaModules.class)
@Singleton
public interface EvitaComponents {

    void inject(EvitaApplication context);

    void inject(TruckDeliveryActivity truckDeliveryActivity);

    void inject(MainActivity mainActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(LoginActivity loginActivity);

    void inject(TVDetailsActivity tvDetailsActivity);

    void inject(DomesticActivity domesticActivity);

    void inject(StockListActivity filterActivity);

    void inject(CommercialActivity commercialActivity);

    void inject(OwnerDashboardActivity ownerActivity);
}
