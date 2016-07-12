package com.stormagain.easycache;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
            this.name = Utils.getCacheName(this.clazz);
            this.type = Utils.getCacheType(this.clazz);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Annotation methodAnnotation : method.getAnnotations()) {
                Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
                if (annotationType == LoadCache.class) {
                    String key = ((LoadCache) methodAnnotation).key();
                    Class clazz = ((LoadCache) methodAnnotation).getClassType();
                    if (clazz.isArray()) {
                        return Utils.loadListCache(name, key, clazz, type);
                    } else {
                        return Utils.loadCache(name, key, clazz, type);
                    }
                } else if (annotationType == Cache.class) {
                    Annotation[][] parameterAnnotationArrays = method.getParameterAnnotations();
                    if (parameterAnnotationArrays.length > 0) {
                        Annotation[] parameterAnnotations = parameterAnnotationArrays[0];
                        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                            Annotation parameterAnnotation = parameterAnnotations[0];
                            Class<? extends Annotation> innerAnnotationType = parameterAnnotation.annotationType();
                            if (innerAnnotationType == Key.class) {
                                String key = ((Key) parameterAnnotation).value();
                                Utils.cache(name, key, Utils.gson.toJson(args[0]), type);
                            }
                        }
                    }
                } else if (annotationType == RemoveKey.class) {
                    String key = ((RemoveKey) methodAnnotation).key();
                    Utils.removeKey(name, key, type);
                } else if (annotationType == Clear.class) {
                    Utils.clear(name, type);
                }
            }
            return null;
        }
    }
}
