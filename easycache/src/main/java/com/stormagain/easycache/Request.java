package com.stormagain.easycache;

import com.stormagain.easycache.annotation.Cache;
import com.stormagain.easycache.annotation.Clear;
import com.stormagain.easycache.annotation.Key;
import com.stormagain.easycache.annotation.LoadCache;
import com.stormagain.easycache.annotation.RemoveKey;

import java.lang.annotation.Annotation;

/**
 * Created by 37X21=777 on 17/11/3.
 */

public class Request {

    //文件名
    public String name;
    //存储类型
    public Type type;

    //操作类型
    public int optType;
    //参数key
    public String key;
    //参数keys
    public String[] keys;
    //参数类型
    public java.lang.reflect.Type returnType;
    //方法的参数
    public Object[] args;

    public interface OptType {
        //存
        int CACHE = 1;
        //取
        int LOAD = 2;
        //删key
        int REMOVE = 3;
        //清空所有
        int CLEAR = 4;
    }

    public static Request createRequest(MethodInfo info, String name, Type t) {
        Request request = new Request();
        request.name = name;
        request.type = t;
        request.args = info.args;
        request.returnType = info.returnType;
        for (Annotation annotation : info.annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == LoadCache.class) {
                String key = ((LoadCache) annotation).key();
                request.key = key;
                request.optType = OptType.LOAD;
            } else if (annotationType == Cache.class) {
                Annotation[][] parameterAnnotationArrays = info.method.getParameterAnnotations();
                if (parameterAnnotationArrays.length > 0) {
                    Annotation[] parameterAnnotations = parameterAnnotationArrays[0];
                    if (parameterAnnotations != null && parameterAnnotations.length > 0) {
                        Annotation parameterAnnotation = parameterAnnotations[0];
                        Class<? extends Annotation> innerAnnotationType = parameterAnnotation.annotationType();
                        if (innerAnnotationType == Key.class) {
                            String key = ((Key) parameterAnnotation).value();
                            request.key = key;
                            request.optType = OptType.CACHE;
                        }
                    }
                }
            } else if (annotationType == RemoveKey.class) {
                String[] keys = ((RemoveKey) annotation).value();
                request.keys = keys;
                request.optType = OptType.REMOVE;

            } else if (annotationType == Clear.class) {
                request.optType = OptType.CLEAR;
            }
        }

        return request;
    }

}
