package com.infosolutions.core;

import android.content.IntentFilter;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.infosolutions.dagger.DaggerEvitaComponents;
import com.infosolutions.dagger.EvitaComponents;
import com.infosolutions.dagger.EvitaModules;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Noman Khan on 12/09/17.
 */

public class EvitaApplication extends MultiDexApplication {

    public static EvitaComponents evitaComponents;
    public static EvitaApplication application;
    public static boolean activityVisible; // Variable that will check the
    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        application = this;

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        injectDependencies(application);
    }

    public static void injectDependencies(EvitaApplication context) {
        evitaComponents = DaggerEvitaComponents.builder()
                .evitaModules(new EvitaModules(context))
                .build();
        evitaComponents.inject(context);
    }

    public static EvitaComponents getEvitaComponents() {
        return evitaComponents;
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed
    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }
}
