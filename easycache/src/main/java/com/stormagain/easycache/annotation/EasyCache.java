package com.stormagain.easycache.annotation;

import com.stormagain.easycache.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 37X21=777 on 15/9/24.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyCache {

    String name() default "";

    Type type() default Type.SHARED_PREFERENCE;
}
