package com.stormagain.easycache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.stormagain.easycache.annotation.EasyCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
    public static void cache(String name, String key, String data, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String historyData = Utils.readStringFromFile(fileInApp);
                    JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                    jsonObject.put(key, data);
                    Utils.writeStringToFile(fileInApp, jsonObject.toString());
                } catch (JSONException | IOException e) {
                    Utils.logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String historyData = Utils.readStringFromFile(fileOnDisk);
                        JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                        jsonObject.put(key, data);
                        Utils.writeStringToFile(fileOnDisk, jsonObject.toString());
                    } catch (JSONException | IOException e) {
                        Utils.logError(Log.getStackTraceString(e));
                    }
                } else {
                    Utils.logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, data);
                Utils.apply(editor);
                break;
        }
    }

    /**
     * @param name  文件名
     * @param key   存储key
     * @param clazz 实体类型
     * @param type  存储方式
     *              加载非集合型缓存数据
     */
    public static <T> T loadCache(String name, String key, Class<T> clazz, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = Utils.readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            return Utils.gson.fromJson(value, clazz);
                        } else {
                            Utils.logError("The specified key:" + key + " is not found");
                        }
                    } else {
                        Utils.logError("No history cache for the specified key:" + key);
                    }

                } catch (JSONException | IOException e) {
                    Utils.logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = Utils.readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            String value = jsonObject.getString(key);
                            if (!TextUtils.isEmpty(value)) {
                                return Utils.gson.fromJson(value, clazz);
                            } else {
                                Utils.logError("The specified key:" + key + " is not found");
                            }
                        } else {
                            Utils.logError("No history cache for the specified key:" + key);
                        }

                    } catch (JSONException | IOException e) {
                        Utils.logError(Log.getStackTraceString(e));
                    }
                } else {
                    Utils.logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                if (sharedPreferences.contains(key)) {
                    String data = sharedPreferences.getString(key, "");
                    if (!TextUtils.isEmpty(data)) {
                        return Utils.gson.fromJson(data, clazz);
                    }
                } else {
                    Utils.logError("The specified key:" + key + " is not found");
                }
                break;
        }
        return null;
    }

    /**
     * @param name       文件名
     * @param key        存储key
     * @param clazz      集合中每条数据的类型
     * @param collection 集合类型（List）
     * @param type       存储类型
     *                   加载List型数据
     */
    public static <T> List<T> loadListCache(String name, String key, Class<T> clazz, Class<T> collection, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = Utils.readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            return getList(clazz, collection, value);
                        } else {
                            Utils.logError("The specified key:" + key + " is not found");
                        }
                    } else {
                        Utils.logError("No history cache for the specified key:" + key);
                    }

                } catch (JSONException | IOException e) {
                    Utils.logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = Utils.readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            String value = jsonObject.getString(key);
                            if (!TextUtils.isEmpty(value)) {
                                return getList(clazz, collection, value);
                            } else {
                                Utils.logError("The specified key:" + key + " is not found");
                            }
                        } else {
                            Utils.logError("No history cache for the specified key:" + key);
                        }
                    } catch (JSONException | IOException e) {
                        Utils.logError(Log.getStackTraceString(e));
                    }
                } else {
                    Utils.logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                if (sharedPreferences.contains(key)) {
                    String data = sharedPreferences.getString(key, "");
                    try {
                        if (!TextUtils.isEmpty(data)) {
                            return getList(clazz, collection, data);
                        }
                    } catch (JSONException e) {
                        Utils.logError(Log.getStackTraceString(e));
                    }
                } else {
                    Utils.logError("The specified key:" + key + " is not found");
                }
                break;
        }
        return null;
    }

    private static <T> List<T> getList(Class<T> clazz, Class<T> collection, String value) throws JSONException {
        List<T> list = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(value);
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(Utils.gson.fromJson(jsonArray.getString(i), clazz));
        }
        return list;
    }

    /**
     * @param name 文件名
     * @param keys 存储key集合
     * @param type 存储类型
     *             删除对应的key
     */
    public static void removeKey(String name, String[] keys, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = Utils.readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        for (String key : keys) {
                            jsonObject.remove(key);
                        }
                        Utils.writeStringToFile(fileInApp, jsonObject.toString());
                    } else {
                        Utils.logError("No history cache for the specified file:" + name);
                    }
                } catch (JSONException | IOException e) {
                    Utils.logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = Utils.readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            for (String key : keys) {
                                jsonObject.remove(key);
                            }
                            Utils.writeStringToFile(fileOnDisk, jsonObject.toString());
                        } else {
                            Utils.logError("No history cache for the specified file:" + name);
                        }
                    } catch (JSONException | IOException e) {
                        Utils.logError(Log.getStackTraceString(e));
                    }
                } else {
                    Utils.logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (String key : keys) {
                    if (sharedPreferences.contains(key)) {
                        editor.remove(key);
                    } else {
                        Utils.logError("The specified key:" + key + " is not found");
                    }
                }
                Utils.apply(editor);
                break;
        }

    }

    /**
     * @param name 文件名
     * @param type 存储类型
     *             清除文件
     */
    public static void clear(String name, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                if (fileInApp.exists()) {
                    fileInApp.delete();
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    if (fileOnDisk.exists()) {
                        fileOnDisk.delete();
                    }
                } else {
                    Utils.logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                Utils.apply(editor);
                break;
        }

    }

    private static String getFileInAppPath(String name) {
        return EasyCacheManager.getInstance().getContext().getCacheDir().getPath() + File.separator + Utils.TAG + File.separator + name;
    }

    private static String getFileOnDiskPath(String name) {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + Utils.TAG + File.separator + EasyCacheManager.getInstance().getContext().getPackageName().replaceAll("\\.", "_") + File.separator + name;
    }


    public static void clear(Class<?> clazz, Type type) {
        String name = getCacheName(clazz);
        clear(name, type);
    }

    /**
     * 获取文件名，如果没有指定，则以类名代替
     */
    public static String getCacheName(Class<?> clazz) {
        String name = "";
        if (clazz.getAnnotation(EasyCache.class) != null) {
            EasyCache easyCache = clazz.getAnnotation(EasyCache.class);
            name = easyCache.name();
        }
        if (TextUtils.isEmpty(name)) {
            name = clazz.getSimpleName();
        }
        return name;
    }

    /**
     * 获取存储类型，如果没有指定，则默认SHARED_PREFERENCE
     */
    public static Type getCacheType(Class<?> clazz) {
        Type type = null;
        if (clazz.getAnnotation(EasyCache.class) != null) {
            EasyCache easyCache = clazz.getAnnotation(EasyCache.class);
            type = easyCache.type();
        }

        if (type == null) {
            type = Type.SHARED_PREFERENCE;
        }

        return type;
    }

}
