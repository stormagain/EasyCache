package com.stormagain.example;

import android.app.Application;

import com.stormagain.easycache.EasyCacheManager;

/**
 * Created by 37X21=777 on 15/11/18.
 */
public class EasyCacheApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyCacheManager.getInstance().setup(this);
    }
}
