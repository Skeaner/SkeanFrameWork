package skean.me.base.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.lang.reflect.Type;

/**
 * GSON工具类
 */
public class GsonUtils {
    public static Gson getSerializer() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm").setPrettyPrinting().create();
    }

    public static Gson getDeserializer() {
        return new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    public static Gson getDeserializer(Type type, Object adapter) {
        return new GsonBuilder().enableComplexMapKeySerialization().registerTypeAdapter(type, adapter).create();
    }

    public static Gson getDeserializer(RuntimeTypeAdapterFactory factory) {
        return new GsonBuilder().enableComplexMapKeySerialization().registerTypeAdapterFactory(factory).create();
    }

}
