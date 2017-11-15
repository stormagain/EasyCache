package com.stormagain.easycache;

/**
 * Created by 37X21=777 on 17/11/3.
 */

public interface Interceptor {

    Response<?> intercept(Request request);
}
