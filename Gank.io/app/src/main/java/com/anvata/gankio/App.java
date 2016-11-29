package com.anvata.gankio;

import android.app.Application;

import com.anvata.gankio.util.CrashUtils;

public class App extends Application {
    private static App INSTANCE;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        CrashUtils.getInstance().init(this);
    }
}
