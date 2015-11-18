package com.stormagain.easycache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;


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
        cacheProxy=new CacheProxy();
    }

    public void setup(Context context) {
        if (mContext == null) {
            this.mContext = context.getApplicationContext();
        }
    }

    public void cache(String name, String key, String t) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, t);
        apply(editor);
    }

    public <T> T loadCache(String name, String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(key, "");
        if (!TextUtils.isEmpty(data)) {
            return Utils.gson.fromJson(data, clazz);
        }
        return null;
    }


    public String getCacheName(Class<?> clazz) {
        if (clazz.getAnnotation(EasySpCache.class) != null) {
            EasySpCache easySpCache = clazz.getAnnotation(EasySpCache.class);
            return easySpCache.name();
        } else {
            return clazz.getSimpleName();
        }
    }

    public void clear(String name) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        apply(editor);
    }

    public void clear(Class<?> clazz) {
        String name = getCacheName(clazz);
        clear(name);
    }

    private void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    private Context getContext() {
        return mContext;
    }

    public CacheProxy getCacheProxy() {
        return cacheProxy;
    }
}
