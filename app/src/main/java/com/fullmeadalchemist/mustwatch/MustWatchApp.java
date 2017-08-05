package com.fullmeadalchemist.mustwatch;

import android.app.Activity;
import android.app.Application;

import com.fullmeadalchemist.mustwatch.di.AppInjector;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;


public class MustWatchApp extends Application implements HasActivityInjector {

    public static final String MUST_WATCH_SHARED_PREFS = "MUST_WATCH_SHARED_PREFS";

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (BuildConfig.DEBUG) {
//            Timber.plant(new Timber.DebugTree());
//        }
        AppInjector.init(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
