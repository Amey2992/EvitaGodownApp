package com.infosolutions.dagger;

import com.infosolutions.core.EvitaApplication;
import com.infosolutions.utils.EvitaPreferences;
import com.infosolutions.utils.GlobalVariables;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Named;
import javax.inject.Singleton;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Noman Khan on 12/09/17.
 */

@Module
public class EvitaModules {


    EvitaApplication evitaApplication;

    public EvitaModules(EvitaApplication evitaApplication) {
        this.evitaApplication = evitaApplication;
    }


    @Provides
    @Singleton
    public EvitaPreferences providesBlabPreferences() {
        return EvitaPreferences.getInstance(this.evitaApplication);
    }


    @Provides
    @Singleton
    @Named(GlobalVariables.Globals.MAIN_THREAD)
    Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @Named(GlobalVariables.Globals.NEW_THREAD)
    Scheduler provideNewThreadScheduler() {
        return Schedulers.newThread();
    }


    @Provides
    @Singleton
    EventBus providesBusProvider() {
        return EventBus.getDefault();
    }


}
