package com.siddhantkushwaha.lucy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

public class CommonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static JsonObject fromJsonFile(String fileName) {
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(jsonReader == null)
            return null;
        else
            try {
                return gson.fromJson(jsonReader, JsonObject.class);
            }catch (Exception e) {
                return null;
            }
    }

//    public static
}
