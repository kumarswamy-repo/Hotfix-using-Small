package com.kumar.appupdatedemo;

import android.app.Application;

import net.wequick.small.Small;

/**
 * Created by kumara on 23/5/18.
 */

public class AppupdateApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Small.preSetUp(AppupdateApplication.this);
    }
}
