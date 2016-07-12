package com.stormagain.easycache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 37X21=777 on 15/9/24.
 */
public final class EasyCacheManager {

    private static volatile EasyCacheManager manager;
    private Context mContext;
    private CacheProxy cacheProxy;

    public static EasyCacheManager getInstance() {
        if (manager == null) {
            synchronized (EasyCacheManager.class) {
                if (manager == null) {
                    manager = new EasyCacheManager();
                }
            }
        }
        return manager;
    }

    private EasyCacheManager() {
        super();
        cacheProxy = new CacheProxy();
    }

    public void setup(Context context) {
        if (mContext == null) {
            this.mContext = context.getApplicationContext();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public <T> T create(Class<T> clazz) {
        return cacheProxy.create(clazz);
    }
}
