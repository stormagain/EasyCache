package com.stormagain.easycache;

import com.stormagain.easycache.annotation.Cache;
import com.stormagain.easycache.annotation.Clear;
import com.stormagain.easycache.annotation.Key;
import com.stormagain.easycache.annotation.LoadCache;
import com.stormagain.easycache.annotation.RemoveKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

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
                    if (collection != Object.class) {
                        if (Collection.class.isAssignableFrom(collection)) {
                            List list = CacheHelper.loadListCache(name, key, clazz, List.class, type);
                            try {
                                if (list != null) {
                                    Class clz = Class.forName(collection.getName());
                                    Constructor c = clz.getConstructor(Collection.class);
                                    c.setAccessible(true);
                                    return c.newInstance(list);
                                }
                            } catch (Throwable e) {
                                //ignore
                            }
                        }
                    }
                    return CacheHelper.loadCache(name, key, clazz, type);
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
