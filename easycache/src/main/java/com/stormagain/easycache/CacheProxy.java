package com.stormagain.easycache;

import android.support.annotation.Nullable;
import android.util.Log;

import com.stormagain.easycache.annotation.Cache;
import com.stormagain.easycache.annotation.Clear;
import com.stormagain.easycache.annotation.Key;
import com.stormagain.easycache.annotation.LoadCache;
import com.stormagain.easycache.annotation.RemoveKey;
import com.stormagain.example.Student;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

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
            this.name = CacheHelper.getCacheName(this.clazz);
            this.type = CacheHelper.getCacheType(this.clazz);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return handle(method, args);
        }

        private Object handle(final Method method, final Object[] args) {
            final ReturnInfo info = RxJava2Support.isReturnObs(method);
            if (info.hasRxObs) {

                Observable observable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        Object obj = handleAnnotations(method, args, info.returnType);
                        if (obj != null) {
                            e.onNext(obj);
                        } else {
                            e.onError(new Exception("no data"));
                        }
                    }
                });

                return observable;
            } else {
                return handleAnnotations(method, args, info.returnType);
            }
        }

        @Nullable
        private Object handleAnnotations(Method method, Object[] args, java.lang.reflect.Type t) {
            for (Annotation methodAnnotation : method.getAnnotations()) {
                Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
                if (annotationType == LoadCache.class) {
                    String key = ((LoadCache) methodAnnotation).key();
                    return CacheHelper.loadCache(name, key, t, type);
                } else if (annotationType == Cache.class) {
                    Annotation[][] parameterAnnotationArrays = method.getParameterAnnotations();
                    if (parameterAnnotationArrays.length > 0) {
                        Annotation[] parameterAnnotations = parameterAnnotationArrays[0];
                        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                            Annotation parameterAnnotation = parameterAnnotations[0];
                            Class<? extends Annotation> innerAnnotationType = parameterAnnotation.annotationType();
                            if (innerAnnotationType == Key.class) {
                                String key = ((Key) parameterAnnotation).value();
                                CacheHelper.cache(name, key, Utils.gson.toJson(args[0]), type);
                            }
                        }
                    }
                } else if (annotationType == RemoveKey.class) {
                    String[] keys = ((RemoveKey) methodAnnotation).value();
                    CacheHelper.removeKey(name, keys, type);
                } else if (annotationType == Clear.class) {
                    CacheHelper.clear(name, type);
                }
            }
            return null;
        }
    }
}
