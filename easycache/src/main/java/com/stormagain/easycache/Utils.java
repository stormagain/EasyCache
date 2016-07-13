package com.stormagain.easycache;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 37X21=777 on 15/9/23.
 */

final class Utils {
    private final static String TAG = "EasyCache";

    static <T> void validateClass(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Only interface endpoint definitions are supported.");
        }

        if (clazz.getInterfaces().length > 0) {
            throw new IllegalArgumentException("Interface definitions must not extend other interfaces.");
        }
    }

    private Utils() {
        // No instances.
    }

    public static Gson gson = new Gson();

    public static void logError(String msg) {
        Log.e(TAG, msg);
    }

    public static void cache(String name, String key, String data, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String historyData = readStringFromFile(fileInApp);
                    JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                    jsonObject.put(key, data);
                    writeStringToFile(fileInApp, jsonObject.toString());
                } catch (JSONException | IOException e) {
                    logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String historyData = readStringFromFile(fileOnDisk);
                        JSONObject jsonObject = TextUtils.isEmpty(historyData) ? new JSONObject() : new JSONObject(historyData);
                        jsonObject.put(key, data);
                        writeStringToFile(fileOnDisk, jsonObject.toString());
                    } catch (JSONException | IOException e) {
                        logError(Log.getStackTraceString(e));
                    }
                } else {
                    logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, data);
                apply(editor);
                break;
        }
    }

    public static <T> T loadCache(String name, String key, Class<T> clazz, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            return Utils.gson.fromJson(value, clazz);
                        } else {
                            logError("The specified key:" + key + " is not found");
                        }
                    } else {
                        logError("No history cache for the specified key:" + key);
                    }

                } catch (JSONException | IOException e) {
                    logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            String value = jsonObject.getString(key);
                            if (!TextUtils.isEmpty(value)) {
                                return Utils.gson.fromJson(value, clazz);
                            } else {
                                logError("The specified key:" + key + " is not found");
                            }
                        } else {
                            logError("No history cache for the specified key:" + key);
                        }

                    } catch (JSONException | IOException e) {
                        logError(Log.getStackTraceString(e));
                    }
                } else {
                    logError("No SDCard");
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
                    logError("The specified key:" + key + " is not found");
                }
                break;
        }
        return null;
    }

    public static <T> List<T> loadListCache(String name, String key, Class<T[]> clazz, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        String value = jsonObject.getString(key);
                        if (!TextUtils.isEmpty(value)) {
                            T[] arr = Utils.gson.fromJson(value, clazz);
                            List<T> arrayList = new ArrayList<>();
                            for (T t : arr) {
                                arrayList.add(t);
                            }
                            return arrayList;
                        } else {
                            logError("The specified key:" + key + " is not found");
                        }
                    } else {
                        logError("No history cache for the specified key:" + key);
                    }

                } catch (JSONException | IOException e) {
                    logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            String value = jsonObject.getString(key);
                            if (!TextUtils.isEmpty(value)) {
                                T[] arr = Utils.gson.fromJson(value, clazz);
                                List<T> arrayList = new ArrayList<>();
                                for (T t : arr) {
                                    arrayList.add(t);
                                }
                                return arrayList;
                            } else {
                                logError("The specified key:" + key + " is not found");
                            }
                        } else {
                            logError("No history cache for the specified key:" + key);
                        }
                    } catch (JSONException | IOException e) {
                        logError(Log.getStackTraceString(e));
                    }
                } else {
                    logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                if (sharedPreferences.contains(key)) {
                    String data = sharedPreferences.getString(key, "");
                    if (!TextUtils.isEmpty(data)) {
                        T[] arr = Utils.gson.fromJson(data, clazz);
                        List<T> arrayList = new ArrayList<>();
                        for (T t : arr) {
                            arrayList.add(t);
                        }
                        return arrayList;
                    }
                } else {
                    logError("The specified key:" + key + " is not found");
                }
                break;
        }
        return null;
    }

    public static void removeKey(String name, String key, Type type) {
        switch (type) {
            case FILE_IN_APP:
                File fileInApp = new File(getFileInAppPath(name));
                try {
                    String data = readStringFromFile(fileInApp);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        jsonObject.remove(key);
                        writeStringToFile(fileInApp, jsonObject.toString());
                    } else {
                        logError("No history cache for the specified key:" + key);
                    }
                } catch (JSONException | IOException e) {
                    logError(Log.getStackTraceString(e));
                }
                break;
            case FILE_ON_DISK:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File fileOnDisk = new File(getFileOnDiskPath(name));
                    try {
                        String data = readStringFromFile(fileOnDisk);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
                            jsonObject.remove(key);
                            writeStringToFile(fileOnDisk, jsonObject.toString());
                        } else {
                            logError("No history cache for the specified key:" + key);
                        }
                    } catch (JSONException | IOException e) {
                        logError(Log.getStackTraceString(e));
                    }
                } else {
                    logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                if (sharedPreferences.contains(key)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(key);
                    apply(editor);
                } else {
                    logError("The specified key:" + key + " is not found");
                }
                break;
        }

    }

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
                    logError("No SDCard");
                }
                break;
            case SHARED_PREFERENCE:
            default:
                SharedPreferences sharedPreferences = EasyCacheManager.getInstance().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                apply(editor);
                break;
        }

    }

    private static String getFileInAppPath(String name) {
        return EasyCacheManager.getInstance().getContext().getCacheDir().getPath() + File.separator + TAG + File.separator + name;
    }

    private static String getFileOnDiskPath(String name) {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + TAG + File.separator + EasyCacheManager.getInstance().getContext().getPackageName().replaceAll("\\.", "_") + File.separator + name;
    }


    public static void clear(Class<?> clazz, Type type) {
        String name = getCacheName(clazz);
        clear(name, type);
    }

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

    public static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * 将内容写到文件
     *
     * @param file 待写的文件
     * @param data 文件内容
     * @return 写入是否成功
     * @throws IOException
     */
    public static boolean writeStringToFile(File file, String data) throws IOException {
        if (file != null && data != null) {
            if (file.exists()) {
                file.delete();
            }

            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write(data);
            writer.flush();
            writer.close();
            return true;
        }

        return false;
    }


    /**
     * 读取文件的内容
     *
     * @param file 读取的文件
     * @return 文件内容
     * @throws IOException
     */
    public static String readStringFromFile(File file) throws IOException {
        if (file != null && file.exists()) {
            StringBuilder result = new StringBuilder();
            FileReader reader;
            int length;
            char[] buffer = new char[1024];
            reader = new FileReader(file);
            while ((length = reader.read(buffer)) != -1) {
                result.append(buffer, 0, length);
            }
            reader.close();
            return result.toString();
        } else {
            return "";
        }
    }
}

