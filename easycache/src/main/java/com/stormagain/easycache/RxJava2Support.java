package com.stormagain.easycache;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import io.reactivex.Observable;

/**
 * Created by 37X21=777 on 17/11/2.
 */

public class RxJava2Support {


    private static int rxJava2Status = -1;

    public static boolean hasRxJava2() {
        if (rxJava2Status == -1) {
            try {
                Class c = Class.forName("io.reactivex.Observable");
                if (c != null) {
                    rxJava2Status = 1;
                } else {
                    rxJava2Status = 0;
                }
            } catch (ClassNotFoundException e) {
                //ignore
                e.printStackTrace();
            }
        }

        return rxJava2Status == 1;
    }


    public static ReturnInfo isReturnObs(Method method) {
        ReturnInfo info = new ReturnInfo();
        java.lang.reflect.Type responseObjectType;
        java.lang.reflect.Type returnType = method.getGenericReturnType();
        if (returnType != void.class) {
            if (hasRxJava2()) {
                Class rawReturnType = TypeUtils.getRawType(returnType);
                if (rawReturnType == Observable.class) {
                    returnType = TypeUtils.getSupertype(returnType, rawReturnType, Observable.class);
                    responseObjectType = TypeUtils.getParameterUpperBound((ParameterizedType) returnType);
                    info.hasRxObs = true;
                    info.returnType = responseObjectType;
                    return info;
                }
            }

            info.hasRxObs = false;
            info.returnType = returnType;
        }
        return info;
    }


}
