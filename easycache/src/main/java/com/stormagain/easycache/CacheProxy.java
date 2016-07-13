package com.stormagain.easycache;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
            for (Annotation methodAnnotation : method.getAnnotations()) {
                Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
                if (annotationType == LoadCache.class) {
                    String key = ((LoadCache) methodAnnotation).key();
                    Class clazz = ((LoadCache) methodAnnotation).classType();
                    Class collection = ((LoadCache) methodAnnotation).collectionType();
                    if (collection == ArrayList.class || collection == LinkedList.class || collection == List.class) {
                        return CacheHelper.loadListCache(name, key, clazz, collection, type);
                    } else if (collection == Set.class || collection == HashSet.class || collection == TreeSet.class) {
                        if (collection == TreeSet.class) {
                            return new TreeSet<>(CacheHelper.loadListCache(name, key, clazz, List.class, type));
                        }
                        return new HashSet<>(CacheHelper.loadListCache(name, key, clazz, List.class, type));
                    } else {
                        return CacheHelper.loadCache(name, key, clazz, type);
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
                                CacheHelper.cache(name, key, Utils.gson.toJson(args[0]), type);
                            }
                        }
                    }
                } else if (annotationType == RemoveKey.class) {
                    String key = ((RemoveKey) methodAnnotation).key();
                    CacheHelper.removeKey(name, key, type);
                } else if (annotationType == Clear.class) {
                    CacheHelper.clear(name, type);
                }
            }
            return null;
        }
    }
}
