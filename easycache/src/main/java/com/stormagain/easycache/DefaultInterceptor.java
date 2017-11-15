package com.stormagain.easycache;

/**
 * Created by 37X21=777 on 17/11/3.
 */

public class DefaultInterceptor implements Interceptor {

    @Override
    public Response intercept(Request request) {
        Response response = null;
        if (request.optType == Request.OptType.LOAD) {
            response = CacheHelper.loadCache(request.name, request.key, request.returnType, request.type);
        } else if (request.optType == Request.OptType.CACHE) {
            response = CacheHelper.cache(request.name, request.key, Utils.gson.toJson(request.args[0]), request.type);
        } else if (request.optType == Request.OptType.REMOVE) {
            response = CacheHelper.removeKey(request.name, request.keys, request.type);
        } else if (request.optType == Request.OptType.CLEAR) {
            response = CacheHelper.clear(request.name, request.type);
        }
        return response;
    }
}
