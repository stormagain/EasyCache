package com.stormagain.easycache;

import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 37X21=777 on 15/9/23.
 */

final class Utils {
    public final static String TAG = "EasyCache";

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

