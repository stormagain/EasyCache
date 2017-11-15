package com.stormagain.easycache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by 37X21=777 on 16/7/13.
 */
public class CacheHelper {

    /**
     * @param name 文件名
     * @param key  存储Key
     * @param data 存储数据
     * @param type 存储方式
     *             缓存数据
     */
    public static Response cache(String name, String key, String data, Type type) {
        Response response = new Response();
        switch (type) {
            case FILE_IN_APP:
                try {
                    File fileInApp = new File(getFileInAppPath(name));
                    String historyData = Utils.readStringFromFile(fileInApp);
                    JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                    jsonObject.put(key, data);
                    Utils.writeStringToFile(fileInApp, jsonObject.toString());
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case FILE_ON_DISK:
                try {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    String historyData = Utils.readStringFromFile(fileOnDisk);
                    JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                    jsonObject.put(key, data);
                    Utils.writeStringToFile(fileOnDisk, jsonObject.toString());
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case SHARED_PREFERENCE:
            default:
                try {
                    SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, data);
                    Utils.apply(editor);
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
        }
        return response;
    }

    /**
     * @param name  文件名
     * @param key   存储key
     * @param clazz 实体类型
     * @param type  存储方式
     *              加载非集合型缓存数据
     */
    public static <T> Response<T> loadCache(String name, String key, java.lang.reflect.Type clazz, Type type) {
        Response<T> response = new Response<>();
        switch (type) {
            case FILE_IN_APP:
                try {
                    File fileInApp = new File(getFileInAppPath(name));
                    String data = Utils.readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            response.object = Utils.gson.fromJson(value, clazz);
                        } else {
                            response.error = new IOException("The specified key:" + key + " is not found");
                        }
                    } else {
                        response.error = new IOException("No history cache for the specified key:" + key);
                    }
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case FILE_ON_DISK:
                try {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    String data = Utils.readStringFromFile(fileOnDisk);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            response.object = Utils.gson.fromJson(value, clazz);
                        } else {
                            response.error = new IOException("The specified key:" + key + " is not found");
                        }
                    } else {
                        response.error = new IOException("No history cache for the specified key:" + key);
                    }
                } catch (Throwable e) {
                    response.error = e;
                }

                break;
            case SHARED_PREFERENCE:
            default:
                try {
                    SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                    if (sharedPreferences.contains(key)) {
                        String data = sharedPreferences.getString(key, "");
                        if (!TextUtils.isEmpty(data)) {
                            response.object = Utils.gson.fromJson(data, clazz);
                        } else {
                            response.error = new IOException("The specified key:" + key + " is no data");
                        }
                    } else {
                        response.error = new IOException("The specified key:" + key + " is not found");
                    }
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
        }
        return response;
    }


    /**
     * @param name 文件名
     * @param keys 存储key集合
     * @param type 存储类型
     *             删除对应的key
     */
    public static Response removeKey(String name, String[] keys, Type type) {
        Response response = new Response();
        switch (type) {
            case FILE_IN_APP:
                try {
                    File fileInApp = new File(getFileInAppPath(name));
                    String data = Utils.readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        for (String key : keys) {
                            jsonObject.remove(key);
                        }
                        Utils.writeStringToFile(fileInApp, jsonObject.toString());
                        response.object = true;
                    } else {
                        response.error = new IOException("No history cache for the specified file:" + name);
                    }
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case FILE_ON_DISK:
                try {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    String data = Utils.readStringFromFile(fileOnDisk);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        for (String key : keys) {
                            jsonObject.remove(key);
                        }
                        Utils.writeStringToFile(fileOnDisk, jsonObject.toString());
                        response.object = true;
                    } else {
                        response.error = new IOException("No history cache for the specified file:" + name);
                    }
                } catch (Throwable e) {
                    response.error = e;
                }

                break;
            case SHARED_PREFERENCE:
            default:
                try {
                    SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    for (String key : keys) {
                        if (sharedPreferences.contains(key)) {
                            editor.remove(key);
                        }
                    }
                    Utils.apply(editor);
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
        }

        return response;
    }

    /**
     * @param name 文件名
     * @param type 存储类型
     *             清除文件
     */
    public static Response clear(String name, Type type) {
        Response response = new Response();
        switch (type) {
            case FILE_IN_APP:
                try {
                    File fileInApp = new File(getFileInAppPath(name));
                    if (fileInApp.exists()) {
                        fileInApp.delete();
                    }
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case FILE_ON_DISK:
                try {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    if (fileOnDisk.exists()) {
                        fileOnDisk.delete();
                    }
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
            case SHARED_PREFERENCE:
            default:
                try {
                    SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    Utils.apply(editor);
                    response.object = true;
                } catch (Throwable e) {
                    response.error = e;
                }
                break;
        }
        return response;
    }

    private static String getFileInAppPath(String name) {
        return EasyCacheManager.getInstance().getContext().getCacheDir().getPath() + File.separator + Utils.TAG + File.separator + name;
    }

    private static String getFileOnDiskPath(String name) {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + Utils.TAG + File.separator + EasyCacheManager.getInstance().getContext().getPackageName().replaceAll("\\.", "_") + File.separator + name;
    }


}
