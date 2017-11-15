package com.stormagain.easycache;

import android.text.TextUtils;

import com.stormagain.easycache.annotation.EasyCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by 37X21=777 on 15/9/23.
 */
public final class CacheProxy {

    public <T> T create(Class<T> clazz) {
        Utils.validateClass(clazz);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new CacheHandler(clazz));
    }

    private class CacheHandler implements InvocationHandler {

        private Class<?> clazz;
        private String name;
        private Type type;

        public CacheHandler(Class<?> clazz) {
            this.clazz = clazz;
            this.name = getCacheName(this.clazz);
            this.type = getCacheType(this.clazz);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return handle(method, args);
        }

        private Object handle(final Method method, final Object[] args) {
            final MethodInfo info = MethodInfo.handleMethod(method, args);
            final Request request = Request.createRequest(info, name, type);
            if (info.hasRxObs) {
                return createObs(request);
            } else {
                Response response = EasyCacheManager.getInstance().getInterceptor().intercept(request);
                if (info.isVoid) {
                    return null;
                } else {
                    if (response != null) {
                        if (response.error != null) {
                            response.error.printStackTrace();
                        } else {
                            return response.object;
                        }
                    }
                }
                return null;
            }
        }
    }

    private Observable createObs(final Request request) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Response response = EasyCacheManager.getInstance().getInterceptor().intercept(request);
                if (response != null) {
                    if (response.error != null) {
                        e.onError(response.error);
                    } else {
                        e.onNext(response.object);
                    }
                } else {
                    e.onError(new Exception("no response"));
                }
            }
        });
    }


    /**
     * 获取文件名，如果没有指定，则以类名代替
     */
    public static String getCacheName(Class<?> clazz) {
        String name = "";
        if (clazz.getAnnotation(EasyCache.class) != null) {
            EasyCache easyCache = clazz.getAnnotation(EasyCache.class);
            name = easyCache.name();
        }
        if (TextUtils.isEmpty(name)) {
            name = clazz.getSimpleName();
        }
        return name;
    }

    /**
     * 获取存储类型，如果没有指定，则默认SHARED_PREFERENCE
     */
    public static Type getCacheType(Class<?> clazz) {
        Type type = null;
        if (clazz.getAnnotation(EasyCache.class) != null) {
            EasyCache easyCache = clazz.getAnnotation(EasyCache.class);
            type = easyCache.type();
        }

        if (type == null) {
            type = Type.SHARED_PREFERENCE;
        }

        return type;
    }
}
