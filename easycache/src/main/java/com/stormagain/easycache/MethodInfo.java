package com.stormagain.easycache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import io.reactivex.Observable;

/**
 * Created by 37X21=777 on 17/11/2.
 */

public class MethodInfo {

    //原始方法
    public Method method;

    //是否有Observable
    public boolean hasRxObs;

    //是否返回void
    public boolean isVoid;

    //返回类型，null表示void
    public java.lang.reflect.Type returnType;

    //方法的注解
    public Annotation[] annotations;

    //方法的参数
    public Object[] args;


    public static MethodInfo handleMethod(Method method, Object[] args) {
        MethodInfo info = new MethodInfo();
        info.method = method;
        info.args = args;
        info.annotations = method.getAnnotations();
        java.lang.reflect.Type responseObjectType;
        java.lang.reflect.Type returnType = method.getGenericReturnType();
        if (returnType != void.class) {
            if (RxJava2Support.hasRxJava2()) {
                Class rawReturnType = TypeUtils.getRawType(returnType);
                if (rawReturnType == Observable.class) {
                    returnType = TypeUtils.getSupertype(returnType, rawReturnType, Observable.class);
                    responseObjectType = TypeUtils.getParameterUpperBound((ParameterizedType) returnType);

                    info.isVoid = false;
                    info.hasRxObs = true;
                    info.returnType = responseObjectType;
                    return info;
                }
            }

            info.isVoid = false;
            info.hasRxObs = false;
            info.returnType = returnType;
            return info;
        }

        info.isVoid = true;
        info.hasRxObs = false;
        info.returnType = returnType;
        return info;
    }
}
