package com.origin.esports;

import android.app.Application;

import com.onesignal.OneSignal;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initialization();
    }
    private void initialization() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();
    }
}
