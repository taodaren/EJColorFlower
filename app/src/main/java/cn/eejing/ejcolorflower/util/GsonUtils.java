package cn.eejing.ejcolorflower.util;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/9/8.
 */

public class GsonUtils {
    private static Gson gson;

    public static <T> T toObj(String json, Class<T> clz) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(json, clz);
    }

    public static <T> String toJson(T obj) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.toJson(obj);
    }
}
